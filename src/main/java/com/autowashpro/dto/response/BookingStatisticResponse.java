package com.autowashpro.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BookingStatisticResponse {

    private String status;

    private Long count;
}