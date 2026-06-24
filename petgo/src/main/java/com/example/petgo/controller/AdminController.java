package com.example.petgo.controller;

import com.example.petgo.dto.*;
import com.example.petgo.service.AdminCatalogService;
import com.example.petgo.service.AdminService;
import com.example.petgo.service.CloudinaryStorageService;
import com.example.petgo.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final AdminService adminService;
    private final AdminCatalogService adminCatalogService;
    private final CloudinaryStorageService cloudinaryStorageService;

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
    public ResponseEntity<Map<String, Object>> deleteCategory(
            @PathVariable Long id,
            @RequestParam(required = false) Boolean hardDelete,
            @RequestParam(required = false) Long moveServicesToCategoryId) {
        adminService.deleteCategory(id, new ServiceCategoryDeleteRequest(moveServicesToCategoryId, hardDelete));
        String msg = Boolean.TRUE.equals(hardDelete) ? "Xóa danh mục thành công." : "Ẩn danh mục thành công.";
        return ResponseEntity.ok(Map.of("message", msg));
    }

    @GetMapping("/services/list")
    public ResponseEntity<Map<String, Object>> getAdminServiceList() {
        return ResponseEntity.ok(Map.of(
                "message", "Lấy danh sách dịch vụ thành công.",
                "result", adminCatalogService.getAllServices()));
    }

    @PostMapping("/services")
    public ResponseEntity<Map<String, Object>> createService(@Valid @RequestBody AdminServiceCreateRequest request) {
        return ResponseEntity.ok(Map.of(
                "message", "Tạo dịch vụ thành công.",
                "result", adminCatalogService.createService(request)));
    }

    @PutMapping("/services/{id}")
    public ResponseEntity<Map<String, Object>> updateService(@PathVariable Long id,
            @Valid @RequestBody AdminServiceUpdateRequest request) {
        return ResponseEntity.ok(Map.of(
                "message", "Cập nhật dịch vụ thành công.",
                "result", adminCatalogService.updateService(id, request)));
    }

    @DeleteMapping("/services/{id}")
    public ResponseEntity<Map<String, Object>> deleteService(@PathVariable Long id) {
        adminCatalogService.deleteService(id);
        return ResponseEntity.ok(Map.of(
                "message", "Xóa dịch vụ thành công."));
    }

    @PostMapping("/services/upload-image")
    public ResponseEntity<Map<String, Object>> uploadServiceImage(@RequestParam("file") MultipartFile file) {
        String imageUrl = cloudinaryStorageService.uploadPlatformServiceImage(file);
        return ResponseEntity.ok(Map.of(
                "message", "Upload ảnh dịch vụ thành công.",
                "result", imageUrl));
    }

    @GetMapping("/home-sliders")
    public ResponseEntity<Map<String, Object>> getHomeSliders() {
        return ResponseEntity.ok(Map.of(
                "message", "Lấy danh sách slider trang chủ thành công.",
                "result", adminService.getAllHomeSliders()));
    }

    @PostMapping("/home-sliders")
    public ResponseEntity<Map<String, Object>> createHomeSlider(@Valid @RequestBody HomeSliderRequest request) {
        return ResponseEntity.ok(Map.of(
                "message", "Tạo slider trang chủ thành công.",
                "result", adminService.createHomeSlider(request)));
    }

    @PutMapping("/home-sliders/{id}")
    public ResponseEntity<Map<String, Object>> updateHomeSlider(@PathVariable Long id,
            @Valid @RequestBody HomeSliderRequest request) {
        return ResponseEntity.ok(Map.of(
                "message", "Cập nhật slider trang chủ thành công.",
                "result", adminService.updateHomeSlider(id, request)));
    }

    @PatchMapping("/home-sliders/{id}/visibility")
    public ResponseEntity<Map<String, Object>> updateHomeSliderVisibility(@PathVariable Long id,
            @RequestBody Map<String, Boolean> request) {
        return ResponseEntity.ok(Map.of(
                "message", "Cập nhật trạng thái slider trang chủ thành công.",
                "result", adminService.updateHomeSliderVisibility(id, request.get("active"))));
    }

    @DeleteMapping("/home-sliders/{id}")
    public ResponseEntity<Map<String, Object>> deleteHomeSlider(@PathVariable Long id) {
        adminService.deleteHomeSlider(id);
        return ResponseEntity.ok(Map.of(
                "message", "Xóa slider trang chủ thành công."));
    }
}