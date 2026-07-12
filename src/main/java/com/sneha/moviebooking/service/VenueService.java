package com.sneha.moviebooking.service;

import com.sneha.moviebooking.dto.VenueRequest;
import com.sneha.moviebooking.dto.VenueResponse;
import com.sneha.moviebooking.entity.Venue;
import com.sneha.moviebooking.entity.VenueStatus;
import com.sneha.moviebooking.mapper.VenueMapper;
import com.sneha.moviebooking.repository.VenueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;
import com.sneha.moviebooking.specification.VenueSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@Service
@RequiredArgsConstructor
public class VenueService {

    private final VenueRepository venueRepository;

    public VenueResponse createVenue(VenueRequest request) {

        Venue venue = VenueMapper.toEntity(request);

        Venue savedVenue = venueRepository.save(venue);

        return VenueMapper.toResponse(savedVenue);
    }

    public List<VenueResponse> getAllVenues() {

        List<Venue> venues = venueRepository.findByStatus(VenueStatus.ACTIVE);

        return venues.stream()
                .map(VenueMapper::toResponse)
                .collect(Collectors.toList());
    }

    public VenueResponse getVenueById(Long id) {

        Venue venue = venueRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Venue not found"));

        return VenueMapper.toResponse(venue);
    }

    public VenueResponse updateVenue(Long id, VenueRequest request) {

        Venue venue = venueRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Venue not found"));

        venue.setName(request.getName());
        venue.setAddress(request.getAddress());
        venue.setCity(request.getCity());
        venue.setState(request.getState());
        venue.setTotalSeats(request.getTotalSeats());

        Venue updatedVenue = venueRepository.save(venue);

        return VenueMapper.toResponse(updatedVenue);
    }

    public void deleteVenue(Long id) {

        Venue venue = venueRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Venue not found"));

        venue.setStatus(VenueStatus.INACTIVE);

        venueRepository.save(venue);
    }
    public Page<VenueResponse> getAllVenues(Pageable pageable) {

        return venueRepository
                .findByStatus(VenueStatus.ACTIVE, pageable)
                .map(VenueMapper::toResponse);
    }

    public Page<VenueResponse> filterVenues(
            VenueStatus status,
            String city,
            String state,
            String name,
            Pageable pageable
    ) {

        Specification<Venue> specification = Specification.<Venue>unrestricted()
                .and(VenueSpecification.hasStatus(status))
                .and(VenueSpecification.hasCity(city))
                .and(VenueSpecification.hasState(state))
                .and(VenueSpecification.hasName(name));

        return venueRepository.findAll(specification, pageable)
                .map(VenueMapper::toResponse);
    }
}