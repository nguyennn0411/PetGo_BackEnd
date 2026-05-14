package com.example.petgo.controller.partner;

import com.example.petgo.dto.partner.PartnerScheduleResponse;
import com.example.petgo.dto.partner.PartnerWeeklyScheduleRequest;
import com.example.petgo.service.partner.PartnerScheduleService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/partner/schedule")
@RequiredArgsConstructor
public class PartnerScheduleController {

    private final PartnerScheduleService partnerScheduleService;

    @GetMapping
    public ResponseEntity<PartnerScheduleResponse> getSchedule(HttpServletRequest request,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to) {
        return ResponseEntity.ok(partnerScheduleService.getSchedule(request, from, to));
    }

    @PutMapping("/weekly")
    public ResponseEntity<PartnerScheduleResponse> updateWeeklySchedule(HttpServletRequest request,
            @Valid @RequestBody PartnerWeeklyScheduleRequest requestBody) {
        return ResponseEntity.ok(partnerScheduleService.updateWeeklySchedule(request, requestBody));
    }
}