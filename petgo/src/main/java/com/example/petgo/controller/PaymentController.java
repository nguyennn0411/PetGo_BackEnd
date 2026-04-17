package com.example.petgo.controller;

import com.example.petgo.dto.PaymentCheckoutContextResponse;
import com.example.petgo.dto.PaymentCheckoutRequest;
import com.example.petgo.dto.PaymentCheckoutResponse;
import com.example.petgo.service.PaymentService;
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
}
