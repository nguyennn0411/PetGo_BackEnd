package com.example.petgo.controller;

import com.example.petgo.dto.UserResponse;
import com.example.petgo.dto.UserStatusRequest;
import com.example.petgo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;

    @GetMapping("/users")
    public ResponseEntity<Map<String, Object>> getAllUsers() {
        return ResponseEntity.ok(Map.of(
                "message", "Lấy danh sách người dùng thành công.",
                "result", userService.getAllUsers()
        ));
    }

    @PutMapping("/users/status")
    public ResponseEntity<Map<String, Object>> updateUserStatus(@RequestBody UserStatusRequest request) {
        userService.updateUserStatus(request);
        return ResponseEntity.ok(Map.of(
                "message", "Cập nhật trạng thái người dùng thành công."
        ));
    }
}