package com.autowashpro.repository;

import com.autowashpro.entity.MemberTier;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberTierRepository extends JpaRepository<MemberTier, Integer> {
}