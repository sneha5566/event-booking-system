package com.sneha.moviebooking.service;

import com.sneha.moviebooking.dto.ShowRequest;
import com.sneha.moviebooking.dto.ShowResponse;
import com.sneha.moviebooking.entity.*;
import com.sneha.moviebooking.mapper.ShowMapper;
import com.sneha.moviebooking.repository.*;
import com.sneha.moviebooking.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import com.sneha.moviebooking.specification.ShowSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ShowService {

    private final ShowRepository showRepository;
    private final EventRepository eventRepository;
    private final VenueRepository venueRepository;
    private final SeatRepository seatRepository;
    private final ShowSeatRepository showSeatRepository;
    private final SecurityUtil securityUtil;

    @Transactional
    public ShowResponse createShow(ShowRequest request) {

        User organizer = securityUtil.getCurrentUser();

        Event event = eventRepository.findById(request.getEventId())
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Event not found"));

        Venue venue = venueRepository.findById(request.getVenueId())
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Venue not found"));
        List<Seat> seats = seatRepository.findByVenue(venue);

        if (seats.isEmpty()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Generate seat layout before creating a show");
        }
        if (!request.getEndTime().isAfter(request.getStartTime())) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "End time must be after start time");
        }
        boolean overlap = showRepository
                .existsByVenueAndStartTimeLessThanAndEndTimeGreaterThan(
                        venue,
                        request.getEndTime(),
                        request.getStartTime());

        if (overlap) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Venue already has another show during this time");
        }
        Show show = new Show();

        show.setEvent(event);
        show.setVenue(venue);
        show.setOrganizer(organizer);
        show.setStartTime(request.getStartTime());
        show.setEndTime(request.getEndTime());
        show.setVipPrice(request.getVipPrice());
        show.setPremiumPrice(request.getPremiumPrice());
        show.setRegularPrice(request.getRegularPrice());
        show.setStatus(ShowStatus.ACTIVE);

        Show savedShow = showRepository.save(show);

        List<ShowSeat> showSeats = new ArrayList<>();

        for (Seat seat : seats) {

            ShowSeat showSeat = new ShowSeat();

            showSeat.setShow(savedShow);

            showSeat.setSeat(seat);

            switch (seat.getSeatType()) {

                case VIP:
                    showSeat.setPrice(savedShow.getVipPrice());
                    break;

                case PREMIUM:
                    showSeat.setPrice(savedShow.getPremiumPrice());
                    break;

                case REGULAR:
                    showSeat.setPrice(savedShow.getRegularPrice());
                    break;
            }

            showSeat.setStatus(ShowSeatStatus.AVAILABLE);

            showSeats.add(showSeat);
        }
        showSeatRepository.saveAll(showSeats);
        return ShowMapper.toResponse(savedShow);
    }
    @Transactional(readOnly = true)
    public List<ShowResponse> getAllShows() {

        return showRepository.findByStatus(ShowStatus.ACTIVE)
                .stream()
                .map(ShowMapper::toResponse)
                .toList();
    }
    @Transactional(readOnly = true)
    public Page<ShowResponse> getAllShows(Pageable pageable) {

        return showRepository
                .findByStatus(ShowStatus.ACTIVE, pageable)
                .map(ShowMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public ShowResponse getShowById(Long id) {

        Show show = showRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Show not found"));

        return ShowMapper.toResponse(show);
    }
    @Transactional(readOnly = true)
    public Page<ShowResponse> filterShows(
            ShowStatus status,
            Long eventId,
            Long venueId,
            Long organizerId,
            LocalDateTime startFrom,
            LocalDateTime startTo,
            Pageable pageable
    ) {

        Specification<Show> specification =
                Specification.where(ShowSpecification.hasStatus(status))
                        .and(ShowSpecification.hasEvent(eventId))
                        .and(ShowSpecification.hasVenue(venueId))
                        .and(ShowSpecification.hasOrganizer(organizerId))
                        .and(ShowSpecification.startsAfter(startFrom))
                        .and(ShowSpecification.startsBefore(startTo));

        return showRepository.findAll(specification, pageable)
                .map(ShowMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public List<com.sneha.moviebooking.dto.ShowSeatResponse> getShowSeats(Long showId) {
        Show show = showRepository.findById(showId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Show not found"));
        return showSeatRepository.findByShow(show)
                .stream()
                .map(ss -> com.sneha.moviebooking.dto.ShowSeatResponse.builder()
                        .id(ss.getId())
                        .rowName(ss.getSeat().getRowName())
                        .seatNumber(ss.getSeat().getSeatNumber())
                        .seatType(ss.getSeat().getSeatType())
                        .price(ss.getPrice())
                        .status(ss.getStatus())
                        .build())
                .sorted(java.util.Comparator
                        .comparing(com.sneha.moviebooking.dto.ShowSeatResponse::getRowName)
                        .thenComparing(com.sneha.moviebooking.dto.ShowSeatResponse::getSeatNumber))
                .toList();
    }
}
