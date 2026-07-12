package com.sneha.moviebooking.repository;

import com.sneha.moviebooking.entity.Booking;
import com.sneha.moviebooking.entity.BookingStatus;
import com.sneha.moviebooking.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.sneha.moviebooking.dto.analytics.MonthlyBookingResponse;
import com.sneha.moviebooking.dto.analytics.CategoryAnalyticsResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import com.sneha.moviebooking.dto.analytics.TopVenueResponse;
import org.springframework.data.domain.Pageable;

public interface BookingRepository extends JpaRepository<Booking, Long>,
        JpaSpecificationExecutor<Booking> {

    List<Booking> findByUser(User user);
    Optional<Booking> findByIdAndUser(Long id, User user);

    List<Booking> findByUserAndStatus(User user, BookingStatus status);

    List<Booking> findByStatusAndCreatedAtBefore(
            BookingStatus status,
            LocalDateTime time
    );
    long countByShowOrganizer(User organizer);

    long countByShowOrganizerAndStatus(
            User organizer,
            BookingStatus status
    );

    long countByStatus(BookingStatus status);
    long countByUser(User user);

    long countByUserAndStatus(
            User user,
            BookingStatus status
    );

    long countByUserAndShowStartTimeAfter(
            User user,
            LocalDateTime time
    );
    @Query("""
SELECT new com.sneha.moviebooking.dto.analytics.MonthlyBookingResponse(
    YEAR(b.createdAt),
    MONTH(b.createdAt),
    COUNT(b)
)
FROM Booking b
GROUP BY YEAR(b.createdAt), MONTH(b.createdAt)
ORDER BY YEAR(b.createdAt), MONTH(b.createdAt)
""")
    List<MonthlyBookingResponse> getMonthlyBookings();

    @Query("""
SELECT new com.sneha.moviebooking.dto.analytics.TopVenueResponse(
    b.show.venue.name,
    COUNT(b)
)
FROM Booking b
WHERE b.status = com.sneha.moviebooking.entity.BookingStatus.CONFIRMED
GROUP BY b.show.venue.name
ORDER BY COUNT(b) DESC
""")
    List<TopVenueResponse> getTopVenues(Pageable pageable);

    @Query("""
SELECT new com.sneha.moviebooking.dto.analytics.CategoryAnalyticsResponse(
    b.show.event.category,
    COUNT(b)
)
FROM Booking b
WHERE b.status = com.sneha.moviebooking.entity.BookingStatus.CONFIRMED
GROUP BY b.show.event.category
ORDER BY COUNT(b) DESC
""")
    List<CategoryAnalyticsResponse> getBookingsByCategory();

    Page<Booking> findByUser(User user, Pageable pageable);
}
