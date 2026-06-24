package com.example.petgo.dto;

import lombok.Builder;

@Builder
public record ProviderReviewResponse(
        Long id,
        String user,
        String avatar,
        Integer rating,
        String comment,
        String date,
        String petName,
        String petBreed
) {
}
