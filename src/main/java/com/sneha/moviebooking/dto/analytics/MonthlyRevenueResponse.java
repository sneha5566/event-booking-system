package com.sneha.moviebooking.dto.analytics;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class MonthlyRevenueResponse {

    private Integer year;

    private Integer month;

    private BigDecimal revenue;
}