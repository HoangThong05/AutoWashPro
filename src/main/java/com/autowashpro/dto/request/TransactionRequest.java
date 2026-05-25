package com.autowashpro.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class TransactionRequest {

    @NotNull(message = "BookingID is required")
    private Integer bookingId;

    @NotBlank(message = "Payment method is required")
    private String paymentMethod; // CASH, BANK_TRANSFER, PAYOS

    private String promotionCode;
}