package com.example.petgo.dto;

import lombok.Builder;

@Builder
public record HomeReviewResponse(
        Long id,
        String customerName,
        String petLabel,
        Integer rating,
        String text,
        String avatar
) {
}
