package com.example.petgo.dto.partner;

public record PartnerBookingActionRequest(
        String reasonCode,
        String reasonText,
        String note) {
}