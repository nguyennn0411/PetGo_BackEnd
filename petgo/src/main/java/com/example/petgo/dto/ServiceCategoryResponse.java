package com.example.petgo.dto;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record ServiceCategoryResponse(
                Long id,
                Long parentId,
                String parentName,
                String name,
                String description,
                Boolean active,
                LocalDateTime createdAt,
                List<ServiceCategoryResponse> children) {
}
