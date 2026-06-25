package com.example.petgo.service.impl;

import com.example.petgo.config.AuthenticatedUser;
import com.example.petgo.dto.BookingResponse;
import com.example.petgo.dto.BookingStatusUpdateRequest;
import com.example.petgo.entity.*;
import com.example.petgo.exception.BadRequestException;
import com.example.petgo.exception.ResourceNotFoundException;
import com.example.petgo.exception.UnauthorizedException;
import com.example.petgo.repository.*;
import com.example.petgo.service.AdminBookingService;
import com.example.petgo.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminBookingServiceImpl implements AdminBookingService {

    private final ShippingBookingRepository shippingBookingRepository;
    private final BookingStatusHistoryRepository bookingStatusHistoryRepository;
    private final WalletRepository walletRepository;
    private final WalletTransactionRepository walletTransactionRepository;
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final AuthService authService;

    private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> getAllBookings(Long areaId, String status, String date) {
        List<ShippingBooking> bookings;

        if (areaId != null) {
            bookings = shippingBookingRepository.findByAreaIdOrderByCreatedAtDesc(areaId);
        } else {
            bookings = shippingBookingRepository.findAllOrderByCreatedAtDesc();
        }

        if (status != null && !status.isEmpty() && !"ALL".equalsIgnoreCase(status)) {
            bookings = bookings.stream()
                    .filter(b -> status.equalsIgnoreCase(b.getStatus()))
                    .toList();
        }

        if (date != null && !date.isEmpty()) {
            java.time.LocalDate filterDate = java.time.LocalDate.parse(date);
            bookings = bookings.stream()
                    .filter(b -> b.getAppointmentDate() != null && b.getAppointmentDate().equals(filterDate))
                    .toList();
        }

        return bookings.stream().map(this::toBookingResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public BookingResponse getBookingDetail(Long id) {
        ShippingBooking booking = shippingBookingRepository.findDetailedById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy booking."));
        return toBookingResponse(booking);
    }

    @Override
    @Transactional
    public BookingResponse confirmBooking(HttpServletRequest request, Long id, BookingStatusUpdateRequest req) {
        User admin = requireAdmin(request);
        ShippingBooking booking = getBooking(id);

        if (!"PENDING".equals(booking.getStatus())) {
            throw new BadRequestException("Booking không ở trạng thái PENDING để xác nhận.");
        }

        changeStatus(booking, "CONFIRMED", "ADMIN", admin.getId(), admin.getFullName(),
                req != null ? req.getNote() : null);

        return toBookingResponse(shippingBookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingResponse completeBooking(HttpServletRequest request, Long id, BookingStatusUpdateRequest req) {
        User admin = requireAdmin(request);
        ShippingBooking booking = getBooking(id);

        if (!"CONFIRMED".equals(booking.getStatus()) && !"IN_PROGRESS".equals(booking.getStatus())) {
            throw new BadRequestException("Booking phải ở trạng thái CONFIRMED hoặc IN_PROGRESS để hoàn tất.");
        }

        releasePayment(booking);

        changeStatus(booking, "COMPLETED", "ADMIN", admin.getId(), admin.getFullName(),
                req != null ? req.getNote() : null);

        return toBookingResponse(shippingBookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingResponse cancelBooking(HttpServletRequest request, Long id, BookingStatusUpdateRequest req) {
        User admin = requireAdmin(request);
        ShippingBooking booking = getBooking(id);

        if (!"PENDING".equals(booking.getStatus()) && !"CONFIRMED".equals(booking.getStatus())) {
            throw new BadRequestException("Chỉ có thể hủy booking ở trạng thái PENDING hoặc CONFIRMED.");
        }

        refundPayment(booking);

        changeStatus(booking, "CANCELLED", "ADMIN", admin.getId(), admin.getFullName(),
                req != null ? req.getNote() : null);

        return toBookingResponse(shippingBookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingResponse rejectBooking(HttpServletRequest request, Long id, BookingStatusUpdateRequest req) {
        User admin = requireAdmin(request);
        ShippingBooking booking = getBooking(id);

        if (!"PENDING".equals(booking.getStatus())) {
            throw new BadRequestException("Chỉ có thể từ chối booking ở trạng thái PENDING.");
        }

        refundPayment(booking);

        changeStatus(booking, "REJECTED", "ADMIN", admin.getId(), admin.getFullName(),
                req != null ? req.getNote() : null);

        return toBookingResponse(shippingBookingRepository.save(booking));
    }

    @Override
    @Transactional
    public void updateBookingNote(Long id, BookingStatusUpdateRequest req) {
        ShippingBooking booking = getBooking(id);
        booking.setAdminNote(req != null ? req.getNote() : null);
        shippingBookingRepository.save(booking);
    }

    private ShippingBooking getBooking(Long id) {
        return shippingBookingRepository.findDetailedById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy booking."));
    }

    private void changeStatus(ShippingBooking booking, String newStatus, String changedByType,
                               Long changedById, String changedByName, String note) {
        String oldStatus = booking.getStatus();
        booking.setStatus(newStatus);
        shippingBookingRepository.save(booking);

        BookingStatusHistory history = new BookingStatusHistory();
        history.setBooking(booking);
        history.setFromStatus(oldStatus);
        history.setToStatus(newStatus);
        history.setChangedByType(changedByType);
        history.setChangedById(changedById);
        history.setChangedByName(changedByName);
        history.setNote(note);
        bookingStatusHistoryRepository.save(history);
    }

    private void releasePayment(ShippingBooking booking) {
        if (booking.getTotalAmount() == null || booking.getTotalAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }

        Wallet userWallet = walletRepository.findWithLockByUserId(booking.getUser().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy ví người dùng."));

        BigDecimal holdAmount = booking.getTotalAmount();

        if (userWallet.getHeldBalance() == null || userWallet.getHeldBalance().compareTo(holdAmount) < 0) {
            throw new BadRequestException("Số dư held không đủ để giải ngân.");
        }

        // Subtract held balance from user wallet
        userWallet.setHeldBalance(userWallet.getHeldBalance().subtract(holdAmount));
        walletRepository.save(userWallet);

        String txnCode = "RLS-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
        WalletTransaction txn = new WalletTransaction();
        txn.setTransactionCode(txnCode);
        txn.setWallet(userWallet);
        txn.setUser(booking.getUser());
        txn.setType("RELEASE");
        txn.setStatus("COMPLETED");
        txn.setAmount(holdAmount);
        txn.setBalanceBefore(userWallet.getBalance());
        txn.setBalanceAfter(userWallet.getBalance());
        txn.setNote("Giải ngân booking " + booking.getBookingCode());
        walletTransactionRepository.save(txn);

        // Credit system wallet
        Wallet systemWallet = walletRepository.findWithLockByIsSystemTrue()
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy ví hệ thống."));
        BigDecimal systemBefore = systemWallet.getBalance();
        systemWallet.setBalance(systemBefore.add(holdAmount));
        walletRepository.save(systemWallet);

        String sysTxnCode = "TRANSFER_IN-SYS-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
        WalletTransaction sysTxn = new WalletTransaction();
        sysTxn.setTransactionCode(sysTxnCode);
        sysTxn.setWallet(systemWallet);
        sysTxn.setUser(null);
        sysTxn.setCounterpartyUser(booking.getUser());
        sysTxn.setType("TRANSFER_IN");
        sysTxn.setStatus("COMPLETED");
        sysTxn.setAmount(holdAmount);
        sysTxn.setBalanceBefore(systemBefore);
        sysTxn.setBalanceAfter(systemWallet.getBalance());
        sysTxn.setNote("Nhận tiền giải ngân booking " + booking.getBookingCode());
        walletTransactionRepository.save(sysTxn);
    }

    private void refundPayment(ShippingBooking booking) {
        if (booking.getTotalAmount() == null || booking.getTotalAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }

        Wallet wallet = walletRepository.findWithLockByUserId(booking.getUser().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy ví người dùng."));

        BigDecimal holdAmount = booking.getTotalAmount();

        if (wallet.getHeldBalance() == null || wallet.getHeldBalance().compareTo(holdAmount) < 0) {
            throw new BadRequestException("Số dư held không đủ để hoàn tiền.");
        }

        wallet.setHeldBalance(wallet.getHeldBalance().subtract(holdAmount));
        wallet.setBalance(wallet.getBalance().add(holdAmount));
        walletRepository.save(wallet);

        String txnCode = "UNHOLD-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
        WalletTransaction txn = new WalletTransaction();
        txn.setTransactionCode(txnCode);
        txn.setWallet(wallet);
        txn.setUser(booking.getUser());
        txn.setType("UNHOLD");
        txn.setStatus("COMPLETED");
        txn.setAmount(holdAmount);
        txn.setBalanceBefore(wallet.getBalance().subtract(holdAmount));
        txn.setBalanceAfter(wallet.getBalance());
        txn.setNote("Hoàn tiền booking " + booking.getBookingCode());
        walletTransactionRepository.save(txn);
    }

    private User requireAdmin(HttpServletRequest request) {
        AuthenticatedUser current = authService.requireAccessUser(request);
        User user = userRepository.findById(current.userId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng."));
        boolean isAdmin = userRoleRepository.findByUser_Id(user.getId()).stream()
                .anyMatch(ur -> ur.getRole() != null
                        && RoleType.ADMIN.equals(ur.getRole().getCode()));
        if (!isAdmin) throw new UnauthorizedException("Bạn không có quyền admin.");
        return user;
    }

    private BookingResponse toBookingResponse(ShippingBooking b) {
        return BookingResponse.builder()
                .id(b.getId())
                .bookingCode(b.getBookingCode())
                .userId(b.getUser().getId())
                .userName(b.getUser().getFullName())
                .userPhone(b.getUser().getPhoneNumber())
                .petId(b.getPet().getId())
                .petName(b.getPetNameSnapshot())
                .areaId(b.getArea() != null ? b.getArea().getId() : null)
                .areaName(b.getArea() != null ? b.getArea().getName() : null)
                .serviceId(b.getService() != null ? b.getService().getId() : null)
                .serviceName(b.getService() != null ? b.getService().getName() : null)
                .appointmentDate(b.getAppointmentDate())
                .startTime(b.getStartTime())
                .endTime(b.getEndTime())
                .timeSlot(b.getTimeSlot())
                .bookingType(b.getBookingType())
                .shippingFee(b.getShippingFee())
                .priceAmount(b.getPriceAmount())
                .discountAmount(b.getDiscountAmount())
                .totalAmount(b.getTotalAmount())
                .promoCode(b.getPromoCode())
                .status(b.getStatus())
                .customerNote(b.getCustomerNote())
                .adminNote(b.getAdminNote())
                .createdAt(b.getCreatedAt() != null ? b.getCreatedAt().format(DT_FMT) : null)
                .updatedAt(b.getUpdatedAt() != null ? b.getUpdatedAt().format(DT_FMT) : null)
                .build();
    }
}
