package com.example.petgo.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record HomeCategoryResponse(
        Long id,
        Long parentId,
        String name,
        String description,
        List<HomeCategoryResponse> children
) {
}
