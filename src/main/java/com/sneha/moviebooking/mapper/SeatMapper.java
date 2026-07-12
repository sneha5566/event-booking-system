package com.sneha.moviebooking.mapper;

import com.sneha.moviebooking.dto.SeatResponse;
import com.sneha.moviebooking.entity.Seat;

public class SeatMapper {

    public static SeatResponse toResponse(Seat seat) {

        return SeatResponse.builder()
                .id(seat.getId())
                .venueId(seat.getVenue().getId())
                .rowName(seat.getRowName())
                .seatNumber(seat.getSeatNumber())
                .seatType(seat.getSeatType())
                .status(seat.getStatus())
                .build();
    }
}
