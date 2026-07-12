package com.sneha.moviebooking.repository;

import com.sneha.moviebooking.entity.BookingSeat;
import com.sneha.moviebooking.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.sneha.moviebooking.dto.analytics.TopEventResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BookingSeatRepository extends JpaRepository<BookingSeat, Long> {

    @Query("""
        SELECT COUNT(bs)
        FROM BookingSeat bs
        WHERE bs.booking.show.organizer = :organizer
        AND bs.booking.status = com.sneha.moviebooking.entity.BookingStatus.CONFIRMED
    """)
    long countTicketsSold(
            @Param("organizer") User organizer
    );
    @Query("""
SELECT new com.sneha.moviebooking.dto.analytics.TopEventResponse(
    bs.booking.show.event.title,
    COUNT(bs)
)
FROM BookingSeat bs
WHERE bs.booking.status = com.sneha.moviebooking.entity.BookingStatus.CONFIRMED
GROUP BY bs.booking.show.event.title
ORDER BY COUNT(bs) DESC
""")
    List<TopEventResponse> getTopEvents(Pageable pageable);
}