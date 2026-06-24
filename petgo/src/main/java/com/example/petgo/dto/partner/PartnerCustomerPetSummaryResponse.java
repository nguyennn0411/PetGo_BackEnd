package com.example.petgo.dto.partner;

import lombok.Builder;

@Builder
public record PartnerCustomerPetSummaryResponse(
        Long petId,
        String petName,
        String species,
        String breed,
        String avatarUrl,
        long bookingCount,
        String lastBookingDate,
        String lastBookingDateDisplay) {
}