package com.example.petgo.controller;

import com.example.petgo.dto.InvoiceDetailResponse;
import com.example.petgo.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;

    @GetMapping("/{invoiceId}")
    public ResponseEntity<InvoiceDetailResponse> getInvoiceById(@PathVariable Long invoiceId) {
        return ResponseEntity.ok(invoiceService.getInvoiceById(invoiceId));
    }

    @GetMapping("/by-booking/{bookingId}")
    public ResponseEntity<InvoiceDetailResponse> getInvoiceByBookingId(@PathVariable Long bookingId) {
        return ResponseEntity.ok(invoiceService.getInvoiceByBookingId(bookingId));
    }
}
