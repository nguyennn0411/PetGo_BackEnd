package com.example.petgo.controller.partner;

import com.example.petgo.dto.partner.PartnerReviewListResponse;
import com.example.petgo.service.partner.PartnerReviewManagementService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/partner/reviews")
@RequiredArgsConstructor
public class PartnerReviewController {

    private final PartnerReviewManagementService partnerReviewManagementService;

    @GetMapping
    public ResponseEntity<PartnerReviewListResponse> listReviews(HttpServletRequest request,
            @RequestParam(required = false) Integer rating,
            @RequestParam(required = false) Long serviceId,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "12") Integer size) {
        return ResponseEntity.ok(partnerReviewManagementService.listReviews(request, rating, serviceId, from, to,
                keyword, page, size));
    }
}