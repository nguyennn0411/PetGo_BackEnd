package com.example.petgo.controller;

import com.example.petgo.dto.RegistrationAdditionalInfoRequest;
import com.example.petgo.dto.RegistrationSubmitRequest;
import com.example.petgo.dto.RegistrationUpsertRequest;
import com.example.petgo.exception.BadRequestException;
import com.example.petgo.service.AuthService;
import com.example.petgo.service.CloudinaryStorageService;
import com.example.petgo.service.RegistrationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/registrations")
@RequiredArgsConstructor
public class RegistrationController {

        private final RegistrationService registrationService;
        private final CloudinaryStorageService cloudinaryStorageService;
        private final AuthService authService;

        @GetMapping("/partner")
        public ResponseEntity<Map<String, Object>> getMyPartnerRegistration(HttpServletRequest request) {
                return ResponseEntity.ok(response(
                                "Lấy hồ sơ đăng ký partner thành công.",
                                registrationService.getMyPartnerRegistration(request)));
        }

        @GetMapping("/partner/history")
        public ResponseEntity<Map<String, Object>> getMyPartnerRegistrationHistory(HttpServletRequest request) {
                return ResponseEntity.ok(Map.of(
                                "message", "Lấy lịch sử đăng ký partner thành công.",
                                "result", registrationService.getPartnerRegistrationHistory(request)));
        }

        @PostMapping(value = "/partner/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
        public ResponseEntity<Map<String, Object>> uploadPartnerLocationImage(HttpServletRequest request,
                        @RequestParam(value = "file", required = false) MultipartFile file) {
                authService.requireAccessUser(request);
                String imageUrl = cloudinaryStorageService.uploadPartnerLocationImage(file);

                if (imageUrl == null || imageUrl.isBlank()) {
                        throw new BadRequestException("Upload ảnh lên Cloudinary thất bại. Vui lòng thử lại.");
                }

                Map<String, Object> result = new LinkedHashMap<>();
                result.put("imageUrl", imageUrl);
                result.put("url", imageUrl);

                return ResponseEntity.ok(response(
                                "Upload ảnh địa điểm nhà cung cấp thành công.",
                                result));
        }

        @PostMapping("/partner/draft")
        public ResponseEntity<Map<String, Object>> createPartnerDraft(HttpServletRequest request,
                        @Valid @RequestBody RegistrationUpsertRequest requestBody) {
                return ResponseEntity.ok(Map.of(
                                "message", "Lưu nháp hồ sơ partner thành công.",
                                "result", registrationService.savePartnerDraft(request, requestBody)));
        }

        @PutMapping("/partner/draft")
        public ResponseEntity<Map<String, Object>> updatePartnerDraft(HttpServletRequest request,
                        @Valid @RequestBody RegistrationUpsertRequest requestBody) {
                return ResponseEntity.ok(Map.of(
                                "message", "Cập nhật nháp hồ sơ partner thành công.",
                                "result", registrationService.savePartnerDraft(request, requestBody)));
        }

        @PostMapping("/partner/submit")
        public ResponseEntity<Map<String, Object>> submitPartnerRegistration(HttpServletRequest request,
                        @Valid @RequestBody(required = false) RegistrationSubmitRequest requestBody) {
                return ResponseEntity.ok(Map.of(
                                "message", "Gửi hồ sơ partner chờ duyệt thành công.",
                                "result", registrationService.submitPartnerRegistration(request, requestBody)));
        }

        @PostMapping("/partner/additional-info")
        public ResponseEntity<Map<String, Object>> submitPartnerAdditionalInformation(HttpServletRequest request,
                        @Valid @RequestBody RegistrationAdditionalInfoRequest requestBody) {
                return ResponseEntity.ok(Map.of(
                                "message", "Gửi thông tin bổ sung thành công.",
                                "result",
                                registrationService.submitPartnerAdditionalInformation(request, requestBody)));
        }

        private Map<String, Object> response(String message, Object result) {
                Map<String, Object> body = new LinkedHashMap<>();
                body.put("message", message);
                body.put("result", result);
                return body;
        }
}