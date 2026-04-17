package com.example.petgo.dto;

import lombok.Builder;

@Builder
public record PetPhotoResponse(
        Long id,
        String photoUrl,
        Boolean primary,
        Integer sortOrder
) {
}
