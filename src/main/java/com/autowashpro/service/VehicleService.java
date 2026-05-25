package com.autowashpro.service;

import com.autowashpro.dto.request.VehicleRequest;
import com.autowashpro.dto.response.VehicleResponse;
import com.autowashpro.entity.Customer;
import com.autowashpro.entity.Vehicle;
import com.autowashpro.entity.VehicleType;
import com.autowashpro.repository.CustomerRepository;
import com.autowashpro.repository.VehicleRepository;
import com.autowashpro.repository.VehicleTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final VehicleTypeRepository vehicleTypeRepository;
    private final CustomerRepository customerRepository;

    public List<VehicleResponse> getMyVehicles(String email) {
        Customer customer = customerRepository.findByUser_Email(email)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        return vehicleRepository.findByCustomer_CustomerId(customer.getCustomerId())
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public VehicleResponse addVehicle(String email, VehicleRequest req) {
        Customer customer = customerRepository.findByUser_Email(email)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        if (vehicleRepository.existsByLicensePlate(req.getLicensePlate())) {
            throw new RuntimeException("License plate already registered");
        }

        VehicleType vehicleType = vehicleTypeRepository.findById(req.getVehicleTypeId())
                .orElseThrow(() -> new RuntimeException("VehicleType not found"));

        if (Boolean.TRUE.equals(req.getIsDefault())) {
            vehicleRepository.clearDefaultByCustomer(customer.getCustomerId());
        }

        Vehicle vehicle = Vehicle.builder()
                .customer(customer)
                .vehicleType(vehicleType)
                .licensePlate(req.getLicensePlate().trim().toUpperCase())
                .brand(req.getBrand())
                .model(req.getModel())
                .color(req.getColor())
                .isDefault(Boolean.TRUE.equals(req.getIsDefault()))
                .build();

        return toResponse(vehicleRepository.save(vehicle));
    }

    @Transactional
    public VehicleResponse updateVehicle(String email, Integer vehicleId, VehicleRequest req) {
        Vehicle vehicle = getOwnedVehicle(email, vehicleId);

        VehicleType vehicleType = vehicleTypeRepository.findById(req.getVehicleTypeId())
                .orElseThrow(() -> new RuntimeException("VehicleType not found"));

        if (Boolean.TRUE.equals(req.getIsDefault())) {
            vehicleRepository.clearDefaultByCustomer(vehicle.getCustomer().getCustomerId());
        }

        vehicle.setVehicleType(vehicleType);
        vehicle.setBrand(req.getBrand());
        vehicle.setModel(req.getModel());
        vehicle.setColor(req.getColor());
        vehicle.setIsDefault(Boolean.TRUE.equals(req.getIsDefault()));

        return toResponse(vehicleRepository.save(vehicle));
    }

    @Transactional
    public void deleteVehicle(String email, Integer vehicleId) {
        Vehicle vehicle = getOwnedVehicle(email, vehicleId);
        vehicleRepository.delete(vehicle);
    }

    @Transactional
    public VehicleResponse setDefault(String email, Integer vehicleId) {
        Vehicle vehicle = getOwnedVehicle(email, vehicleId);
        vehicleRepository.clearDefaultByCustomer(vehicle.getCustomer().getCustomerId());
        vehicle.setIsDefault(true);
        return toResponse(vehicleRepository.save(vehicle));
    }

    // ===== HELPERS =====

    private Vehicle getOwnedVehicle(String email, Integer vehicleId) {
        Customer customer = customerRepository.findByUser_Email(email)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));
        if (!vehicle.getCustomer().getCustomerId().equals(customer.getCustomerId())) {
            throw new RuntimeException("Access denied: vehicle does not belong to you");
        }
        return vehicle;
    }

    private VehicleResponse toResponse(Vehicle v) {
        return VehicleResponse.builder()
                .vehicleId(v.getVehicleId())
                .vehicleTypeId(v.getVehicleType().getVehicleTypeId())
                .vehicleTypeName(v.getVehicleType().getName())
                .licensePlate(v.getLicensePlate())
                .brand(v.getBrand())
                .model(v.getModel())
                .color(v.getColor())
                .imageUrl(v.getImageUrl())
                .isDefault(v.getIsDefault())
                .createdAt(v.getCreatedAt())
                .build();
    }
}