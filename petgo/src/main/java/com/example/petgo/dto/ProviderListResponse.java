package com.example.petgo.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record ProviderListResponse(
        List<ProviderCardResponse> items,
        long totalItems,
        int page,
        int size,
        boolean hasNext,
        ProviderAppliedFiltersResponse appliedFilters,
        ProviderFilterOptionsResponse filterOptions
) {
}
