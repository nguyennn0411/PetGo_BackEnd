package com.example.petgo.controller;

import com.example.petgo.dto.PaymentRequestDTO;
import com.example.petgo.dto.PaymentResponseDTO;
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

    private final PayOsService payOsService;

    @PostMapping("/payos/create")
    public ResponseEntity<PaymentResponseDTO> createPayOsPayment(@Valid @RequestBody PaymentRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(payOsService.createPayment(request));
    }

    @GetMapping("/payos/verify")
    public ResponseEntity<PaymentResponseDTO> verifyPayOsPayment(@RequestParam Long invoiceId) {
        return ResponseEntity.ok(payOsService.verifyPayment(invoiceId));
    }

    @PostMapping("/payos/webhook")
    public ResponseEntity<java.util.Map<String, Object>> handlePayOsWebhook(
            @RequestBody vn.payos.type.Webhook webhook) {
        payOsService.handleWebhook(webhook);
        java.util.Map<String, Object> response = new java.util.HashMap<>();
        response.put("success", true);
        response.put("message", "Webhook processed successfully");
        return ResponseEntity.ok(response);
    }
}
