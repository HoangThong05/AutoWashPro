package com.autowashpro.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class VehicleResponse {

    private Integer vehicleId;
    private Integer vehicleTypeId;
    private String vehicleTypeName;
    private String licensePlate;
    private String brand;
    private String model;
    private String color;
    private String imageUrl;
    private Boolean isDefault;
    private LocalDateTime createdAt;
}