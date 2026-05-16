package com.example.petgo.service.impl;

import com.example.petgo.config.AuthenticatedUser;
import com.example.petgo.dto.*;
import com.example.petgo.entity.*;
import com.example.petgo.exception.BadRequestException;
import com.example.petgo.exception.ResourceNotFoundException;
import com.example.petgo.repository.*;
import com.example.petgo.service.AuthService;
import com.example.petgo.service.MembershipService;
import com.example.petgo.service.PromotionPolicyService;
import com.example.petgo.service.PromotionPolicyService.PromoPreview;
import jakarta.servlet.http.HttpServletRequest;
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
public class MembershipServiceImpl implements MembershipService {

    private static final ZoneId APP_ZONE = ZoneId.of("Asia/Ho_Chi_Minh");
    private static final DateTimeFormatter DATE_TIME_VIEW = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final List<String> CHECKOUT_PAYMENT_METHODS = List.of("MOMO", "VNPAY", "CARD", "BANK_TRANSFER");
    private static final List<String> CURRENT_MEMBERSHIP_STATUSES = List.of("ACTIVE", "PENDING_PAYMENT", "PAST_DUE");

    private final MembershipPlanRepository membershipPlanRepository;
    private final MembershipSubscriptionRepository membershipSubscriptionRepository;
    private final InvoiceRepository invoiceRepository;
    private final InvoiceItemRepository invoiceItemRepository;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final AuthService authService;
    private final PromotionPolicyService promotionPolicyService;

    @Override
    @Transactional(readOnly = true)
    public MembershipPlansResponse getPlans() {
        List<MembershipPlanCardResponse> plans = membershipPlanRepository
                .findByActiveTrueOrderByPopularDescSortOrderAscIdAsc()
                .stream()
                .map(plan -> mapPlan(plan, false))
                .toList();

        return MembershipPlansResponse.builder()
                .plans(plans)
                .currentSubscription(null)
                .build();
    }

