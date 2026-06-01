package com.example.petgo.service;

import com.example.petgo.dto.PaymentRequestDTO;
import com.example.petgo.dto.PaymentResponseDTO;

public interface PayOsService {
    PaymentResponseDTO createPayment(PaymentRequestDTO request);
    PaymentResponseDTO verifyPayment(Long invoiceId);
    void handleWebhook(vn.payos.type.Webhook webhook);
}
