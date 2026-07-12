package com.sneha.moviebooking.mapper;

import com.sneha.moviebooking.dto.VenueRequest;
import com.sneha.moviebooking.dto.VenueResponse;
import com.sneha.moviebooking.entity.Venue;
import com.sneha.moviebooking.entity.VenueStatus;

public class VenueMapper {

    public static Venue toEntity(VenueRequest request) {

        Venue venue = new Venue();

        venue.setName(request.getName());
        venue.setAddress(request.getAddress());
        venue.setCity(request.getCity());
        venue.setState(request.getState());
        venue.setTotalSeats(request.getTotalSeats());
        venue.setStatus(VenueStatus.ACTIVE);

        return venue;
    }

    public static VenueResponse toResponse(Venue venue) {

        return VenueResponse.builder()
                .id(venue.getId())
                .name(venue.getName())
                .address(venue.getAddress())
                .city(venue.getCity())
                .state(venue.getState())
                .totalSeats(venue.getTotalSeats())
                .status(venue.getStatus())
                .build();
    }
}
