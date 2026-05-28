package com.autowashpro.dto.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DashboardResponse {

    private Long totalBookings;

    private Long pendingBookings;

    private Long completedBookings;

    private Long cancelledBookings;

    private Long totalCustomers;

    private BigDecimal totalRevenue;
}