    @Override
    @Transactional
    public MembershipSubscriptionResponse getMyMembership(HttpServletRequest request) {
        User user = requireCurrentUser(request);
        MembershipSubscription subscription = membershipSubscriptionRepository
                .findTopByUser_IdOrderByCreatedAtDescIdDesc(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Bạn chưa có gói membership nào."));

        normalizeStatusIfExpired(subscription);
        return mapSubscription(subscription);
    }

    @Override
    @Transactional
    public MembershipCheckoutContextResponse getCheckoutContext(HttpServletRequest request, String planSlug,
            String promoCode) {
        User user = requireCurrentUser(request);
        MembershipPlan plan = findActivePlan(planSlug);
        MembershipSubscription currentSubscription = findCurrentMembership(user.getId()).orElse(null);

        PromoPreview promoPreview = promotionPolicyService.previewForMembership(user, plan, promoCode);
        BigDecimal subtotal = defaultMoney(plan.getPriceAmount());
        BigDecimal discount = promoPreview.discountAmount();
        BigDecimal tax = BigDecimal.ZERO;
        BigDecimal total = promotionPolicyService.calculateTotal(subtotal, discount, tax);

        return MembershipCheckoutContextResponse.builder()
                .plan(mapPlan(plan,
                        currentSubscription != null && currentSubscription.getMembershipPlan() != null
                                && currentSubscription.getMembershipPlan().getId().equals(plan.getId())))
                .currentSubscription(currentSubscription != null ? mapSubscription(currentSubscription) : null)
                .subtotalAmount(subtotal)
                .discountAmount(discount)
                .taxAmount(tax)
                .totalAmount(total)
                .totalAmountDisplay(formatMoney(total))
                .currencyCode(firstNonBlank(plan.getCurrencyCode(), "VND"))
                .promoCode(promoPreview.appliedCode())
                .promoMessage(promoPreview.message())
                .autoRenewDefault(true)
                .paymentMethods(CHECKOUT_PAYMENT_METHODS)
                .build();
    }

    @Override
    @Transactional
    public MembershipCheckoutResponse checkout(HttpServletRequest request, MembershipCheckoutRequest requestBody) {
        User user = requireCurrentUser(request);
        MembershipPlan plan = findActivePlan(requestBody.planSlug());
        String paymentMethod = normalizePaymentMethod(requestBody.paymentMethod());
        boolean autoRenew = requestBody.autoRenew() == null || requestBody.autoRenew();

        PromoPreview promoPreview = promotionPolicyService.previewForMembership(user, plan, requestBody.promoCode());
        BigDecimal subtotal = defaultMoney(plan.getPriceAmount());
        BigDecimal discount = promoPreview.discountAmount();
        BigDecimal tax = BigDecimal.ZERO;
        BigDecimal total = promotionPolicyService.calculateTotal(subtotal, discount, tax);

        MembershipSubscription subscription = upsertSubscriptionForCheckout(user, plan, autoRenew);

        Invoice invoice = new Invoice();
        invoice.setInvoiceNumber(generateCode("INV"));
        invoice.setUser(user);
        invoice.setBooking(null);
        invoice.setMembershipSubscription(subscription);
        invoice.setInvoiceType("MEMBERSHIP");
        invoice.setStatus("PAID");
        invoice.setBillingName(firstNonBlank(user.getFullName(), "Khách hàng PetGo"));
        invoice.setBillingEmail(user.getEmail());
        invoice.setBillingPhone(user.getPhoneNumber());
        invoice.setBillingAddress(buildUserAddress(user));
        invoice.setSubtotalAmount(subtotal);
        invoice.setDiscountAmount(discount);
        invoice.setTaxAmount(tax);
        invoice.setTotalAmount(total);
        invoice.setCurrencyCode(firstNonBlank(plan.getCurrencyCode(), "VND"));
        invoice.setIssuedAt(LocalDateTime.now(APP_ZONE));
        invoice.setDueAt(LocalDateTime.now(APP_ZONE));
        invoice.setPaidAt(LocalDateTime.now(APP_ZONE));
        invoice.setNote("Thanh toán membership plan " + plan.getPlanCode());
        invoiceRepository.save(invoice);

        refreshInvoiceItems(invoice, plan, subtotal, discount, tax);

        Payment payment = new Payment();
        payment.setPaymentCode(generateCode("PAY"));
        payment.setInvoice(invoice);
        payment.setPayerUser(user);
        payment.setAmount(total);
        payment.setCurrencyCode(firstNonBlank(plan.getCurrencyCode(), "VND"));
        payment.setPaymentMethod(paymentMethod);
        payment.setGatewayName(resolveGatewayName(paymentMethod));
        payment.setGatewayTransactionId(generateCode("TXN"));
        payment.setStatus("SUCCEEDED");
        payment.setPaidAt(LocalDateTime.now(APP_ZONE));
        payment.setMetadataJson("{\"source\":\"petgo-membership-checkout\"}");
        paymentRepository.save(payment);

        promotionPolicyService.recordMembershipRedemption(promoPreview, user, subscription, invoice);

        return MembershipCheckoutResponse.builder()
                .subscriptionId(subscription.getId())
                .subscriptionCode(subscription.getSubscriptionCode())
                .subscriptionStatus(subscription.getStatus())
                .invoiceId(invoice.getId())
                .invoiceNumber(invoice.getInvoiceNumber())
                .invoiceStatus(invoice.getStatus())
                .paymentId(payment.getId())
                .paymentCode(payment.getPaymentCode())
                .paymentStatus(payment.getStatus())
                .paymentMethod(paymentMethod)
                .subtotalAmount(subtotal)
                .discountAmount(discount)
                .taxAmount(tax)
                .totalAmount(total)
                .totalAmountDisplay(formatMoney(total))
                .currencyCode(firstNonBlank(plan.getCurrencyCode(), "VND"))
                .promoCode(promoPreview.appliedCode())
                .currentSubscription(mapSubscription(subscription, invoice))
                .build();
    }

    @Override
    @Transactional
    public MembershipSubscriptionResponse cancelAutoRenew(HttpServletRequest request,
            MembershipCancelRequest requestBody) {
        User user = requireCurrentUser(request);
        MembershipSubscription subscription = findCurrentMembership(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Bạn chưa có gói membership đang hoạt động."));

        normalizeStatusIfExpired(subscription);
        if (!"ACTIVE".equalsIgnoreCase(subscription.getStatus())
                && !"PAST_DUE".equalsIgnoreCase(subscription.getStatus())) {
            throw new BadRequestException("Không thể hủy gia hạn cho subscription hiện tại.");
        }

        subscription.setAutoRenew(false);
        if (subscription.getCancelledAt() == null) {
            subscription.setCancelledAt(LocalDateTime.now(APP_ZONE));
        }
        String reason = requestBody != null ? normalizeBlank(requestBody.reason()) : null;
        subscription.setCancelReason(
                firstNonBlank(reason, subscription.getCancelReason(), "Người dùng tắt tự động gia hạn"));
        membershipSubscriptionRepository.save(subscription);

        return mapSubscription(subscription);
    }

    private MembershipSubscription upsertSubscriptionForCheckout(User user, MembershipPlan plan, boolean autoRenew) {
        Optional<MembershipSubscription> currentOpt = findCurrentMembership(user.getId());
        LocalDateTime now = LocalDateTime.now(APP_ZONE);

        if (currentOpt.isPresent()) {
            MembershipSubscription current = currentOpt.get();
            normalizeStatusIfExpired(current);

            if ("ACTIVE".equalsIgnoreCase(current.getStatus()) && current.getMembershipPlan() != null
                    && current.getMembershipPlan().getId().equals(plan.getId())) {
                LocalDateTime base = current.getExpiresAt() != null && current.getExpiresAt().isAfter(now)
                        ? current.getExpiresAt()
                        : now;
                current.setStatus("ACTIVE");
                current.setAutoRenew(autoRenew);
                current.setStartedAt(firstNonNull(current.getStartedAt(), now));
                current.setExpiresAt(calculateNextExpiry(base, plan.getBillingCycle()));
                current.setNextBillingAt(current.getExpiresAt());
                current.setCancelledAt(null);
                current.setCancelReason(null);
                return membershipSubscriptionRepository.save(current);
            }

            if ("ACTIVE".equalsIgnoreCase(current.getStatus()) || "PAST_DUE".equalsIgnoreCase(current.getStatus())) {
                current.setAutoRenew(false);
                current.setCancelledAt(now);
                current.setCancelReason("Chuyển sang gói " + plan.getName());
                current.setStatus("CANCELLED");
                membershipSubscriptionRepository.save(current);
            }
        }

        MembershipSubscription created = new MembershipSubscription();
        created.setSubscriptionCode(generateCode("SUB"));
        created.setUser(user);
        created.setMembershipPlan(plan);
        created.setStatus("ACTIVE");
        created.setAutoRenew(autoRenew);
        created.setStartedAt(now);
        created.setExpiresAt(calculateNextExpiry(now, plan.getBillingCycle()));
        created.setNextBillingAt(created.getExpiresAt());
        return membershipSubscriptionRepository.save(created);
    }

    private MembershipPlan findActivePlan(String rawSlug) {
        String slug = normalizeBlank(rawSlug);
        if (slug == null) {
            throw new BadRequestException("Thiếu planSlug.");
        }
        return membershipPlanRepository.findBySlugAndActiveTrue(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy gói membership phù hợp."));
    }

    private Optional<MembershipSubscription> findCurrentMembership(Long userId) {
        Optional<MembershipSubscription> subscription = membershipSubscriptionRepository
                .findTopByUser_IdAndStatusInOrderByCreatedAtDescIdDesc(userId, CURRENT_MEMBERSHIP_STATUSES);
        subscription.ifPresent(this::normalizeStatusIfExpired);
        if (subscription.isPresent()
                && List.of("ACTIVE", "PAST_DUE", "PENDING_PAYMENT").contains(subscription.get().getStatus())) {
            return subscription;
        }
        return Optional.empty();
    }

    private void normalizeStatusIfExpired(MembershipSubscription subscription) {
        if (subscription == null || subscription.getExpiresAt() == null)
            return;
        if ("ACTIVE".equalsIgnoreCase(subscription.getStatus())
                && subscription.getExpiresAt().isBefore(LocalDateTime.now(APP_ZONE))) {
            subscription.setStatus("EXPIRED");
            membershipSubscriptionRepository.save(subscription);
        }
    }

    private void refreshInvoiceItems(Invoice invoice,
            MembershipPlan plan,
            BigDecimal subtotal,
            BigDecimal discount,
            BigDecimal tax) {
        if (invoice.getId() != null) {
            invoiceItemRepository.deleteByInvoiceId(invoice.getId());
        }

        List<InvoiceItem> items = new ArrayList<>();

        InvoiceItem planItem = new InvoiceItem();
        planItem.setInvoice(invoice);
        planItem.setItemType("MEMBERSHIP_PLAN");
        planItem.setItemName(firstNonBlank(plan.getName(), "Membership Plan"));
        planItem.setDescription(firstNonBlank(plan.getDescription(), buildMembershipDescription(plan)));
        planItem.setQuantity(1);
        planItem.setUnitPrice(subtotal);
        planItem.setLineTotal(subtotal);
        planItem.setSortOrder(1);
        items.add(planItem);

        if (discount.compareTo(BigDecimal.ZERO) > 0) {
            InvoiceItem discountItem = new InvoiceItem();
            discountItem.setInvoice(invoice);
            discountItem.setItemType("DISCOUNT");
            discountItem.setItemName("Ưu đãi membership");
            discountItem.setDescription("Giảm giá thanh toán membership");
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

    private MembershipPlanCardResponse mapPlan(MembershipPlan plan, boolean currentPlan) {
        return MembershipPlanCardResponse.builder()
                .id(plan.getId())
                .planCode(plan.getPlanCode())
                .name(plan.getName())
                .slug(plan.getSlug())
                .description(plan.getDescription())
                .billingCycle(plan.getBillingCycle())
                .priceAmount(defaultMoney(plan.getPriceAmount()))
                .currencyCode(firstNonBlank(plan.getCurrencyCode(), "VND"))
                .discountPercent(defaultMoney(plan.getDiscountPercent()))
                .monthlyVoucherAmount(defaultMoney(plan.getMonthlyVoucherAmount()))
                .priorityBooking(Boolean.TRUE.equals(plan.getPriorityBooking()))
                .prioritySupport(Boolean.TRUE.equals(plan.getPrioritySupport()))
                .popular(Boolean.TRUE.equals(plan.getPopular()))
                .features(plan.getFeatures().stream().map(MembershipPlanFeature::getFeatureText)
                        .filter(v -> v != null && !v.isBlank()).toList())
                .currentPlan(currentPlan)
                .build();
    }

    private MembershipSubscriptionResponse mapSubscription(MembershipSubscription subscription) {
        Invoice latestInvoice = invoiceRepository
                .findTopByMembershipSubscriptionIdOrderByCreatedAtDescIdDesc(subscription.getId()).orElse(null);
        return mapSubscription(subscription, latestInvoice);
    }

    private MembershipSubscriptionResponse mapSubscription(MembershipSubscription subscription, Invoice latestInvoice) {
        MembershipPlan plan = subscription.getMembershipPlan();
        return MembershipSubscriptionResponse.builder()
                .id(subscription.getId())
                .subscriptionCode(subscription.getSubscriptionCode())
                .status(subscription.getStatus())
                .autoRenew(Boolean.TRUE.equals(subscription.getAutoRenew()))
                .startedAt(formatDateTime(subscription.getStartedAt()))
                .expiresAt(formatDateTime(subscription.getExpiresAt()))
                .nextBillingAt(formatDateTime(subscription.getNextBillingAt()))
                .cancelledAt(formatDateTime(subscription.getCancelledAt()))
                .cancelReason(subscription.getCancelReason())
                .planId(plan != null ? plan.getId() : null)
                .planCode(plan != null ? plan.getPlanCode() : null)
                .planName(plan != null ? plan.getName() : null)
                .planSlug(plan != null ? plan.getSlug() : null)
                .billingCycle(plan != null ? plan.getBillingCycle() : null)
                .priceAmount(plan != null ? defaultMoney(plan.getPriceAmount()) : BigDecimal.ZERO)
                .currencyCode(plan != null ? firstNonBlank(plan.getCurrencyCode(), "VND") : "VND")
                .discountPercent(plan != null ? defaultMoney(plan.getDiscountPercent()) : BigDecimal.ZERO)
                .monthlyVoucherAmount(plan != null ? defaultMoney(plan.getMonthlyVoucherAmount()) : BigDecimal.ZERO)
                .priorityBooking(plan != null && Boolean.TRUE.equals(plan.getPriorityBooking()))
                .prioritySupport(plan != null && Boolean.TRUE.equals(plan.getPrioritySupport()))
                .popular(plan != null && Boolean.TRUE.equals(plan.getPopular()))
                .features(
                        plan != null
                                ? plan.getFeatures().stream().map(MembershipPlanFeature::getFeatureText)
                                        .filter(v -> v != null && !v.isBlank()).toList()
                                : List.of())
                .latestInvoiceId(latestInvoice != null ? latestInvoice.getId() : null)
                .latestInvoiceNumber(latestInvoice != null ? latestInvoice.getInvoiceNumber() : null)
                .latestInvoiceStatus(latestInvoice != null ? latestInvoice.getStatus() : null)
                .build();
    }

    private User requireCurrentUser(HttpServletRequest request) {
        AuthenticatedUser authenticatedUser = authService.requireAccessUser(request);
        return userRepository.findById(authenticatedUser.userId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng."));
    }

    private LocalDateTime calculateNextExpiry(LocalDateTime base, String billingCycle) {
        String cycle = firstNonBlank(billingCycle, "MONTHLY").toUpperCase(Locale.ROOT);
        return switch (cycle) {
            case "YEARLY" -> base.plusYears(1);
            case "QUARTERLY" -> base.plusMonths(3);
            default -> base.plusMonths(1);
        };
    }

    private String buildMembershipDescription(MembershipPlan plan) {
        return switch (firstNonBlank(plan.getBillingCycle(), "MONTHLY").toUpperCase(Locale.ROOT)) {
            case "YEARLY" -> "Thanh toán membership theo năm";
            case "QUARTERLY" -> "Thanh toán membership theo quý";
            default -> "Thanh toán membership theo tháng";
        };
    }

    private String resolveGatewayName(String paymentMethod) {
        return switch (paymentMethod) {
            case "MOMO" -> "MoMo";
            case "VNPAY" -> "VNPay";
            case "CARD" -> "Card";
            case "BANK_TRANSFER" -> "Bank Transfer";
            default -> "PetGo Gateway";
        };
    }

    private String normalizePaymentMethod(String paymentMethod) {
        String normalized = firstNonBlank(paymentMethod, "").trim().toUpperCase(Locale.ROOT);
        if (!CHECKOUT_PAYMENT_METHODS.contains(normalized)) {
            throw new BadRequestException("Phương thức thanh toán membership không được hỗ trợ");
        }
        return normalized;
    }

    private String buildUserAddress(User user) {
        if (user == null)
            return null;
        List<String> parts = java.util.stream.Stream.of(
                user.getAddressLine1(),
                user.getAddressLine2(),
                user.getWard(),
                user.getDistrict(),
                user.getCity(),
                user.getProvince())
                .filter(value -> value != null && !value.isBlank())
                .toList();
        return parts.isEmpty() ? null : String.join(", ", parts);
    }

    private BigDecimal defaultMoney(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private String formatMoney(BigDecimal amount) {
        return String.format(Locale.forLanguageTag("vi-VN"), "%,.0fđ", defaultMoney(amount));
    }

    private String formatDateTime(LocalDateTime dateTime) {
        return dateTime == null ? null : dateTime.format(DATE_TIME_VIEW);
    }

    private String generateCode(String prefix) {
        return prefix + "-" + UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase(Locale.ROOT);
    }

    private String firstNonBlank(String... values) {
        if (values == null)
            return null;
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return null;
    }

    private String normalizeBlank(String value) {
        if (value == null)
            return null;
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private <T> T firstNonNull(T... values) {
        if (values == null)
            return null;
        for (T value : values) {
            if (value != null)
                return value;
        }
        return null;
    }
}
