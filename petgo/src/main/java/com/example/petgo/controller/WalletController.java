package com.example.petgo.controller;

import com.example.petgo.dto.*;
import com.example.petgo.service.WalletService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/wallet")
@RequiredArgsConstructor
public class WalletController {
        private final WalletService walletService;

        @GetMapping("/me")
        public ResponseEntity<Map<String, Object>> myWallet(HttpServletRequest request) {
                return ResponseEntity.ok(
                                Map.of("message", "Lấy ví thành công.", "result", walletService.getMyWallet(request)));
        }

        @GetMapping("/transactions")
        public ResponseEntity<Map<String, Object>> myTransactions(HttpServletRequest request) {
                return ResponseEntity.ok(
                                Map.of("message", "Lấy lịch sử ví thành công.", "result",
                                                walletService.getMyTransactions(request)));
        }

        @PostMapping("/top-up")
        public ResponseEntity<Map<String, Object>> topUp(HttpServletRequest request,
                        @Valid @RequestBody WalletTopUpRequest topUpRequest) {
                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(Map.of("message",
                                                "Tạo yêu cầu nạp ví PayOS thành công. Tiền sẽ tự động được cộng vào ví sau khi PayOS xác nhận thanh toán.",
                                                "result", walletService.createTopUp(request, topUpRequest)));
        }

        @PostMapping("/top-up/{transactionId}/verify")
        public ResponseEntity<Map<String, Object>> verifyTopUp(HttpServletRequest request,
                        @PathVariable Long transactionId) {
                return ResponseEntity.ok(Map.of("message", "Đồng bộ trạng thái nạp ví thành công.", "result",
                                walletService.verifyTopUp(request, transactionId)));
        }

        @PostMapping("/transfer")
        public ResponseEntity<Map<String, Object>> transfer(HttpServletRequest request,
                        @Valid @RequestBody WalletTransferRequest transferRequest) {
                return ResponseEntity.ok(Map.of("message", "Chuyển tiền thành công.", "result",
                                walletService.transfer(request, transferRequest)));
        }

        @PostMapping("/withdraw")
        public ResponseEntity<Map<String, Object>> withdraw(HttpServletRequest request,
                        @Valid @RequestBody WalletWithdrawRequest withdrawRequest) {
                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(Map.of("message", "Tạo yêu cầu rút tiền thành công. Admin sẽ xác nhận thủ công.",
                                                "result",
                                                walletService.requestWithdraw(request, withdrawRequest)));
        }
}