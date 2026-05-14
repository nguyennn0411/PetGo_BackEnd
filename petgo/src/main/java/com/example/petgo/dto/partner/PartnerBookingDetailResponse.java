package com.example.petgo.dto.partner;

import com.example.petgo.dto.BookingTimelineItemResponse;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record PartnerBookingDetailResponse(
        Long bookingId,
        String bookingCode,
        String status,
        String statusLabel,
        Long providerId,
        String providerName,
        Long customerUserId,
        String customerName,
        String customerPhone,
        String customerEmail,
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
        String internalNote,
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
        boolean canConfirm,
        boolean canStart,
        boolean canComplete,
        boolean canCancel,
        List<BookingTimelineItemResponse> timeline) {
}