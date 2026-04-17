package com.example.petgo.dto;

import lombok.Builder;

@Builder
public record BookingPetOptionResponse(
        Long id,
        String name,
        String species,
        String breed,
        String avatarUrl,
        String ageLabel,
        String label
) {
}
