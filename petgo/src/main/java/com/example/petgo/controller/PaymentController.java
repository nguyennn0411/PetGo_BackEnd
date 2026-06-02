package com.example.petgo.controller;

import com.example.petgo.dto.PaymentCheckoutContextResponse;
import com.example.petgo.dto.PaymentCheckoutRequest;
import com.example.petgo.dto.PaymentCheckoutResponse;
import com.example.petgo.dto.PaymentRequestDTO;
import com.example.petgo.dto.PaymentResponseDTO;
import com.example.petgo.service.PaymentService;
import com.example.petgo.service.PayOsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final PayOsService payOsService;

    // ─── Luồng checkout cũ (COD / mock) ───────────────────────────────────────

    @GetMapping("/checkout-context")
    public ResponseEntity<PaymentCheckoutContextResponse> getCheckoutContext(
            @RequestParam Long bookingId,
            @RequestParam(required = false) String promoCode
    ) {
        return ResponseEntity.ok(paymentService.getCheckoutContext(bookingId, promoCode));
    }

    @PostMapping("/checkout")
    public ResponseEntity<PaymentCheckoutResponse> checkout(@Valid @RequestBody PaymentCheckoutRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentService.checkout(request));
    }

    // ─── Luồng PayOS (VietQR / QR Code thực tế) ───────────────────────────────

    /**
     * Tạo link thanh toán PayOS và trả về QR Code.
     * Frontend dùng checkoutUrl để redirect hoặc qrImageUrl để hiển thị QR trực tiếp.
     *
     * Body: { invoiceId / bookingId / subscriptionId, paymentMethod: "PAYOS", returnUrl, cancelUrl }
     */
    @PostMapping("/payos/create")
    public ResponseEntity<PaymentResponseDTO> createPayOsPayment(@Valid @RequestBody PaymentRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(payOsService.createPayment(request));
    }

    /**
     * Đồng bộ và kiểm tra trạng thái thanh toán từ cổng PayOS.
     */
    @GetMapping("/payos/verify")
    public ResponseEntity<PaymentResponseDTO> verifyPayOsPayment(@RequestParam Long invoiceId) {
        return ResponseEntity.ok(payOsService.verifyPayment(invoiceId));
    }

    /**
     * Nhận callback webhook trực tiếp từ PayOS khi giao dịch thành công.
     */
    @PostMapping("/payos/webhook")
    public ResponseEntity<java.util.Map<String, Object>> handlePayOsWebhook(@RequestBody vn.payos.type.Webhook webhook) {
        payOsService.handleWebhook(webhook);
        java.util.Map<String, Object> response = new java.util.HashMap<>();
        response.put("success", true);
        response.put("message", "Webhook processed successfully");
        return ResponseEntity.ok(response);
    }
}
