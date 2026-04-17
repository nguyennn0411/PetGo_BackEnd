package com.example.petgo.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record PetResponse(
        Long id,
        String petCode,
        Long ownerUserId,
        String name,
        String species,
        String speciesLabel,
        String breed,
        String gender,
        String genderLabel,
        LocalDate dateOfBirth,
        String ageLabel,
        BigDecimal weightKg,
        String color,
        String size,
        String sizeLabel,
        String avatarUrl,
        String healthNotes,
        String allergyNotes,
        String behaviorNotes,
        String vaccinationNotes,
        String status,
        List<PetPhotoResponse> photos,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
