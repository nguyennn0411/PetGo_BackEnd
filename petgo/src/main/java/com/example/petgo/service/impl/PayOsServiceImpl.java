package com.example.petgo.service.impl;

import com.example.petgo.dto.PaymentRequestDTO;
import com.example.petgo.dto.PaymentResponseDTO;
import com.example.petgo.entity.Booking;
import com.example.petgo.entity.BookingStatusHistory;
import com.example.petgo.entity.Invoice;
import com.example.petgo.entity.Payment;
import com.example.petgo.entity.MembershipSubscription;
import com.example.petgo.entity.MembershipPlan;
import com.example.petgo.entity.ShopOrder;
import com.example.petgo.entity.ShopOrderStatusHistory;
import com.example.petgo.exception.BadRequestException;
import com.example.petgo.exception.ResourceNotFoundException;
import com.example.petgo.repository.BookingRepository;
import com.example.petgo.repository.BookingStatusHistoryRepository;
import com.example.petgo.repository.InvoiceRepository;
import com.example.petgo.repository.PaymentRepository;
import com.example.petgo.repository.MembershipSubscriptionRepository;
import com.example.petgo.repository.ShopOrderRepository;
import com.example.petgo.repository.ShopOrderStatusHistoryRepository;
import com.example.petgo.service.PayOsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.payos.PayOS;
import vn.payos.type.CheckoutResponseData;
import vn.payos.type.PaymentData;
import vn.payos.type.Webhook;
import vn.payos.type.WebhookData;
import vn.payos.type.PaymentLinkData;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PayOsServiceImpl implements PayOsService {

    private static final ZoneId APP_ZONE = ZoneId.of("Asia/Ho_Chi_Minh");
    private static final DateTimeFormatter DATE_TIME_VIEW = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final PayOS payOS;
    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final BookingStatusHistoryRepository bookingStatusHistoryRepository;
    private final MembershipSubscriptionRepository membershipSubscriptionRepository;
    private final ShopOrderRepository shopOrderRepository;
    private final ShopOrderStatusHistoryRepository shopOrderStatusHistoryRepository;

    @Override
    @Transactional
    public PaymentResponseDTO createPayment(PaymentRequestDTO request) {
        Invoice invoice = resolveInvoice(request);

        String invoiceStatus = invoice.getStatus();
        if ("PAID".equalsIgnoreCase(invoiceStatus)) {
            throw new BadRequestException("Hóa đơn này đã được thanh toán, không cần tạo link mới.");
        }
        if ("VOID".equalsIgnoreCase(invoiceStatus)) {
            throw new BadRequestException("Hóa đơn này đã bị hủy, không thể thanh toán.");
        }

        BigDecimal totalAmount = invoice.getTotalAmount() != null ? invoice.getTotalAmount() : BigDecimal.ZERO;
        if (totalAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Số tiền thanh toán phải lớn hơn 0.");
        }

        int amountInt = totalAmount.setScale(0, java.math.RoundingMode.HALF_UP).intValue();
        String description = buildDescription(invoice);

        String returnUrl = resolveUrl(request.returnUrl(), "http://localhost:5173/payment/success");
        String cancelUrl = resolveUrl(request.cancelUrl(), "http://localhost:5173/payment/cancel");

        int randomSuffix = 1000 + new java.util.Random().nextInt(9000);
        long orderCodeId = (invoice.getId() * 10000) + randomSuffix;

        String checkoutUrl = null;
        String paymentLinkId = null;
        String qrCodeText = null;

        try {
            // 1. Tự xây dựng chuỗi băm chiều đi đúng chuẩn tài liệu PayOS
            String signatureData = "amount=" + amountInt
                    + "&cancelUrl=" + cancelUrl
                    + "&description=" + description
                    + "&orderCode=" + orderCodeId
                    + "&returnUrl=" + returnUrl;

            // 2. Sử dụng Reflection lấy Key cấu hình an toàn từ Bean PayOS
            java.lang.reflect.Field fieldChecksum = payOS.getClass().getDeclaredField("checksumKey");
            java.lang.reflect.Field fieldId = payOS.getClass().getDeclaredField("clientId");
            java.lang.reflect.Field fieldKey = payOS.getClass().getDeclaredField("apiKey");
            fieldChecksum.setAccessible(true);
            fieldId.setAccessible(true);
            fieldKey.setAccessible(true);

            String currentChecksumKey = (String) fieldChecksum.get(payOS);
            String currentClientId = (String) fieldId.get(payOS);
            String currentApiKey = (String) fieldKey.get(payOS);

            String outboundSignature = generateHmacSha256(signatureData, currentChecksumKey);

            // 3. Đóng gói Payload
            java.util.Map<String, Object> requestBody = new java.util.HashMap<>();
            requestBody.put("orderCode", orderCodeId);
            requestBody.put("amount", amountInt);
            requestBody.put("description", description);
            requestBody.put("cancelUrl", cancelUrl);
            requestBody.put("returnUrl", returnUrl);
            requestBody.put("signature", outboundSignature);
            requestBody.put("items", new java.util.ArrayList<>());

            // 4. Thực thi gọi API trực tiếp
            org.springframework.web.client.RestTemplate restTemplate = new org.springframework.web.client.RestTemplate();
            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
            headers.set("x-client-id", currentClientId);
            headers.set("x-api-key", currentApiKey);

            org.springframework.http.HttpEntity<java.util.Map<String, Object>> entity = new org.springframework.http.HttpEntity<>(requestBody, headers);

            log.info("Bypass SDK: Đang gửi API trực tiếp tới cổng PayOS cho đơn hàng {}...", orderCodeId);
            org.springframework.http.ResponseEntity<java.util.Map> response = restTemplate.postForEntity(
                    "https://api-merchant.payos.vn/v2/payment-requests", entity, java.util.Map.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                java.util.Map<String, Object> resBody = response.getBody();
                String code = String.valueOf(resBody.get("code"));
                if ("00".equals(code)) {
                    java.util.Map<String, Object> data = (java.util.Map<String, Object>) resBody.get("data");
                    paymentLinkId = String.valueOf(data.get("paymentLinkId"));
                    checkoutUrl = String.valueOf(data.get("checkoutUrl"));
                    qrCodeText = String.valueOf(data.get("qrCode"));
                    log.info("Bypass thành công! Link thanh toán đã được khởi tạo: {}", checkoutUrl);
                } else {
                    throw new BadRequestException("Cổng PayOS từ chối: " + resBody.get("desc"));
                }
            } else {
                throw new BadRequestException("Không thể kết nối đến máy chủ PayOS.");
            }

        } catch (Exception e) {
            log.error("Lỗi tạo link thanh toán trực tiếp: {}", e.getMessage(), e);
            throw new BadRequestException("Không thể tạo link thanh toán PayOS. Vui lòng thử lại sau.");
        }

        String paymentCode = "PAY" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
        String currency = invoice.getCurrencyCode() != null ? invoice.getCurrencyCode() : "VND";

        // Lưu thông tin thanh toán vào DB của bạn
        Payment payment = new Payment();
        payment.setPaymentCode(paymentCode);
        payment.setInvoice(invoice);
        payment.setPayerUser(invoice.getUser());
        payment.setAmount(totalAmount);
        payment.setCurrencyCode(currency);
        payment.setPaymentMethod("BANK_TRANSFER");
        payment.setGatewayName("PayOS");
        payment.setGatewayTransactionId(paymentLinkId);
        payment.setStatus("PENDING");

        // Tạo chuỗi metadata thủ công, không dùng hàm buildMetadataJson(mockResponse) cũ nữa để tránh ép kiểu lỗi
        String customMetadataJson = "{\"paymentLinkId\":\"" + paymentLinkId + "\",\"checkoutUrl\":\"" + checkoutUrl + "\",\"orderCode\":" + orderCodeId + "}";
        payment.setMetadataJson(customMetadataJson);

        paymentRepository.save(payment);

        // Sinh link ảnh VietQR thủ công (Bypass qua hàm cũ)
        String customQrImageUrl = "https://img.vietqr.io/image/970416-" + (qrCodeText != null && qrCodeText.contains("accountNo=") ? qrCodeText.split("accountNo=")[1].split("&")[0] : "payos") + "-compact2.jpg?amount=" + amountInt + "&addInfo=" + description;

        return PaymentResponseDTO.builder()
                .invoiceId(invoice.getId())
                .invoiceNumber(invoice.getInvoiceNumber())
                .paymentCode(paymentCode)
                .amount(totalAmount)
                .currencyCode(currency)
                .status("PENDING")
                .paymentMethod("BANK_TRANSFER")
                .checkoutUrl(checkoutUrl)
                .qrCodeText(qrCodeText)
                .qrImageUrl(customQrImageUrl)
                .paymentLinkId(paymentLinkId)
                .createdAt(LocalDateTime.now(APP_ZONE).format(DATE_TIME_VIEW))
                .expiredAt(null)
                .bookingId(invoice.getBooking() != null ? invoice.getBooking().getId() : null)
                .subscriptionId(invoice.getMembershipSubscription() != null ? invoice.getMembershipSubscription().getId() : null)
                .build();
    }

    private Invoice resolveInvoice(PaymentRequestDTO request) {
        if (request.invoiceId() != null) {
            return invoiceRepository.findDetailedById(request.invoiceId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Không tìm thấy hóa đơn với ID: " + request.invoiceId()));
        }
        if (request.bookingId() != null) {
            return invoiceRepository.findByBookingId(request.bookingId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Không tìm thấy hóa đơn cho booking ID: " + request.bookingId()));
        }
        if (request.subscriptionId() != null) {
            return invoiceRepository
                    .findTopByMembershipSubscriptionIdOrderByCreatedAtDescIdDesc(request.subscriptionId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Không tìm thấy hóa đơn cho subscription ID: " + request.subscriptionId()));
        }
        throw new BadRequestException("Phải cung cấp invoiceId, bookingId hoặc subscriptionId để tạo link thanh toán.");
    }

    private String buildDescription(Invoice invoice) {
        // Sử dụng ID hóa đơn dạng số thuần túy để làm description
        // Điều này đảm bảo chuỗi KHÔNG BAO GIỜ chứa ký tự đặc biệt hay tiếng Việt ngầm làm lỗi chữ ký
        String raw = "PetGo " + invoice.getId();
        return raw.length() > 25 ? raw.substring(0, 25) : raw;
    }

    private String resolveUrl(String provided, String fallback) {
        return (provided != null && !provided.isBlank()) ? provided.trim() : fallback;
    }

    private String buildVietQrImageUrl(CheckoutResponseData data, int amount, String description) {
        try {
            String encodedDesc = URLEncoder.encode(description, StandardCharsets.UTF_8);
            String encodedName = URLEncoder.encode(
                    data.getAccountName() != null ? data.getAccountName() : "PETGO",
                    StandardCharsets.UTF_8);
            return String.format(
                    "https://img.vietqr.io/image/%s-%s-compact2.jpg?amount=%d&addInfo=%s&accountName=%s",
                    data.getBin(),
                    data.getAccountNumber(),
                    amount,
                    encodedDesc,
                    encodedName);
        } catch (Exception e) {
            log.warn("Cannot build VietQR image URL: {}", e.getMessage());
            return null;
        }
    }

    private String buildMetadataJson(CheckoutResponseData data) {
        return String.format(
                "{\"source\":\"petgo-payos\",\"paymentLinkId\":\"%s\",\"orderCode\":%s,\"bin\":\"%s\",\"accountNumber\":\"%s\",\"checkoutUrl\":\"%s\"}",
                data.getPaymentLinkId(),
                data.getOrderCode(),
                data.getBin(),
                data.getAccountNumber(),
                data.getCheckoutUrl());
    }

    @Override
    @Transactional
    public PaymentResponseDTO verifyPayment(Long invoiceId) {
        Invoice invoice = invoiceRepository.findDetailedById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hóa đơn với ID: " + invoiceId));

        Payment latestPayment = paymentRepository.findTopByInvoiceIdOrderByCreatedAtDesc(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông tin thanh toán cho hóa đơn: " + invoiceId));

        if ("PAID".equalsIgnoreCase(invoice.getStatus())) {
            return buildPaymentResponse(invoice, latestPayment);
        }

        try {
            // SỬA ĐỔI: Bóc tách chính xác mã orderCode phức hợp đã lưu trong metadata của lần thanh toán gần nhất
            long orderCode = extractOrderCodeFromMetadata(latestPayment.getMetadataJson(), invoice.getId());

            PaymentLinkData paymentLinkData = payOS.getPaymentLinkInformation(orderCode);
            log.info("PayOS status check — invoiceId={}, orderCode={}, status={}", invoiceId, orderCode, paymentLinkData.getStatus());

            if ("PAID".equalsIgnoreCase(paymentLinkData.getStatus())) {
                processSuccessfulPayment(invoice, latestPayment);
            } else if ("CANCELLED".equalsIgnoreCase(paymentLinkData.getStatus())) {
                processCancelledPayment(invoice, latestPayment);
            }
        } catch (Exception e) {
            log.error("PayOS status check failed — invoiceId={}: {}", invoiceId, e.getMessage(), e);
            throw new BadRequestException("Không thể xác nhận thanh toán từ PayOS: " + e.getMessage());
        }

        return buildPaymentResponse(invoice, latestPayment);
    }

    @Override
    @Transactional
    public void handleWebhook(Webhook webhook) {
        try {
            WebhookData webhookData = payOS.verifyPaymentWebhookData(webhook);
            log.info("PayOS webhook verified — orderCode={}, amount={}", webhookData.getOrderCode(), webhookData.getAmount());

            // SỬA ĐỔI: Giải mã ngược từ orderCode phức hợp về invoiceId gốc (chia nguyên cho 10000)
            long compositeOrderCode = webhookData.getOrderCode();
            Long invoiceId = compositeOrderCode / 10000;

            Invoice invoice = invoiceRepository.findDetailedById(invoiceId).orElse(null);
            if (invoice == null) {
                log.warn("Invoice not found for webhook orderCode: {} (decoded from {})", invoiceId, compositeOrderCode);
                return;
            }

            Payment payment = paymentRepository.findTopByInvoiceIdOrderByCreatedAtDesc(invoiceId).orElse(null);
            if (payment == null) {
                log.warn("Payment not found for webhook invoice ID: {}", invoiceId);
                return;
            }

            if (!"PAID".equalsIgnoreCase(invoice.getStatus())) {
                processSuccessfulPayment(invoice, payment);
            }

        } catch (Exception e) {
            log.error("PayOS webhook verification failed: {}", e.getMessage(), e);
            throw new BadRequestException("Xác thực chữ ký webhook thất bại: " + e.getMessage());
        }
    }

    private void processSuccessfulPayment(Invoice invoice, Payment payment) {
        LocalDateTime now = LocalDateTime.now(APP_ZONE);

        invoice.setStatus("PAID");
        invoice.setPaidAt(now);
        invoiceRepository.save(invoice);

        payment.setStatus("SUCCEEDED");
        payment.setPaidAt(now);
        paymentRepository.save(payment);

        if ("BOOKING".equalsIgnoreCase(invoice.getInvoiceType()) && invoice.getBooking() != null) {
            Booking booking = invoice.getBooking();
            if (!"PENDING_CONFIRMATION".equalsIgnoreCase(booking.getStatus()) && !"CONFIRMED".equalsIgnoreCase(booking.getStatus())) {
                String previousStatus = booking.getStatus();
                booking.setStatus("PENDING_CONFIRMATION");
                bookingRepository.save(booking);

                BookingStatusHistory history = new BookingStatusHistory();
                history.setBooking(booking);
                history.setFromStatus(previousStatus);
                history.setToStatus(booking.getStatus());
                history.setChangedByUser(invoice.getUser());
                history.setNote("Thanh toán thành công qua PayOS (VietQR)");
                bookingStatusHistoryRepository.save(history);
            }
        } else if ("MEMBERSHIP".equalsIgnoreCase(invoice.getInvoiceType()) && invoice.getMembershipSubscription() != null) {
            MembershipSubscription subscription = invoice.getMembershipSubscription();
            if (!"ACTIVE".equalsIgnoreCase(subscription.getStatus())) {
                MembershipPlan plan = subscription.getMembershipPlan();
                subscription.setStatus("ACTIVE");
                subscription.setStartedAt(now);
                subscription.setExpiresAt(calculateNextExpiry(now, plan != null ? plan.getBillingCycle() : "MONTHLY"));
                subscription.setNextBillingAt(subscription.getExpiresAt());
                membershipSubscriptionRepository.save(subscription);
            }
        } else if ("SHOP_ORDER".equalsIgnoreCase(invoice.getInvoiceType()) && invoice.getShopOrder() != null) {
            ShopOrder order = invoice.getShopOrder();
            if (!"PAID".equalsIgnoreCase(order.getStatus())) {
                order.setStatus("PAID");
                shopOrderRepository.save(order);

                ShopOrderStatusHistory history = new ShopOrderStatusHistory();
                history.setShopOrder(order);
                history.setFromStatus("PENDING_PAYMENT");
                history.setToStatus("PAID");
                history.setNote("Thanh toán thành công qua PayOS (VietQR)");
                shopOrderStatusHistoryRepository.save(history);
            }
        }
    }

    private void processCancelledPayment(Invoice invoice, Payment payment) {
        invoice.setStatus("VOID");
        invoiceRepository.save(invoice);

        payment.setStatus("FAILED");
        payment.setFailureReason("Thanh toán bị hủy bởi người dùng hoặc hệ thống PayOS.");
        paymentRepository.save(payment);

        if ("BOOKING".equalsIgnoreCase(invoice.getInvoiceType()) && invoice.getBooking() != null) {
            Booking booking = invoice.getBooking();
            if (!"CANCELLED".equalsIgnoreCase(booking.getStatus())) {
                booking.setStatus("PENDING_PAYMENT");
                bookingRepository.save(booking);
            }
        } else if ("MEMBERSHIP".equalsIgnoreCase(invoice.getInvoiceType()) && invoice.getMembershipSubscription() != null) {
            MembershipSubscription subscription = invoice.getMembershipSubscription();
            if (!"ACTIVE".equalsIgnoreCase(subscription.getStatus())) {
                subscription.setStatus("CANCELLED");
                subscription.setCancelReason("Hủy thanh toán PayOS");
                membershipSubscriptionRepository.save(subscription);
            }
        } else if ("SHOP_ORDER".equalsIgnoreCase(invoice.getInvoiceType()) && invoice.getShopOrder() != null) {
            ShopOrder order = invoice.getShopOrder();
            if (!"CANCELLED".equalsIgnoreCase(order.getStatus())) {
                order.setStatus("CANCELLED");
                shopOrderRepository.save(order);

                ShopOrderStatusHistory history = new ShopOrderStatusHistory();
                history.setShopOrder(order);
                history.setFromStatus("PENDING_PAYMENT");
                history.setToStatus("CANCELLED");
                history.setNote("Hủy thanh toán PayOS");
                shopOrderStatusHistoryRepository.save(history);
            }
        }
    }

    private LocalDateTime calculateNextExpiry(LocalDateTime base, String billingCycle) {
        String cycle = billingCycle != null ? billingCycle.trim().toUpperCase() : "MONTHLY";
        return switch (cycle) {
            case "YEARLY" -> base.plusYears(1);
            case "QUARTERLY" -> base.plusMonths(3);
            default -> base.plusMonths(1);
        };
    }

    private PaymentResponseDTO buildPaymentResponse(Invoice invoice, Payment payment) {
        String currency = invoice.getCurrencyCode() != null ? invoice.getCurrencyCode() : "VND";

        // TỐI ƯU HIỆU NĂNG: Vì Entity không có trường checkoutUrl, bóc trực tiếp từ metadataJson để tránh sinh lỗi ngầm
        String checkoutUrl = (payment != null) ? extractCheckoutUrlFromMetadata(payment.getMetadataJson()) : null;

        return PaymentResponseDTO.builder()
                .invoiceId(invoice.getId())
                .invoiceNumber(invoice.getInvoiceNumber())
                .paymentCode(payment != null ? payment.getPaymentCode() : null)
                .amount(invoice.getTotalAmount())
                .currencyCode(currency)
                .status(payment != null ? payment.getStatus() : "PENDING")
                .paymentMethod("BANK_TRANSFER")
                .checkoutUrl(checkoutUrl)
                .qrCodeText(null)
                .qrImageUrl(null)
                .paymentLinkId(payment != null ? payment.getGatewayTransactionId() : null)
                .createdAt(payment != null && payment.getCreatedAt() != null ? payment.getCreatedAt().format(DATE_TIME_VIEW) : LocalDateTime.now(APP_ZONE).format(DATE_TIME_VIEW))
                .expiredAt(null)
                .bookingId(invoice.getBooking() != null ? invoice.getBooking().getId() : null)
                .subscriptionId(invoice.getMembershipSubscription() != null ? invoice.getMembershipSubscription().getId() : null)
                .build();
    }

    private String extractCheckoutUrlFromMetadata(String metadataJson) {
        if (metadataJson == null || !metadataJson.contains("checkoutUrl")) {
            return null;
        }
        try {
            int startIndex = metadataJson.indexOf("\"checkoutUrl\":\"") + 15;
            int endIndex = metadataJson.indexOf("\"", startIndex);
            return metadataJson.substring(startIndex, endIndex);
        } catch (Exception e) {
            return null;
        }
    }

    // HELPER: Hàm lấy ngược trường orderCode dạng số Long nằm trong chuỗi metadataJson
    private long extractOrderCodeFromMetadata(String metadataJson, long fallbackId) {
        if (metadataJson == null || !metadataJson.contains("orderCode")) {
            return fallbackId * 10000;
        }
        try {
            int startIndex = metadataJson.indexOf("\"orderCode\":") + 12;
            int endIndex = metadataJson.indexOf(",", startIndex);
            if (endIndex == -1) {
                endIndex = metadataJson.indexOf("}", startIndex);
            }
            return Long.parseLong(metadataJson.substring(startIndex, endIndex).trim());
        } catch (Exception e) {
            return fallbackId * 10000;
        }
    }

    private String generateHmacSha256(String data, String key) {
        try {
            javax.crypto.Mac sha256HMAC = javax.crypto.Mac.getInstance("HmacSHA256");
            javax.crypto.spec.SecretKeySpec secretKey = new javax.crypto.spec.SecretKeySpec(key.getBytes(java.nio.charset.StandardCharsets.UTF_8), "HmacSHA256");
            sha256HMAC.init(secretKey);
            byte[] hash = sha256HMAC.doFinal(data.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Lỗi tạo chữ ký bảo mật", e);
        }
    }
}