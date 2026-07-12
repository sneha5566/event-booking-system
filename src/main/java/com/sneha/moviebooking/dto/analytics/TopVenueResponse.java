package com.sneha.moviebooking.dto.analytics;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TopVenueResponse {

    private String venueName;

    private Long bookings;
}