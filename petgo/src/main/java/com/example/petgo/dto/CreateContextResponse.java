package com.example.petgo.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record CreateContextResponse(
        List<PetInfo> pets,
        List<AreaInfo> areas,
        List<ServiceInfo> services,
        BigDecimal walletBalance,
        String currencyCode) {

    @Builder
    public record PetInfo(Long id, String name, String breed, String avatarUrl) {}

    @Builder
    public record AreaInfo(
            Long id, String name,
            BigDecimal pickupLatitude, BigDecimal pickupLongitude,
            String pickupAddress, String pickupPhone, String pickupInstructions) {}

    @Builder
    public record ServiceInfo(
            Long id, String name, String bookingType,
            Integer defaultDurationMinutes, BigDecimal basePriceAmount,
            String currencyCode, String priceUnit,
            Long categoryId, String categoryName) {}
}
