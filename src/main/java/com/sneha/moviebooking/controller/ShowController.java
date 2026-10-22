package com.sneha.moviebooking.controller;

import com.sneha.moviebooking.dto.ShowRequest;
import com.sneha.moviebooking.dto.ShowResponse;
import com.sneha.moviebooking.entity.ShowStatus;
import com.sneha.moviebooking.service.ShowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/shows")
@RequiredArgsConstructor
@Tag(
        name = "Show APIs",
        description = "APIs for managing event shows"
)
public class ShowController {

    private final ShowService showService;

    @Operation(
            summary = "Create Show",
            description = "Create a new show for an event. Only ADMIN and ORGANIZER can create shows."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Show created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasAnyRole('ADMIN','ORGANIZER')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ShowResponse createShow(
            @Valid @RequestBody ShowRequest request) {

        return showService.createShow(request);
    }

    @Operation(
            summary = "Get All Shows",
            description = "Retrieve all available shows with pagination."
    )
    @ApiResponse(responseCode = "200", description = "Shows retrieved successfully")
    @GetMapping
    public Page<ShowResponse> getAllShows(@ParameterObject Pageable pageable) {

        return showService.getAllShows(pageable);
    }

    @Operation(
            summary = "Get Show by ID",
            description = "Retrieve a show using its unique ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Show found"),
            @ApiResponse(responseCode = "404", description = "Show not found")
    })
    @Operation(
            summary = "Get Seats of a Show",
            description = "Seat map for a show, with the showSeat id, price and status (AVAILABLE / LOCKED / BOOKED)."
    )
    @GetMapping("/{id}/seats")
    public java.util.List<com.sneha.moviebooking.dto.ShowSeatResponse> getShowSeats(
            @PathVariable Long id) {
        return showService.getShowSeats(id);
    }

    @GetMapping("/{id}")
    public ShowResponse getShowById(
            @PathVariable Long id) {

        return showService.getShowById(id);
    }

    @Operation(
            summary = "Filter Shows",
            description = "Filter shows by status, event, venue, organizer and start time range."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Filtered shows retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasAnyRole('ADMIN','ORGANIZER')")
    @GetMapping("/filter")
    public Page<ShowResponse> filterShows(
            @RequestParam(required = false) ShowStatus status,
            @RequestParam(required = false) Long eventId,
            @RequestParam(required = false) Long venueId,
            @RequestParam(required = false) Long organizerId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime startFrom,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime startTo,
            @ParameterObject Pageable pageable
    ) {

        return showService.filterShows(
                status,
                eventId,
                venueId,
                organizerId,
                startFrom,
                startTo,
                pageable
        );
    }
}