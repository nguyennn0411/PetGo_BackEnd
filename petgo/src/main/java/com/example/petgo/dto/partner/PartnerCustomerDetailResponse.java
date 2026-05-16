package com.example.petgo.dto.partner;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record PartnerCustomerDetailResponse(
        Long providerId,
        String businessName,
        Long customerUserId,
        String customerName,
        String customerAvatarUrl,
        String maskedPhone,
        String maskedEmail,
        long bookingCount,
        long completedBookingCount,
        long cancelledBookingCount,
        BigDecimal totalSpent,
        String totalSpentDisplay,
        Long lastBookingId,
        String lastBookingCode,
        String lastBookingStatus,
        String lastBookingStatusLabel,
        String lastBookingDate,
        String lastBookingDateDisplay,
        List<PartnerCustomerPetSummaryResponse> pets,
        List<PartnerBookingSummaryResponse> bookings) {
}