package com.example.petgo.dto.partner;

import lombok.Builder;

import java.util.List;

@Builder
public record PartnerCustomerListResponse(
        Long providerId,
        String businessName,
        String keyword,
        String filterStatus,
        int page,
        int size,
        long totalItems,
        int totalPages,
        List<PartnerCustomerSummaryResponse> customers) {
}