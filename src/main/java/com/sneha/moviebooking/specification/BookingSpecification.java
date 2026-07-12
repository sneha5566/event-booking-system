package com.sneha.moviebooking.specification;

import com.sneha.moviebooking.entity.Booking;
import com.sneha.moviebooking.entity.BookingStatus;
import com.sneha.moviebooking.entity.User;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class BookingSpecification {
    public static Specification<Booking> hasUser(User user) {
        return (root, query, cb) ->
                user == null ? null :
                        cb.equal(root.get("user"), user);
    }

    public static Specification<Booking> hasUser(Long userId) {
        return (root, query, cb) ->
                userId == null ? null :
                        cb.equal(root.get("user").get("id"), userId);
    }

    public static Specification<Booking> hasShow(Long showId) {
        return (root, query, cb) ->
                showId == null ? null :
                        cb.equal(root.get("show").get("id"), showId);
    }

    public static Specification<Booking> hasStatus(BookingStatus status) {
        return (root, query, cb) ->
                status == null ? null :
                        cb.equal(root.get("status"), status);
    }

    public static Specification<Booking> bookedAfter(LocalDateTime bookingFrom) {
        return (root, query, cb) ->
                bookingFrom == null ? null :
                        cb.greaterThanOrEqualTo(root.get("createdAt"), bookingFrom);
    }

    public static Specification<Booking> bookedBefore(LocalDateTime bookingTo) {
        return (root, query, cb) ->
                bookingTo == null ? null :
                        cb.lessThanOrEqualTo(root.get("createdAt"), bookingTo);
    }
}