package com.sneha.moviebooking.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class OrganizerDashboardResponse {

    private long totalShows;

    private long activeShows;

    private long upcomingShows;

    private long totalBookings;

    private long confirmedBookings;

    private long cancelledBookings;

    private long ticketsSold;

    private BigDecimal revenue;
}