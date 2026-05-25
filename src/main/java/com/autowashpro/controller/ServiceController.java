package com.autowashpro.controller;

import com.autowashpro.dto.response.ServiceResponse;
import com.autowashpro.entity.Service;
import com.autowashpro.entity.ServicePrice;
import com.autowashpro.repository.ServicePriceRepository;
import com.autowashpro.repository.ServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
public class ServiceController {

    private final ServiceRepository serviceRepository;
    private final ServicePriceRepository servicePriceRepository;

    /** GET /api/services — tất cả dịch vụ active, kèm giá */
    @GetMapping
    public ResponseEntity<List<ServiceResponse>> getAllServices() {
        List<Service> services = serviceRepository.findByIsActiveTrueOrderByCategory();
        List<ServiceResponse> response = services.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    /** GET /api/services/category/{category} — lọc theo category */
    @GetMapping("/category/{category}")
    public ResponseEntity<List<ServiceResponse>> getByCategory(
            @PathVariable String category) {
        List<Service> services = serviceRepository
                .findByCategoryAndIsActiveTrue(category.toUpperCase());
        return ResponseEntity.ok(services.stream()
                .map(this::toResponse)
                .collect(Collectors.toList()));
    }

    /** GET /api/services/{id} — chi tiết 1 dịch vụ */
    @GetMapping("/{id}")
    public ResponseEntity<ServiceResponse> getById(@PathVariable Integer id) {
        return serviceRepository.findById(id)
                .map(s -> ResponseEntity.ok(toResponse(s)))
                .orElse(ResponseEntity.notFound().build());
    }

    /** GET /api/services/vehicle-type/{vehicleTypeId} — dịch vụ theo loại xe */
    @GetMapping("/vehicle-type/{vehicleTypeId}")
    public ResponseEntity<List<ServiceResponse>> getByVehicleType(
            @PathVariable Integer vehicleTypeId) {
        List<ServicePrice> prices = servicePriceRepository
                .findByVehicleType_VehicleTypeIdAndIsActiveTrue(vehicleTypeId);

        // Group by service
        List<ServiceResponse> response = prices.stream()
                .map(sp -> toResponseFromPrice(sp))
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    // ===== HELPERS =====

    private ServiceResponse toResponse(Service s) {
        List<ServicePrice> prices = servicePriceRepository
                .findByService_ServiceIdAndIsActiveTrue(s.getServiceId());

        List<ServiceResponse.PriceDetail> priceDetails = prices.stream()
                .map(sp -> ServiceResponse.PriceDetail.builder()
                        .servicePriceId(sp.getServicePriceId())
                        .vehicleTypeId(sp.getVehicleType().getVehicleTypeId())
                        .vehicleTypeName(sp.getVehicleType().getName())
                        .price(sp.getPrice())
                        .durationMinutes(sp.getDurationMinutes())
                        .build())
                .collect(Collectors.toList());

        return ServiceResponse.builder()
                .serviceId(s.getServiceId())
                .name(s.getName())
                .description(s.getDescription())
                .category(s.getCategory())
                .isActive(s.getIsActive())
                .prices(priceDetails)
                .build();
    }

    private ServiceResponse toResponseFromPrice(ServicePrice sp) {
        return ServiceResponse.builder()
                .serviceId(sp.getService().getServiceId())
                .name(sp.getService().getName())
                .description(sp.getService().getDescription())
                .category(sp.getService().getCategory())
                .isActive(sp.getService().getIsActive())
                .prices(List.of(ServiceResponse.PriceDetail.builder()
                        .servicePriceId(sp.getServicePriceId())
                        .vehicleTypeId(sp.getVehicleType().getVehicleTypeId())
                        .vehicleTypeName(sp.getVehicleType().getName())
                        .price(sp.getPrice())
                        .durationMinutes(sp.getDurationMinutes())
                        .build()))
                .build();
    }
}