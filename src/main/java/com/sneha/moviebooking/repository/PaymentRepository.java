package com.sneha.moviebooking.repository;

import com.sneha.moviebooking.entity.Booking;
import com.sneha.moviebooking.entity.Payment;
import com.sneha.moviebooking.entity.PaymentStatus;
import com.sneha.moviebooking.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import com.sneha.moviebooking.dto.analytics.MonthlyRevenueResponse;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByBooking(Booking booking);

    List<Payment> findByStatus(PaymentStatus status);

    List<Payment> findByStatusAndCreatedAtBefore(
            PaymentStatus status,
            LocalDateTime time
    );
    boolean existsByBookingAndStatus(
            Booking booking,
            PaymentStatus status
    );
    Optional<Payment> findFirstByBookingAndStatus(
            Booking booking,
            PaymentStatus status
    );
    @Query("""
SELECT COALESCE(SUM(p.amount),0)
FROM Payment p
WHERE p.booking.show.organizer = :organizer
AND p.status = com.sneha.moviebooking.entity.PaymentStatus.SUCCESS
""")
    BigDecimal getRevenue(@Param("organizer") User organizer);

    @Query("""
SELECT COALESCE(SUM(p.amount), 0)
FROM Payment p
WHERE p.status = com.sneha.moviebooking.entity.PaymentStatus.SUCCESS
""")
    BigDecimal getTotalRevenue();
    @Query("""
SELECT COALESCE(SUM(p.amount), 0)
FROM Payment p
WHERE p.booking.user = :user
AND p.status = com.sneha.moviebooking.entity.PaymentStatus.SUCCESS
""")
    BigDecimal getTotalSpent(
            @Param("user") User user
    );
    @Query("""
SELECT new com.sneha.moviebooking.dto.analytics.MonthlyRevenueResponse(
    YEAR(p.paidAt),
    MONTH(p.paidAt),
    SUM(p.amount)
)
FROM Payment p
WHERE p.status = com.sneha.moviebooking.entity.PaymentStatus.SUCCESS
GROUP BY YEAR(p.paidAt), MONTH(p.paidAt)
ORDER BY YEAR(p.paidAt), MONTH(p.paidAt)
""")
    List<MonthlyRevenueResponse> getMonthlyRevenue();
}
