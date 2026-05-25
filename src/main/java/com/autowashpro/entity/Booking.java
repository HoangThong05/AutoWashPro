package com.autowashpro.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "Bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BookingID")
    private Integer bookingId;

    @ManyToOne
    @JoinColumn(name = "CustomerID", nullable = false)
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "VehicleID", nullable = false)
    private Vehicle vehicle;

    @ManyToOne
    @JoinColumn(name = "SlotID", nullable = false)
    private TimeSlot timeSlot;

    @Column(name = "booking_date", nullable = false)
    private LocalDate bookingDate;

    @Column(name = "status", nullable = false, length = 20)
    private String status = "PENDING";

    @Column(name = "booking_type", nullable = false, length = 10)
    private String bookingType = "ADVANCE";

    @Column(name = "qr_code", length = 255)
    private String qrCode;

    @Column(name = "notes", length = 500)
    private String notes;

    @Column(name = "cancel_reason", length = 500)
    private String cancelReason;

    @Column(name = "checkin_at")
    private LocalDateTime checkinAt;

    @Column(name = "start_at")
    private LocalDateTime startAt;

    @Column(name = "complete_at")
    private LocalDateTime completeAt;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true,
        fetch = FetchType.EAGER
    )
    private List<BookingServiceItem> bookingServices = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}