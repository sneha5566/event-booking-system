package com.sneha.moviebooking.dto.analytics;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TopEventResponse {

    private String eventName;

    private Long ticketsSold;
}