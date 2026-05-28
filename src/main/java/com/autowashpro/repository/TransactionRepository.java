package com.autowashpro.repository;

import com.autowashpro.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    Optional<Transaction> findByBooking_BookingId(Integer bookingId);
    List<Transaction> findByCustomer_CustomerIdOrderByCreatedAtDesc(Integer customerId);

@Query("""
    SELECT COALESCE(SUM(t.finalAmount), 0)
    FROM Transaction t
    WHERE t.paymentStatus = 'PAID'
""")
BigDecimal getTotalRevenue();

@Query("""
    SELECT CAST(t.createdAt AS date), SUM(t.finalAmount)
    FROM Transaction t
    WHERE t.paymentStatus = 'PAID'
    GROUP BY CAST(t.createdAt AS date)
    ORDER BY CAST(t.createdAt AS date)
""")
List<Object[]> getRevenueStatistics();
}