package com.example.petgo.dto;

import lombok.Builder;

@Builder
public record HomeCategoryResponse(
        Long id,
        String name,
        String slug,
        String iconKey,
        String description
) {
}
