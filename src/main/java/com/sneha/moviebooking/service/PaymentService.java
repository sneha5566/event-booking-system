package com.sneha.moviebooking.service;

import com.sneha.moviebooking.dto.PaymentRequest;
import com.sneha.moviebooking.dto.PaymentResponse;
import com.sneha.moviebooking.entity.*;
import com.sneha.moviebooking.mapper.PaymentMapper;
import com.sneha.moviebooking.repository.BookingRepository;
import com.sneha.moviebooking.repository.PaymentRepository;
import com.sneha.moviebooking.repository.ShowSeatRepository;
import com.sneha.moviebooking.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final ShowSeatRepository showSeatRepository;
    private final SecurityUtil securityUtil;
    private final MockPaymentGatewayService mockPaymentGatewayService;

    public PaymentResponse makePayment(PaymentRequest request) {

        User currentUser = securityUtil.getCurrentUser();

        Booking booking = bookingRepository
                .findByIdAndUser(request.getBookingId(), currentUser)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Booking not found"
                        ));

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Only pending bookings can be paid."
            );
        }

        if (paymentRepository.existsByBookingAndStatus(
                booking,
                PaymentStatus.SUCCESS)) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Booking has already been paid."
            );
        }

        Payment payment = new Payment();

        payment.setBooking(booking);
        payment.setAmount(booking.getTotalAmount());
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setStatus(PaymentStatus.PENDING);

        payment = paymentRepository.save(payment);

        return processPayment(payment, booking, request.getSimulateSuccess());
    }

    private PaymentResponse processPayment(
            Payment payment,
            Booking booking,
            Boolean simulateSuccess
    ) {

        String transactionId =
                mockPaymentGatewayService.processPayment(simulateSuccess);

        if (transactionId == null) {
            return handlePaymentFailure(payment, booking);
        }

        return handlePaymentSuccess(
                payment,
                booking,
                transactionId
        );
    }

    private PaymentResponse handlePaymentSuccess(
            Payment payment,
            Booking booking,
            String transactionId
    ) {

        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setTransactionId(transactionId);
        payment.setPaidAt(java.time.LocalDateTime.now());

        booking.setStatus(BookingStatus.CONFIRMED);

        for (BookingSeat bookingSeat : booking.getBookingSeats()) {

            ShowSeat showSeat = bookingSeat.getShowSeat();

            showSeat.setStatus(ShowSeatStatus.BOOKED);

        }
        showSeatRepository.saveAll(
                booking.getBookingSeats()
                        .stream()
                        .map(BookingSeat::getShowSeat)
                        .toList()
        );

        paymentRepository.save(payment);
        bookingRepository.save(booking);

        return PaymentMapper.toResponse(payment);
    }

    private PaymentResponse handlePaymentFailure(
            Payment payment,
            Booking booking
    ) {

        payment.setStatus(PaymentStatus.FAILED);

        booking.setStatus(BookingStatus.CANCELLED);

        for (BookingSeat bookingSeat : booking.getBookingSeats()) {

            ShowSeat showSeat = bookingSeat.getShowSeat();

            showSeat.setStatus(ShowSeatStatus.AVAILABLE);
        }

        paymentRepository.save(payment);
        bookingRepository.save(booking);
        showSeatRepository.saveAll(
                booking.getBookingSeats()
                        .stream()
                        .map(BookingSeat::getShowSeat)
                        .toList()
        );

        return PaymentMapper.toResponse(payment);
    }
    public PaymentResponse getPaymentById(Long id) {

        User currentUser = securityUtil.getCurrentUser();

        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Payment not found"
                        ));

        if (!payment.getBooking().getUser().getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You are not allowed to view this payment."
            );
        }

        return PaymentMapper.toResponse(payment);
    }
    public List<PaymentResponse> getPaymentsByBooking(Long bookingId) {

        User currentUser = securityUtil.getCurrentUser();

        Booking booking = bookingRepository
                .findByIdAndUser(bookingId, currentUser)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Booking not found"
                        ));

        List<Payment> payments =
                paymentRepository.findByBooking(booking);

        return payments.stream()
                .map(PaymentMapper::toResponse)
                .toList();
    }
}