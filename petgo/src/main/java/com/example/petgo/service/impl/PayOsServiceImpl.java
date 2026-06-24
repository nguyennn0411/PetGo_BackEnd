package com.example.petgo.service.impl;

import com.example.petgo.dto.PaymentRequestDTO;
import com.example.petgo.dto.PaymentResponseDTO;
import com.example.petgo.entity.Invoice;
import com.example.petgo.entity.Payment;
import com.example.petgo.entity.MembershipSubscription;
import com.example.petgo.entity.MembershipPlan;
import com.example.petgo.entity.ShopOrder;
import com.example.petgo.entity.ShopOrderStatusHistory;
import com.example.petgo.exception.BadRequestException;
import com.example.petgo.exception.ResourceNotFoundException;
import com.example.petgo.repository.InvoiceRepository;
import com.example.petgo.repository.PaymentRepository;
import com.example.petgo.repository.MembershipSubscriptionRepository;
import com.example.petgo.repository.ShopOrderRepository;
import com.example.petgo.repository.ShopOrderStatusHistoryRepository;
import com.example.petgo.service.PayOsService;
import com.example.petgo.service.WalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.payos.PayOS;
import vn.payos.type.PaymentLinkData;
import vn.payos.type.Webhook;
import vn.payos.type.WebhookData;

