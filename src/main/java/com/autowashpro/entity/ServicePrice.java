package com.autowashpro.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "ServicePrices")
public class ServicePrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ServicePriceID")
    private Integer servicePriceId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ServiceID", nullable = false)
    private Service service;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "VehicleTypeID", nullable = false)
    private VehicleType vehicleType;

    @Column(name = "price", nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
}