package com.autowashpro.repository;

import com.autowashpro.entity.LoyaltyLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LoyaltyLogRepository extends JpaRepository<LoyaltyLog, Integer> {
    List<LoyaltyLog> findByCustomer_CustomerIdOrderByCreatedAtDesc(Integer customerId);
}