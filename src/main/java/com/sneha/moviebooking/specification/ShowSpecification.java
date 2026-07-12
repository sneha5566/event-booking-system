package com.sneha.moviebooking.specification;

import com.sneha.moviebooking.entity.Show;
import com.sneha.moviebooking.entity.ShowStatus;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class ShowSpecification {

    public static Specification<Show> hasStatus(ShowStatus status) {
        return (root, query, cb) ->
                status == null ? null : cb.equal(root.get("status"), status);
    }

    public static Specification<Show> hasEvent(Long eventId) {
        return (root, query, cb) ->
                eventId == null ? null :
                        cb.equal(root.get("event").get("id"), eventId);
    }

    public static Specification<Show> hasVenue(Long venueId) {
        return (root, query, cb) ->
                venueId == null ? null :
                        cb.equal(root.get("venue").get("id"), venueId);
    }

    public static Specification<Show> hasOrganizer(Long organizerId) {
        return (root, query, cb) ->
                organizerId == null ? null :
                        cb.equal(root.get("organizer").get("id"), organizerId);
    }

    public static Specification<Show> startsAfter(LocalDateTime startFrom) {
        return (root, query, cb) ->
                startFrom == null ? null :
                        cb.greaterThanOrEqualTo(root.get("startTime"), startFrom);
    }

    public static Specification<Show> startsBefore(LocalDateTime startTo) {
        return (root, query, cb) ->
                startTo == null ? null :
                        cb.lessThanOrEqualTo(root.get("startTime"), startTo);
    }
}