package com.autowashpro.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CustomerStatisticResponse {

    private Long totalCustomers;

    private Long customersWithPoints;

    private Long frequentCustomers;
}