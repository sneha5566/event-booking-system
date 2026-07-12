package com.sneha.moviebooking.service;

import com.sneha.moviebooking.dto.CancelBookingResponse;
import com.sneha.moviebooking.entity.*;
import com.sneha.moviebooking.mapper.BookingMapper;
import com.sneha.moviebooking.repository.BookingRepository;
import com.sneha.moviebooking.repository.PaymentRepository;
import com.sneha.moviebooking.repository.ShowRepository;
import com.sneha.moviebooking.repository.ShowSeatRepository;
import com.sneha.moviebooking.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import java.util.List;
import java.time.LocalDateTime;

import com.sneha.moviebooking.dto.BookingResponse;
import com.sneha.moviebooking.dto.CreateBookingRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.transaction.annotation.Transactional;
import com.sneha.moviebooking.specification.BookingSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;


import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Transactional
public class BookingService {

    private final BookingRepository bookingRepository;
    private final ShowRepository showRepository;
    private final ShowSeatRepository showSeatRepository;
    private final SecurityUtil securityUtil;
    private final PaymentRepository paymentRepository;

    public BookingResponse createBooking(CreateBookingRequest request) {

        User currentUser = securityUtil.getCurrentUser();

        Show show = showRepository.findById(request.getShowId())
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Show not found"));
        List<ShowSeat> showSeats =
                showSeatRepository.findByShowAndIdIn(
                        show,
                        request.getShowSeatIds()
                );
        if (showSeats.size() != request.getShowSeatIds().size()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "One or more seats are invalid."
            );
        }
        for (ShowSeat showSeat : showSeats) {

            if (showSeat.getStatus() != ShowSeatStatus.AVAILABLE) {

                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "One or more seats are no longer available."
                );
            }
        }
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (ShowSeat showSeat : showSeats) {

            totalAmount = totalAmount.add(showSeat.getPrice());
        }
        Booking booking = new Booking();

        booking.setUser(currentUser);
        booking.setShow(show);
        booking.setTotalAmount(totalAmount);
        booking.setStatus(BookingStatus.PENDING);
        booking = bookingRepository.save(booking);

        for (ShowSeat showSeat : showSeats) {

            BookingSeat bookingSeat = new BookingSeat();

            bookingSeat.setBooking(booking);
            bookingSeat.setShowSeat(showSeat);

            booking.getBookingSeats().add(bookingSeat);
            showSeat.setStatus(ShowSeatStatus.LOCKED);
        }
        showSeatRepository.saveAll(showSeats);
        booking = bookingRepository.save(booking);
        return BookingMapper.toResponse(booking);
    }

    public List<BookingResponse> getMyBookings() {

        User currentUser = securityUtil.getCurrentUser();

        List<Booking> bookings = bookingRepository.findByUser(currentUser);

        return bookings.stream()
                .map(BookingMapper::toResponse)
                .toList();
    }
    public Page<BookingResponse> getMyBookings(Pageable pageable) {

        User currentUser = securityUtil.getCurrentUser();

        return bookingRepository
                .findByUser(currentUser, pageable)
                .map(BookingMapper::toResponse);
    }

    public BookingResponse getBookingById(Long id) {

        User currentUser = securityUtil.getCurrentUser();

        Booking booking = bookingRepository
                .findByIdAndUser(id, currentUser)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Booking not found"
                        ));

        return BookingMapper.toResponse(booking);
    }
    public CancelBookingResponse cancelBooking(Long bookingId) {

        User currentUser = securityUtil.getCurrentUser();

        Booking booking = bookingRepository
                .findByIdAndUser(bookingId, currentUser)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Booking not found"
                        ));

        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Only confirmed bookings can be cancelled."
            );
        }

        if (booking.getShow().getStartTime().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Cannot cancel after the show has started."
            );
        }

        Payment payment = paymentRepository
                .findFirstByBookingAndStatus(
                        booking,
                        PaymentStatus.SUCCESS
                )
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Successful payment not found."
                        ));

        payment.setStatus(PaymentStatus.REFUNDED);

        booking.setStatus(BookingStatus.CANCELLED);

        for (BookingSeat bookingSeat : booking.getBookingSeats()) {

            bookingSeat.getShowSeat()
                    .setStatus(ShowSeatStatus.AVAILABLE);
        }

        showSeatRepository.saveAll(
                booking.getBookingSeats()
                        .stream()
                        .map(BookingSeat::getShowSeat)
                        .toList()
        );

        paymentRepository.save(payment);
        bookingRepository.save(booking);

        return CancelBookingResponse.builder()
                .bookingId(booking.getId())
                .bookingStatus(booking.getStatus())
                .paymentStatus(payment.getStatus())
                .refundAmount(payment.getAmount())
                .message("Refund initiated successfully.")
                .build();
    }
    public Page<BookingResponse> filterMyBookings(
            BookingStatus status,
            Long showId,
            LocalDateTime bookingFrom,
            LocalDateTime bookingTo,
            Pageable pageable
    ) {

        User currentUser = securityUtil.getCurrentUser();

        Specification<Booking> specification =
                Specification.where(BookingSpecification.hasUser(currentUser))
                        .and(BookingSpecification.hasStatus(status))
                        .and(BookingSpecification.hasShow(showId))
                        .and(BookingSpecification.bookedAfter(bookingFrom))
                        .and(BookingSpecification.bookedBefore(bookingTo));

        return bookingRepository.findAll(specification, pageable)
                .map(BookingMapper::toResponse);
    }
}
