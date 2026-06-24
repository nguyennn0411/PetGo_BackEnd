package com.example.petgo.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record BookingRescheduleContextResponse(
        Long bookingId,
        String bookingCode,
        String currentStatus,
        String currentStatusLabel,
        Long providerId,
        String providerName,
        Long providerServiceId,
        String serviceName,
        String currentDate,
        String currentDateDisplay,
        String currentTime,
        String currentTimeDisplay,
        Integer rescheduleCount,
        List<String> availableDates,
        List<BookingSlotOptionResponse> slots,
        boolean canReschedule,
        String note
) {
}
