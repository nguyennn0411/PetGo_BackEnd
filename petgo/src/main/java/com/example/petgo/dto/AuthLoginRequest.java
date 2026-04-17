package com.example.petgo.dto;

import jakarta.validation.constraints.NotBlank;

public record AuthLoginRequest(
        @NotBlank(message = "Email hoặc tên đăng nhập không được để trống")
        String userName,

        @NotBlank(message = "Mật khẩu không được để trống")
        String password
) {
}
