package com.sneha.moviebooking.scheduler;

import com.sneha.moviebooking.entity.*;
import com.sneha.moviebooking.repository.BookingRepository;
import com.sneha.moviebooking.repository.PaymentRepository;
import com.sneha.moviebooking.repository.ShowSeatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class BookingTimeoutScheduler {

    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final ShowSeatRepository showSeatRepository;

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void releaseExpiredBookings() {

        LocalDateTime expiryTime = LocalDateTime.now().minusMinutes(5);

        List<Booking> expiredBookings =
                bookingRepository.findByStatusAndCreatedAtBefore(
                        BookingStatus.PENDING,
                        expiryTime
                );

        for (Booking booking : expiredBookings) {

            booking.setStatus(BookingStatus.CANCELLED);

            paymentRepository
                    .findFirstByBookingAndStatus(
                            booking,
                            PaymentStatus.PENDING
                    )
                    .ifPresent(payment -> {

                        payment.setStatus(PaymentStatus.FAILED);

                        paymentRepository.save(payment);
                    });

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

            bookingRepository.save(booking);

            log.info("Booking {} expired automatically.", booking.getId());
        }
    }
}
