package com.sneha.moviebooking.dto.analytics;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MonthlyBookingResponse {

    private Integer year;

    private Integer month;

    private Long bookings;
}