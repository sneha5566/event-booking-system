package com.sneha.moviebooking.service;

import com.sneha.moviebooking.dto.analytics.*;
import com.sneha.moviebooking.repository.BookingRepository;
import com.sneha.moviebooking.repository.BookingSeatRepository;
import com.sneha.moviebooking.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.PageRequest;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final BookingSeatRepository bookingSeatRepository;

    public List<MonthlyRevenueResponse> getMonthlyRevenue() {
        return paymentRepository.getMonthlyRevenue();
    }

    public List<MonthlyBookingResponse> getMonthlyBookings() {
        return bookingRepository.getMonthlyBookings();
    }
    public List<TopEventResponse> getTopEvents() {

        return bookingSeatRepository.getTopEvents(
                PageRequest.of(0, 5)
        );
    }
    public List<TopVenueResponse> getTopVenues() {

        return bookingRepository.getTopVenues(
                PageRequest.of(0, 5)
        );
    }
    public List<CategoryAnalyticsResponse> getBookingsByCategory() {

        return bookingRepository.getBookingsByCategory();
    }
}