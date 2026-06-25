package com.example.petgo.controller;

import com.example.petgo.dto.AdminDisputeResolveRequest;
import com.example.petgo.service.AdminBookingDisputeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/disputes")
@RequiredArgsConstructor
public class AdminBookingDisputeController {

    private final AdminBookingDisputeService adminBookingDisputeService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getDisputes(HttpServletRequest request) {
        return ResponseEntity.ok(Map.of(
                "message", "Lấy danh sách khiếu nại booking thành công.",
                "result", adminBookingDisputeService.getDisputes(request)));
    }

    @PutMapping("/{bookingId}/resolve")
    public ResponseEntity<Map<String, Object>> resolveDispute(HttpServletRequest request,
            @PathVariable Long bookingId,
            @Valid @RequestBody AdminDisputeResolveRequest resolveRequest) {
        return ResponseEntity.ok(Map.of(
                "message", "Xử lý khiếu nại booking thành công.",
                "result", adminBookingDisputeService.resolveDispute(request, bookingId, resolveRequest)));
    }

}
