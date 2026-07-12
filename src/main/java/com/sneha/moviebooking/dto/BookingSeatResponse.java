package com.sneha.moviebooking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingSeatResponse {

    private Long showSeatId;

    private String rowName;

    private Integer seatNumber;

    private String seatType;

    private BigDecimal price;
}
