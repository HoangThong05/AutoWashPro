package com.autowashpro.repository;

import com.autowashpro.entity.Reward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RewardRepository extends JpaRepository<Reward, Integer> {
    List<Reward> findByIsActiveTrueOrderByPointsRequiredAsc();
}