package com.example.petgo.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record BookingCreateContextResponse(
        Long ownerUserId,
        BookingProviderOptionResponse provider,
        List<BookingPetOptionResponse> pets,
        List<BookingServiceOptionResponse> services,
        List<String> availableDates,
        List<BookingSlotOptionResponse> slots,
        Long selectedProviderId,
        Long selectedProviderServiceId,
        String selectedDate,
        String selectedTime,
        Long selectedSlotId,
        BigDecimal walletBalance,
        String walletCurrencyCode
) {
}
