package com.sneha.moviebooking.specification;

import com.sneha.moviebooking.entity.Event;
import com.sneha.moviebooking.entity.EventCategory;
import com.sneha.moviebooking.entity.EventStatus;
import org.springframework.data.jpa.domain.Specification;

public class EventSpecification {

    public static Specification<Event> hasStatus(EventStatus status) {
        return (root, query, cb) ->
                status == null ? null : cb.equal(root.get("status"), status);
    }

    public static Specification<Event> hasCategory(EventCategory category) {
        return (root, query, cb) ->
                category == null ? null : cb.equal(root.get("category"), category);
    }

    public static Specification<Event> hasLanguage(String language) {
        return (root, query, cb) -> {

            if (language == null || language.isBlank()) {
                return null;
            }

            return cb.equal(root.get("language"), language);
        };
    }
}