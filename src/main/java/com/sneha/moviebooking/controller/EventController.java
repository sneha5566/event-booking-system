package com.sneha.moviebooking.controller;

import com.sneha.moviebooking.dto.EventRequest;
import com.sneha.moviebooking.dto.EventResponse;
import com.sneha.moviebooking.entity.EventCategory;
import com.sneha.moviebooking.entity.EventStatus;
import com.sneha.moviebooking.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springdoc.core.annotations.ParameterObject;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
@Tag(
        name = "Event APIs",
        description = "APIs for managing events"
)
public class EventController {

    private final EventService eventService;

    @Operation(
            summary = "Create Event",
            description = "Create a new event. Only ORGANIZER and ADMIN can create events."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Event created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasAnyRole('ADMIN','ORGANIZER')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventResponse createEvent(@Valid @RequestBody EventRequest request) {
        return eventService.createEvent(request);
    }

    @Operation(
            summary = "Get All Events",
            description = "Retrieve all active events with pagination."
    )
    @ApiResponse(responseCode = "200", description = "Events retrieved successfully")
    @GetMapping
    public Page<EventResponse> getAllEvents(@ParameterObject Pageable pageable) {

        return eventService.getAllEvents(pageable);
    }

    @Operation(
            summary = "Get Event by ID",
            description = "Retrieve a specific event using its ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Event found"),
            @ApiResponse(responseCode = "404", description = "Event not found")
    })
    @GetMapping("/{id}")
    public EventResponse getEventById(@PathVariable Long id) {
        return eventService.getEventById(id);
    }

    @Operation(
            summary = "Update Event",
            description = "Update an existing event. Only ORGANIZER and ADMIN can update events."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Event updated successfully"),
            @ApiResponse(responseCode = "404", description = "Event not found"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasAnyRole('ADMIN','ORGANIZER')")
    @PutMapping("/{id}")
    public ResponseEntity<EventResponse> updateEvent(
            @PathVariable Long id,
            @Valid @RequestBody EventRequest request) {

        return ResponseEntity.ok(eventService.updateEvent(id, request));
    }

    @Operation(
            summary = "Delete Event",
            description = "Soft delete an event by marking it inactive. Only ORGANIZER and ADMIN can delete events."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Event deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Event not found"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasAnyRole('ADMIN','ORGANIZER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {

        eventService.deleteEvent(id);

        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Filter Events",
            description = "Filter events by status, category, and language with pagination."
    )
    @ApiResponse(responseCode = "200", description = "Filtered events retrieved successfully")
    @PreAuthorize("hasAnyRole('ADMIN','ORGANIZER')")
    @GetMapping("/filter")
    public Page<EventResponse> filterEvents(
            @RequestParam(required = false) EventStatus status,
            @RequestParam(required = false) EventCategory category,
            @RequestParam(required = false) String language,
            Pageable pageable
    ) {

        return eventService.filterEvents(
                status,
                category,
                language,
                pageable
        );
    }
}