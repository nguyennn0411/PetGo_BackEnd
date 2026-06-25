package com.example.petgo.service.impl;

import com.example.petgo.config.AuthenticatedUser;
import com.example.petgo.dto.*;
import com.example.petgo.entity.*;
import com.example.petgo.exception.BadRequestException;
import com.example.petgo.exception.ResourceNotFoundException;
import com.example.petgo.repository.*;
import com.example.petgo.service.AuthService;
import com.example.petgo.service.BookingService;
import com.example.petgo.service.PromotionPolicyService;
import com.example.petgo.service.RoutingService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final PetRepository petRepository;
    private final AreaRepository areaRepository;
    private final CatalogServiceRepository catalogServiceRepository;
    private final AreaServiceConfigRepository areaServiceConfigRepository;
    private final AreaScheduleRepository areaScheduleRepository;
    private final AreaScheduleOverrideRepository areaScheduleOverrideRepository;
    private final ShippingBookingRepository shippingBookingRepository;
    private final BookingStatusHistoryRepository bookingStatusHistoryRepository;
    private final WalletRepository walletRepository;
    private final WalletTransactionRepository walletTransactionRepository;
    private final ShippingFeeConfigRepository shippingFeeConfigRepository;
    private final PromotionPolicyService promotionPolicyService;
    private final RoutingService routingService;

    private static final int MINIMUM_LEAD_TIME_MINUTES = 60;
    private static final List<String> ACTIVE_STATUSES = List.of("PENDING", "CONFIRMED", "IN_PROGRESS");

    @Override
    @Transactional(readOnly = true)
    public CreateContextResponse getCreateContext(HttpServletRequest request, Long areaId) {
        AuthenticatedUser current = authService.requireAccessUser(request);
        User user = userRepository.findById(current.userId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng."));

        List<Pet> pets = petRepository.findActiveByOwnerUserId(user.getId());

        List<CreateContextResponse.AreaInfo> areaInfos;
        if (areaId != null) {
            Area area = areaRepository.findById(areaId)
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khu vực."));
            areaInfos = List.of(toAreaInfo(area));
        } else {
            areaInfos = areaRepository.findAllByOrderByNameAsc().stream()
                    .map(this::toAreaInfo).toList();
        }

        List<CreateContextResponse.ServiceInfo> serviceInfos;
        if (areaId != null) {
            serviceInfos = areaServiceConfigRepository.findActiveByAreaIdWithService(areaId).stream()
                    .map(c -> toServiceInfo(c.getService()))
                    .toList();
        } else {
            serviceInfos = catalogServiceRepository.findByActiveTrueOrderByNameAscIdAsc().stream()
                    .map(this::toServiceInfo).toList();
        }

        BigDecimal walletBalance = walletRepository.findByUserId(user.getId())
                .map(Wallet::getBalance)
                .orElse(BigDecimal.ZERO);

        return CreateContextResponse.builder()
                .pets(pets.stream().map(p -> CreateContextResponse.PetInfo.builder()
                        .id(p.getId()).name(p.getName()).breed(p.getBreed())
                        .avatarUrl(p.getAvatarUrl()).build()).toList())
                .areas(areaInfos)
                .services(serviceInfos)
                .walletBalance(walletBalance)
                .currencyCode("VND")
                .build();
    }

    @Override
    @Transactional
    public BookingCreateResponse createBooking(HttpServletRequest request, BookingCreateRequest req) {
        AuthenticatedUser current = authService.requireAccessUser(request);
        User user = userRepository.findById(current.userId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng."));

        Pet pet = petRepository.findOwnedActivePet(user.getId(), req.getPetId())
                .orElseThrow(() -> new BadRequestException("Thú cưng không hợp lệ."));

        Area area = areaRepository.findById(req.getAreaId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khu vực."));

        CatalogService service = catalogServiceRepository.findById(req.getServiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy dịch vụ."));

        AreaServiceConfig config = areaServiceConfigRepository
                .findByAreaIdAndServiceId(req.getAreaId(), req.getServiceId())
                .orElseThrow(() -> new BadRequestException("Dịch vụ không khả dụng trong khu vực này."));

        String bookingType = service.getBookingType() != null ? service.getBookingType() : "SHORT";
        int durationMinutes = service.getDefaultDurationMinutes() != null ? service.getDefaultDurationMinutes() : 60;
        LocalTime endTime = req.getStartTime().plusMinutes(durationMinutes);

        validateSchedule(area, req.getAppointmentDate(), req.getStartTime(), endTime);
        validateCapacity(area, service, config, bookingType, req.getAppointmentDate(), req.getStartTime(), endTime);

        BigDecimal priceAmount = service.getBasePriceAmount() != null ? service.getBasePriceAmount() : BigDecimal.ZERO;
        BigDecimal shippingFee = calculateShippingFee(area, req.getPickupLatitude(), req.getPickupLongitude());

        PromotionPolicyService.PromoPreview promoPreview = promotionPolicyService
                .previewForBooking(priceAmount, shippingFee, user, req.getPromoCode(), area.getId(),
                        service.getCategories() == null ? List.of()
                                : service.getCategories().stream().map(c -> c.getId()).toList());
        BigDecimal discountAmount = promoPreview != null && promoPreview.applied() ? promoPreview.discountAmount() : BigDecimal.ZERO;

        BigDecimal totalAmount = priceAmount.add(shippingFee).subtract(discountAmount);
        if (totalAmount.compareTo(BigDecimal.ZERO) < 0) totalAmount = BigDecimal.ZERO;

        Wallet wallet = walletRepository.findWithLockByUserId(user.getId())
                .orElseThrow(() -> new BadRequestException("Không tìm thấy ví người dùng."));

        if (wallet.getBalance().compareTo(totalAmount) < 0) {
            BigDecimal missing = totalAmount.subtract(wallet.getBalance());
            throw new BadRequestException("INSUFFICIENT_WALLET_BALANCE: Số dư ví không đủ. " +
                    "Còn thiếu " + missing.setScale(0, RoundingMode.HALF_UP) + " đ");
        }

        wallet.setBalance(wallet.getBalance().subtract(totalAmount));
        wallet.setHeldBalance(wallet.getHeldBalance() != null ? wallet.getHeldBalance().add(totalAmount) : totalAmount);
        walletRepository.save(wallet);

        String bookingCode = "BK-" + UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();

        ShippingBooking booking = new ShippingBooking();
        booking.setBookingCode(bookingCode);
        booking.setUser(user);
        booking.setPet(pet);
        booking.setArea(area);
        booking.setService(service);
        booking.setPetNameSnapshot(pet.getName());
        booking.setPetBreedSnapshot(pet.getBreed());
        booking.setAppointmentDate(req.getAppointmentDate());
        booking.setTimeSlot(req.getStartTime().toString() + "-" + endTime.toString());
        booking.setStartTime(req.getStartTime());
        booking.setEndTime(endTime);
        booking.setBookingType(bookingType);
        booking.setPriceAmount(priceAmount);
        booking.setShippingFee(shippingFee);
        booking.setDiscountAmount(discountAmount);
        booking.setTotalAmount(totalAmount);
        booking.setPickupLatitude(req.getPickupLatitude());
        booking.setPickupLongitude(req.getPickupLongitude());
        booking.setPickupAddress(req.getPickupAddress());
        booking.setPromoCode(req.getPromoCode());
        booking.setCustomerNote(req.getCustomerNote());
        booking.setStatus("PENDING");
        booking.setCurrencyCode("VND");
        booking = shippingBookingRepository.save(booking);

        if (promoPreview != null && promoPreview.applied()) {
            promotionPolicyService.recordBookingRedemption(promoPreview, user, booking);
        }

        String txnCode = "HOLD-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
        WalletTransaction txn = new WalletTransaction();
        txn.setTransactionCode(txnCode);
        txn.setWallet(wallet);
        txn.setUser(user);
        txn.setType("HOLD");
        txn.setStatus("HELD");
        txn.setAmount(totalAmount);
        txn.setBalanceBefore(wallet.getBalance().add(totalAmount));
        txn.setBalanceAfter(wallet.getBalance());
        txn.setNote("Giữ tiền booking " + bookingCode);
        walletTransactionRepository.save(txn);

        BookingStatusHistory history = new BookingStatusHistory();
        history.setBooking(booking);
        history.setFromStatus(null);
        history.setToStatus("PENDING");
        history.setChangedByType("USER");
        history.setChangedById(user.getId());
        history.setChangedByName(user.getFullName());
        history.setNote("Tạo booking");
        bookingStatusHistoryRepository.save(history);

        return BookingCreateResponse.builder()
                .id(booking.getId())
                .bookingCode(bookingCode)
                .status("PENDING")
                .appointmentDate(req.getAppointmentDate())
                .startTime(req.getStartTime())
                .endTime(endTime)
                .priceAmount(priceAmount)
                .shippingFee(shippingFee)
                .discountAmount(discountAmount)
                .totalAmount(totalAmount)
                .message("Đặt lịch thành công. Vui lòng chờ admin xác nhận.")
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> getMyBookings(HttpServletRequest request) {
        AuthenticatedUser current = authService.requireAccessUser(request);
        return shippingBookingRepository.findByUserIdOrderByCreatedAtDesc(current.userId())
                .stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public BookingResponse getMyBookingDetail(HttpServletRequest request, Long id) {
        AuthenticatedUser current = authService.requireAccessUser(request);
        ShippingBooking booking = shippingBookingRepository.findDetailedOwnedById(id, current.userId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy booking."));
        return toResponse(booking);
    }

    @Override
    @Transactional
    public BookingResponse cancelMyBooking(HttpServletRequest request, Long id) {
        AuthenticatedUser current = authService.requireAccessUser(request);
        ShippingBooking booking = shippingBookingRepository.findDetailedOwnedById(id, current.userId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy booking."));

        if (!"PENDING".equals(booking.getStatus())) {
            throw new BadRequestException("Chỉ có thể hủy booking ở trạng thái PENDING.");
        }

        refundToUser(booking);
        changeStatus(booking, "CANCELLED", "USER", current.userId(),
                userRepository.findById(current.userId()).map(User::getFullName).orElse(null),
                "Người dùng hủy booking");

        return toResponse(shippingBookingRepository.save(booking));
    }

    private void validateSchedule(Area area, LocalDate date, LocalTime startTime, LocalTime endTime) {
        if (date.isBefore(LocalDate.now())) {
            throw new BadRequestException("Ngày hẹn không được trong quá khứ.");
        }

        if (date.equals(LocalDate.now())) {
            if (startTime.isBefore(LocalTime.now().plusMinutes(MINIMUM_LEAD_TIME_MINUTES))) {
                throw new BadRequestException("Thời gian đặt phải trước giờ hẹn ít nhất " + MINIMUM_LEAD_TIME_MINUTES + " phút.");
            }
        }

        AreaScheduleOverride override = areaScheduleOverrideRepository
                .findByAreaIdAndOverrideDate(area.getId(), date).orElse(null);

        if (override != null && Boolean.TRUE.equals(override.getClosed())) {
            throw new BadRequestException("Khu vực đóng cửa vào ngày này.");
        }

        if (override != null && override.getOpenTime() != null && override.getCloseTime() != null) {
            if (startTime.isBefore(override.getOpenTime()) || endTime.isAfter(override.getCloseTime())) {
                throw new BadRequestException("Thời gian nằm ngoài giờ làm việc.");
            }
            return;
        }

        int dow = date.getDayOfWeek().getValue() % 7;
        AreaSchedule schedule = areaScheduleRepository.findByAreaIdAndDayOfWeek(area.getId(), dow)
                .orElse(null);

        if (schedule == null || !Boolean.TRUE.equals(schedule.getActive())) {
            throw new BadRequestException("Khu vực không có lịch làm việc vào ngày này.");
        }

        if (startTime.isBefore(schedule.getOpenTime()) || endTime.isAfter(schedule.getCloseTime())) {
            throw new BadRequestException("Thời gian nằm ngoài giờ làm việc.");
        }
    }

    private void validateCapacity(Area area, CatalogService service, AreaServiceConfig config,
                                   String bookingType, LocalDate date, LocalTime startTime, LocalTime endTime) {
        int maxSlots = "LONG".equals(bookingType) ? area.getLongSlots() : area.getShortSlots();

        if ("SHORT".equals(bookingType)) {
            List<ShippingBooking> overlaps = shippingBookingRepository.findOverlappingBookings(
                    area.getId(), date, "SHORT", ACTIVE_STATUSES, startTime, endTime);
            if (!overlaps.isEmpty()) {
                throw new BadRequestException("Khung giờ này đã có booking. Vui lòng chọn giờ khác.");
            }
        }

        List<ShippingBooking> concurrent = shippingBookingRepository
                .findByAreaIdAndAppointmentDateAndBookingTypeAndStatusIn(
                        area.getId(), date, bookingType, ACTIVE_STATUSES);

        long count = 0;
        for (ShippingBooking b : concurrent) {
            if (b.getStartTime() != null && b.getEndTime() != null) {
                if (startTime.isBefore(b.getEndTime()) && endTime.isAfter(b.getStartTime())) {
                    count++;
                }
            }
        }

        if (count >= maxSlots) {
            throw new BadRequestException("Khu vực đã hết chỗ cho khung giờ này.");
        }
    }

    private BigDecimal calculateShippingFee(Area area, BigDecimal pickupLat, BigDecimal pickupLng) {
        if (pickupLat == null || pickupLng == null) return BigDecimal.ZERO;
        if (area.getPickupLatitude() == null || area.getPickupLongitude() == null) return BigDecimal.ZERO;

        double distance = routingService.getDrivingDistanceKm(
                area.getPickupLatitude().doubleValue(),
                area.getPickupLongitude().doubleValue(),
                pickupLat.doubleValue(),
                pickupLng.doubleValue()
        );
        BigDecimal distanceKm = BigDecimal.valueOf(distance).setScale(2, RoundingMode.HALF_UP);

        List<ShippingFeeConfig> feeConfigs = shippingFeeConfigRepository
                .findByAreaIdAndActiveTrueOrderByFromKmAsc(area.getId());

        for (ShippingFeeConfig cfg : feeConfigs) {
            boolean inRange = distanceKm.compareTo(cfg.getFromKm()) >= 0;
            if (cfg.getToKm() != null) {
                inRange = inRange && distanceKm.compareTo(cfg.getToKm()) < 0;
            }
            if (inRange) return cfg.getFee();
        }

        if (!feeConfigs.isEmpty()) {
            ShippingFeeConfig last = feeConfigs.get(feeConfigs.size() - 1);
            if (last.getToKm() == null) return last.getFee();
        }

        return BigDecimal.ZERO;
    }

    private void refundToUser(ShippingBooking booking) {
        if (booking.getTotalAmount() == null || booking.getTotalAmount().compareTo(BigDecimal.ZERO) <= 0) return;

        Wallet wallet = walletRepository.findWithLockByUserId(booking.getUser().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy ví người dùng."));

        BigDecimal holdAmount = booking.getTotalAmount();

        if (wallet.getHeldBalance() != null && wallet.getHeldBalance().compareTo(holdAmount) >= 0) {
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
            txn.setNote("Hoàn tiền hủy booking " + booking.getBookingCode());
            walletTransactionRepository.save(txn);
        }
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

    private CreateContextResponse.AreaInfo toAreaInfo(Area area) {
        return CreateContextResponse.AreaInfo.builder()
                .id(area.getId()).name(area.getName())
                .pickupLatitude(area.getPickupLatitude())
                .pickupLongitude(area.getPickupLongitude())
                .pickupAddress(area.getPickupAddress())
                .pickupPhone(area.getPickupPhone())
                .pickupInstructions(area.getPickupInstructions())
                .build();
    }

    private CreateContextResponse.ServiceInfo toServiceInfo(CatalogService s) {
        Long catId = null;
        String catName = null;
        if (s.getCategories() != null && !s.getCategories().isEmpty()) {
            ServiceCategory first = s.getCategories().get(0);
            catId = first.getId();
            catName = first.getName();
        }
        return CreateContextResponse.ServiceInfo.builder()
                .id(s.getId()).name(s.getName())
                .bookingType(s.getBookingType())
                .defaultDurationMinutes(s.getDefaultDurationMinutes())
                .basePriceAmount(s.getBasePriceAmount())
                .currencyCode(s.getCurrencyCode())
                .priceUnit(s.getPriceUnit())
                .categoryId(catId)
                .categoryName(catName)
                .build();
    }

    private BookingResponse toResponse(ShippingBooking b) {
        return BookingResponse.builder()
                .id(b.getId()).bookingCode(b.getBookingCode())
                .userId(b.getUser().getId()).userName(b.getUser().getFullName())
                .userPhone(b.getUser().getPhoneNumber())
                .petId(b.getPet().getId()).petName(b.getPetNameSnapshot())
                .areaId(b.getArea() != null ? b.getArea().getId() : null)
                .areaName(b.getArea() != null ? b.getArea().getName() : null)
                .serviceId(b.getService() != null ? b.getService().getId() : null)
                .serviceName(b.getService() != null ? b.getService().getName() : null)
                .appointmentDate(b.getAppointmentDate())
                .startTime(b.getStartTime()).endTime(b.getEndTime())
                .timeSlot(b.getTimeSlot()).bookingType(b.getBookingType())
                .shippingFee(b.getShippingFee()).priceAmount(b.getPriceAmount())
                .discountAmount(b.getDiscountAmount()).totalAmount(b.getTotalAmount())
                .promoCode(b.getPromoCode()).status(b.getStatus())
                .customerNote(b.getCustomerNote()).adminNote(b.getAdminNote())
                .createdAt(b.getCreatedAt() != null ? b.getCreatedAt().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : null)
                .updatedAt(b.getUpdatedAt() != null ? b.getUpdatedAt().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : null)
                .build();
    }
}
