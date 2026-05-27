package com.autowashpro.repository;

import com.autowashpro.entity.TierHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TierHistoryRepository extends JpaRepository<TierHistory, Integer> {
}