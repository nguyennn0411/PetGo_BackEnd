package com.example.petgo.controller;

import com.example.petgo.dto.ProviderFilterOptionsResponse;
import com.example.petgo.dto.ProviderDetailServiceItemResponse;
import com.example.petgo.dto.ProviderListResponse;
import com.example.petgo.dto.ProviderSearchCriteria;
import com.example.petgo.service.ProviderDiscoveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/v1/providers")
@RequiredArgsConstructor
public class ProviderDiscoveryController {

        private final ProviderDiscoveryService providerDiscoveryService;

        @GetMapping
        public ResponseEntity<ProviderListResponse> getProviders(
                        @RequestParam(required = false) String query,
                        @RequestParam(required = false) String city,
                        @RequestParam(required = false) String serviceCategoryIds,
                        @RequestParam(required = false) BigDecimal minPrice,
                        @RequestParam(required = false) BigDecimal maxPrice,
                        @RequestParam(required = false) BigDecimal minRating,
                        @RequestParam(required = false) Double latitude,
                        @RequestParam(required = false) Double longitude,
                        @RequestParam(required = false) Double maxDistanceKm,
                        @RequestParam(required = false) String timeOfDay,
                        @RequestParam(required = false, defaultValue = "FEATURED") String sortBy,
                        @RequestParam(required = false, defaultValue = "false") Boolean featuredOnly,
                        @RequestParam(required = false, defaultValue = "0") Integer page,
                        @RequestParam(required = false, defaultValue = "12") Integer size) {
                return ResponseEntity.ok(providerDiscoveryService.findProviders(buildCriteria(
                                query, city, serviceCategoryIds, minPrice,
                                maxPrice, minRating,
                                latitude, longitude, maxDistanceKm, timeOfDay, sortBy, featuredOnly, page, size)));
        }

        @GetMapping("/search")
        public ResponseEntity<ProviderListResponse> searchProviders(
                        @RequestParam(required = false) String query,
                        @RequestParam(required = false) String city,
                        @RequestParam(required = false) String serviceCategoryIds,
                        @RequestParam(required = false) BigDecimal minPrice,
                        @RequestParam(required = false) BigDecimal maxPrice,
                        @RequestParam(required = false) BigDecimal minRating,
                        @RequestParam(required = false) Double latitude,
                        @RequestParam(required = false) Double longitude,
                        @RequestParam(required = false) Double maxDistanceKm,
                        @RequestParam(required = false) String timeOfDay,
                        @RequestParam(required = false, defaultValue = "FEATURED") String sortBy,
                        @RequestParam(required = false, defaultValue = "false") Boolean featuredOnly,
                        @RequestParam(required = false, defaultValue = "0") Integer page,
                        @RequestParam(required = false, defaultValue = "12") Integer size) {
                return ResponseEntity.ok(providerDiscoveryService.findProviders(buildCriteria(
                                query, city, serviceCategoryIds, minPrice,
                                maxPrice, minRating,
                                latitude, longitude, maxDistanceKm, timeOfDay, sortBy, featuredOnly, page, size)));
        }

        @GetMapping("/nearby")
        public ResponseEntity<ProviderListResponse> getNearbyProviders(
                        @RequestParam(required = false) String query,
                        @RequestParam(required = false) String city,
                        @RequestParam(required = false) String serviceCategoryIds,
                        @RequestParam(required = false) BigDecimal minPrice,
                        @RequestParam(required = false) BigDecimal maxPrice,
                        @RequestParam(required = false) BigDecimal minRating,
                        @RequestParam(required = false) Double latitude,
                        @RequestParam(required = false) Double longitude,
                        @RequestParam(required = false) Double maxDistanceKm,
                        @RequestParam(required = false) String timeOfDay,
                        @RequestParam(required = false, defaultValue = "NEAREST") String sortBy,
                        @RequestParam(required = false, defaultValue = "false") Boolean featuredOnly,
                        @RequestParam(required = false, defaultValue = "0") Integer page,
                        @RequestParam(required = false, defaultValue = "12") Integer size) {
                return ResponseEntity.ok(providerDiscoveryService.findProviders(buildCriteria(
                                query, city, serviceCategoryIds, minPrice,
                                maxPrice, minRating,
                                latitude, longitude, maxDistanceKm, timeOfDay, sortBy, featuredOnly, page, size)));
        }

        @GetMapping("/filter-options")
        public ResponseEntity<ProviderFilterOptionsResponse> getFilterOptions() {
                return ResponseEntity.ok(providerDiscoveryService.getFilterOptions());
        }

        @GetMapping("/services")
        public ResponseEntity<List<ProviderDetailServiceItemResponse>> getActiveServices(
                        @RequestParam(required = false) String query,
                        @RequestParam(required = false) String city,
                        @RequestParam(required = false) String serviceCategoryIds,
                        @RequestParam(required = false) BigDecimal minPrice,
                        @RequestParam(required = false) BigDecimal maxPrice,
                        @RequestParam(required = false) BigDecimal minRating,
                        @RequestParam(required = false) Double latitude,
                        @RequestParam(required = false) Double longitude,
                        @RequestParam(required = false) Double maxDistanceKm,
                        @RequestParam(required = false) String timeOfDay,
                        @RequestParam(required = false, defaultValue = "FEATURED") String sortBy,
                        @RequestParam(required = false, defaultValue = "false") Boolean featuredOnly,
                        @RequestParam(required = false, defaultValue = "0") Integer page,
                        @RequestParam(required = false, defaultValue = "200") Integer size) {
                return ResponseEntity.ok(providerDiscoveryService.findActiveServices(buildCriteria(
                                query, city, serviceCategoryIds, minPrice,
                                maxPrice, minRating,
                                latitude, longitude, maxDistanceKm, timeOfDay, sortBy, featuredOnly, page, size)));
        }

        private ProviderSearchCriteria buildCriteria(
                        String query,
                        String city,
                        String serviceCategoryIds,
                        BigDecimal minPrice,
                        BigDecimal maxPrice,
                        BigDecimal minRating,
                        Double latitude,
                        Double longitude,
                        Double maxDistanceKm,
                        String timeOfDay,
                        String sortBy,
                        Boolean featuredOnly,
                        Integer page,
                        Integer size) {
                List<Long> categoryIds = parseLongCsv(serviceCategoryIds);
                return ProviderSearchCriteria.builder()
                                .query(query)
                                .city(city)
                                .serviceCategoryIds(categoryIds)
                                .minPrice(minPrice)
                                .maxPrice(maxPrice)
                                .minRating(minRating)
                                .latitude(latitude)
                                .longitude(longitude)
                                .maxDistanceKm(maxDistanceKm)
                                .timeOfDay(timeOfDay)
                                .sortBy(sortBy)
                                .featuredOnly(featuredOnly)
                                .page(page)
                                .size(size)
                                .build();
        }

        private List<Long> parseLongCsv(String csv) {
                if (csv == null || csv.isBlank()) {
                        return List.of();
                }
                return Arrays.stream(csv.split(","))
                                .map(String::trim)
                                .filter(value -> !value.isBlank())
                                .map(value -> {
                                        try {
                                                return Long.parseLong(value);
                                        } catch (NumberFormatException ignored) {
                                                return null;
                                        }
                                })
                                .filter(value -> value != null)
                                .distinct()
                                .toList();
        }
}
