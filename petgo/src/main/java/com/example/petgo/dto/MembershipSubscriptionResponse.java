package com.example.petgo.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record MembershipSubscriptionResponse(
        Long id,
        String subscriptionCode,
        String status,
        Boolean autoRenew,
        String startedAt,
        String expiresAt,
        String nextBillingAt,
        String cancelledAt,
        String cancelReason,
        Long planId,
        String planCode,
        String planName,
        String planSlug,
        String billingCycle,
        BigDecimal priceAmount,
        String currencyCode,
        BigDecimal discountPercent,
        BigDecimal monthlyVoucherAmount,
        Boolean priorityBooking,
        Boolean prioritySupport,
        Boolean popular,
        List<String> features,
        Long latestInvoiceId,
        String latestInvoiceNumber,
        String latestInvoiceStatus
) {
}
