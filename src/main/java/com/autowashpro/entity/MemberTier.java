package com.autowashpro.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "MemberTiers")
public class MemberTier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TierID")
    private Integer tierId;

    @Column(name = "name", nullable = false, length = 20)
    private String name;

    @Column(name = "min_points", nullable = false)
    private Integer minPoints;

    @Column(name = "min_washes", nullable = false)
    private Integer minWashes;

    @Column(name = "min_spend", nullable = false)
    private java.math.BigDecimal minSpend;

    @Column(name = "points_per_100k_vnd", nullable = false)
    private Integer pointsPer100kVnd;

    @Column(name = "discount_percent", nullable = false)
    private Integer discountPercent;

    @Column(name = "advance_booking_hours", nullable = false)
    private Integer advanceBookingHours;

    @Column(name = "max_bookings_per_day", nullable = false)
    private Integer maxBookingsPerDay;

    @Column(name = "priority", nullable = false)
    private Integer priority;
}