import java.math.BigDecimal;
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
    private final MembershipSubscriptionRepository membershipSubscriptionRepository;
    private final ShopOrderRepository shopOrderRepository;
    private final ShopOrderStatusHistoryRepository shopOrderStatusHistoryRepository;
    private final WalletService walletService;

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

        String returnUrl = resolveUrl(request.returnUrl(), "http://localhost:3000/payment/success");
        String cancelUrl = resolveUrl(request.cancelUrl(), "http://localhost:3000/payment/cancel");

        int randomSuffix = 1000 + new java.util.Random().nextInt(9000);
        long orderCodeId = (invoice.getId() * 10000) + randomSuffix;

        String checkoutUrl = null;
        String paymentLinkId = null;
        String qrCodeText = null;

        try {
            String signatureData = "amount=" + amountInt
                    + "&cancelUrl=" + cancelUrl
                    + "&description=" + description
                    + "&orderCode=" + orderCodeId
                    + "&returnUrl=" + returnUrl;

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

            java.util.Map<String, Object> requestBody = new java.util.HashMap<>();
            requestBody.put("orderCode", orderCodeId);
            requestBody.put("amount", amountInt);
            requestBody.put("description", description);
            requestBody.put("cancelUrl", cancelUrl);
            requestBody.put("returnUrl", returnUrl);
            requestBody.put("signature", outboundSignature);
            requestBody.put("items", new java.util.ArrayList<>());

            org.springframework.web.client.RestTemplate restTemplate = new org.springframework.web.client.RestTemplate();
            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
            headers.set("x-client-id", currentClientId);
            headers.set("x-api-key", currentApiKey);

            org.springframework.http.HttpEntity<java.util.Map<String, Object>> entity = new org.springframework.http.HttpEntity<>(
                    requestBody, headers);

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

        String customMetadataJson = "{\"paymentLinkId\":\"" + paymentLinkId + "\",\"checkoutUrl\":\"" + checkoutUrl
                + "\",\"orderCode\":" + orderCodeId + "}";
        payment.setMetadataJson(customMetadataJson);

        paymentRepository.save(payment);

        String customQrImageUrl = "https://img.vietqr.io/image/970416-"
                + (qrCodeText != null && qrCodeText.contains("accountNo=")
                        ? qrCodeText.split("accountNo=")[1].split("&")[0]
                        : "payos")
                + "-compact2.jpg?amount=" + amountInt + "&addInfo=" + description;

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
                .subscriptionId(
                        invoice.getMembershipSubscription() != null ? invoice.getMembershipSubscription().getId()
                                : null)
                .build();
    }

    private Invoice resolveInvoice(PaymentRequestDTO request) {
        if (request.invoiceId() != null) {
            return invoiceRepository.findDetailedById(request.invoiceId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Không tìm thấy hóa đơn với ID: " + request.invoiceId()));
        }
        if (request.subscriptionId() != null) {
            return invoiceRepository
                    .findTopByMembershipSubscriptionIdOrderByCreatedAtDescIdDesc(request.subscriptionId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Không tìm thấy hóa đơn cho subscription ID: " + request.subscriptionId()));
        }
        throw new BadRequestException("Phải cung cấp invoiceId hoặc subscriptionId để tạo link thanh toán.");
    }

    private String buildDescription(Invoice invoice) {
        String raw = "PetGo " + invoice.getId();
        return raw.length() > 25 ? raw.substring(0, 25) : raw;
    }

    private String resolveUrl(String provided, String fallback) {
        return (provided != null && !provided.isBlank()) ? provided.trim() : fallback;
    }

    private String buildVietQrImageUrl(Object data, int amount, String description) {
        try {
            String encodedDesc = java.net.URLEncoder.encode(description, java.nio.charset.StandardCharsets.UTF_8);
            return String.format(
                    "https://img.vietqr.io/image/970416-payos-compact2.jpg?amount=%d&addInfo=%s",
                    amount,
                    encodedDesc);
        } catch (Exception e) {
            log.warn("Cannot build VietQR image URL: {}", e.getMessage());
            return null;
        }
    }

    @Override
    @Transactional
    public PaymentResponseDTO verifyPayment(Long invoiceId) {
        Invoice invoice = invoiceRepository.findDetailedById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hóa đơn với ID: " + invoiceId));

        Payment latestPayment = paymentRepository.findTopByInvoiceIdOrderByCreatedAtDesc(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy thông tin thanh toán cho hóa đơn: " + invoiceId));

        if ("PAID".equalsIgnoreCase(invoice.getStatus())) {
            return buildPaymentResponse(invoice, latestPayment);
        }

        try {
            long orderCode = extractOrderCodeFromMetadata(latestPayment.getMetadataJson(), invoice.getId());

            PaymentLinkData paymentLinkData = payOS.getPaymentLinkInformation(orderCode);
            log.info("PayOS status check — invoiceId={}, orderCode={}, status={}", invoiceId, orderCode,
                    paymentLinkData.getStatus());

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
            log.info("PayOS webhook verified — orderCode={}, amount={}", webhookData.getOrderCode(),
                    webhookData.getAmount());

            long compositeOrderCode = webhookData.getOrderCode();
            walletService.handlePayOsWebhookOrderCode(compositeOrderCode);
            Long invoiceId = compositeOrderCode / 10000;

            Invoice invoice = invoiceRepository.findDetailedById(invoiceId).orElse(null);
            if (invoice == null) {
                log.warn("Invoice not found for webhook orderCode: {} (decoded from {})", invoiceId,
                        compositeOrderCode);
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

        if ("MEMBERSHIP".equalsIgnoreCase(invoice.getInvoiceType())
                && invoice.getMembershipSubscription() != null) {
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
            ShopOrder shopOrder = invoice.getShopOrder();
            String alreadyDone = shopOrder.getStatus();
            if (!"PAID".equalsIgnoreCase(alreadyDone)
                    && !"PACKING".equalsIgnoreCase(alreadyDone)
                    && !"SHIPPING".equalsIgnoreCase(alreadyDone)
                    && !"COMPLETED".equalsIgnoreCase(alreadyDone)) {
                shopOrder.setStatus("PAID");
                shopOrderRepository.save(shopOrder);

                ShopOrderStatusHistory history = new ShopOrderStatusHistory();
                history.setShopOrder(shopOrder);
                history.setFromStatus(alreadyDone);
                history.setToStatus("PAID");
                history.setChangedByUser(invoice.getUser());
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

        if ("MEMBERSHIP".equalsIgnoreCase(invoice.getInvoiceType())
                && invoice.getMembershipSubscription() != null) {
            MembershipSubscription subscription = invoice.getMembershipSubscription();
            if (!"ACTIVE".equalsIgnoreCase(subscription.getStatus())) {
                subscription.setStatus("CANCELLED");
                subscription.setCancelReason("Hủy thanh toán PayOS");
                membershipSubscriptionRepository.save(subscription);
            }
        } else if ("SHOP_ORDER".equalsIgnoreCase(invoice.getInvoiceType()) && invoice.getShopOrder() != null) {
            ShopOrder shopOrder = invoice.getShopOrder();
            if (!"CANCELLED".equalsIgnoreCase(shopOrder.getStatus()) && !"COMPLETED".equalsIgnoreCase(shopOrder.getStatus())) {
                shopOrder.setStatus("PENDING_PAYMENT");
                shopOrderRepository.save(shopOrder);
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
                .createdAt(payment != null && payment.getCreatedAt() != null
                        ? payment.getCreatedAt().format(DATE_TIME_VIEW)
                        : LocalDateTime.now(APP_ZONE).format(DATE_TIME_VIEW))
                .expiredAt(null)
                .subscriptionId(
                        invoice.getMembershipSubscription() != null ? invoice.getMembershipSubscription().getId()
                                : null)
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
            javax.crypto.spec.SecretKeySpec secretKey = new javax.crypto.spec.SecretKeySpec(
                    key.getBytes(java.nio.charset.StandardCharsets.UTF_8), "HmacSHA256");
            sha256HMAC.init(secretKey);
            byte[] hash = sha256HMAC.doFinal(data.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1)
                    hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Lỗi tạo chữ ký bảo mật", e);
        }
    }
}