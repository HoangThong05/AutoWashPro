package com.autowashpro.repository;

import com.autowashpro.entity.ServicePrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ServicePriceRepository extends JpaRepository<ServicePrice, Integer> {
    List<ServicePrice> findByService_ServiceIdAndIsActiveTrue(Integer serviceId);
    List<ServicePrice> findByVehicleType_VehicleTypeIdAndIsActiveTrue(Integer vehicleTypeId);
    List<ServicePrice> findByIsActiveTrue();
}