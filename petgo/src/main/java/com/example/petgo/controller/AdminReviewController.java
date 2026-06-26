package com.example.petgo.controller;

import com.example.petgo.service.AdminReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/reviews")
@RequiredArgsConstructor
public class AdminReviewController {

    private final AdminReviewService adminReviewService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllReviews(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Integer rating,
            @RequestParam(required = false) Boolean hidden) {
        return ResponseEntity.ok(Map.of(
                "message", "Lấy danh sách đánh giá thành công.",
                "result", adminReviewService.getAllReviews(search, rating, hidden)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getReviewDetail(@PathVariable Long id) {
        return ResponseEntity.ok(Map.of(
                "message", "Lấy chi tiết đánh giá thành công.",
                "result", adminReviewService.getReviewDetail(id)));
    }

    @PutMapping("/{id}/toggle-hidden")
    public ResponseEntity<Map<String, Object>> toggleHidden(@PathVariable Long id) {
        return ResponseEntity.ok(Map.of(
                "message", "Cập nhật trạng thái hiển thị thành công.",
                "result", adminReviewService.toggleHidden(id)));
    }

    @PutMapping("/{id}/reply")
    public ResponseEntity<Map<String, Object>> reply(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        String reply = body.get("reply");
        return ResponseEntity.ok(Map.of(
                "message", "Phản hồi đánh giá thành công.",
                "result", adminReviewService.reply(id, reply)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteReview(@PathVariable Long id) {
        adminReviewService.deleteReview(id);
        return ResponseEntity.ok(Map.of(
                "message", "Xóa đánh giá thành công."));
    }
}
