package com.example.petgo.dto;

import lombok.Builder;

@Builder
public record ProviderCategoryOptionResponse(
        Long id,
        String name,
        String slug
) {
}
