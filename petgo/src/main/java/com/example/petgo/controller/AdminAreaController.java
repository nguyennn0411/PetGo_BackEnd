package com.example.petgo.controller;

import com.example.petgo.dto.*;
import com.example.petgo.service.AdminAreaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/areas")
@RequiredArgsConstructor
public class AdminAreaController {

    private final AdminAreaService adminAreaService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllAreas() {
        return ResponseEntity.ok(Map.of(
                "message", "Lấy danh sách khu vực thành công.",
                "result", adminAreaService.getAllAreas()));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createArea(@Valid @RequestBody AreaRequest request) {
        return ResponseEntity.ok(Map.of(
                "message", "Tạo khu vực thành công.",
                "result", adminAreaService.createArea(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateArea(@PathVariable Long id, @Valid @RequestBody AreaRequest request) {
        return ResponseEntity.ok(Map.of(
                "message", "Cập nhật khu vực thành công.",
                "result", adminAreaService.updateArea(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteArea(@PathVariable Long id) {
        adminAreaService.deleteArea(id);
        return ResponseEntity.ok(Map.of("message", "Xóa khu vực thành công."));
    }

    @GetMapping("/{areaId}/services")
    public ResponseEntity<Map<String, Object>> getServiceConfigs(@PathVariable Long areaId) {
        return ResponseEntity.ok(Map.of(
                "message", "Lấy danh sách cấu hình dịch vụ thành công.",
                "result", adminAreaService.getAreaServiceConfigs(areaId)));
    }

    @PostMapping("/{areaId}/services")
    public ResponseEntity<Map<String, Object>> addServiceConfig(@PathVariable Long areaId,
                                                                  @Valid @RequestBody AreaServiceConfigRequest request) {
        return ResponseEntity.ok(Map.of(
                "message", "Thêm cấu hình dịch vụ thành công.",
                "result", adminAreaService.addAreaServiceConfig(areaId, request)));
    }

    @PutMapping("/{areaId}/services/{configId}")
    public ResponseEntity<Map<String, Object>> updateServiceConfig(@PathVariable Long areaId,
                                                                     @PathVariable Long configId,
                                                                     @Valid @RequestBody AreaServiceConfigRequest request) {
        return ResponseEntity.ok(Map.of(
                "message", "Cập nhật cấu hình dịch vụ thành công.",
                "result", adminAreaService.updateAreaServiceConfig(areaId, configId, request)));
    }

    @DeleteMapping("/{areaId}/services/{configId}")
    public ResponseEntity<Map<String, Object>> removeServiceConfig(@PathVariable Long areaId,
                                                                    @PathVariable Long configId) {
        adminAreaService.removeAreaServiceConfig(areaId, configId);
        return ResponseEntity.ok(Map.of("message", "Xóa cấu hình dịch vụ thành công."));
    }

    @GetMapping("/{areaId}/schedule")
    public ResponseEntity<Map<String, Object>> getSchedules(@PathVariable Long areaId) {
        return ResponseEntity.ok(Map.of(
                "message", "Lấy lịch làm việc thành công.",
                "result", adminAreaService.getAreaSchedules(areaId)));
    }

    @PutMapping("/{areaId}/schedule")
    public ResponseEntity<Map<String, Object>> updateSchedules(@PathVariable Long areaId,
                                                                @Valid @RequestBody List<AreaScheduleRequest> requests) {
        return ResponseEntity.ok(Map.of(
                "message", "Cập nhật lịch làm việc thành công.",
                "result", adminAreaService.updateAreaSchedules(areaId, requests)));
    }

    @GetMapping("/{areaId}/schedule/overrides")
    public ResponseEntity<Map<String, Object>> getOverrides(@PathVariable Long areaId,
                                                             @RequestParam(required = false) String from,
                                                             @RequestParam(required = false) String to) {
        return ResponseEntity.ok(Map.of(
                "message", "Lấy danh sách override thành công.",
                "result", adminAreaService.getAreaScheduleOverrides(areaId, from, to)));
    }

    @PutMapping("/{areaId}/schedule/overrides")
    public ResponseEntity<Map<String, Object>> upsertOverride(@PathVariable Long areaId,
                                                               @Valid @RequestBody AreaScheduleOverrideRequest request) {
        return ResponseEntity.ok(Map.of(
                "message", "Cập nhật lịch override thành công.",
                "result", adminAreaService.upsertAreaScheduleOverride(areaId, request)));
    }

    @DeleteMapping("/{areaId}/schedule/overrides/{date}")
    public ResponseEntity<Map<String, Object>> deleteOverride(@PathVariable Long areaId,
                                                               @PathVariable String date) {
        adminAreaService.deleteAreaScheduleOverride(areaId, date);
        return ResponseEntity.ok(Map.of("message", "Xóa lịch override thành công."));
    }

    @GetMapping("/{areaId}/shipping-fees")
    public ResponseEntity<Map<String, Object>> getShippingFees(@PathVariable Long areaId) {
        return ResponseEntity.ok(Map.of(
                "message", "Lấy danh sách phí vận chuyển thành công.",
                "result", adminAreaService.getShippingFeeConfigs(areaId)));
    }

    @PostMapping("/{areaId}/shipping-fees")
    public ResponseEntity<Map<String, Object>> addShippingFee(@PathVariable Long areaId,
                                                               @Valid @RequestBody ShippingFeeConfigRequest request) {
        return ResponseEntity.ok(Map.of(
                "message", "Thêm cấu hình phí vận chuyển thành công.",
                "result", adminAreaService.addShippingFeeConfig(areaId, request)));
    }

    @PutMapping("/{areaId}/shipping-fees/{configId}")
    public ResponseEntity<Map<String, Object>> updateShippingFee(@PathVariable Long areaId,
                                                                  @PathVariable Long configId,
                                                                  @Valid @RequestBody ShippingFeeConfigRequest request) {
        return ResponseEntity.ok(Map.of(
                "message", "Cập nhật cấu hình phí vận chuyển thành công.",
                "result", adminAreaService.updateShippingFeeConfig(areaId, configId, request)));
    }

    @DeleteMapping("/{areaId}/shipping-fees/{configId}")
    public ResponseEntity<Map<String, Object>> deleteShippingFee(@PathVariable Long areaId,
                                                                  @PathVariable Long configId) {
        adminAreaService.deleteShippingFeeConfig(areaId, configId);
        return ResponseEntity.ok(Map.of("message", "Xóa cấu hình phí vận chuyển thành công."));
    }
}
