package com.example.petgo.controller;

import com.example.petgo.dto.ProviderDetailResponse;
import com.example.petgo.dto.ProviderVerificationRequest;
import com.example.petgo.dto.ServiceCategoryRequest;
import com.example.petgo.dto.UserStatusRequest;
import com.example.petgo.service.AdminService;
import com.example.petgo.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final AdminService adminService;

    @GetMapping("/users")
    public ResponseEntity<Map<String, Object>> getAdminUsers() {
        return ResponseEntity.ok(Map.of(
                "message", "Lấy danh sách người dùng thành công.",
                "result", userService.getAllUsers()));
    }

    @PutMapping("/users/status")
    public ResponseEntity<Map<String, Object>> updateUserStatus(@RequestBody UserStatusRequest request) {
        userService.updateUserStatus(request);
        return ResponseEntity.ok(Map.of(
                "message", "Cập nhật trạng thái người dùng thành công."));
    }

    @GetMapping("/providers/pending")
    public ResponseEntity<Map<String, Object>> getPendingProviders() {
        return ResponseEntity.ok(Map.of(
                "message", "Lấy danh sách shop chờ duyệt thành công.",
                "result", adminService.getPendingProviders()));
    }

    @GetMapping("/providers/verified")
    public ResponseEntity<Map<String, Object>> getVerifiedProviders() {
        return ResponseEntity.ok(Map.of(
                "message", "Lấy danh sách đối tác đang hoạt động thành công.",
                "result", adminService.getVerifiedProviders()));
    }

    @GetMapping("/providers/{id}")
    public ResponseEntity<ProviderDetailResponse> getProviderDetail(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.getProviderDetail(id));
    }

    @PutMapping("/providers/verification")
    public ResponseEntity<Map<String, Object>> updateProviderStatus(@RequestBody ProviderVerificationRequest request) {
        adminService.updateProviderStatus(request);
        return ResponseEntity.ok(Map.of(
                "message", "Cập nhật trạng thái duyệt shop thành công."));
    }

    @PutMapping("/providers/status")
    public ResponseEntity<Map<String, Object>> updateProviderAccountStatus(
            @RequestBody ProviderVerificationRequest request) {
        adminService.updateProviderAccountStatus(request);
        return ResponseEntity.ok(Map.of(
                "message", "Cập nhật trạng thái hoạt động shop thành công."));
    }

    @GetMapping("/categories")
    public ResponseEntity<Map<String, Object>> getAllCategories() {
        return ResponseEntity.ok(Map.of(
                "message", "Lấy danh sách danh mục thành công.",
                "result", adminService.getAllCategories()));
    }

    @PostMapping("/categories")
    public ResponseEntity<Map<String, Object>> createCategory(@Valid @RequestBody ServiceCategoryRequest request) {
        return ResponseEntity.ok(Map.of(
                "message", "Tạo danh mục thành công.",
                "result", adminService.createCategory(request)));
    }

    @PutMapping("/categories/{id}")
    public ResponseEntity<Map<String, Object>> updateCategory(@PathVariable Long id,
            @Valid @RequestBody ServiceCategoryRequest request) {
        return ResponseEntity.ok(Map.of(
                "message", "Cập nhật danh mục thành công.",
                "result", adminService.updateCategory(id, request)));
    }

    @DeleteMapping("/categories/{id}")
    public ResponseEntity<Map<String, Object>> deleteCategory(@PathVariable Long id) {
        adminService.deleteCategory(id);
        return ResponseEntity.ok(Map.of(
                "message", "Xóa (ẩn) danh mục thành công."));
    }
}