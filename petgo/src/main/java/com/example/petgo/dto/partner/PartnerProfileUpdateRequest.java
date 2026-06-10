package com.example.petgo.dto.partner;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;

public record PartnerProfileUpdateRequest(
        @Size(max = 180, message = "Tên nhà cung cấp không được vượt quá 180 ký tự") String businessName,

        String description,

        @Size(max = 30, message = "Số điện thoại không được vượt quá 30 ký tự") String emergencyPhone,

        @Email(message = "Email shop không hợp lệ") String businessEmail,

        @Size(max = 255, message = "Địa chỉ cụ thể không được vượt quá 255 ký tự") String primaryAddressLine1,

        @Size(max = 120, message = "Phường/xã không được vượt quá 120 ký tự") String ward,

        @Size(max = 120, message = "Quận/huyện không được vượt quá 120 ký tự") String district,

        @Size(max = 120, message = "Tỉnh/Thành phố không được vượt quá 120 ký tự") String city,

        BigDecimal latitude,
        BigDecimal longitude,

        @Size(max = 500, message = "URL ảnh chính không được vượt quá 500 ký tự") String mainImageUrl,

        @Size(max = 500, message = "URL ảnh cover không được vượt quá 500 ký tự") String coverImageUrl,

        @Size(max = 10, message = "Thư viện ảnh chỉ được tối đa 10 ảnh") List<@Size(max = 500, message = "URL ảnh thư viện không được vượt quá 500 ký tự") String> photoUrls) {
}