package com.example.petgo.service.impl;

import com.example.petgo.config.AuthenticatedUser;
import com.example.petgo.dto.*;
import com.example.petgo.entity.User;
import com.example.petgo.entity.Wallet;
import com.example.petgo.entity.WalletSetting;
import com.example.petgo.entity.WalletTransaction;
import com.example.petgo.exception.BadRequestException;
import com.example.petgo.exception.ResourceNotFoundException;
import com.example.petgo.exception.UnauthorizedException;
import com.example.petgo.repository.UserRepository;
import com.example.petgo.repository.UserRoleRepository;
import com.example.petgo.repository.WalletRepository;
import com.example.petgo.repository.WalletSettingRepository;
import com.example.petgo.repository.WalletTransactionRepository;
import com.example.petgo.service.AuthService;
import com.example.petgo.service.WalletService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.payos.PayOS;
import vn.payos.type.PaymentLinkData;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {
    private static final ZoneId APP_ZONE = ZoneId.of("Asia/Ho_Chi_Minh");
    private static final DateTimeFormatter DATE_TIME_VIEW = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final BigDecimal MIN_TOP_UP_AMOUNT = new BigDecimal("50000.00");
    private static final BigDecimal MIN_WITHDRAW_AMOUNT = new BigDecimal("50000.00");
    private static final String WALLET_ACTIVE = "ACTIVE";
    private static final String WALLET_INBOUND_LOCKED = "INBOUND_LOCKED";
    private static final String WALLET_OUTBOUND_LOCKED = "OUTBOUND_LOCKED";
    private static final String WALLET_LOCKED = "LOCKED";
    private static final String AUTO_CONFIRM_TOP_UP_KEY = "WALLET_AUTO_CONFIRM_TOP_UP";

    private final WalletRepository walletRepository;
    private final WalletSettingRepository walletSettingRepository;
    private final WalletTransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final AuthService authService;
    private final PayOS payOS;

    @Override
    @Transactional(readOnly = true)
    public WalletResponse getMyWallet(HttpServletRequest request) {
        AuthenticatedUser current = authService.requireAccessUser(request);
        Wallet wallet = walletRepository.findByUserId(current.userId())
                .orElseThrow(() -> new ResourceNotFoundException("Ví chưa được khởi tạo."));
        return mapWallet(wallet);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WalletTransactionResponse> getMyTransactions(HttpServletRequest request) {
        AuthenticatedUser current = authService.requireAccessUser(request);
        return transactionRepository.findByUserIdOrderByCreatedAtDescIdDesc(current.userId()).stream()
                .map(this::mapTransaction).toList();
    }

    @Override
    @Transactional
    public WalletTransactionResponse createTopUp(HttpServletRequest request, WalletTopUpRequest topUpRequest) {
        AuthenticatedUser current = authService.requireAccessUser(request);
        Wallet wallet = getOrCreateWallet(current.userId());
        ensureCanReceive(wallet);
        BigDecimal amount = normalizeAmount(topUpRequest.amount());
        if (amount.compareTo(MIN_TOP_UP_AMOUNT) < 0)
            throw new BadRequestException("Số tiền nạp tối thiểu là 50.000.");

        WalletTransaction tx = new WalletTransaction();
        tx.setTransactionCode(generateCode("TOPUP"));
        tx.setWallet(wallet);
        tx.setUser(wallet.getUser());
        tx.setType("TOP_UP");
        tx.setStatus("PAYMENT_PENDING");
        tx.setAmount(amount);
        tx.setGatewayName("PayOS");
        tx.setNote(topUpRequest.note());

        transactionRepository.saveAndFlush(tx);
        createPayOsLink(tx, topUpRequest.returnUrl(), topUpRequest.cancelUrl());
        transactionRepository.save(tx);
        return mapTransaction(tx);
    }

    @Override
    @Transactional
    public WalletTransactionResponse verifyTopUp(Long transactionId) {
        WalletTransaction tx = transactionRepository.findDetailedById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy giao dịch ví."));
        if (!"TOP_UP".equalsIgnoreCase(tx.getType()))
            throw new BadRequestException("Giao dịch không phải nạp ví.");
        if (!"PAYMENT_PENDING".equalsIgnoreCase(tx.getStatus()))
            return mapTransaction(tx);
        try {
            PaymentLinkData data = payOS.getPaymentLinkInformation(extractOrderCode(tx.getGatewayTransactionId()));
            if ("PAID".equalsIgnoreCase(data.getStatus()))
                markTopUpPaid(tx);
            if ("CANCELLED".equalsIgnoreCase(data.getStatus()))
                tx.setStatus("CANCELLED");
            transactionRepository.save(tx);
        } catch (Exception e) {
            log.error("Cannot verify wallet top-up {}: {}", transactionId, e.getMessage(), e);
            throw new BadRequestException("Không thể xác nhận thanh toán PayOS cho giao dịch ví.");
        }
        return mapTransaction(tx);
    }

    @Override
    @Transactional
    public WalletTransactionResponse transfer(HttpServletRequest request, WalletTransferRequest transferRequest) {
        AuthenticatedUser current = authService.requireAccessUser(request);
        BigDecimal amount = normalizeAmount(transferRequest.amount());
        Wallet senderWallet = walletRepository.findWithLockByUserId(current.userId())
                .orElseThrow(() -> new ResourceNotFoundException("Ví người gửi chưa được khởi tạo."));
        ensureCanSend(senderWallet);
        User recipient = resolveRecipient(transferRequest);
        if (recipient.getId().equals(current.userId()))
            throw new BadRequestException("Không thể tự chuyển tiền cho chính mình.");
        Wallet recipientWallet = getOrCreateWalletWithLock(recipient.getId());
        ensureCanReceive(recipientWallet);
        if (senderWallet.getBalance().compareTo(amount) < 0)
            throw new BadRequestException("Số dư ví không đủ để chuyển tiền.");

        BigDecimal senderBefore = senderWallet.getBalance();
        BigDecimal recipientBefore = recipientWallet.getBalance();
        senderWallet.setBalance(senderBefore.subtract(amount));
        recipientWallet.setBalance(recipientBefore.add(amount));
        walletRepository.save(senderWallet);
        walletRepository.save(recipientWallet);

        WalletTransaction debit = completedTx(senderWallet, senderWallet.getUser(), recipient, "TRANSFER_OUT", amount,
                senderBefore, senderWallet.getBalance(), transferRequest.note());
        WalletTransaction credit = completedTx(recipientWallet, recipient, senderWallet.getUser(), "TRANSFER_IN",
                amount, recipientBefore, recipientWallet.getBalance(), transferRequest.note());
        transactionRepository.save(credit);
        transactionRepository.save(debit);
        return mapTransaction(debit);
    }

    @Override
    @Transactional
    public WalletTransactionResponse requestWithdraw(HttpServletRequest request,
            WalletWithdrawRequest withdrawRequest) {
        AuthenticatedUser current = authService.requireAccessUser(request);
        BigDecimal amount = normalizeAmount(withdrawRequest.amount());
        if (amount.compareTo(MIN_WITHDRAW_AMOUNT) < 0)
            throw new BadRequestException("Số tiền rút tối thiểu là 50.000.");
        Wallet wallet = walletRepository.findWithLockByUserId(current.userId())
                .orElseThrow(() -> new ResourceNotFoundException("Ví chưa được khởi tạo."));
        ensureCanSend(wallet);
        if (wallet.getBalance().compareTo(amount) < 0)
            throw new BadRequestException("Số dư ví không đủ để tạo yêu cầu rút tiền.");

        WalletTransaction tx = new WalletTransaction();
        tx.setTransactionCode(generateCode("WITHDRAW"));
        tx.setWallet(wallet);
        tx.setUser(wallet.getUser());
        tx.setType("WITHDRAW");
        tx.setStatus("PENDING_ADMIN_APPROVAL");
        tx.setAmount(amount);
        tx.setBankName(withdrawRequest.bankName().trim());
        tx.setBankAccountNumber(withdrawRequest.bankAccountNumber().trim());
        tx.setBankAccountHolder(withdrawRequest.bankAccountHolder().trim());
        tx.setNote(withdrawRequest.note());
        transactionRepository.save(tx);
        return mapTransaction(tx);
    }

    @Override
    @Transactional
    public WalletResponse updateWalletStatus(HttpServletRequest request, Long userId,
            WalletStatusUpdateRequest statusRequest) {
        requireAdmin(request);
        Wallet wallet = getOrCreateWallet(userId);
        String status = normalizeWalletStatus(statusRequest.status());
        wallet.setStatus(status);
        walletRepository.save(wallet);
        return mapWallet(wallet);
    }

    @Override
    @Transactional(readOnly = true)
    public WalletAutoConfirmSettingResponse getAutoConfirmSetting(HttpServletRequest request) {
        requireAdmin(request);
        return WalletAutoConfirmSettingResponse.builder().enabled(isAutoConfirmTopUpEnabled()).build();
    }

    @Override
    @Transactional
    public WalletAutoConfirmSettingResponse updateAutoConfirmSetting(HttpServletRequest request,
            WalletAutoConfirmSettingRequest settingRequest) {
        requireAdmin(request);
        WalletSetting setting = walletSettingRepository.findBySettingKey(AUTO_CONFIRM_TOP_UP_KEY).orElseGet(() -> {
            WalletSetting next = new WalletSetting();
            next.setSettingKey(AUTO_CONFIRM_TOP_UP_KEY);
            return next;
        });
        setting.setSettingValue(Boolean.TRUE.equals(settingRequest.enabled()) ? "true" : "false");
        walletSettingRepository.save(setting);
        return WalletAutoConfirmSettingResponse.builder().enabled(Boolean.TRUE.equals(settingRequest.enabled()))
                .build();
    }

    @Override
    @Transactional
    public void handlePayOsWebhookOrderCode(Long orderCode) {
        if (orderCode == null)
            return;
        WalletTransaction tx = transactionRepository.findByGatewayTransactionId(String.valueOf(orderCode)).orElse(null);
        if (tx == null || !"TOP_UP".equalsIgnoreCase(tx.getType()))
            return;
        if ("PAYMENT_PENDING".equalsIgnoreCase(tx.getStatus())) {
            markTopUpPaid(tx);
            transactionRepository.save(tx);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<WalletTransactionResponse> getPendingAdminTransactions(HttpServletRequest request) {
        requireAdmin(request);
        return transactionRepository.findByStatusOrderByCreatedAtAscIdAsc("PENDING_ADMIN_APPROVAL").stream()
                .map(this::mapTransaction).toList();
    }

    @Override
    @Transactional
    public WalletTransactionResponse reviewAdminTransaction(HttpServletRequest request, Long transactionId,
            WalletAdminReviewRequest reviewRequest) {
        User admin = requireAdmin(request);
        WalletTransaction tx = transactionRepository.findDetailedById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy giao dịch ví."));
        if (!"PENDING_ADMIN_APPROVAL".equalsIgnoreCase(tx.getStatus()))
            throw new BadRequestException("Giao dịch không ở trạng thái chờ admin duyệt.");
        String action = reviewRequest.action().trim().toUpperCase();
        if ("APPROVE".equals(action))
            approveTransaction(tx);
        else if ("REJECT".equals(action))
            tx.setStatus("REJECTED");
        else
            throw new BadRequestException("action phải là APPROVE hoặc REJECT.");
        tx.setReviewedByAdmin(admin);
        tx.setReviewNote(reviewRequest.reviewNote());
        tx.setReviewedAt(LocalDateTime.now(APP_ZONE));
        transactionRepository.save(tx);
        return mapTransaction(tx);
    }

    @Override
    @Transactional
    public void ensureWalletForUser(Long userId) {
        getOrCreateWallet(userId);
    }

    private void approveTransaction(WalletTransaction tx) {
        Wallet wallet = walletRepository.findWithLockByUserId(tx.getUser().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Ví chưa được khởi tạo."));
        BigDecimal before = wallet.getBalance();
        if ("TOP_UP".equalsIgnoreCase(tx.getType())) {
            ensureCanReceive(wallet);
            wallet.setBalance(before.add(tx.getAmount()));
        } else if ("WITHDRAW".equalsIgnoreCase(tx.getType())) {
            ensureCanSend(wallet);
            if (before.compareTo(tx.getAmount()) < 0)
                throw new BadRequestException("Số dư ví không đủ để duyệt rút tiền.");
            wallet.setBalance(before.subtract(tx.getAmount()));
        } else
            throw new BadRequestException("Loại giao dịch không hỗ trợ duyệt thủ công.");
        walletRepository.save(wallet);
        tx.setBalanceBefore(before);
        tx.setBalanceAfter(wallet.getBalance());
        tx.setStatus("COMPLETED");
    }

    private void markTopUpPaid(WalletTransaction tx) {
        if (isAutoConfirmTopUpEnabled()) {
            approveTransaction(tx);
            tx.setReviewNote("Tự động cộng tiền vào ví sau khi PayOS xác nhận thanh toán thành công.");
            tx.setReviewedAt(LocalDateTime.now(APP_ZONE));
        } else {
            tx.setStatus("PENDING_ADMIN_APPROVAL");
        }
    }

    private boolean isAutoConfirmTopUpEnabled() {
        return walletSettingRepository.findBySettingKey(AUTO_CONFIRM_TOP_UP_KEY)
                .map(WalletSetting::getSettingValue)
                .map(Boolean::parseBoolean)
                .orElse(false);
    }

    private Wallet getOrCreateWallet(Long userId) {
        return walletRepository.findByUserId(userId).orElseGet(() -> {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng."));
            Wallet wallet = new Wallet();
            wallet.setUser(user);
            return walletRepository.save(wallet);
        });
    }

    private Wallet getOrCreateWalletWithLock(Long userId) {
        getOrCreateWallet(userId);
        return walletRepository.findWithLockByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Ví chưa được khởi tạo."));
    }

    private User resolveRecipient(WalletTransferRequest request) {
        if (request.recipientUserId() != null)
            return userRepository.findById(request.recipientUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người nhận."));
        String account = request.recipientAccount().trim();
        if (account.contains("@"))
            return userRepository.findByEmailIgnoreCaseAndDeletedAtIsNull(account)
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người nhận."));
        return userRepository.findByUserCodeAndDeletedAtIsNull(account)
                .or(() -> userRepository.findByPhoneNumberAndDeletedAtIsNull(account))
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người nhận."));
    }

    private User requireAdmin(HttpServletRequest request) {
        AuthenticatedUser current = authService.requireAccessUser(request);
        boolean isAdmin = userRoleRepository.findByUser_Id(current.userId()).stream()
                .anyMatch(ur -> ur.getRole() != null && ur.getRole().getCode() != null
                        && "ADMIN".equalsIgnoreCase(ur.getRole().getCode().getCode()));
        if (!isAdmin)
            throw new UnauthorizedException("Bạn không có quyền admin để thực hiện thao tác này.");
        return userRepository.findById(current.userId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy admin."));
    }

    private WalletTransaction completedTx(Wallet wallet, User user, User counterparty, String type, BigDecimal amount,
            BigDecimal before, BigDecimal after, String note) {
        WalletTransaction tx = new WalletTransaction();
        tx.setTransactionCode(generateCode(type));
        tx.setWallet(wallet);
        tx.setUser(user);
        tx.setCounterpartyUser(counterparty);
        tx.setType(type);
        tx.setStatus("COMPLETED");
        tx.setAmount(amount);
        tx.setBalanceBefore(before);
        tx.setBalanceAfter(after);
        tx.setNote(note);
        return tx;
    }

    private void createPayOsLink(WalletTransaction tx, String returnUrl, String cancelUrl) {
        int amountInt = tx.getAmount().setScale(0, RoundingMode.HALF_UP).intValue();
        long orderCode = Math.abs((System.currentTimeMillis() * 1000) + new java.util.Random().nextInt(900));
        String description = buildPayOsDescription(tx);
        String resolvedReturnUrl = resolveUrl(returnUrl, "http://localhost:3000/wallet");
        String resolvedCancelUrl = resolveUrl(cancelUrl, "http://localhost:3000/wallet");
        try {
            java.lang.reflect.Field fieldChecksum = payOS.getClass().getDeclaredField("checksumKey");
            java.lang.reflect.Field fieldId = payOS.getClass().getDeclaredField("clientId");
            java.lang.reflect.Field fieldKey = payOS.getClass().getDeclaredField("apiKey");
            fieldChecksum.setAccessible(true);
            fieldId.setAccessible(true);
            fieldKey.setAccessible(true);

            String checksumKey = (String) fieldChecksum.get(payOS);
            String clientId = (String) fieldId.get(payOS);
            String apiKey = (String) fieldKey.get(payOS);
            String signatureData = "amount=" + amountInt
                    + "&cancelUrl=" + resolvedCancelUrl
                    + "&description=" + description
                    + "&orderCode=" + orderCode
                    + "&returnUrl=" + resolvedReturnUrl;

            java.util.Map<String, Object> requestBody = new java.util.HashMap<>();
            requestBody.put("orderCode", orderCode);
            requestBody.put("amount", amountInt);
            requestBody.put("description", description);
            requestBody.put("cancelUrl", resolvedCancelUrl);
            requestBody.put("returnUrl", resolvedReturnUrl);
            requestBody.put("signature", generateHmacSha256(signatureData, checksumKey));
            requestBody.put("items", new java.util.ArrayList<>());

            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
            headers.set("x-client-id", clientId);
            headers.set("x-api-key", apiKey);
            org.springframework.http.HttpEntity<java.util.Map<String, Object>> entity = new org.springframework.http.HttpEntity<>(
                    requestBody, headers);
            org.springframework.http.ResponseEntity<java.util.Map> response = new org.springframework.web.client.RestTemplate()
                    .postForEntity(
                            "https://api-merchant.payos.vn/v2/payment-requests", entity, java.util.Map.class);

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null
                    || !"00".equals(String.valueOf(response.getBody().get("code")))) {
                throw new BadRequestException("Cổng PayOS từ chối yêu cầu nạp ví.");
            }
            java.util.Map<String, Object> data = (java.util.Map<String, Object>) response.getBody().get("data");
            String checkoutUrl = String.valueOf(data.get("checkoutUrl"));
            String qrCode = String.valueOf(data.get("qrCode"));
            tx.setGatewayTransactionId(String.valueOf(orderCode));
            tx.setCheckoutUrl(checkoutUrl);
            tx.setQrCodeText(qrCode);
            tx.setPaymentContent(description);
        } catch (Exception e) {
            log.error("Cannot create PayOS wallet top-up link: {}", e.getMessage(), e);
            throw new BadRequestException("Không thể tạo link nạp ví PayOS.");
        }
    }

    private String generateHmacSha256(String data, String key) {
        try {
            javax.crypto.Mac sha256HMAC = javax.crypto.Mac.getInstance("HmacSHA256");
            javax.crypto.spec.SecretKeySpec secretKey = new javax.crypto.spec.SecretKeySpec(
                    key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha256HMAC.init(secretKey);
            byte[] hash = sha256HMAC.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1)
                    hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new IllegalStateException("Lỗi tạo chữ ký PayOS", e);
        }
    }

    private String buildPayOsDescription(WalletTransaction tx) {
        String raw = "PetGo Wallet " + (tx.getId() != null ? tx.getId() : tx.getTransactionCode());
        return raw.length() > 25 ? raw.substring(0, 25) : raw;
    }

    private long extractOrderCode(String value) {
        try {
            return Long.parseLong(value);
        } catch (Exception e) {
            throw new BadRequestException("Mã giao dịch PayOS không hợp lệ.");
        }
    }

    private String resolveUrl(String provided, String fallback) {
        return provided != null && !provided.isBlank() ? provided.trim() : fallback;
    }

    private void ensureCanSend(Wallet wallet) {
        String status = wallet.getStatus() == null ? WALLET_ACTIVE : wallet.getStatus();
        if (WALLET_LOCKED.equalsIgnoreCase(status) || WALLET_OUTBOUND_LOCKED.equalsIgnoreCase(status)) {
            throw new BadRequestException("Ví đang bị khóa chiều chuyển/rút tiền.");
        }
    }

    private void ensureCanReceive(Wallet wallet) {
        String status = wallet.getStatus() == null ? WALLET_ACTIVE : wallet.getStatus();
        if (WALLET_LOCKED.equalsIgnoreCase(status) || WALLET_INBOUND_LOCKED.equalsIgnoreCase(status)) {
            throw new BadRequestException("Ví đang bị khóa chiều nhận/nạp tiền.");
        }
    }

    private String normalizeWalletStatus(String status) {
        String normalized = status == null ? "" : status.trim().toUpperCase();
        return switch (normalized) {
            case WALLET_ACTIVE, WALLET_INBOUND_LOCKED, WALLET_OUTBOUND_LOCKED, WALLET_LOCKED -> normalized;
            default -> throw new BadRequestException(
                    "Trạng thái ví phải là ACTIVE, INBOUND_LOCKED, OUTBOUND_LOCKED hoặc LOCKED.");
        };
    }

    private BigDecimal normalizeAmount(BigDecimal amount) {
        return amount == null ? BigDecimal.ZERO : amount.setScale(2, RoundingMode.HALF_UP);
    }

    private String generateCode(String prefix) {
        return prefix.replace("_", "") + "-"
                + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    }

    private WalletResponse mapWallet(Wallet wallet) {
        User user = wallet.getUser();
        return WalletResponse.builder().walletId(wallet.getId()).userId(user.getId()).userCode(user.getUserCode())
                .fullName(user.getFullName()).balance(wallet.getBalance()).currencyCode(wallet.getCurrencyCode())
                .status(wallet.getStatus()).build();
    }

    private WalletTransactionResponse mapTransaction(WalletTransaction tx) {
        return WalletTransactionResponse.builder()
                .id(tx.getId()).transactionCode(tx.getTransactionCode()).userId(tx.getUser().getId())
                .userCode(tx.getUser().getUserCode()).userName(tx.getUser().getFullName())
                .counterpartyUserId(tx.getCounterpartyUser() != null ? tx.getCounterpartyUser().getId() : null)
                .counterpartyUserCode(tx.getCounterpartyUser() != null ? tx.getCounterpartyUser().getUserCode() : null)
                .counterpartyUserName(tx.getCounterpartyUser() != null ? tx.getCounterpartyUser().getFullName() : null)
                .type(tx.getType()).status(tx.getStatus()).amount(tx.getAmount()).balanceBefore(tx.getBalanceBefore())
                .balanceAfter(tx.getBalanceAfter())
                .gatewayName(tx.getGatewayName()).gatewayTransactionId(tx.getGatewayTransactionId())
                .checkoutUrl(tx.getCheckoutUrl()).qrCodeText(tx.getQrCodeText()).paymentContent(tx.getPaymentContent())
                .bankName(tx.getBankName()).bankAccountNumber(tx.getBankAccountNumber())
                .bankAccountHolder(tx.getBankAccountHolder()).note(tx.getNote()).reviewNote(tx.getReviewNote())
                .reviewedAt(tx.getReviewedAt() != null ? tx.getReviewedAt().format(DATE_TIME_VIEW) : null)
                .createdAt(tx.getCreatedAt() != null ? tx.getCreatedAt().format(DATE_TIME_VIEW) : null).build();
    }
}