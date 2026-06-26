package com.example.petgo.controller;

import com.example.petgo.dto.WalletAdminReviewRequest;
import com.example.petgo.dto.WalletAutoConfirmSettingRequest;
import com.example.petgo.dto.WalletStatusUpdateRequest;
import com.example.petgo.dto.WalletWithdrawRequest;
import com.example.petgo.service.WalletService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/wallet")
@RequiredArgsConstructor
public class AdminWalletController {
        private final WalletService walletService;

        @GetMapping("/pending-transactions")
        public ResponseEntity<Map<String, Object>> pending(HttpServletRequest request) {
                return ResponseEntity.ok(Map.of("message", "Lấy danh sách giao dịch ví chờ duyệt thành công.", "result",
                                walletService.getPendingAdminTransactions(request)));
        }

        @GetMapping("/failed-top-ups")
        public ResponseEntity<Map<String, Object>> failedTopUps(HttpServletRequest request) {
                return ResponseEntity
                                .ok(Map.of("message", "Lấy danh sách giao dịch nạp ví thất bại thành công.", "result",
                                                walletService.getFailedTopUpTransactions(request)));
        }

        @GetMapping("/system-wallet")
        public ResponseEntity<Map<String, Object>> systemWallet(HttpServletRequest request) {
                return ResponseEntity.ok(Map.of("message", "Lấy thông tin ví hệ thống thành công.", "result",
                                walletService.getSystemWallet(request)));
        }

        @GetMapping("/system-transactions")
        public ResponseEntity<Map<String, Object>> systemTransactions(HttpServletRequest request) {
                return ResponseEntity.ok(Map.of("message", "Lấy lịch sử giao dịch ví hệ thống thành công.", "result",
                                walletService.getSystemWalletTransactions(request)));
        }

        @PostMapping("/system-withdraw")
        public ResponseEntity<Map<String, Object>> systemWithdraw(HttpServletRequest request,
                        @Valid @RequestBody WalletWithdrawRequest withdrawRequest) {
                return ResponseEntity.ok(Map.of("message", "Rút tiền từ ví hệ thống thành công.", "result",
                                walletService.systemWithdraw(request, withdrawRequest)));
        }

        @PostMapping("/transactions/{transactionId}/review")
        public ResponseEntity<Map<String, Object>> review(HttpServletRequest request, @PathVariable Long transactionId,
                        @Valid @RequestBody WalletAdminReviewRequest reviewRequest) {
                return ResponseEntity.ok(Map.of("message", "Duyệt giao dịch ví thành công.", "result",
                                walletService.reviewAdminTransaction(request, transactionId, reviewRequest)));
        }

        @PostMapping("/failed-top-ups/{transactionId}/resolve")
        public ResponseEntity<Map<String, Object>> resolveFailedTopUp(HttpServletRequest request,
                        @PathVariable Long transactionId,
                        @Valid @RequestBody WalletAdminReviewRequest reviewRequest) {
                return ResponseEntity.ok(Map.of("message", "Xử lý giao dịch nạp ví thất bại thành công.", "result",
                                walletService.resolveFailedTopUp(request, transactionId, reviewRequest)));
        }

        @PatchMapping("/users/{userId}/status")
        public ResponseEntity<Map<String, Object>> updateWalletStatus(HttpServletRequest request,
                        @PathVariable Long userId,
                        @Valid @RequestBody WalletStatusUpdateRequest statusRequest) {
                return ResponseEntity.ok(Map.of("message", "Cập nhật trạng thái ví thành công.", "result",
                                walletService.updateWalletStatus(request, userId, statusRequest)));
        }

        @GetMapping("/settings/auto-confirm-top-up")
        public ResponseEntity<Map<String, Object>> getAutoConfirmTopUp(HttpServletRequest request) {
                return ResponseEntity.ok(Map.of("message", "Lấy cấu hình tự động cộng tiền ví thành công.", "result",
                                walletService.getAutoConfirmSetting(request)));
        }

        @PatchMapping("/settings/auto-confirm-top-up")
        public ResponseEntity<Map<String, Object>> updateAutoConfirmTopUp(HttpServletRequest request,
                        @Valid @RequestBody WalletAutoConfirmSettingRequest settingRequest) {
                return ResponseEntity
                                .ok(Map.of("message", "Cập nhật cấu hình tự động cộng tiền ví thành công.", "result",
                                                walletService.updateAutoConfirmSetting(request, settingRequest)));
        }
}