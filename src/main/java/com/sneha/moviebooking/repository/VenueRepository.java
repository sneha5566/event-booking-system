package com.sneha.moviebooking.repository;

import com.sneha.moviebooking.entity.Venue;
import com.sneha.moviebooking.entity.VenueStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface VenueRepository extends JpaRepository<Venue, Long>,
        JpaSpecificationExecutor<Venue> {

    List<Venue> findByStatus(VenueStatus status);

    Page<Venue> findByStatus(VenueStatus status, Pageable pageable);
}