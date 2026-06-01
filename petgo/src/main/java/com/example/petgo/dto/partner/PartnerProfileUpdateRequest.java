package com.example.petgo.dto.partner;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record PartnerProfileUpdateRequest(
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

                @Size(max = 500, message = "URL ảnh cover không được vượt quá 500 ký tự") String coverImageUrl) {
}