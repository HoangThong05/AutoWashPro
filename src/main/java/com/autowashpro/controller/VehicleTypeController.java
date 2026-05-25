package com.autowashpro.controller;

import com.autowashpro.entity.VehicleType;
import com.autowashpro.repository.VehicleTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/vehicle-types")
@RequiredArgsConstructor
public class VehicleTypeController {

    private final VehicleTypeRepository vehicleTypeRepository;

    @GetMapping
    public ResponseEntity<List<VehicleType>> getAll() {
        return ResponseEntity.ok(vehicleTypeRepository.findAll());
    }
}