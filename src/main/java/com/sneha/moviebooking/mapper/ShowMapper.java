package com.sneha.moviebooking.mapper;

import com.sneha.moviebooking.dto.ShowResponse;
import com.sneha.moviebooking.entity.Show;

public class ShowMapper {

    public static ShowResponse toResponse(Show show) {

        return ShowResponse.builder()
                .id(show.getId())
                .eventId(show.getEvent().getId())
                .eventTitle(show.getEvent().getTitle())
                .venueId(show.getVenue().getId())
                .venueName(show.getVenue().getName())
                .organizerId(show.getOrganizer().getId())
                .organizerName(show.getOrganizer().getName())
                .startTime(show.getStartTime())
                .endTime(show.getEndTime())
                .vipPrice(show.getVipPrice())
                .premiumPrice(show.getPremiumPrice())
                .regularPrice(show.getRegularPrice())
                .status(show.getStatus())
                .build();
    }
}