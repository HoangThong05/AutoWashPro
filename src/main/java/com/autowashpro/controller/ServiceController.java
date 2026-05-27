package com.autowashpro.controller;

import com.autowashpro.dto.response.ServiceResponse;
import com.autowashpro.service.ServiceCatalogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
public class ServiceController {

    private final ServiceCatalogService serviceService;

    @GetMapping
    public ResponseEntity<List<ServiceResponse>> getAllServices() {
        return ResponseEntity.ok(serviceService.getAllServices());
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<ServiceResponse>> getByCategory(
            @PathVariable String category) {
        return ResponseEntity.ok(serviceService.getByCategory(category));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceResponse> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(serviceService.getById(id));
    }

    @GetMapping("/vehicle-type/{vehicleTypeId}")
    public ResponseEntity<List<ServiceResponse>> getByVehicleType(
            @PathVariable Integer vehicleTypeId) {
        return ResponseEntity.ok(serviceService.getByVehicleType(vehicleTypeId));
    }
}