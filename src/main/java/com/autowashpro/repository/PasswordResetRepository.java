package com.autowashpro.repository;

import com.autowashpro.entity.PasswordReset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PasswordResetRepository extends JpaRepository<PasswordReset, Integer> {
    Optional<PasswordReset> findByTokenAndUsedFalse(String token);
    void deleteByUser_UserId(Integer userId);
}