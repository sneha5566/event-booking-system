package com.sneha.moviebooking.specification;

import com.sneha.moviebooking.entity.Venue;
import com.sneha.moviebooking.entity.VenueStatus;
import org.springframework.data.jpa.domain.Specification;

public class VenueSpecification {

    public static Specification<Venue> hasStatus(VenueStatus status) {
        return (root, query, cb) ->
                status == null ? null : cb.equal(root.get("status"), status);
    }

    public static Specification<Venue> hasCity(String city) {
        return (root, query, cb) -> {

            if (city == null || city.isBlank()) {
                return null;
            }

            return cb.equal(root.get("city"), city);
        };
    }

    public static Specification<Venue> hasState(String state) {
        return (root, query, cb) -> {

            if (state == null || state.isBlank()) {
                return null;
            }

            return cb.equal(root.get("state"), state);
        };
    }

    public static Specification<Venue> hasName(String name) {
        return (root, query, cb) -> {

            if (name == null || name.isBlank()) {
                return null;
            }

            return cb.like(
                    cb.lower(root.get("name")),
                    "%" + name.toLowerCase() + "%"
            );
        };
    }
}