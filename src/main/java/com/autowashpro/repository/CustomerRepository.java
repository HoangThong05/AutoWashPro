package com.autowashpro.repository;

import com.autowashpro.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    Optional<Customer> findByUser_UserId(Integer userId);
    Optional<Customer> findByUser_Email(String email);
}