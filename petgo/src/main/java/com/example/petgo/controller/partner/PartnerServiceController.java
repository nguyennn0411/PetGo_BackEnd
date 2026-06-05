package com.example.petgo.controller.partner;

import com.example.petgo.dto.partner.PartnerServiceRequest;
import com.example.petgo.dto.partner.PartnerServiceResponse;
import com.example.petgo.dto.partner.PartnerServiceStatusRequest;
import com.example.petgo.service.partner.PartnerServiceManagementService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/partner/services")
@RequiredArgsConstructor
public class PartnerServiceController {

    private final PartnerServiceManagementService partnerServiceManagementService;

    @GetMapping
    public ResponseEntity<List<PartnerServiceResponse>> listServices(HttpServletRequest request) {
        return ResponseEntity.ok(partnerServiceManagementService.listServices(request));
    }

    @PostMapping
    public ResponseEntity<PartnerServiceResponse> createService(HttpServletRequest request,
            @Valid @RequestBody PartnerServiceRequest requestBody) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(partnerServiceManagementService.createService(request, requestBody));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PartnerServiceResponse> updateService(HttpServletRequest request,
            @PathVariable Long id,
            @Valid @RequestBody PartnerServiceRequest requestBody) {
        return ResponseEntity.ok(partnerServiceManagementService.updateService(request, id, requestBody));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<PartnerServiceResponse> updateServiceStatus(HttpServletRequest request,
            @PathVariable Long id,
            @Valid @RequestBody PartnerServiceStatusRequest requestBody) {
        return ResponseEntity
                .ok(partnerServiceManagementService.updateServiceStatus(request, id, requestBody.active()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<PartnerServiceResponse> archiveService(HttpServletRequest request, @PathVariable Long id) {
        return ResponseEntity.ok(partnerServiceManagementService.archiveService(request, id));
    }
}