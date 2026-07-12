package com.sneha.moviebooking.dto;

import com.sneha.moviebooking.entity.VenueStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VenueResponse {

    private Long id;

    private String name;

    private String address;

    private String city;

    private String state;

    private Integer totalSeats;

    private VenueStatus status;
}
