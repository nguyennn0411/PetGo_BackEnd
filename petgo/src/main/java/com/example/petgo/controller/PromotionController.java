package com.example.petgo.controller;

import com.example.petgo.dto.promotion.PromotionOptionsResponse;
import com.example.petgo.dto.promotion.PromotionRequest;
import com.example.petgo.dto.promotion.PromotionResponse;
import com.example.petgo.service.PromotionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class PromotionController {

    private final PromotionService promotionService;

    @GetMapping("/admin/promotions")
    public ResponseEntity<Map<String, Object>> listAdminPromotions(HttpServletRequest request,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "targetType", required = false) String targetType) {
        List<PromotionResponse> result = promotionService.listAdminPromotions(request, status, targetType);
        return ResponseEntity.ok(Map.of("message", "Lấy danh sách khuyến mãi thành công.", "result", result));
    }

    @GetMapping("/admin/promotions/options")
    public ResponseEntity<Map<String, Object>> getAdminOptions(HttpServletRequest request) {
        PromotionOptionsResponse result = promotionService.getAdminOptions(request);
        return ResponseEntity.ok(Map.of("message", "Lấy cấu hình khuyến mãi thành công.", "result", result));
    }

    @PostMapping("/admin/promotions")
    public ResponseEntity<Map<String, Object>> createAdminPromotion(HttpServletRequest request,
            @Valid @RequestBody PromotionRequest requestBody) {
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "message", "Tạo khuyến mãi admin thành công.",
                "result", promotionService.createAdminPromotion(request, requestBody)));
    }

    @PutMapping("/admin/promotions/{id}")
    public ResponseEntity<Map<String, Object>> updateAdminPromotion(HttpServletRequest request,
            @PathVariable Long id,
            @Valid @RequestBody PromotionRequest requestBody) {
        return ResponseEntity.ok(Map.of(
                "message", "Cập nhật khuyến mãi admin thành công.",
                "result", promotionService.updateAdminPromotion(request, id, requestBody)));
    }

    @PatchMapping("/admin/promotions/{id}/status")
    public ResponseEntity<Map<String, Object>> updateAdminPromotionStatus(HttpServletRequest request,
            @PathVariable Long id,
            @RequestBody Map<String, Boolean> requestBody) {
        return ResponseEntity.ok(Map.of(
                "message", "Cập nhật trạng thái khuyến mãi thành công.",
                "result", promotionService.updateAdminPromotionStatus(request, id, requestBody.get("active"))));
    }

    @DeleteMapping("/admin/promotions/{id}")
    public ResponseEntity<Map<String, Object>> deleteAdminPromotion(HttpServletRequest request,
            @PathVariable Long id) {
        promotionService.deleteAdminPromotion(request, id);
        return ResponseEntity.ok(Map.of("message", "Xóa khuyến mãi thành công."));
    }

}