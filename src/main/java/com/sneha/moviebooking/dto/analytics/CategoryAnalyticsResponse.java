package com.sneha.moviebooking.dto.analytics;

import com.sneha.moviebooking.entity.EventCategory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CategoryAnalyticsResponse {

    private EventCategory category;

    private Long bookings;
}