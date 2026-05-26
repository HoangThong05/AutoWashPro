package com.autowashpro.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "LoyaltyLogs")
public class LoyaltyLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LogID")
    private Integer logId;

    @ManyToOne
    @JoinColumn(name = "CustomerID", nullable = false)
    private Customer customer;

    @Column(name = "points_change", nullable = false)
    private Integer pointsChange;

    @Column(name = "reason", nullable = false, length = 255)
    private String reason;

    @Column(name = "ref_type", nullable = false, length = 20)
    private String refType; // BOOKING, MANUAL, REDEEM, EXPIRE

    @Column(name = "ref_id")
    private Integer refId;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}