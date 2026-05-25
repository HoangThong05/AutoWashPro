package com.autowashpro.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "BookingServices")
public class BookingServiceItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BookingServiceID")
    private Integer bookingServiceId;

    @ManyToOne
    @JoinColumn(name = "BookingID", nullable = false)
    private Booking booking;

    @ManyToOne
    @JoinColumn(name = "ServicePriceID", nullable = false)
    private ServicePrice servicePrice;

    @Column(name = "price_at_booking", nullable = false, precision = 12, scale = 2)
    private BigDecimal priceAtBooking;

    @Column(name = "duration_at_booking", nullable = false)
    private Integer durationAtBooking;
}