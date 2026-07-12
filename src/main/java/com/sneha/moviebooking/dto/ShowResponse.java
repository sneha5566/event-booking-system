package com.sneha.moviebooking.dto;

import com.sneha.moviebooking.entity.ShowStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShowResponse {

    private Long id;

    private Long eventId;

    private String eventTitle;

    private Long venueId;

    private String venueName;

    private Long organizerId;

    private String organizerName;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private BigDecimal vipPrice;

    private BigDecimal premiumPrice;

    private BigDecimal regularPrice;

    private ShowStatus status;
}