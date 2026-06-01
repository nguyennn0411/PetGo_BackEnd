package com.example.petgo.service.impl;

import com.example.petgo.dto.PaymentCheckoutContextResponse;
import com.example.petgo.dto.PaymentCheckoutRequest;
import com.example.petgo.dto.PaymentCheckoutResponse;
import com.example.petgo.entity.*;
import com.example.petgo.exception.BadRequestException;
import com.example.petgo.exception.ResourceNotFoundException;
import com.example.petgo.repository.*;
import com.example.petgo.service.PaymentService;
import com.example.petgo.service.PromotionPolicyService;
import com.example.petgo.service.PromotionPolicyService.PromoPreview;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private static final ZoneId APP_ZONE = ZoneId.of("Asia/Ho_Chi_Minh");
    private static final DateTimeFormatter DATE_TIME_VIEW = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final List<String> SUPPORTED_METHODS = List.of("COD", "MOMO", "VNPAY", "CARD", "BANK_TRANSFER");

    private final BookingRepository bookingRepository;
    private final BookingStatusHistoryRepository bookingStatusHistoryRepository;
    private final InvoiceRepository invoiceRepository;
    private final InvoiceItemRepository invoiceItemRepository;
    private final PaymentRepository paymentRepository;
    private final PromotionPolicyService promotionPolicyService;

    @Override
    @Transactional(readOnly = true)
    public PaymentCheckoutContextResponse getCheckoutContext(Long bookingId, String promoCode) {
        Booking booking = bookingRepository.findDetailedById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy booking để thanh toán"));

        PromoPreview promoPreview = promotionPolicyService.previewForBooking(booking, promoCode);
        Invoice invoice = invoiceRepository.findByBookingId(bookingId).orElse(null);
        Payment payment = invoice != null
                ? paymentRepository.findTopByInvoiceIdOrderByCreatedAtDesc(invoice.getId()).orElse(null)
                : null;

        BigDecimal subtotal = defaultMoney(booking.getSubtotalAmount());
        BigDecimal tax = defaultMoney(booking.getTaxAmount());
        BigDecimal total = promotionPolicyService.calculateTotal(subtotal, promoPreview.discountAmount(), tax);

        return PaymentCheckoutContextResponse.builder()
                .bookingId(booking.getId())
                .bookingCode(booking.getBookingCode())
                .bookingStatus(booking.getStatus())
                .ownerUserId(booking.getCustomerUser() != null ? booking.getCustomerUser().getId() : null)
                .providerName(booking.getProviderNameSnapshot())
                .providerPhone(booking.getProviderPhoneSnapshot())
                .providerAddress(booking.getProviderAddressSnapshot())
                .serviceName(booking.getServiceNameSnapshot())
                .petName(buildPetLabel(booking))
                .appointmentDate(booking.getAppointmentDate() != null
                        ? booking.getAppointmentDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                        : null)
                .startTime(formatTimeRange(booking))
                .subtotalAmount(subtotal)
                .discountAmount(promoPreview.discountAmount())
                .taxAmount(tax)
                .totalAmount(total)
                .totalAmountDisplay(formatMoney(total))
                .currencyCode(firstNonBlank(booking.getCurrencyCode(), "VND"))
                .promoCode(promoPreview.appliedCode())
                .promoMessage(promoPreview.message())
                .invoiceId(invoice != null ? invoice.getId() : null)
                .invoiceNumber(invoice != null ? invoice.getInvoiceNumber() : null)
                .invoiceStatus(invoice != null ? invoice.getStatus() : null)
                .paymentStatus(payment != null ? payment.getStatus() : null)
                .paymentMethods(SUPPORTED_METHODS)
                .build();
    }

    @Override
    @Transactional
    public PaymentCheckoutResponse checkout(PaymentCheckoutRequest request) {
        Booking booking = bookingRepository.findDetailedById(request.bookingId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy booking để thanh toán"));

        if (!List.of("PENDING_PAYMENT", "PENDING_CONFIRMATION").contains(booking.getStatus())) {
            throw new BadRequestException("Booking hiện không ở trạng thái có thể thanh toán");
        }

        String paymentMethod = normalizePaymentMethod(request.paymentMethod());
        PromoPreview promoPreview = promotionPolicyService.previewForBooking(booking, request.promoCode());

        BigDecimal subtotal = defaultMoney(booking.getSubtotalAmount());
        BigDecimal tax = defaultMoney(booking.getTaxAmount());
        BigDecimal total = promotionPolicyService.calculateTotal(subtotal, promoPreview.discountAmount(), tax);

        booking.setPromoDiscountAmount(promoPreview.discountAmount());
        booking.setTaxAmount(tax);
        booking.setTotalAmount(total);
        String previousStatus = booking.getStatus();
        booking.setStatus("PENDING_CONFIRMATION");
        bookingRepository.save(booking);

        Invoice invoice = invoiceRepository.findByBookingId(booking.getId()).orElseGet(Invoice::new);
        boolean isNewInvoice = invoice.getId() == null;
        populateInvoice(invoice, booking, subtotal, promoPreview.discountAmount(), tax, total, paymentMethod);
        invoiceRepository.save(invoice);
        refreshInvoiceItems(invoice, booking, subtotal, promoPreview.discountAmount(), tax);

        Payment payment = new Payment();
        populatePayment(payment, invoice, booking.getCustomerUser(), paymentMethod, total, booking.getCurrencyCode());
        paymentRepository.save(payment);

        if (isOnlineSuccess(paymentMethod) || total.compareTo(BigDecimal.ZERO) == 0) {
            invoice.setStatus("PAID");
            invoice.setPaidAt(payment.getPaidAt());
            invoiceRepository.save(invoice);
        }

        promotionPolicyService.recordBookingRedemption(promoPreview, booking.getCustomerUser(), booking, invoice);

        if (!Objects.equals(previousStatus, booking.getStatus())) {
            BookingStatusHistory history = new BookingStatusHistory();
            history.setBooking(booking);
            history.setFromStatus(previousStatus);
            history.setToStatus(booking.getStatus());
            history.setChangedByUser(booking.getCustomerUser());
            history.setNote("Thanh toán checkout bằng " + paymentMethod);
            bookingStatusHistoryRepository.save(history);
        }

        return PaymentCheckoutResponse.builder()
                .bookingId(booking.getId())
                .bookingCode(booking.getBookingCode())
                .bookingStatus(booking.getStatus())
                .invoiceId(invoice.getId())
                .invoiceNumber(invoice.getInvoiceNumber())
                .invoiceStatus(invoice.getStatus())
                .paymentId(payment.getId())
                .paymentCode(payment.getPaymentCode())
                .paymentStatus(payment.getStatus())
                .paymentMethod(paymentMethod)
                .subtotalAmount(subtotal)
                .discountAmount(promoPreview.discountAmount())
                .taxAmount(tax)
                .totalAmount(total)
                .totalAmountDisplay(formatMoney(total))
                .currencyCode(firstNonBlank(booking.getCurrencyCode(), "VND"))
                .promoCode(promoPreview.appliedCode())
                .paidAt(payment.getPaidAt() != null ? payment.getPaidAt().format(DATE_TIME_VIEW) : null)
                .build();
    }

    private void populateInvoice(Invoice invoice,
            Booking booking,
            BigDecimal subtotal,
            BigDecimal discount,
            BigDecimal tax,
            BigDecimal total,
            String paymentMethod) {
        User user = booking.getCustomerUser();
        invoice.setInvoiceNumber(invoice.getInvoiceNumber() != null ? invoice.getInvoiceNumber() : generateCode("INV"));
        invoice.setUser(user);
        invoice.setBooking(booking);
        invoice.setInvoiceType("BOOKING");
        invoice.setStatus(isOnlineSuccess(paymentMethod) || total.compareTo(BigDecimal.ZERO) == 0 ? "PAID" : "ISSUED");
        invoice.setBillingName(firstNonBlank(user != null ? user.getFullName() : null, booking.getPetNameSnapshot(),
                "Khách hàng PetGo"));
        invoice.setBillingEmail(user != null ? user.getEmail() : null);
        invoice.setBillingPhone(user != null ? user.getPhoneNumber() : null);
        invoice.setBillingAddress(buildUserAddress(user));
        invoice.setSubtotalAmount(subtotal);
        invoice.setDiscountAmount(discount);
        invoice.setTaxAmount(tax);
        invoice.setTotalAmount(total);
        invoice.setCurrencyCode(firstNonBlank(booking.getCurrencyCode(), "VND"));
        invoice.setIssuedAt(invoice.getIssuedAt() != null ? invoice.getIssuedAt() : LocalDateTime.now(APP_ZONE));
        invoice.setDueAt(LocalDateTime.now(APP_ZONE).plusHours(24));
        invoice.setPaidAt(
                isOnlineSuccess(paymentMethod) || total.compareTo(BigDecimal.ZERO) == 0 ? LocalDateTime.now(APP_ZONE)
                        : null);
        invoice.setNote("Tạo từ checkout booking " + booking.getBookingCode());
    }

    private void refreshInvoiceItems(Invoice invoice,
            Booking booking,
            BigDecimal subtotal,
            BigDecimal discount,
            BigDecimal tax) {
        if (invoice.getId() != null) {
            invoiceItemRepository.deleteByInvoiceId(invoice.getId());
        }

        List<InvoiceItem> items = new ArrayList<>();

        InvoiceItem serviceItem = new InvoiceItem();
        serviceItem.setInvoice(invoice);
        serviceItem.setItemType("BOOKING_SERVICE");
        serviceItem.setItemName(firstNonBlank(booking.getServiceNameSnapshot(), "Dịch vụ PetGo"));
        serviceItem.setDescription(buildAppointmentDescription(booking));
        serviceItem.setQuantity(1);
        serviceItem.setUnitPrice(subtotal);
        serviceItem.setLineTotal(subtotal);
        serviceItem.setSortOrder(1);
        items.add(serviceItem);

        if (discount.compareTo(BigDecimal.ZERO) > 0) {
            InvoiceItem discountItem = new InvoiceItem();
            discountItem.setInvoice(invoice);
            discountItem.setItemType("DISCOUNT");
            discountItem.setItemName("Ưu đãi promo PetGo");
            discountItem.setDescription("Giảm giá cho booking thú cưng");
            discountItem.setQuantity(1);
            discountItem.setUnitPrice(discount.negate());
            discountItem.setLineTotal(discount.negate());
            discountItem.setSortOrder(2);
            items.add(discountItem);
        }

        if (tax.compareTo(BigDecimal.ZERO) > 0) {
            InvoiceItem taxItem = new InvoiceItem();
            taxItem.setInvoice(invoice);
            taxItem.setItemType("TAX");
            taxItem.setItemName("Thuế dịch vụ");
            taxItem.setDescription("Thuế VAT/thuế nền tảng");
            taxItem.setQuantity(1);
            taxItem.setUnitPrice(tax);
            taxItem.setLineTotal(tax);
            taxItem.setSortOrder(3);
            items.add(taxItem);
        }

        invoiceItemRepository.saveAll(items);
    }

    private void populatePayment(Payment payment,
            Invoice invoice,
            User payer,
            String paymentMethod,
            BigDecimal total,
            String currencyCode) {
        payment.setPaymentCode(generateCode("PAY"));
        payment.setInvoice(invoice);
        payment.setPayerUser(payer);
        payment.setAmount(total);
        payment.setCurrencyCode(firstNonBlank(currencyCode, "VND"));
        payment.setPaymentMethod(paymentMethod);
        payment.setGatewayName(resolveGatewayName(paymentMethod));
        payment.setGatewayTransactionId(generateCode("TXN"));
        payment.setStatus(
                isOnlineSuccess(paymentMethod) || total.compareTo(BigDecimal.ZERO) == 0 ? "SUCCEEDED" : "PENDING");
        payment.setPaidAt(
                isOnlineSuccess(paymentMethod) || total.compareTo(BigDecimal.ZERO) == 0 ? LocalDateTime.now(APP_ZONE)
                        : null);
        payment.setFailureReason(null);
        payment.setMetadataJson("{\"source\":\"petgo-checkout\"}");
    }

    private boolean isOnlineSuccess(String paymentMethod) {
        return List.of("MOMO", "VNPAY", "CARD", "BANK_TRANSFER").contains(paymentMethod);
    }

    private String normalizePaymentMethod(String rawValue) {
        String value = normalizeBlank(rawValue);
        if (value == null) {
            throw new BadRequestException("Vui lòng chọn phương thức thanh toán");
        }
        String normalized = value.trim().replace('-', '_').replace(' ', '_').toUpperCase(Locale.ROOT);
        if (!SUPPORTED_METHODS.contains(normalized)) {
            throw new BadRequestException("PetGo chưa hỗ trợ phương thức thanh toán này");
        }
        return normalized;
    }

    private String resolveGatewayName(String paymentMethod) {
        return switch (paymentMethod) {
            case "MOMO" -> "MoMo";
            case "VNPAY" -> "VNPay";
            case "CARD" -> "Card";
            case "BANK_TRANSFER" -> "Bank Transfer";
            default -> "COD";
        };
    }

    private String formatMoney(BigDecimal amount) {
        if (amount == null)
            return "0 đ";
        return String.format(Locale.forLanguageTag("vi-VN"), "%,.0f đ", amount);
    }

    private String formatTimeRange(Booking booking) {
        if (booking.getStartTime() == null)
            return null;
        String start = booking.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm"));
        if (booking.getEndTime() == null)
            return start;
        return start + " - " + booking.getEndTime().format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    private String buildPetLabel(Booking booking) {
        if (booking.getPetBreedSnapshot() != null && !booking.getPetBreedSnapshot().isBlank()) {
            return booking.getPetNameSnapshot() + " (" + booking.getPetBreedSnapshot() + ")";
        }
        return booking.getPetNameSnapshot();
    }

    private String buildAppointmentDescription(Booking booking) {
        String date = booking.getAppointmentDate() != null
                ? booking.getAppointmentDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                : "";
        String time = formatTimeRange(booking);
        return (date + (time != null ? " • " + time : "")).trim();
    }

    private String buildUserAddress(User user) {
        if (user == null)
            return null;
        List<String> parts = new ArrayList<>();
        addIfPresent(parts, user.getAddressLine1());
        addIfPresent(parts, user.getWard());
        addIfPresent(parts, user.getDistrict());
        addIfPresent(parts, user.getCity());
        addIfPresent(parts, user.getProvince());
        return parts.isEmpty() ? null : String.join(", ", parts);
    }

    private void addIfPresent(List<String> parts, String value) {
        if (value != null && !value.isBlank()) {
            parts.add(value.trim());
        }
    }

    private BigDecimal defaultMoney(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }

    private String normalizeBlank(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private String firstNonBlank(String... values) {
        if (values == null)
            return null;
        for (String value : values) {
            if (value != null && !value.isBlank())
                return value.trim();
        }
        return null;
    }

    private String generateCode(String prefix) {
        return prefix + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase(Locale.ROOT);
    }
}
