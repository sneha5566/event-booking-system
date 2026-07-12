package com.sneha.moviebooking.controller;

import com.sneha.moviebooking.dto.analytics.MonthlyBookingResponse;
import com.sneha.moviebooking.dto.analytics.MonthlyRevenueResponse;
import com.sneha.moviebooking.dto.analytics.TopVenueResponse;
import com.sneha.moviebooking.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.sneha.moviebooking.dto.analytics.TopEventResponse;
import com.sneha.moviebooking.dto.analytics.CategoryAnalyticsResponse;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/admin/analytics/revenue/monthly")
    @PreAuthorize("hasRole('ADMIN')")
    public List<MonthlyRevenueResponse> getMonthlyRevenue() {
        return analyticsService.getMonthlyRevenue();
    }

    @GetMapping("/admin/analytics/bookings/monthly")
    @PreAuthorize("hasRole('ADMIN')")
    public List<MonthlyBookingResponse> getMonthlyBookings() {
        return analyticsService.getMonthlyBookings();
    }
    @GetMapping("/admin/analytics/top-events")
    @PreAuthorize("hasRole('ADMIN')")
    public List<TopEventResponse> getTopEvents() {

        return analyticsService.getTopEvents();
    }
    @GetMapping("/admin/analytics/top-venues")
    @PreAuthorize("hasRole('ADMIN')")
    public List<TopVenueResponse> getTopVenues() {

        return analyticsService.getTopVenues();
    }
    @GetMapping("/admin/analytics/bookings/category")
    @PreAuthorize("hasRole('ADMIN')")
    public List<CategoryAnalyticsResponse> getBookingsByCategory() {

        return analyticsService.getBookingsByCategory();
    }
}