package com.example.petgo.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record PetListResponse(
        Long ownerUserId,
        Integer total,
        List<PetResponse> items
) {
}
