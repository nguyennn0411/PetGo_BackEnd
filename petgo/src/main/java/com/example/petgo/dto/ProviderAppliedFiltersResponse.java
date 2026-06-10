package com.example.petgo.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record ProviderAppliedFiltersResponse(
                String query,
                String city,
                List<Long> serviceCategoryIds,
                BigDecimal minPrice,
                BigDecimal maxPrice,
                BigDecimal minRating,
                Double latitude,
                Double longitude,
                Double maxDistanceKm,
                String timeOfDay,
                String sortBy,
                Boolean featuredOnly) {
}
