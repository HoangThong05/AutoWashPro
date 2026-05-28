package com.autowashpro.repository;

import com.autowashpro.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

@Repository
public interface CustomerRepository
        extends JpaRepository<Customer, Integer> {

    Optional<Customer> findByUser_UserId(Integer userId);

    Optional<Customer> findByUser_Email(String email);

    @Query("SELECT COUNT(c) FROM Customer c")
    Long countAllCustomers();

    @Query("""
        SELECT COUNT(c)
        FROM Customer c
        WHERE c.loyaltyPoints > 0
    """)
    Long countCustomersWithPoints();

    @Query("""
        SELECT COUNT(c)
        FROM Customer c
        WHERE c.totalWashes > 0
    """)
    Long countFrequentCustomers();
}