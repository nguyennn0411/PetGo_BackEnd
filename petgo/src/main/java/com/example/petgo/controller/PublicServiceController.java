package com.example.petgo.controller;

import com.example.petgo.dto.AreaResponse;
import com.example.petgo.dto.CatalogServiceResponse;
import com.example.petgo.dto.ServiceCategoryResponse;
import com.example.petgo.repository.AreaRepository;
import com.example.petgo.repository.AreaServiceConfigRepository;
import com.example.petgo.service.AdminCatalogService;
import com.example.petgo.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class PublicServiceController {

    private final AdminCatalogService adminCatalogService;
    private final AdminService adminService;
    private final AreaServiceConfigRepository areaServiceConfigRepository;
    private final AreaRepository areaRepository;

    @GetMapping("/services")
    public ResponseEntity<List<CatalogServiceResponse>> getAllServices(
            @RequestParam(required = false) Long categoryId) {
        List<CatalogServiceResponse> services = adminCatalogService.getAllServices();
        if (categoryId != null) {
            services = services.stream()
                    .filter(s -> s.categories() != null
                            && s.categories().stream().anyMatch(c -> c.id().equals(categoryId)))
                    .toList();
        }
        return ResponseEntity.ok(services);
    }

    @GetMapping("/categories")
    public ResponseEntity<List<ServiceCategoryResponse>> getAllCategories() {
        return ResponseEntity.ok(adminService.getAllCategories());
    }

    @GetMapping("/areas")
    public ResponseEntity<List<AreaResponse>> getAllAreas() {
        List<AreaResponse> areas = areaRepository.findAllByOrderByNameAsc()
                .stream().map(a -> AreaResponse.builder()
                        .id(a.getId())
                        .name(a.getName())
                        .pickupLatitude(a.getPickupLatitude())
                        .pickupLongitude(a.getPickupLongitude())
                        .pickupAddress(a.getPickupAddress())
                        .build())
                .toList();
        return ResponseEntity.ok(areas);
    }

    @GetMapping("/services/{serviceId}")
    public ResponseEntity<CatalogServiceResponse> getServiceById(@PathVariable Long serviceId) {
        return ResponseEntity.ok(adminCatalogService.getServiceById(serviceId));
    }

    @GetMapping("/services/{serviceId}/areas")
    public ResponseEntity<List<AreaResponse>> getServiceAreas(@PathVariable Long serviceId) {
        List<AreaResponse> areas = areaServiceConfigRepository.findActiveByServiceIdWithArea(serviceId)
                .stream().map(asc -> {
                    var a = asc.getArea();
                    return AreaResponse.builder()
                            .id(a.getId())
                            .name(a.getName())
                            .pickupLatitude(a.getPickupLatitude())
                            .pickupLongitude(a.getPickupLongitude())
                            .pickupAddress(a.getPickupAddress())
                            .pickupPhone(a.getPickupPhone())
                            .pickupInstructions(a.getPickupInstructions())
                            .shortSlots(a.getShortSlots())
                            .longSlots(a.getLongSlots())
                            .build();
                }).toList();
        return ResponseEntity.ok(areas);
    }
}
