package com.sneha.moviebooking.service;

import com.sneha.moviebooking.dto.OrganizerDashboardResponse;
import com.sneha.moviebooking.dto.UserDashboardResponse;
import com.sneha.moviebooking.entity.BookingStatus;
import com.sneha.moviebooking.entity.PaymentStatus;
import com.sneha.moviebooking.entity.ShowStatus;
import com.sneha.moviebooking.entity.User;
import com.sneha.moviebooking.repository.*;
import com.sneha.moviebooking.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.sneha.moviebooking.dto.AdminDashboardResponse;
import com.sneha.moviebooking.entity.EventStatus;
import com.sneha.moviebooking.entity.Role;
import com.sneha.moviebooking.repository.EventRepository;
import com.sneha.moviebooking.repository.UserRepository;
import com.sneha.moviebooking.repository.VenueRepository;
import com.sneha.moviebooking.repository.BookingSeatRepository;


import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final ShowRepository showRepository;
    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final SecurityUtil securityUtil;
    private final BookingSeatRepository bookingSeatRepository;
    private final UserRepository userRepository;

    private final EventRepository eventRepository;

    private final VenueRepository venueRepository;

    public OrganizerDashboardResponse getOrganizerDashboard() {

        User organizer = securityUtil.getCurrentUser();

        OrganizerDashboardResponse response =
                new OrganizerDashboardResponse();

        response.setTotalShows(
                showRepository.countByOrganizer(organizer)
        );

        response.setActiveShows(
                showRepository.countByOrganizerAndStatus(
                        organizer,
                        ShowStatus.ACTIVE
                )
        );

        response.setUpcomingShows(
                showRepository.countByOrganizerAndStartTimeAfter(
                        organizer,
                        LocalDateTime.now()
                )
        );

        response.setTotalBookings(
                bookingRepository.countByShowOrganizer(organizer)
        );

        response.setConfirmedBookings(
                bookingRepository.countByShowOrganizerAndStatus(
                        organizer,
                        BookingStatus.CONFIRMED
                )
        );

        response.setCancelledBookings(
                bookingRepository.countByShowOrganizerAndStatus(
                        organizer,
                        BookingStatus.CANCELLED
                )
        );

        response.setTicketsSold(
                bookingSeatRepository.countTicketsSold(organizer)
        );

        BigDecimal revenue =
                paymentRepository.getRevenue(organizer);

        response.setRevenue(
                revenue == null ? BigDecimal.ZERO : revenue
        );

        return response;
    }
    public AdminDashboardResponse getAdminDashboard() {

        AdminDashboardResponse response = new AdminDashboardResponse();

        response.setTotalUsers(
                userRepository.count()
        );

        response.setTotalOrganizers(
                userRepository.countByRole(Role.ORGANIZER)
        );

        response.setTotalEvents(
                eventRepository.count()
        );

        response.setActiveEvents(
                eventRepository.countByStatus(EventStatus.ACTIVE)
        );

        response.setTotalVenues(
                venueRepository.count()
        );

        response.setTotalShows(
                showRepository.count()
        );

        response.setActiveShows(
                showRepository.countByStatus(ShowStatus.ACTIVE)
        );

        response.setTotalBookings(
                bookingRepository.count()
        );

        response.setConfirmedBookings(
                bookingRepository.countByStatus(
                        BookingStatus.CONFIRMED
                )
        );

        response.setCancelledBookings(
                bookingRepository.countByStatus(
                        BookingStatus.CANCELLED
                )
        );

        response.setTotalRevenue(
                paymentRepository.getTotalRevenue()
        );

        return response;
    }
    public UserDashboardResponse getUserDashboard() {

        User user = securityUtil.getCurrentUser();

        UserDashboardResponse response = new UserDashboardResponse();

        response.setTotalBookings(
                bookingRepository.countByUser(user)
        );

        response.setConfirmedBookings(
                bookingRepository.countByUserAndStatus(
                        user,
                        BookingStatus.CONFIRMED
                )
        );

        response.setCancelledBookings(
                bookingRepository.countByUserAndStatus(
                        user,
                        BookingStatus.CANCELLED
                )
        );

        response.setPendingBookings(
                bookingRepository.countByUserAndStatus(
                        user,
                        BookingStatus.PENDING
                )
        );

        response.setUpcomingBookings(
                bookingRepository.countByUserAndShowStartTimeAfter(
                        user,
                        LocalDateTime.now()
                )
        );

        response.setTotalSpent(
                paymentRepository.getTotalSpent(user)
        );

        return response;
    }
}