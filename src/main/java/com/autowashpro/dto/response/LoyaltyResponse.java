package com.autowashpro.dto.response;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class LoyaltyResponse {
    private Integer customerId;
    private String customerName;
    private Integer loyaltyPoints;
    private List<LogDetail> logs;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class LogDetail {
        private Integer pointsChange;
        private String reason;
        private String refType;
        private LocalDateTime createdAt;
    }
}