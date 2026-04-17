package com.example.petgo.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record HomeMembershipPlanResponse(
        Long id,
        String code,
        String name,
        String slug,
        BigDecimal price,
        String currencyCode,
        String billingCycle,
        BigDecimal discountPercent,
        BigDecimal monthlyVoucherAmount,
        Boolean popular,
        List<String> features
) {
}
