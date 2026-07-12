package com.sneha.moviebooking.controller;

import com.sneha.moviebooking.dto.GenerateSeatRequest;
import com.sneha.moviebooking.dto.SeatResponse;
import com.sneha.moviebooking.service.SeatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("/api/seats")
@RequiredArgsConstructor
public class SeatController {

    private final SeatService seatService;

    @PreAuthorize("hasAnyRole('ADMIN','ORGANIZER')")
    @PostMapping("/generate")
    @ResponseStatus(HttpStatus.CREATED)
    public List<SeatResponse> generateSeats(
            @Valid @RequestBody GenerateSeatRequest request) {

        return seatService.generateSeats(request);
    }

    @GetMapping("/venue/{venueId}")
    public List<SeatResponse> getSeatsByVenue(
            @PathVariable Long venueId) {

        return seatService.getSeatsByVenue(venueId);
    }
}
