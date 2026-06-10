package com.example.petgo.controller.partner;

import com.example.petgo.dto.partner.PartnerCustomerDetailResponse;
import com.example.petgo.dto.partner.PartnerCustomerListResponse;
import com.example.petgo.service.partner.PartnerCustomerManagementService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/partner/customers")
@RequiredArgsConstructor
public class PartnerCustomerController {

    private final PartnerCustomerManagementService partnerCustomerManagementService;

    @GetMapping
    public ResponseEntity<PartnerCustomerListResponse> listCustomers(HttpServletRequest request,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "ALL") String status,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "12") Integer size) {
        return ResponseEntity.ok(partnerCustomerManagementService.listCustomers(request, keyword, status, page, size));
    }

    @GetMapping("/{customerUserId}")
    public ResponseEntity<PartnerCustomerDetailResponse> getCustomerDetail(HttpServletRequest request,
            @PathVariable Long customerUserId) {
        return ResponseEntity.ok(partnerCustomerManagementService.getCustomerDetail(request, customerUserId));
    }
}