package com.autowashpro.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "TierHistory")
public class TierHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "HistoryID")
    private Integer historyId;

    @ManyToOne
    @JoinColumn(name = "CustomerID", nullable = false)
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "old_tier_id")
    private MemberTier oldTier;

    @ManyToOne
    @JoinColumn(name = "new_tier_id", nullable = false)
    private MemberTier newTier;

    @Column(name = "change_reason", nullable = false, length = 30)
    private String changeReason; // AUTO_UPGRADE, AUTO_DOWNGRADE, MANUAL

    @ManyToOne
    @JoinColumn(name = "changed_by")
    private User changedBy;

    @CreationTimestamp
    @Column(name = "changed_at", updatable = false)
    private LocalDateTime changedAt;
}