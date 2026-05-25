package com.autowashpro.repository;

import com.autowashpro.entity.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ServiceRepository extends JpaRepository<Service, Integer> {
    List<Service> findByIsActiveTrueOrderByCategory();
    List<Service> findByCategoryAndIsActiveTrue(String category);
}