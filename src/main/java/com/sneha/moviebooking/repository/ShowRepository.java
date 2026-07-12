package com.sneha.moviebooking.repository;
import com.sneha.moviebooking.entity.User;

import com.sneha.moviebooking.entity.Show;
import com.sneha.moviebooking.entity.ShowStatus;
import com.sneha.moviebooking.entity.User;
import com.sneha.moviebooking.entity.Venue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ShowRepository extends JpaRepository<Show, Long>,
        JpaSpecificationExecutor<Show> {

    List<Show> findByStatus(ShowStatus status);
    Page<Show> findByStatus(ShowStatus status, Pageable pageable);

    List<Show> findByVenue(Venue venue);

    boolean existsByVenueAndStartTimeLessThanAndEndTimeGreaterThan(
            Venue venue,
            LocalDateTime endTime,
            LocalDateTime startTime
    );

    long countByOrganizer(User organizer);

    long countByOrganizerAndStatus(
            User organizer,
            ShowStatus status
    );

    long countByOrganizerAndStartTimeAfter(
            User organizer,
            LocalDateTime time
    );
    long countByStatus(ShowStatus status);

}