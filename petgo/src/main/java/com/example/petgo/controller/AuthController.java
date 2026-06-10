package com.example.petgo.controller;

import com.example.petgo.dto.AuthLoginRequest;
import com.example.petgo.dto.AuthRegisterRequest;
import com.example.petgo.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@Valid @RequestBody AuthRegisterRequest request) {
        return ResponseEntity.ok(Map.of(
                "message", "Đăng ký thành công. Vui lòng kiểm tra email để nhận mã OTP.",
                "result", Map.of("user", authService.register(request))
        ));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<Map<String, Object>> verifyOtp(@Valid @RequestBody com.example.petgo.dto.VerifyOtpRequest request) {
        authService.verifyOtp(request);
        return ResponseEntity.ok(Map.of("message", "Xác thực email thành công. Bạn có thể đăng nhập ngay bây giờ."));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody AuthLoginRequest request) {
        return ResponseEntity.ok(Map.of(
                "message", "Đăng nhập thành công.",
                "result", authService.login(request)
        ));
    }

    @PostMapping("/refresh")
    public ResponseEntity<Map<String, Object>> refresh(@RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        return ResponseEntity.ok(Map.of(
                "message", "Làm mới token thành công.",
                "result", authService.refresh(authorizationHeader)
        ));
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> me(HttpServletRequest request) {
        return ResponseEntity.ok(Map.of(
                "message", "Lấy thông tin tài khoản thành công.",
                "result", Map.of("user", authService.getCurrentUserProfile(request))
        ));
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(@RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        authService.logout(authorizationHeader);
        return ResponseEntity.ok(Map.of("message", "Đăng xuất thành công."));
    }
}
