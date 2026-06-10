package com.example.petgo.dto;

import lombok.Builder;

@Builder
public record RegistrationServiceCategoryResponse(
                Long id,
                String name,
                Long parentId,
                String parentName) {
}