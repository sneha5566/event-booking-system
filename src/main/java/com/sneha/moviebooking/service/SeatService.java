package com.sneha.moviebooking.service;

import com.sneha.moviebooking.dto.GenerateSeatRequest;
import com.sneha.moviebooking.dto.SeatResponse;
import com.sneha.moviebooking.entity.*;
import com.sneha.moviebooking.mapper.SeatMapper;
import com.sneha.moviebooking.repository.SeatRepository;
import com.sneha.moviebooking.repository.VenueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SeatService {

    private final SeatRepository seatRepository;
    private final VenueRepository venueRepository;

    @Transactional
    public List<SeatResponse> generateSeats(GenerateSeatRequest request) {

        Venue venue = venueRepository.findById(request.getVenueId())
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Venue not found"));

        if (seatRepository.existsByVenueId(request.getVenueId())) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Seats already exist for this venue");
        }

        if (request.getVipRows() + request.getPremiumRows() > request.getRows()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "VIP rows + Premium rows cannot exceed total rows");
        }

        if (request.getRows() > 26) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Maximum supported rows is 26");
        }

        List<Seat> seats = new ArrayList<>();

        for (int row = 0; row < request.getRows(); row++) {

            String rowName = String.valueOf((char) ('A' + row));

            SeatType seatType;

            if (row < request.getVipRows()) {

                seatType = SeatType.VIP;

            } else if (row < request.getVipRows() + request.getPremiumRows()) {

                seatType = SeatType.PREMIUM;

            } else {

                seatType = SeatType.REGULAR;
            }

            for (int seatNumber = 1;
                 seatNumber <= request.getSeatsPerRow();
                 seatNumber++) {

                Seat seat = new Seat();

                seat.setVenue(venue);
                seat.setRowName(rowName);
                seat.setSeatNumber(seatNumber);
                seat.setSeatType(seatType);
                seat.setStatus(SeatStatus.ACTIVE);

                seats.add(seat);
            }
        }

        List<Seat> savedSeats = seatRepository.saveAll(seats);

        return savedSeats.stream()
                .map(SeatMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<SeatResponse> getSeatsByVenue(Long venueId) {

        Venue venue = venueRepository.findById(venueId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Venue not found"));

        return seatRepository.findByVenue(venue)
                .stream()
                .map(SeatMapper::toResponse)
                .collect(Collectors.toList());
    }
}
