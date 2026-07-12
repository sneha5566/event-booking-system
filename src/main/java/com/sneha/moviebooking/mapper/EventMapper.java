package com.sneha.moviebooking.mapper;

import com.sneha.moviebooking.dto.EventRequest;
import com.sneha.moviebooking.dto.EventResponse;
import com.sneha.moviebooking.entity.Event;
import com.sneha.moviebooking.entity.EventStatus;

public class EventMapper {

    public static Event toEntity(EventRequest request) {

        Event event = new Event();

        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setCategory(request.getCategory());
        event.setDuration(request.getDuration());
        event.setLanguage(request.getLanguage());
        event.setAgeRating(request.getAgeRating());
        event.setPosterUrl(request.getPosterUrl());
        event.setStatus(EventStatus.ACTIVE);

        return event;
    }

    public static EventResponse toResponse(Event event) {

        return EventResponse.builder()
                .id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .category(event.getCategory())
                .duration(event.getDuration())
                .language(event.getLanguage())
                .ageRating(event.getAgeRating())
                .posterUrl(event.getPosterUrl())
                .status(event.getStatus())
                .build();
    }
}