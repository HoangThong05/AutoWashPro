package com.autowashpro.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class VehicleRequest {

    @NotNull(message = "VehicleTypeId is required")
    private Integer vehicleTypeId;

    @NotBlank(message = "License plate is required")
    private String licensePlate;

    private String brand;
    private String model;
    private String color;
    private Boolean isDefault = false;
}