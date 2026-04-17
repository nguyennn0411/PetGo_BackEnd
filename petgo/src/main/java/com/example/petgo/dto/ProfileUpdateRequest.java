package com.example.petgo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProfileUpdateRequest(
        @NotBlank(message = "Họ tên không được để trống")
        @Size(max = 150, message = "Họ tên tối đa 150 ký tự")
        String fullName,

        @NotBlank(message = "Email không được để trống")
        @Email(message = "Email không hợp lệ")
        @Size(max = 190, message = "Email tối đa 190 ký tự")
        String email,

        @Size(max = 30, message = "Số điện thoại tối đa 30 ký tự")
        String phoneNumber,

        @Size(max = 500, message = "Avatar URL tối đa 500 ký tự")
        String avatarUrl,

        @Size(max = 500, message = "Cover URL tối đa 500 ký tự")
        String coverUrl,

        @Size(max = 255, message = "Địa chỉ tối đa 255 ký tự")
        String addressLine1,

        @Size(max = 255, message = "Địa chỉ bổ sung tối đa 255 ký tự")
        String addressLine2,

        @Size(max = 120, message = "Phường/xã tối đa 120 ký tự")
        String ward,

        @Size(max = 120, message = "Quận/huyện tối đa 120 ký tự")
        String district,

        @Size(max = 120, message = "Thành phố tối đa 120 ký tự")
        String city,

        @Size(max = 120, message = "Tỉnh tối đa 120 ký tự")
        String province
) {
}
