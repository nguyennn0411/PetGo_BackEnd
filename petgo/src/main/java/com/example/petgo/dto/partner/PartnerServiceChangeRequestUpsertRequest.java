package com.example.petgo.dto.partner;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;

public record PartnerServiceChangeRequestUpsertRequest(
                Long providerServiceId,
                @Size(max = 150, message = "Tên dịch vụ không được vượt quá 150 ký tự") String serviceName,
                List<@Size(max = 500, message = "URL ảnh không được vượt quá 500 ký tự") String> photoUrls,
                @DecimalMin(value = "0.0", inclusive = true, message = "Giá phải >= 0") BigDecimal priceAmount,
                @Size(max = 3, message = "Mã tiền tệ không hợp lệ") String currencyCode,
                @Size(max = 40, message = "Đơn vị tính không được vượt quá 40 ký tự") String priceUnit,
                String description) {
}