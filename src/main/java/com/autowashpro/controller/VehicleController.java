package com.autowashpro.controller;

import com.autowashpro.dto.request.VehicleRequest;
import com.autowashpro.dto.response.VehicleResponse;
import com.autowashpro.service.VehicleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;

    @GetMapping("/my")
    public ResponseEntity<List<VehicleResponse>> getMyVehicles(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                vehicleService.getMyVehicles(userDetails.getUsername()));
    }

    @PostMapping
    public ResponseEntity<VehicleResponse> addVehicle(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody VehicleRequest req) {
        return ResponseEntity.status(201).body(
                vehicleService.addVehicle(userDetails.getUsername(), req));
    }

    @PutMapping("/{id}")
    public ResponseEntity<VehicleResponse> updateVehicle(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Integer id,
            @Valid @RequestBody VehicleRequest req) {
        return ResponseEntity.ok(
                vehicleService.updateVehicle(userDetails.getUsername(), id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVehicle(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Integer id) {
        vehicleService.deleteVehicle(userDetails.getUsername(), id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/default")
    public ResponseEntity<VehicleResponse> setDefault(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Integer id) {
        return ResponseEntity.ok(
                vehicleService.setDefault(userDetails.getUsername(), id));
    }
}