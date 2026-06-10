package com.example.petgo.dto.partner;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PartnerBookingPolicyRequest(
        @NotBlank(message = "Timezone không được để trống") String timezone,
        @NotNull(message = "Cancel window không được để trống") @Min(value = 0, message = "Cancel window phải >= 0") Integer cancelWindowHours,
        @NotBlank(message = "Cancel fee type không được để trống") String cancelFeeType,
        @NotNull(message = "Cancel fee amount không được để trống") @DecimalMin(value = "0.00", message = "Cancel fee amount phải >= 0") BigDecimal cancelFeeAmount,
        @Min(value = 0, message = "Cancel fee applies after hours phải >= 0") Integer cancelFeeAppliesAfterHours,
        @NotNull(message = "Allow user reschedule không được để trống") Boolean allowUserReschedule,
        @NotNull(message = "Reschedule window không được để trống") @Min(value = 0, message = "Reschedule window phải >= 0") Integer rescheduleWindowHours,
        @NotNull(message = "Max reschedules không được để trống") @Min(value = 0, message = "Max reschedules phải >= 0") Integer maxReschedulesPerBooking) {
}