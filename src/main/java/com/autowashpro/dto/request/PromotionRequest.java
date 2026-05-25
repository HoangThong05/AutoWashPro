package com.autowashpro.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class PromotionRequest {

    @NotBlank(message = "Code is required")
    private String code;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Discount type is required")
    private String discountType; // PERCENT hoặc FIXED

    @NotNull(message = "Discount value is required")
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal discountValue;

    private BigDecimal minOrderValue = BigDecimal.ZERO;
    private BigDecimal maxDiscount;
    private Integer usageLimit;

    @NotNull(message = "Start date is required")
    private LocalDateTime startAt;

    @NotNull(message = "End date is required")
    private LocalDateTime endAt;
}