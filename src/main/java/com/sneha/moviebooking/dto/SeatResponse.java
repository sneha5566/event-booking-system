package com.sneha.moviebooking.dto;

import com.sneha.moviebooking.entity.SeatStatus;
import com.sneha.moviebooking.entity.SeatType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeatResponse {

    private Long id;

    private Long venueId;

    private String rowName;

    private Integer seatNumber;

    private SeatType seatType;

    private SeatStatus status;
}
