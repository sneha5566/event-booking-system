package com.sneha.moviebooking.repository;

import com.sneha.moviebooking.entity.Seat;
import com.sneha.moviebooking.entity.Venue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {

    List<Seat> findByVenue(Venue venue);

    boolean existsByVenueId(Long venueId);
}
