package com.sneha.moviebooking.service;

import com.sneha.moviebooking.dto.EventRequest;
import com.sneha.moviebooking.dto.EventResponse;
import com.sneha.moviebooking.entity.EventCategory;
import com.sneha.moviebooking.entity.EventStatus;
import com.sneha.moviebooking.mapper.EventMapper;
import com.sneha.moviebooking.entity.Event;
import com.sneha.moviebooking.repository.EventRepository;
import com.sneha.moviebooking.specification.EventSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Optional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    public EventResponse createEvent(EventRequest request) {

        Event event = EventMapper.toEntity(request);

        Event savedEvent = eventRepository.save(event);

        return EventMapper.toResponse(savedEvent);
    }
    public List<EventResponse> getAllEvents() {

        List<Event> events = eventRepository.findByStatus(EventStatus.ACTIVE);

        return events.stream()
                .map(EventMapper::toResponse)
                .collect(Collectors.toList());
    }
    public EventResponse getEventById(Long id) {

        Event event = eventRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Event not found"));

        return EventMapper.toResponse(event);
    }

    public EventResponse updateEvent(Long id, EventRequest request) {

        Event event = eventRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Event not found"));

        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setCategory(request.getCategory());
        event.setDuration(request.getDuration());
        event.setLanguage(request.getLanguage());
        event.setAgeRating(request.getAgeRating());
        event.setPosterUrl(request.getPosterUrl());

        Event updatedEvent = eventRepository.save(event);

        return EventMapper.toResponse(updatedEvent);
    }

    public void deleteEvent(Long id) {

        Event event = eventRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Event not found"));

        event.setStatus(EventStatus.INACTIVE);

        eventRepository.save(event);
    }
    public Page<EventResponse> getAllEvents(Pageable pageable) {

        return eventRepository
                .findByStatus(EventStatus.ACTIVE, pageable)
                .map(EventMapper::toResponse);
    }
    public Page<EventResponse> filterEvents(
            EventStatus status,
            EventCategory category,
            String language,
            Pageable pageable
    ) {

        Specification<Event> specification =
                Specification.where(EventSpecification.hasStatus(status))
                        .and(EventSpecification.hasCategory(category))
                        .and(EventSpecification.hasLanguage(language));

        return eventRepository.findAll(specification, pageable)
                .map(EventMapper::toResponse);
    }
}
