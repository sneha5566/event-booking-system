package com.sneha.moviebooking.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GenerateSeatRequest {

    @NotNull(message = "Venue ID is required")
    private Long venueId;

    @NotNull(message = "Rows are required")
    @Min(value = 1, message = "Rows must be at least 1")
    private Integer rows;

    @NotNull(message = "Seats per row are required")
    @Min(value = 1, message = "Seats per row must be at least 1")
    private Integer seatsPerRow;

    @NotNull(message = "VIP rows are required")
    @Min(value = 0, message = "VIP rows cannot be negative")
    private Integer vipRows;

    @NotNull(message = "Premium rows are required")
    @Min(value = 0, message = "Premium rows cannot be negative")
    private Integer premiumRows;
}