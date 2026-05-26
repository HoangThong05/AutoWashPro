package com.autowashpro.dto.response;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RewardResponse {
    private Integer rewardId;
    private String name;
    private String description;
    private Integer pointsRequired;
    private String rewardType;
    private BigDecimal discountValue;
    private Boolean isActive;
    private Integer stock;
    private LocalDateTime expiresAt;
}