package com.sneha.moviebooking.dto;

import com.sneha.moviebooking.entity.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PaymentRequest {

    @NotNull
    private Long bookingId;

    @NotNull
    private PaymentMethod paymentMethod;

    // Used only for testing our mock payment gateway
    @NotNull
    private Boolean simulateSuccess;
}