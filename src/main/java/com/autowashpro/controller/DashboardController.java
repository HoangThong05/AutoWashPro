package com.autowashpro.controller;

import com.autowashpro.dto.response.BookingStatisticResponse;
import com.autowashpro.dto.response.DashboardResponse;
import com.autowashpro.service.DashboardService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import com.autowashpro.dto.response.RevenueStatisticResponse;
import com.autowashpro.dto.response.CustomerStatisticResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/manager/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    public ResponseEntity<DashboardResponse> getOverview() {

        return ResponseEntity.ok(       
                dashboardService.getOverview()
        );
    }
     @GetMapping("/bookings")
    public ResponseEntity<List<BookingStatisticResponse>>
    getBookingStatistics() {

        return ResponseEntity.ok(
                dashboardService.getBookingStatistics()
        );
    }
     @GetMapping("/revenue")
    public ResponseEntity<List<RevenueStatisticResponse>>
    getRevenueStatistics() {

        return ResponseEntity.ok(
                dashboardService.getRevenueStatistics()
        );
    }
     @GetMapping("/customers")
    public ResponseEntity<CustomerStatisticResponse>
    getCustomerStatistics() {

        return ResponseEntity.ok(
                dashboardService.getCustomerStatistics()
        );
    }
}