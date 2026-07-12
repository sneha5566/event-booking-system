package com.sneha.moviebooking.controller;

import com.sneha.moviebooking.dto.BookingResponse;
import com.sneha.moviebooking.dto.CancelBookingResponse;
import com.sneha.moviebooking.dto.CreateBookingRequest;
import com.sneha.moviebooking.entity.BookingStatus;
import com.sneha.moviebooking.service.BookingService;
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
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
@Tag(
        name = "Booking APIs",
        description = "APIs for booking, viewing and cancelling event tickets"
)
public class BookingController {

    private final BookingService bookingService;

    @Operation(
            summary = "Create Booking",
            description = "Book one or more seats for a show."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Booking created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "409", description = "Selected seats are unavailable")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponse createBooking(
            @Valid @RequestBody CreateBookingRequest request) {

        return bookingService.createBooking(request);
    }

    @Operation(
            summary = "My Bookings",
            description = "Retrieve all bookings of the currently logged-in user with pagination."
    )
    @ApiResponse(responseCode = "200", description = "Bookings retrieved successfully")
    @GetMapping("/my")
    public Page<BookingResponse> getMyBookings(
            @ParameterObject Pageable pageable) {

        return bookingService.getMyBookings(pageable);
    }

    @Operation(
            summary = "Get Booking by ID",
            description = "Retrieve details of a booking by its ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Booking found"),
            @ApiResponse(responseCode = "404", description = "Booking not found")
    })
    @GetMapping("/{id}")
    public BookingResponse getBookingById(
            @PathVariable Long id) {

        return bookingService.getBookingById(id);
    }

    @Operation(
            summary = "Cancel Booking",
            description = "Cancel an existing booking and release the booked seats."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Booking cancelled successfully"),
            @ApiResponse(responseCode = "400", description = "Booking cannot be cancelled"),
            @ApiResponse(responseCode = "404", description = "Booking not found")
    })
    @PostMapping("/{id}/cancel")
    public CancelBookingResponse cancelBooking(
            @PathVariable Long id) {

        return bookingService.cancelBooking(id);
    }

    @Operation(
            summary = "Filter My Bookings",
            description = "Filter the logged-in user's bookings by booking status, show and booking date."
    )
    @ApiResponse(responseCode = "200", description = "Filtered bookings retrieved successfully")
    @GetMapping("/my/filter")
    public Page<BookingResponse> filterMyBookings(
            @RequestParam(required = false) BookingStatus status,
            @RequestParam(required = false) Long showId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime bookingFrom,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime bookingTo,
            @ParameterObject Pageable pageable
    ) {

        return bookingService.filterMyBookings(
                status,
                showId,
                bookingFrom,
                bookingTo,
                pageable
        );
    }
}