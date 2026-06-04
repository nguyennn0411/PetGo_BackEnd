package com.example.petgo.dto;

import lombok.Builder;

@Builder
public record HomeSliderResponse(
        Long id,
        String title,
        String subtitle,
        String imageUrl,
        String ctaLabel,
        String ctaUrl,
        Integer sortOrder,
        Boolean active) {
}