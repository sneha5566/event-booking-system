package com.sneha.moviebooking.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class AdminDashboardResponse {

    private long totalUsers;

    private long totalOrganizers;

    private long totalEvents;

    private long activeEvents;

    private long totalVenues;

    private long totalShows;

    private long activeShows;

    private long totalBookings;

    private long confirmedBookings;

    private long cancelledBookings;

    private BigDecimal totalRevenue;
}