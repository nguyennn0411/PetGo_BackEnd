package com.example.petgo.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record ProviderCategoryOptionResponse(
        Long id,
        String name,
        Long parentId,
        String description,
        List<ProviderCategoryOptionResponse> children
) {
}
