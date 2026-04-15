package com.example.petgo.controller;

import com.example.petgo.dto.request.LoginRequest;
import com.example.petgo.dto.request.RegisterRequest;
import com.example.petgo.dto.response.ApiResponse;
import com.example.petgo.dto.response.AuthResponse;
import com.example.petgo.dto.response.UserResponse;
import com.example.petgo.repository.UserRepository;
import com.example.petgo.service.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Authentication")
public class AuthController {
    AuthService authService;
    UserRepository  userRepository;

    @PostMapping("/login")
    @Operation(summary = "Đăng nhập")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        var result = authService.login(request);
        return ApiResponse.<AuthResponse>builder()
                .result(result)
                .build();
    }

    @PostMapping("/register")
    @Operation(summary = "Đăng ký tài khoản mới")
    public ApiResponse<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        var result = authService.register(request);
        return ApiResponse.<UserResponse>builder()
                .result(result)
                .message("Đăng ký tài khoản thành công")
                .build();
    }
}
