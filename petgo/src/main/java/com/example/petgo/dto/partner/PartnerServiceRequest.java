package com.example.petgo.dto.partner;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record PartnerServiceRequest(
        @NotNull(message = "Vui lòng chọn dịch vụ trong catalog") Long serviceId,

        @Size(max = 150, message = "Tên tùy chỉnh không được vượt quá 150 ký tự") String customName,

        @Size(max = 255, message = "Mô tả ngắn không được vượt quá 255 ký tự") String shortDescription,

        String description,

        @NotNull(message = "Thời lượng là bắt buộc") @Min(value = 1, message = "Thời lượng phải lớn hơn 0") Integer durationMinutes,

        @NotNull(message = "Giá là bắt buộc") @DecimalMin(value = "0.0", inclusive = true, message = "Giá phải >= 0") BigDecimal priceAmount,

        String currencyCode,
        String priceUnit,
        Boolean featured,
        Boolean active,

        @Min(value = 1, message = "Sức chứa mỗi slot phải >= 1") Integer capacityPerSlot,

        @Min(value = 0, message = "Buffer booking phải >= 0") Integer bookingBufferMinutes,

        Integer displayOrder) {
}