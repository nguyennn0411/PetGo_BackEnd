package com.example.petgo.service.impl;

import com.example.petgo.dto.InvoiceDetailResponse;
import com.example.petgo.dto.InvoiceItemResponse;
import com.example.petgo.entity.Invoice;
import com.example.petgo.entity.InvoiceItem;
import com.example.petgo.entity.Payment;
import com.example.petgo.exception.ResourceNotFoundException;
import com.example.petgo.repository.InvoiceItemRepository;
import com.example.petgo.repository.InvoiceRepository;
import com.example.petgo.repository.PaymentRepository;
import com.example.petgo.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;


@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private static final ZoneId APP_ZONE = ZoneId.of("Asia/Ho_Chi_Minh");
    private static final DateTimeFormatter DATE_TIME_VIEW = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");


    private final InvoiceRepository invoiceRepository;
    private final InvoiceItemRepository invoiceItemRepository;
    private final PaymentRepository paymentRepository;

    @Override
    @Transactional(readOnly = true)
    public InvoiceDetailResponse getInvoiceById(Long invoiceId) {
        Invoice invoice = invoiceRepository.findDetailedById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy invoice"));
        return mapInvoiceDetail(invoice);
    }

    private InvoiceDetailResponse mapInvoiceDetail(Invoice invoice) {
        List<InvoiceItemResponse> items = invoiceItemRepository.findByInvoiceIdOrderBySortOrderAscIdAsc(invoice.getId()).stream()
                .map(this::mapItem)
                .toList();
        Payment payment = paymentRepository.findTopByInvoiceIdOrderByCreatedAtDesc(invoice.getId()).orElse(null);

        return InvoiceDetailResponse.builder()
                .invoiceId(invoice.getId())
                .invoiceNumber(invoice.getInvoiceNumber())
                .invoiceStatus(invoice.getStatus())
                .invoiceType(invoice.getInvoiceType())
                .issuedAt(invoice.getIssuedAt() != null ? invoice.getIssuedAt().atZone(APP_ZONE).format(DATE_TIME_VIEW) : null)
                .paidAt(invoice.getPaidAt() != null ? invoice.getPaidAt().atZone(APP_ZONE).format(DATE_TIME_VIEW) : null)
                .paymentId(payment != null ? payment.getId() : null)
                .paymentCode(payment != null ? payment.getPaymentCode() : null)
                .paymentMethod(payment != null ? payment.getPaymentMethod() : null)
                .paymentStatus(payment != null ? payment.getStatus() : null)
                .subtotalAmount(defaultMoney(invoice.getSubtotalAmount()))
                .discountAmount(defaultMoney(invoice.getDiscountAmount()))
                .taxAmount(defaultMoney(invoice.getTaxAmount()))
                .totalAmount(defaultMoney(invoice.getTotalAmount()))
                .currencyCode(invoice.getCurrencyCode())
                .totalAmountDisplay(formatMoney(invoice.getTotalAmount()))
                .items(items)
                .build();
    }

    private InvoiceItemResponse mapItem(InvoiceItem item) {
        return InvoiceItemResponse.builder()
                .itemType(item.getItemType())
                .itemName(item.getItemName())
                .description(item.getDescription())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .lineTotal(item.getLineTotal())
                .build();
    }

    private BigDecimal defaultMoney(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }

    private String formatMoney(BigDecimal amount) {
        if (amount == null) return "0 đ";
        return String.format(Locale.forLanguageTag("vi-VN"), "%,.0f đ", amount);
    }
}
