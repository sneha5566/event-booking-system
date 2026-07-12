package com.sneha.moviebooking.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class UserDashboardResponse {

    private long totalBookings;

    private long confirmedBookings;

    private long cancelledBookings;

    private long pendingBookings;

    private long upcomingBookings;

    private BigDecimal totalSpent;
}