package com.example.petgo.service;

import com.example.petgo.dto.PaymentCheckoutContextResponse;
import com.example.petgo.dto.PaymentCheckoutRequest;
import com.example.petgo.dto.PaymentCheckoutResponse;

public interface PaymentService {
    PaymentCheckoutContextResponse getCheckoutContext(Long bookingId, String promoCode);
    PaymentCheckoutResponse checkout(PaymentCheckoutRequest request);
}
