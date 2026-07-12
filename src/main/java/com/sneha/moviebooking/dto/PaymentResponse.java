package com.sneha.moviebooking.dto;

import com.sneha.moviebooking.entity.PaymentMethod;
import com.sneha.moviebooking.entity.PaymentStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class PaymentResponse {

    private Long id;

    private Long bookingId;

    private BigDecimal amount;

    private PaymentMethod paymentMethod;

    private PaymentStatus status;

    private String transactionId;

    private LocalDateTime paidAt;

    private LocalDateTime createdAt;
}