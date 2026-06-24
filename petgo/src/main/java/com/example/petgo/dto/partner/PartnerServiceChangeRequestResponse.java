package com.example.petgo.dto.partner;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record PartnerServiceChangeRequestResponse(
        Long id,
        Long providerId,
        String providerName,
        Long providerServiceId,
        String requestType,
        String status,
        String statusLabel,
        List<Long> categoryIds,
        List<PartnerServiceCategoryResponse> categories,
        String serviceName,
        List<String> photoUrls,
        BigDecimal priceAmount,
        String priceDisplay,
        String currencyCode,
        String priceUnit,
        String priceUnitLabel,
        String description,
        String adminMessage,
        String createdAt,
        String updatedAt,
        String submittedAt,
        String reviewedAt,
        Long reviewerId,
        String reviewerName,
        List<PartnerServiceChangeItemResponse> changes) {
}