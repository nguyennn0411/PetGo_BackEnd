package com.example.petgo.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record CatalogServiceResponse(
        Long id,
        String serviceCode,
        String name,
        String slug,
        String shortDescription,
        String description,
        Integer defaultDurationMinutes,
        BigDecimal basePriceAmount,
        String currencyCode,
        String priceUnit,
        String imageUrl,
        Boolean active,
        String bookingType,
        Boolean bookable,
        List<Long> areaIds,
        Long categoryId,
        String categoryName,
        List<CategoryInfo> categories) {

    @Builder
    public record CategoryInfo(Long id, String name) {}
}
