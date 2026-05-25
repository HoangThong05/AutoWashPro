package com.autowashpro.dto.response;

import com.autowashpro.entity.Vehicle;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class VehicleDTO {
    private Integer vehicleId;
    private Integer customerId;
    private String vehicleTypeName;
    private String licensePlate;
    private String brand;
    private String model;
    private String color;
    private String imageUrl;
    private Boolean isDefault;
    private LocalDateTime createdAt;

    public static VehicleDTO fromEntity(Vehicle v) {
        VehicleDTO dto = new VehicleDTO();
        dto.setVehicleId(v.getVehicleId());
        dto.setCustomerId(v.getCustomer().getCustomerId());
        dto.setVehicleTypeName(v.getVehicleType().getName());
        dto.setLicensePlate(v.getLicensePlate());
        dto.setBrand(v.getBrand());
        dto.setModel(v.getModel());
        dto.setColor(v.getColor());
        dto.setImageUrl(v.getImageUrl());
        dto.setIsDefault(v.getIsDefault());
        dto.setCreatedAt(v.getCreatedAt());
        return dto;
    }
}