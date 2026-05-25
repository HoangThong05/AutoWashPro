package com.autowashpro.repository;

import com.autowashpro.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {

    List<Booking> findByCustomer_CustomerIdOrderByCreatedAtDesc(Integer customerId);

    @Query("""
        SELECT COUNT(b) FROM Booking b
        WHERE b.timeSlot.slotId = :slotId
          AND b.bookingDate = :date
          AND b.status NOT IN ('CANCELLED', 'NO_SHOW')
        """)
    long countActiveBookingsInSlot(@Param("slotId") Integer slotId,
                                   @Param("date") LocalDate date);

    @Query("""
        SELECT COUNT(b) FROM Booking b
        WHERE b.customer.customerId = :customerId
          AND b.bookingDate = :date
          AND b.status NOT IN ('CANCELLED', 'NO_SHOW')
        """)
    long countBookingsByCustomerAndDate(@Param("customerId") Integer customerId,
                                        @Param("date") LocalDate date);
}