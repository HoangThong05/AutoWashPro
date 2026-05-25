package com.autowashpro.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "VehicleTypes")
public class VehicleType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "VehicleTypeID")
    private Integer vehicleTypeId;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
}