package com.sneha.moviebooking.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ShowRequest {

    @NotNull(message = "Event ID is required")
    private Long eventId;

    @NotNull(message = "Venue ID is required")
    private Long venueId;

    @NotNull(message = "Start time is required")
    @Future(message = "Start time must be in the future")
    private LocalDateTime startTime;

    @NotNull(message = "End time is required")
    private LocalDateTime endTime;

    @NotNull(message = "VIP price is required")
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal vipPrice;

    @NotNull(message = "Premium price is required")
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal premiumPrice;

    @NotNull(message = "Regular price is required")
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal regularPrice;
}
