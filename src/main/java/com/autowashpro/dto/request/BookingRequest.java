package com.autowashpro.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class BookingRequest {

    @NotNull(message = "VehicleID is required")
    private Integer vehicleId;

    @NotNull(message = "SlotID is required")
    private Integer slotId;

    @NotNull(message = "Booking date is required")
    @Future(message = "Booking date must be in the future")
    private LocalDate bookingDate;

    @NotEmpty(message = "At least one service is required")
    private List<Integer> servicePriceIds;

    private String notes;
}