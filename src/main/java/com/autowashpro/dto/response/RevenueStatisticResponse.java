package com.autowashpro.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class RevenueStatisticResponse {

    private LocalDate date;

    private BigDecimal revenue;
}