package com.autowashpro.service;

import com.autowashpro.dto.response.ServiceResponse;
import com.autowashpro.entity.Service;
import com.autowashpro.entity.ServicePrice;
import com.autowashpro.repository.ServicePriceRepository;
import com.autowashpro.repository.ServiceRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class ServiceCatalogService {

    private final ServiceRepository serviceRepository;
    private final ServicePriceRepository servicePriceRepository;

    public List<ServiceResponse> getAllServices() {
        return serviceRepository.findByIsActiveTrueOrderByCategory()
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<ServiceResponse> getByCategory(String category) {
        return serviceRepository.findByCategoryAndIsActiveTrue(category.toUpperCase())
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public ServiceResponse getById(Integer id) {
        return serviceRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new RuntimeException("Service not found"));
    }

    public List<ServiceResponse> getByVehicleType(Integer vehicleTypeId) {
        return servicePriceRepository
                .findByVehicleType_VehicleTypeIdAndIsActiveTrue(vehicleTypeId)
                .stream().map(this::toResponseFromPrice).collect(Collectors.toList());
    }

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