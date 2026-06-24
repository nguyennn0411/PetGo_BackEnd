package com.example.petgo.service.impl;

import com.example.petgo.config.AuthenticatedUser;
import com.example.petgo.dto.AdminBookingDisputeResponse;
import com.example.petgo.dto.AdminDisputeResolveRequest;
import com.example.petgo.entity.*;
import com.example.petgo.exception.BadRequestException;
import com.example.petgo.exception.ResourceNotFoundException;
import com.example.petgo.exception.UnauthorizedException;
import com.example.petgo.repository.*;
import com.example.petgo.service.AdminBookingDisputeService;
import com.example.petgo.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminBookingDisputeServiceImpl implements AdminBookingDisputeService {

    private static final ZoneId APP_ZONE = ZoneId.of("Asia/Ho_Chi_Minh");
    private static final DateTimeFormatter DATE_VIEW = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_VIEW = DateTimeFormatter.ofPattern("HH:mm");

    private final BookingDisputeRepository bookingDisputeRepository;
    private final ShippingBookingRepository shippingBookingRepository;
    private final WalletRepository walletRepository;
    private final WalletTransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final AuthService authService;

    @Override
    @Transactional(readOnly = true)
    public List<AdminBookingDisputeResponse> getDisputes(HttpServletRequest request) {
        requireAdmin(request);
        List<BookingDispute> disputes = bookingDisputeRepository.findByStatusWithBooking("PENDING");
        return disputes.stream().map(this::mapResponse).toList();
    }

    @Override
    @Transactional
    public AdminBookingDisputeResponse resolveDispute(HttpServletRequest request, Long bookingId,
            AdminDisputeResolveRequest resolveRequest) {
        User admin = requireAdmin(request);

        BookingDispute dispute = bookingDisputeRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khiếu nại cho booking này."));
        if (!"PENDING".equalsIgnoreCase(dispute.getStatus()))
            throw new BadRequestException("Khiếu nại đã được xử lý trước đó.");

        ShippingBooking booking = dispute.getBooking();
        BigDecimal escrowAmount = booking.getPriceAmount() != null ? booking.getPriceAmount() : BigDecimal.ZERO;
        BigDecimal refund = normalizeAmount(resolveRequest.refundToUserAmount());
        BigDecimal release = normalizeAmount(resolveRequest.releaseToPartnerAmount());
        if (refund.add(release).compareTo(escrowAmount) > 0)
            throw new BadRequestException("Tổng tiền phân bổ không được vượt quá escrow (" + escrowAmount + ").");

        User customer = booking.getUser();

        if (refund.compareTo(BigDecimal.ZERO) > 0)
            addToWalletBalance(customer.getId(), refund,
                    "Hoàn tiền khiếu nại booking " + booking.getBookingCode() + ": " + resolveRequest.reason());

        if (release.compareTo(BigDecimal.ZERO) > 0)
            recordReleaseTransaction(release,
                    "Giải ngân khiếu nại booking " + booking.getBookingCode() + ": " + resolveRequest.reason());

        dispute.setRefundToUserAmount(refund);
        dispute.setReleaseToProviderAmount(release);
        dispute.setStatus("RESOLVED");
        dispute.setResolvedBy(admin);
        dispute.setResolvedAt(LocalDateTime.now(APP_ZONE));
        dispute.setNote(resolveRequest.reason());
        bookingDisputeRepository.save(dispute);

        booking.setStatus("DISPUTE_RESOLVED");
        shippingBookingRepository.save(booking);

        return mapResponse(dispute);
    }

    private AdminBookingDisputeResponse mapResponse(BookingDispute dispute) {
        ShippingBooking booking = dispute.getBooking();
        User customer = booking.getUser();
        String rawTime = booking.getTimeSlot();
        String timeDisplay = rawTime != null ? rawTime : "";
        if (rawTime != null && rawTime.length() >= 5) {
            try {
                timeDisplay = LocalDateTime.now().withHour(Integer.parseInt(rawTime.substring(0, 2)))
                        .withMinute(Integer.parseInt(rawTime.substring(3, 5))).format(TIME_VIEW);
            } catch (Exception ignored) {
            }
        }
        BigDecimal escrowAmount = booking.getPriceAmount() != null ? booking.getPriceAmount() : BigDecimal.ZERO;
        var area = booking.getArea();
        var service = booking.getService();
        return new AdminBookingDisputeResponse(
                booking.getId(),
                booking.getBookingCode(),
                customer.getId(),
                customer.getFullName(),
                area != null ? area.getId() : null,
                area != null ? area.getName() : null,
                service != null ? service.getName() : null,
                booking.getAppointmentDate() != null ? booking.getAppointmentDate().format(DATE_VIEW) : null,
                timeDisplay,
                escrowAmount,
                formatMoney(escrowAmount),
                dispute.getReason(),
                dispute.getStatus(),
                "PENDING".equalsIgnoreCase(dispute.getStatus()) ? "Chờ xử lý" : "Đã xử lý");
    }

    private void addToWalletBalance(Long userId, BigDecimal amount, String note) {
        Wallet wallet = walletRepository.findWithLockByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy ví người dùng."));
        BigDecimal before = wallet.getBalance();
        wallet.setBalance(before.add(amount));
        walletRepository.save(wallet);
        String txnCode = "DSPT-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
        WalletTransaction tx = new WalletTransaction();
        tx.setTransactionCode(txnCode);
        tx.setWallet(wallet);
        tx.setUser(wallet.getUser());
        tx.setType("TRANSFER_IN");
        tx.setStatus("COMPLETED");
        tx.setAmount(amount);
        tx.setBalanceBefore(before);
        tx.setBalanceAfter(wallet.getBalance());
        tx.setNote(note);
        transactionRepository.save(tx);
    }

    private void recordReleaseTransaction(BigDecimal amount, String note) {
        String txnCode = "DSPT-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
        WalletTransaction tx = new WalletTransaction();
        tx.setTransactionCode(txnCode);
        tx.setType("TRANSFER_OUT");
        tx.setStatus("COMPLETED");
        tx.setAmount(amount);
        tx.setNote(note + " (giải ngân đối tác - chờ hệ thống thanh toán)");
        transactionRepository.save(tx);
    }

    private User requireAdmin(HttpServletRequest request) {
        AuthenticatedUser current = authService.requireAccessUser(request);
        User user = userRepository.findById(current.userId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng."));
        boolean isAdmin = userRoleRepository.findByUser_Id(user.getId()).stream()
                .anyMatch(ur -> ur.getRole() != null && ur.getRole().getCode() != null
                        && "ADMIN".equalsIgnoreCase(ur.getRole().getCode().getCode()));
        if (!isAdmin)
            throw new UnauthorizedException("Bạn không có quyền admin.");
        return user;
    }

    private BigDecimal normalizeAmount(BigDecimal amount) {
        return amount == null ? BigDecimal.ZERO : amount.setScale(2, RoundingMode.HALF_UP);
    }

    private String formatMoney(BigDecimal amount) {
        if (amount == null)
            return "0 ₫";
        NumberFormat fmt = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
        fmt.setMinimumFractionDigits(0);
        fmt.setMaximumFractionDigits(0);
        return fmt.format(amount) + " ₫";
    }
}
