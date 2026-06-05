package com.example.petgo.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ServiceCategoryResponse(
    Long id,
    String name,
    String slug,
    String iconKey,
    String description,
    Integer sortOrder,
    Boolean active,
    LocalDateTime createdAt
) {}
