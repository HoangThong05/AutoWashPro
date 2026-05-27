package com.autowashpro.repository;

import com.autowashpro.entity.MemberTier;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberTierRepository extends JpaRepository<MemberTier, Integer> {
    List<MemberTier> findAllByOrderByPriorityDesc();
}
