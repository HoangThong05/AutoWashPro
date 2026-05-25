package com.autowashpro.dto.response;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TransactionResponse {

    private Integer transactionId;
    private Integer bookingId;
    private String customerName;
    private BigDecimal subtotal;
    private BigDecimal discountAmount;
    private BigDecimal finalAmount;
    private String paymentMethod;
    private String paymentStatus;
    private String promotionCode;
    private Integer pointsUsed;
    private String checkoutUrl;
    private LocalDateTime paidAt;
    private LocalDateTime createdAt;
}