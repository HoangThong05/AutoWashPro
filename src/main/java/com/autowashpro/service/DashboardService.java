package com.autowashpro.service;

import com.autowashpro.dto.response.DashboardResponse;
import com.autowashpro.repository.BookingRepository;
import com.autowashpro.repository.CustomerRepository;
import com.autowashpro.repository.TransactionRepository;
import com.autowashpro.dto.response.BookingStatisticResponse;
import java.util.List;
import com.autowashpro.dto.response.RevenueStatisticResponse;
import com.autowashpro.dto.response.CustomerStatisticResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final BookingRepository bookingRepository;

    private final CustomerRepository customerRepository;

    private final TransactionRepository transactionRepository;

    public DashboardResponse getOverview() {

        DashboardResponse response =
                new DashboardResponse();

        response.setTotalBookings(
                bookingRepository.count()
        );

        response.setPendingBookings(
                bookingRepository.countByStatus("PENDING")
        );

        response.setCompletedBookings(
                bookingRepository.countByStatus("COMPLETED")
        );

        response.setCancelledBookings(
                bookingRepository.countByStatus("CANCELLED")
        );

        response.setTotalCustomers(
                customerRepository.count()
        );

        response.setTotalRevenue(
                transactionRepository.getTotalRevenue()
        );

        return response;
    }
    public List<BookingStatisticResponse> getBookingStatistics() {

        return bookingRepository.countBookingsByStatus()
                .stream()
                .map(obj -> new BookingStatisticResponse(
                        (String) obj[0],
                        (Long) obj[1]
                ))
                .toList();
    }
    public List<RevenueStatisticResponse> getRevenueStatistics() {

    return transactionRepository.getRevenueStatistics()
            .stream()
            .map(obj -> new RevenueStatisticResponse(
                    ((java.sql.Date) obj[0]).toLocalDate(),
                    (java.math.BigDecimal) obj[1]
            ))
            .toList();
        }
        public CustomerStatisticResponse getCustomerStatistics() {
            return new CustomerStatisticResponse(
                    customerRepository.countAllCustomers(),
                    customerRepository.countCustomersWithPoints(),
                    customerRepository.countFrequentCustomers()
            );
        }
}