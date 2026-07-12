package com.sneha.moviebooking.mapper;

import com.sneha.moviebooking.dto.BookingResponse;
import com.sneha.moviebooking.dto.BookingSeatResponse;
import com.sneha.moviebooking.entity.Booking;
import com.sneha.moviebooking.entity.BookingSeat;

import java.util.List;

public class BookingMapper {

    public static BookingResponse toResponse(Booking booking) {

        List<BookingSeatResponse> seats =
                booking.getBookingSeats()
                        .stream()
                        .map(BookingMapper::toSeatResponse)
                        .toList();

        return BookingResponse.builder()
                .id(booking.getId())
                .userId(booking.getUser().getId())
                .userName(booking.getUser().getName())
                .showId(booking.getShow().getId())
                .totalAmount(booking.getTotalAmount())
                .status(booking.getStatus())
                .createdAt(booking.getCreatedAt())
                .seats(seats)
                .build();
    }

    private static BookingSeatResponse toSeatResponse(BookingSeat bookingSeat) {

        return BookingSeatResponse.builder()
                .showSeatId(bookingSeat.getShowSeat().getId())
                .rowName(bookingSeat.getShowSeat().getSeat().getRowName())
                .seatNumber(bookingSeat.getShowSeat().getSeat().getSeatNumber())
                .seatType(bookingSeat.getShowSeat().getSeat().getSeatType().name())
                .price(bookingSeat.getShowSeat().getPrice())
                .build();
    }
}
