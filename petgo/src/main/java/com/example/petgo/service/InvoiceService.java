package com.example.petgo.service;

import com.example.petgo.dto.InvoiceDetailResponse;

public interface InvoiceService {
    InvoiceDetailResponse getInvoiceById(Long invoiceId);
}
