package com.sneha.moviebooking.repository;

import com.sneha.moviebooking.entity.Event;
import com.sneha.moviebooking.entity.Venue;
import com.sneha.moviebooking.entity.VenueStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import com.sneha.moviebooking.entity.EventStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {
    List<Event> findByStatus(EventStatus status);
    Page<Venue> findByStatus(VenueStatus status, Pageable pageable);

    long countByStatus(EventStatus status);
    Page<Event> findByStatus(
            EventStatus status,
            Pageable pageable
    );
}