package com.autowashpro.repository;

import com.autowashpro.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Integer> {

    List<Vehicle> findByCustomer_CustomerId(Integer customerId);

    boolean existsByLicensePlate(String licensePlate);

    @Modifying
    @Query("UPDATE Vehicle v SET v.isDefault = false WHERE v.customer.customerId = :customerId")
    void clearDefaultByCustomer(@Param("customerId") Integer customerId);
}