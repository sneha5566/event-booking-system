package com.sneha.moviebooking.dto;

import com.sneha.moviebooking.entity.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingResponse {

    private Long id;

    private Long userId;

    private String userName;

    private Long showId;

    private BigDecimal totalAmount;

    private BookingStatus status;

    private List<BookingSeatResponse> seats;

    private LocalDateTime createdAt;
}
