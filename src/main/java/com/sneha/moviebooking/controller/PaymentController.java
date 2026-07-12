package com.sneha.moviebooking.controller;

import com.sneha.moviebooking.dto.PaymentRequest;
import com.sneha.moviebooking.dto.PaymentResponse;
import com.sneha.moviebooking.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(
        name = "Payment APIs",
        description = "APIs for processing and viewing booking payments"
)
public class PaymentController {

    private final PaymentService paymentService;

    @Operation(
            summary = "Make Payment",
            description = "Process payment for an existing booking."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Payment completed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid payment request"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "404", description = "Booking not found")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentResponse makePayment(
            @Valid @RequestBody PaymentRequest request) {

        return paymentService.makePayment(request);
    }

    @Operation(
            summary = "Get Payment by ID",
            description = "Retrieve payment details using the payment ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Payment found"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "404", description = "Payment not found")
    })
    @GetMapping("/{id}")
    public PaymentResponse getPaymentById(
            @PathVariable Long id) {

        return paymentService.getPaymentById(id);
    }

    @Operation(
            summary = "Get Payments by Booking",
            description = "Retrieve all payments associated with a specific booking."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Payments retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "404", description = "Booking not found")
    })
    @GetMapping("/booking/{bookingId}")
    public List<PaymentResponse> getPaymentsByBooking(
            @PathVariable Long bookingId) {

        return paymentService.getPaymentsByBooking(bookingId);
    }
}