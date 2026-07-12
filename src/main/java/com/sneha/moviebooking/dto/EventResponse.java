package com.sneha.moviebooking.dto;

import com.sneha.moviebooking.entity.EventCategory;
import com.sneha.moviebooking.entity.EventStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventResponse {

    private Long id;

    private String title;

    private String description;

    private EventCategory category;

    private Integer duration;

    private String language;

    private String ageRating;

    private String posterUrl;

    private EventStatus status;
}