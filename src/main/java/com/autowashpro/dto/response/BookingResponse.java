package com.autowashpro.dto.response;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BookingResponse {

    private Integer bookingId;
    private LocalDate bookingDate;
    private String status;
    private String bookingType;
    private String qrCode;
    private String notes;

    private String customerName;
    private String customerPhone;

    private String licensePlate;
    private String vehicleType;
    private String brand;
    private String model;

    private LocalTime startTime;
    private LocalTime endTime;

    private List<ServiceDetail> services;
    private BigDecimal subtotal;

    private String employeeName;
    private LocalDateTime createdAt;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class ServiceDetail {
        private String serviceName;
        private String category;
        private BigDecimal price;
        private Integer durationMinutes;
    }
}