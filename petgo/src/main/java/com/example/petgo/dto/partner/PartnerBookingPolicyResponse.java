package com.example.petgo.dto.partner;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record PartnerBookingPolicyResponse(
        Long policyId,
        Long providerId,
        String timezone,
        Integer cancelWindowHours,
        String cancelFeeType,
        BigDecimal cancelFeeAmount,
        Integer cancelFeeAppliesAfterHours,
        Boolean allowUserReschedule,
        Integer rescheduleWindowHours,
        Integer maxReschedulesPerBooking,
        Boolean usingDefault,
        String note) {
}