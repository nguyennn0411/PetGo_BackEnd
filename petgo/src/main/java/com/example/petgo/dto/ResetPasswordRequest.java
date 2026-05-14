package com.example.petgo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequest(
        @NotBlank(message = "Email không được để trống.")
        @Email(message = "Email không hợp lệ.")
        @Size(max = 190, message = "Email tối đa 190 ký tự.")
        String email,

        @NotBlank(message = "Mã OTP không được để trống.")
        @Pattern(regexp = "^\\d{6}$", message = "Mã OTP phải có 6 chữ số.")
        String otpCode,

        @NotBlank(message = "Mật khẩu mới không được để trống.")
        @Size(min = 6, max = 100, message = "Mật khẩu mới phải từ 6 đến 100 ký tự.")
        String newPassword
) {
}