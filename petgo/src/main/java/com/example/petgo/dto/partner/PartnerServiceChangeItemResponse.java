package com.example.petgo.dto.partner;

import lombok.Builder;

@Builder
public record PartnerServiceChangeItemResponse(
        String field,
        String label,
        String currentValue,
        String proposedValue,
        boolean changed) {
}