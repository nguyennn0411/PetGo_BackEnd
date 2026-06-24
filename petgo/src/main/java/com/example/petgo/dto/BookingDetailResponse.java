package com.example.petgo.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record BookingDetailResponse(
        Long bookingId,
        String bookingCode,
        String status,
        String statusLabel,
        Long ownerUserId,
        Long providerId,
        String providerName,
        String providerPhone,
        String providerAddress,
        String providerImage,
        Long providerServiceId,
        String serviceName,
        String serviceDescription,
        Integer serviceDurationMinutes,
        Long petId,
        String petName,
        String petBreed,
        String petAvatarUrl,
        String appointmentDate,
        String appointmentDateDisplay,
        String appointmentTime,
        String timezone,
        String customerNote,
        BigDecimal subtotalAmount,
        BigDecimal promoDiscountAmount,
        BigDecimal taxAmount,
        BigDecimal totalAmount,
        String totalAmountDisplay,
        String currencyCode,
        Long invoiceId,
        String invoiceNumber,
        String invoiceStatus,
        String paymentStatus,
        String paymentMethod,
        Integer rescheduleCount,
        Integer cancellationFreeHours,
        String cancellationDeadline,
        BigDecimal estimatedRefundAmount,
        String estimatedRefundDisplay,
        boolean canCancel,
        boolean canReschedule,
        boolean canReview,
        List<BookingTimelineItemResponse> timeline
) {
}
