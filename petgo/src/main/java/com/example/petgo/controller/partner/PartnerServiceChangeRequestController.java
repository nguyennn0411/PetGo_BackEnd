package com.example.petgo.controller.partner;

import com.example.petgo.dto.partner.PartnerServiceCategoryResponse;
import com.example.petgo.dto.partner.PartnerServiceChangeRequestResponse;
import com.example.petgo.dto.partner.PartnerServiceChangeRequestUpsertRequest;
import com.example.petgo.exception.BadRequestException;
import com.example.petgo.service.CloudinaryStorageService;
import com.example.petgo.service.partner.PartnerServiceChangeRequestService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/partner/services")
@RequiredArgsConstructor
public class PartnerServiceChangeRequestController {

    private final PartnerServiceChangeRequestService changeRequestService;
    private final CloudinaryStorageService cloudinaryStorageService;

    @GetMapping("/category-options")
    public ResponseEntity<List<PartnerServiceCategoryResponse>> listCategoryOptions(HttpServletRequest request) {
        return ResponseEntity.ok(changeRequestService.listCategoryOptions(request));
    }

    @GetMapping("/change-requests")
    public ResponseEntity<List<PartnerServiceChangeRequestResponse>> listMyRequests(HttpServletRequest request) {
        return ResponseEntity.ok(changeRequestService.listMyRequests(request));
    }

    @PostMapping("/change-requests/drafts")
    public ResponseEntity<PartnerServiceChangeRequestResponse> saveDraft(HttpServletRequest request,
            @Valid @RequestBody PartnerServiceChangeRequestUpsertRequest requestBody) {
        return ResponseEntity.status(HttpStatus.CREATED).body(changeRequestService.saveDraft(request, requestBody));
    }

    @PutMapping("/change-requests/drafts/{id}")
    public ResponseEntity<PartnerServiceChangeRequestResponse> updateDraft(HttpServletRequest request,
            @PathVariable Long id,
            @Valid @RequestBody PartnerServiceChangeRequestUpsertRequest requestBody) {
        return ResponseEntity.ok(changeRequestService.updateDraft(request, id, requestBody));
    }

    @PostMapping("/change-requests")
    public ResponseEntity<PartnerServiceChangeRequestResponse> submitNewRequest(HttpServletRequest request,
            @Valid @RequestBody PartnerServiceChangeRequestUpsertRequest requestBody) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(changeRequestService.submitNewRequest(request, requestBody));
    }

    @PostMapping("/change-requests/drafts/{id}/submit")
    public ResponseEntity<PartnerServiceChangeRequestResponse> submitDraft(HttpServletRequest request,
            @PathVariable Long id) {
        return ResponseEntity.ok(changeRequestService.submitDraft(request, id));
    }

    @DeleteMapping("/change-requests/drafts/{id}")
    public ResponseEntity<Map<String, Object>> deleteDraft(HttpServletRequest request, @PathVariable Long id) {
        changeRequestService.deleteDraft(request, id);
        return ResponseEntity.ok(Map.of("message", "Đã xóa bản nháp dịch vụ."));
    }

    @PostMapping("/{id}/copy")
    public ResponseEntity<PartnerServiceChangeRequestResponse> copyFromService(HttpServletRequest request,
            @PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.CREATED).body(changeRequestService.copyFromService(request, id));
    }

    @PostMapping("/change-requests/{id}/copy")
    public ResponseEntity<PartnerServiceChangeRequestResponse> copyFromRequest(HttpServletRequest request,
            @PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.CREATED).body(changeRequestService.copyFromRequest(request, id));
    }

    @PostMapping(value = "/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> uploadServiceImage(
            @RequestParam(value = "file", required = false) MultipartFile file) {
        String imageUrl = cloudinaryStorageService.uploadPartnerServiceImage(file);
        if (imageUrl == null || imageUrl.isBlank()) {
            throw new BadRequestException("Upload ảnh dịch vụ thất bại. Vui lòng thử lại.");
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("imageUrl", imageUrl);
        result.put("url", imageUrl);
        return ResponseEntity.ok(Map.of("message", "Upload ảnh dịch vụ thành công.", "result", result));
    }
}