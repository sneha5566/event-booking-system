package com.sneha.moviebooking.dto;

import com.sneha.moviebooking.entity.SeatType;
import com.sneha.moviebooking.entity.ShowSeatStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShowSeatResponse {
    private Long id;            // showSeatId - send this when creating a booking
    private String rowName;
    private Integer seatNumber;
    private SeatType seatType;
    private BigDecimal price;
    private ShowSeatStatus status;
}
