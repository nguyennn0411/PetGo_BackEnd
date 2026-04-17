package com.example.petgo.dto;

import lombok.Builder;

@Builder
public record ReviewPhotoResponse(
        String photoUrl,
        Integer sortOrder
) {
}
