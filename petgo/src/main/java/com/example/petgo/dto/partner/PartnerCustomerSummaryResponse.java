package com.example.petgo.dto.partner;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record PartnerCustomerSummaryResponse(
        Long customerUserId,
        String customerName,
        String customerAvatarUrl,
        String maskedPhone,
        String maskedEmail,
        long bookingCount,
        long completedBookingCount,
        BigDecimal totalSpent,
        String totalSpentDisplay,
        Long lastBookingId,
        String lastBookingCode,
        String lastBookingStatus,
        String lastBookingStatusLabel,
        String lastBookingDate,
        String lastBookingDateDisplay,
        String lastServiceName,
        List<PartnerCustomerPetSummaryResponse> pets) {
}