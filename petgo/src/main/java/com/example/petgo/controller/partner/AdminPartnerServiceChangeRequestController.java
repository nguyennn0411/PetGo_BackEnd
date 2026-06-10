package com.example.petgo.controller.partner;

import com.example.petgo.dto.partner.AdminPartnerServiceReviewRequest;
import com.example.petgo.dto.partner.PartnerServiceChangeRequestResponse;
import com.example.petgo.service.partner.PartnerServiceChangeRequestService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/partner-service-requests")
@RequiredArgsConstructor
public class AdminPartnerServiceChangeRequestController {

    private final PartnerServiceChangeRequestService changeRequestService;

    @GetMapping
    public ResponseEntity<List<PartnerServiceChangeRequestResponse>> listRequests(HttpServletRequest request,
            @RequestParam(required = false, defaultValue = "PENDING_REVIEW") String status) {
        return ResponseEntity.ok(changeRequestService.listAdminRequests(request, status));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PartnerServiceChangeRequestResponse> getDetail(HttpServletRequest request,
            @PathVariable Long id) {
        return ResponseEntity.ok(changeRequestService.getAdminRequestDetail(request, id));
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<PartnerServiceChangeRequestResponse> approve(HttpServletRequest request,
            @PathVariable Long id,
            @RequestBody(required = false) AdminPartnerServiceReviewRequest requestBody) {
        return ResponseEntity.ok(changeRequestService.approve(request, id, requestBody));
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<PartnerServiceChangeRequestResponse> reject(HttpServletRequest request,
            @PathVariable Long id,
            @RequestBody(required = false) AdminPartnerServiceReviewRequest requestBody) {
        return ResponseEntity.ok(changeRequestService.reject(request, id, requestBody));
    }
}