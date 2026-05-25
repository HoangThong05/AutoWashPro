package com.autowashpro.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class BookingStatusRequest {

    @NotBlank(message = "Status is required")
    private String status;

    private String cancelReason;
    private Integer employeeId;
}