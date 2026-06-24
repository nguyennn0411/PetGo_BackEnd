package com.example.petgo.controller;

import com.example.petgo.dto.UserSavedLocationRequest;
import com.example.petgo.dto.UserSavedLocationResponse;
import com.example.petgo.service.UserSavedLocationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/user/locations")
@RequiredArgsConstructor
public class UserSavedLocationController {

    private final UserSavedLocationService userSavedLocationService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getLocations(HttpServletRequest request) {
        List<UserSavedLocationResponse> locations = userSavedLocationService.getUserLocations(request);
        return ResponseEntity.ok(Map.of("message", "Lấy danh sách địa điểm thành công.", "result", locations));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createLocation(
            HttpServletRequest request,
            @Valid @RequestBody UserSavedLocationRequest req) {
        UserSavedLocationResponse loc = userSavedLocationService.createLocation(request, req);
        return ResponseEntity.ok(Map.of("message", "Lưu địa điểm thành công.", "result", loc));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateLocation(
            HttpServletRequest request,
            @PathVariable Long id,
            @Valid @RequestBody UserSavedLocationRequest req) {
        UserSavedLocationResponse loc = userSavedLocationService.updateLocation(request, id, req);
        return ResponseEntity.ok(Map.of("message", "Cập nhật địa điểm thành công.", "result", loc));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteLocation(
            HttpServletRequest request,
            @PathVariable Long id) {
        userSavedLocationService.deleteLocation(request, id);
        return ResponseEntity.ok(Map.of("message", "Xóa địa điểm thành công."));
    }
}
