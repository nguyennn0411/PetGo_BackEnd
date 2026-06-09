package com.example.petgo.service.impl;

import com.example.petgo.entity.Booking;
import com.example.petgo.entity.BookingStatusHistory;
import com.example.petgo.entity.User;
import com.example.petgo.entity.Wallet;
import com.example.petgo.entity.WalletTransaction;
import com.example.petgo.repository.BookingRepository;
import com.example.petgo.repository.BookingStatusHistoryRepository;
import com.example.petgo.repository.WalletRepository;
import com.example.petgo.repository.WalletTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class BookingWorkflowJob {
    private static final ZoneId APP_ZONE = ZoneId.of("Asia/Ho_Chi_Minh");

    private final BookingRepository bookingRepository;
    private final BookingStatusHistoryRepository bookingStatusHistoryRepository;
    private final WalletRepository walletRepository;
    private final WalletTransactionRepository walletTransactionRepository;
    private final BookingNotificationService bookingNotificationService;

    @Scheduled(fixedDelayString = "${app.bookings.workflow-job-delay-ms:300000}")
    @Transactional
    public void processBookingWorkflow() {
        moveExpiredProviderConfirmationsToAdminReview();
        releaseEligibleCompletedEscrows();
    }

    private void moveExpiredProviderConfirmationsToAdminReview() {
        LocalDateTime now = LocalDateTime.now(APP_ZONE);
        bookingRepository.findByStatusOrderByUpdatedAtAscIdAsc("PENDING_PROVIDER_CONFIRMATION").forEach(booking -> {
            LocalDateTime createdAt = booking.getCreatedAt() != null
                    ? booking.getCreatedAt().atZone(APP_ZONE).toLocalDateTime()
                    : now;
            LocalDateTime appointmentAt = booking.getAppointmentDate() != null && booking.getStartTime() != null
                    ? LocalDateTime.of(booking.getAppointmentDate(), booking.getStartTime())
                    : createdAt.plusHours(24);
            LocalDateTime deadline = createdAt.plusHours(24);
            if (appointmentAt.isBefore(deadline)) {
                deadline = appointmentAt.minusHours(1);
            }
            if (!now.isBefore(deadline)) {
                String previous = booking.getStatus();
                booking.setStatus("ADMIN_REVIEW");
                booking.setInternalNote(appendNote(booking.getInternalNote(),
                        "[AUTO_ADMIN_REVIEW] Provider không xác nhận booking đúng hạn."));
                bookingRepository.save(booking);
                saveHistory(booking, previous, "ADMIN_REVIEW", null,
                        "Tự động chuyển admin review vì provider không xác nhận booking đúng hạn.");
                bookingNotificationService.notifyBookingAdminReview(booking,
                        "Provider không xác nhận booking đúng hạn.");
            }
        });
    }

    private void releaseEligibleCompletedEscrows() {
        LocalDateTime now = LocalDateTime.now(APP_ZONE);
        bookingRepository.findByStatusInOrderByUpdatedAtAscIdAsc(List.of("COMPLETED")).forEach(booking -> {
            WalletTransaction holdTx = walletTransactionRepository
                    .findFirstByGatewayTransactionIdAndTypeAndStatusOrderByCreatedAtDescIdDesc(
                            "BOOKING:" + booking.getId(), "BOOKING_ESCROW_HOLD", "HELD_BY_ADMIN")
                    .orElse(null);
            if (holdTx == null || holdTx.getCreatedAt() == null) {
                return;
            }
            LocalDateTime releaseAt = holdTx.getCreatedAt().atZone(APP_ZONE).toLocalDateTime().plusDays(3);
            if (now.isBefore(releaseAt)) {
                return;
            }
            if (booking.getProvider() == null || booking.getProvider().getUser() == null) {
                booking.setStatus("ADMIN_REVIEW");
                booking.setInternalNote(appendNote(booking.getInternalNote(),
                        "[AUTO_ADMIN_REVIEW] Không tìm thấy provider user để giải ngân escrow."));
                bookingRepository.save(booking);
                saveHistory(booking, "COMPLETED", "ADMIN_REVIEW", null,
                        "Không thể tự giải ngân escrow vì provider user không hợp lệ.");
                bookingNotificationService.notifyBookingAdminReview(booking,
                        "Không tìm thấy provider user để giải ngân escrow.");
                return;
            }
            creditWallet(booking.getProvider().getUser(), money(holdTx.getAmount()), booking,
                    "BOOKING_ESCROW_RELEASE", "Tự động giải ngân escrow booking " + booking.getBookingCode());
            holdTx.setStatus("RELEASED_TO_PROVIDER");
            holdTx.setReviewNote("Auto released to provider after 3-day escrow hold.");
            walletTransactionRepository.save(holdTx);
            booking.setInternalNote(appendNote(booking.getInternalNote(),
                    "[AUTO_ESCROW_RELEASE] Đã tự động giải ngân provider sau 3 ngày giữ escrow."));
            bookingRepository.save(booking);
            saveHistory(booking, "COMPLETED", "COMPLETED", null,
                    "Tự động giải ngân escrow sang ví provider sau 3 ngày.");
            bookingNotificationService.notifyEscrowReleased(booking,
                    "Tự động giải ngân escrow sang ví provider sau 3 ngày.");
        });
    }

    private void creditWallet(User user, BigDecimal amount, Booking booking, String type, String note) {
        Wallet wallet = getOrCreateWalletWithLock(user);
        BigDecimal before = money(wallet.getBalance());
        wallet.setBalance(before.add(amount));
        walletRepository.save(wallet);

        WalletTransaction tx = new WalletTransaction();
        tx.setTransactionCode(type.replace("_", "") + "-" + UUID.randomUUID().toString().replace("-", "")
                .substring(0, 12).toUpperCase(Locale.ROOT));
        tx.setWallet(wallet);
        tx.setUser(user);
        tx.setType(type);
        tx.setStatus("COMPLETED");
        tx.setAmount(amount);
        tx.setGatewayTransactionId("BOOKING:" + booking.getId());
        tx.setBalanceBefore(before);
        tx.setBalanceAfter(wallet.getBalance());
        tx.setNote(note);
        walletTransactionRepository.save(tx);
    }

    private Wallet getOrCreateWalletWithLock(User user) {
        walletRepository.findByUserId(user.getId()).orElseGet(() -> {
            Wallet wallet = new Wallet();
            wallet.setUser(user);
            return walletRepository.save(wallet);
        });
        return walletRepository.findWithLockByUserId(user.getId())
                .orElseThrow(() -> new IllegalStateException("Ví chưa được khởi tạo."));
    }

    private void saveHistory(Booking booking, String from, String to, User changedBy, String note) {
        BookingStatusHistory history = new BookingStatusHistory();
        history.setBooking(booking);
        history.setFromStatus(from);
        history.setToStatus(to);
        history.setChangedByUser(changedBy);
        history.setNote(note);
        bookingStatusHistoryRepository.save(history);
    }

    private BigDecimal money(BigDecimal value) {
        return Optional.ofNullable(value).orElse(BigDecimal.ZERO);
    }

    private String appendNote(String current, String note) {
        String next = (current == null || current.isBlank()) ? note : current + "\n" + note;
        return next.length() <= 2000 ? next : next.substring(0, 2000);
    }
}