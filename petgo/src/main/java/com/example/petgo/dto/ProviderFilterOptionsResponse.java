package com.example.petgo.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record ProviderFilterOptionsResponse(
        List<ProviderCategoryOptionResponse> serviceCategories,
        List<String> cities,
        List<String> sortOptions,
        List<String> timeOfDayOptions
) {
}
