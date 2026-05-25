package com.autowashpro.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Builder;                    // thêm
import lombok.NoArgsConstructor;          // thêm
import lombok.AllArgsConstructor;         // thêm
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Builder                                  // thêm
@NoArgsConstructor                        // thêm — bắt buộc khi dùng @Builder
@AllArgsConstructor                       // thêm — bắt buộc khi dùng @Builder
@Entity
@Table(name = "Vehicles")
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "VehicleID")
    private Integer vehicleId;

    @ManyToOne
    @JoinColumn(name = "CustomerID", nullable = false)
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "VehicleTypeID", nullable = false)
    private VehicleType vehicleType;

    @Column(name = "license_plate", nullable = false, length = 20)
    private String licensePlate;

    @Column(name = "brand", length = 100)
    private String brand;

    @Column(name = "model", length = 100)
    private String model;

    @Column(name = "color", length = 50)
    private String color;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "is_default", nullable = false)
    private Boolean isDefault = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}