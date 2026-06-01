package com.example.petgo.controller;

import com.example.petgo.dto.AdminRegistrationReviewRequest;
import com.example.petgo.entity.RegistrationStatus;
import com.example.petgo.entity.RegistrationType;
import com.example.petgo.service.RegistrationReviewService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/registrations")
@RequiredArgsConstructor
public class AdminRegistrationController {

    private final RegistrationReviewService registrationReviewService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> listRegistrations(HttpServletRequest request,
                                                                 @RequestParam(required = false, defaultValue = "PARTNER") RegistrationType type,
                                                                 @RequestParam(required = false) RegistrationStatus status) {
        return ResponseEntity.ok(Map.of(
                "message", "Lấy danh sách hồ sơ đăng ký thành công.",
                "result", registrationReviewService.listRegistrations(request, type, status)
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getRegistrationDetail(HttpServletRequest request,
                                                                    @PathVariable Long id) {
        return ResponseEntity.ok(Map.of(
                "message", "Lấy chi tiết hồ sơ đăng ký thành công.",
                "result", registrationReviewService.getRegistrationDetail(request, id)
        ));
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<Map<String, Object>> approveRegistration(HttpServletRequest request,
                                                                  @PathVariable Long id,
                                                                  @Valid @RequestBody(required = false) AdminRegistrationReviewRequest requestBody) {
        return ResponseEntity.ok(Map.of(
                "message", "Duyệt hồ sơ đăng ký thành công.",
                "result", registrationReviewService.approve(request, id, requestBody)
        ));
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<Map<String, Object>> rejectRegistration(HttpServletRequest request,
                                                                 @PathVariable Long id,
                                                                 @Valid @RequestBody AdminRegistrationReviewRequest requestBody) {
        return ResponseEntity.ok(Map.of(
                "message", "Từ chối hồ sơ đăng ký thành công.",
                "result", registrationReviewService.reject(request, id, requestBody)
        ));
    }

    @PostMapping("/{id}/request-additional-info")
    public ResponseEntity<Map<String, Object>> requestAdditionalInfo(HttpServletRequest request,
                                                                    @PathVariable Long id,
                                                                    @Valid @RequestBody AdminRegistrationReviewRequest requestBody) {
        return ResponseEntity.ok(Map.of(
                "message", "Yêu cầu bổ sung thông tin thành công.",
                "result", registrationReviewService.requestAdditionalInfo(request, id, requestBody)
        ));
    }
}