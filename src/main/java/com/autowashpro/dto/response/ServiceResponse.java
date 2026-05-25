package com.autowashpro.dto.response;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ServiceResponse {

    private Integer serviceId;
    private String name;
    private String description;
    private String category;
    private Boolean isActive;
    private List<PriceDetail> prices;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class PriceDetail {
        private Integer servicePriceId;
        private Integer vehicleTypeId;
        private String vehicleTypeName;
        private BigDecimal price;
        private Integer durationMinutes;
    }
}