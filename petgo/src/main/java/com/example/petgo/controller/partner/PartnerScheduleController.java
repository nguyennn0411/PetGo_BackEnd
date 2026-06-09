package com.example.petgo.controller.partner;

import com.example.petgo.dto.partner.PartnerScheduleResponse;
import com.example.petgo.dto.partner.PartnerScheduleExceptionRequest;
import com.example.petgo.dto.partner.PartnerScheduleExceptionResponse;
import com.example.petgo.dto.partner.PartnerWeeklyScheduleRequest;
import com.example.petgo.dto.BookingLockRequest;
import com.example.petgo.dto.BookingLockResponse;
import com.example.petgo.service.partner.PartnerScheduleService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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

    @PutMapping("/default")
    public ResponseEntity<PartnerScheduleResponse> updateDefaultSchedule(HttpServletRequest request,
            @Valid @RequestBody PartnerWeeklyScheduleRequest requestBody) {
        return ResponseEntity.ok(partnerScheduleService.updateWeeklySchedule(request, requestBody));
    }

    @GetMapping("/days")
    public ResponseEntity<List<PartnerScheduleExceptionResponse>> listDayOverrides(HttpServletRequest request,
            @RequestParam String from,
            @RequestParam String to) {
        return ResponseEntity.ok(partnerScheduleService.listDayOverrides(request, from, to));
    }

    @GetMapping("/days/{date}")
    public ResponseEntity<PartnerScheduleExceptionResponse> getDayOverride(HttpServletRequest request,
            @PathVariable String date) {
        return ResponseEntity.ok(partnerScheduleService.getDayOverride(request, date));
    }

    @PutMapping("/days/{date}")
    public ResponseEntity<PartnerScheduleExceptionResponse> upsertDayOverride(HttpServletRequest request,
            @PathVariable String date,
            @Valid @RequestBody PartnerScheduleExceptionRequest requestBody) {
        return ResponseEntity.ok(partnerScheduleService.upsertDayOverride(request, date, requestBody));
    }

    @DeleteMapping("/days/{date}/override")
    public ResponseEntity<Void> deleteDayOverride(HttpServletRequest request, @PathVariable String date) {
        partnerScheduleService.deleteDayOverride(request, date);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/copy-day")
    public ResponseEntity<List<PartnerScheduleExceptionResponse>> copyDay(HttpServletRequest request,
            @RequestBody Map<String, Object> body) {
        @SuppressWarnings("unchecked")
        List<String> targetDates = (List<String>) body.getOrDefault("targetDates", List.of());
        return ResponseEntity
                .ok(partnerScheduleService.copyDay(request, String.valueOf(body.get("sourceDate")), targetDates));
    }

    @PostMapping("/copy-month")
    public ResponseEntity<List<PartnerScheduleExceptionResponse>> copyMonth(HttpServletRequest request,
            @RequestBody Map<String, String> body) {
        return ResponseEntity
                .ok(partnerScheduleService.copyMonth(request, body.get("sourceMonth"), body.get("targetMonth")));
    }

    @PostMapping("/copy-year")
    public ResponseEntity<List<PartnerScheduleExceptionResponse>> copyYear(HttpServletRequest request,
            @RequestBody Map<String, String> body) {
        return ResponseEntity
                .ok(partnerScheduleService.copyYear(request, body.get("sourceYear"), body.get("targetYear")));
    }

    @GetMapping("/booking-locks")
    public ResponseEntity<List<BookingLockResponse>> listBookingLocks(HttpServletRequest request,
            @RequestParam(required = false, defaultValue = "true") boolean activeOnly) {
        return ResponseEntity.ok(partnerScheduleService.listBookingLocks(request, activeOnly));
    }

    @PostMapping("/booking-locks")
    public ResponseEntity<BookingLockResponse> createBookingLock(HttpServletRequest request,
            @Valid @RequestBody BookingLockRequest requestBody) {
        return ResponseEntity.ok(partnerScheduleService.createBookingLock(request, requestBody));
    }

    @PutMapping("/booking-locks/{id}/extend")
    public ResponseEntity<BookingLockResponse> extendBookingLock(HttpServletRequest request,
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, Integer> body) {
        return ResponseEntity.ok(partnerScheduleService.extendBookingLock(request, id,
                body != null ? body.get("durationMinutes") : null));
    }

    @PutMapping("/booking-locks/{id}/unlock")
    public ResponseEntity<BookingLockResponse> unlockBookingLock(HttpServletRequest request,
            @PathVariable Long id) {
        return ResponseEntity.ok(partnerScheduleService.unlockBookingLock(request, id));
    }

    @DeleteMapping("/booking-locks/{id}")
    public ResponseEntity<Void> deleteBookingLock(HttpServletRequest request, @PathVariable Long id) {
        partnerScheduleService.deleteBookingLock(request, id);
        return ResponseEntity.noContent().build();
    }
}