package com.sneha.moviebooking.controller;

import com.sneha.moviebooking.dto.VenueRequest;
import com.sneha.moviebooking.dto.VenueResponse;
import com.sneha.moviebooking.entity.VenueStatus;
import com.sneha.moviebooking.service.VenueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/venues")
@RequiredArgsConstructor
@Tag(
        name = "Venue APIs",
        description = "APIs for managing event venues"
)
public class VenueController {

    private final VenueService venueService;

    @Operation(
            summary = "Create Venue",
            description = "Create a new venue. Only ADMIN and ORGANIZER can create venues."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Venue created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasAnyRole('ADMIN','ORGANIZER')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VenueResponse createVenue(@Valid @RequestBody VenueRequest request) {

        return venueService.createVenue(request);
    }

    @Operation(
            summary = "Get All Venues",
            description = "Retrieve all active venues with pagination."
    )
    @ApiResponse(responseCode = "200", description = "Venues retrieved successfully")
    @GetMapping
    public Page<VenueResponse> getAllVenues(@ParameterObject Pageable pageable) {

        return venueService.getAllVenues(pageable);
    }

    @Operation(
            summary = "Get Venue by ID",
            description = "Retrieve a venue using its ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Venue found"),
            @ApiResponse(responseCode = "404", description = "Venue not found")
    })
    @GetMapping("/{id}")
    public VenueResponse getVenueById(@PathVariable Long id) {

        return venueService.getVenueById(id);
    }

    @Operation(
            summary = "Update Venue",
            description = "Update an existing venue. Only ADMIN and ORGANIZER can update venues."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Venue updated successfully"),
            @ApiResponse(responseCode = "404", description = "Venue not found"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasAnyRole('ADMIN','ORGANIZER')")
    @PutMapping("/{id}")
    public ResponseEntity<VenueResponse> updateVenue(
            @PathVariable Long id,
            @Valid @RequestBody VenueRequest request) {

        return ResponseEntity.ok(venueService.updateVenue(id, request));
    }

    @Operation(
            summary = "Delete Venue",
            description = "Soft delete a venue by marking it inactive. Only ADMIN and ORGANIZER can delete venues."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Venue deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Venue not found"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasAnyRole('ADMIN','ORGANIZER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVenue(@PathVariable Long id) {

        venueService.deleteVenue(id);

        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Filter Venues",
            description = "Filter venues by status, city, state and name with pagination."
    )
    @ApiResponse(responseCode = "200", description = "Filtered venues retrieved successfully")
    @PreAuthorize("hasAnyRole('ADMIN','ORGANIZER')")
    @GetMapping("/filter")
    public Page<VenueResponse> filterVenues(
            @RequestParam(required = false) VenueStatus status,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String name,
            @ParameterObject Pageable pageable
    ) {

        return venueService.filterVenues(
                status,
                city,
                state,
                name,
                pageable
        );
    }
}