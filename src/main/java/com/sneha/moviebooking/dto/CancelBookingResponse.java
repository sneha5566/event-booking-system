package com.sneha.moviebooking.dto;

import com.sneha.moviebooking.entity.BookingStatus;
import com.sneha.moviebooking.entity.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CancelBookingResponse {

    private Long bookingId;

    private BookingStatus bookingStatus;

    private PaymentStatus paymentStatus;

    private BigDecimal refundAmount;

    private String message;
}
