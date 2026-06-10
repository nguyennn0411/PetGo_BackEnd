package com.example.petgo.dto.partner;

import lombok.Builder;

@Builder
public record PartnerServiceCategoryResponse(
        Long id,
        String name,
        Long parentId,
        String parentName) {
}