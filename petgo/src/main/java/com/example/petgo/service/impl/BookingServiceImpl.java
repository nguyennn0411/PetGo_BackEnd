package com.example.petgo.service.impl;

import com.example.petgo.dto.*;
import com.example.petgo.entity.*;
import com.example.petgo.exception.BadRequestException;
import com.example.petgo.exception.InsufficientWalletBalanceException;
import com.example.petgo.exception.ResourceNotFoundException;
import com.example.petgo.repository.*;
import com.example.petgo.config.AuthenticatedUser;
import com.example.petgo.service.AuthService;
import com.example.petgo.service.BookingService;
import com.example.petgo.service.ChatService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

        private static final ZoneId APP_ZONE = ZoneId.of("Asia/Ho_Chi_Minh");
        private static final DateTimeFormatter ISO_DATE = DateTimeFormatter.ISO_LOCAL_DATE;
        private static final DateTimeFormatter VIEW_DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

        private final UserRepository userRepository;
        private final PetRepository petRepository;
        private final ProviderProfileRepository providerProfileRepository;
        private final ProviderServiceRepository providerServiceRepository;
        private final ProviderPhotoRepository providerPhotoRepository;
        private final BookingRepository bookingRepository;
        private final BookingStatusHistoryRepository bookingStatusHistoryRepository;
        private final BookingNotificationService bookingNotificationService;
        private final BookingAvailabilityServiceImpl bookingAvailabilityService;
        private final AuthService authService;
        private final WalletRepository walletRepository;
        private final WalletTransactionRepository walletTransactionRepository;
        private final ChatService chatService;

        @Value("${app.providers.slot-lookahead-days:7}")
        private int slotLookaheadDays;

        @Value("${app.bookings.timezone:Asia/Ho_Chi_Minh}")
        private String bookingTimezone;

        @Value("${app.bookings.minimum-lead-time-minutes:60}")
        private int minimumLeadTimeMinutes;

        @Override
        @Transactional(readOnly = true)
        public BookingCreateContextResponse getCreateContext(Long ownerUserId,
                        Long providerId,
                        Long providerServiceId,
                        String slotDate,
                        String time) {
                User owner = userRepository.findById(ownerUserId)
                                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng đặt lịch"));

                ProviderProfile provider = providerProfileRepository.findActiveById(providerId)
                                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhà cung cấp"));

                List<Pet> pets = petRepository.findActiveByOwnerUserId(ownerUserId);
                List<ProviderService> services = providerServiceRepository.findActiveDetailsByProviderId(providerId);
                if (services.isEmpty()) {
                        throw new BadRequestException("Nhà cung cấp chưa có dịch vụ hoạt động");
                }

                Long resolvedServiceId = resolveProviderServiceId(providerServiceId, services);
                LocalDate resolvedDate = parseFlexibleDate(slotDate);
                LocalTime resolvedTime = parseFlexibleTime(time);

                if (resolvedServiceId == null) {
                        resolvedServiceId = services.get(0).getId();
                }

                final Long finalResolvedServiceId = resolvedServiceId;
                ProviderService selectedService = services.stream()
                                .filter(service -> Objects.equals(service.getId(), finalResolvedServiceId))
                                .findFirst()
                                .orElse(services.get(0));

                return BookingCreateContextResponse.builder()
                                .ownerUserId(owner.getId())
                                .provider(mapProvider(provider))
                                .pets(pets.stream().map(this::mapPet).toList())
                                .services(services.stream().map(this::mapService).toList())
                                .availableDates(List.of())
                                .slots(List.of())
                                .selectedProviderId(provider.getId())
                                .selectedProviderServiceId(finalResolvedServiceId)
                                .selectedDate(resolvedDate != null ? resolvedDate.format(ISO_DATE) : null)
                                .selectedTime(resolvedTime != null ? resolvedTime.format(TIME_FORMATTER) : null)
                                .selectedSlotId(null)
                                .walletBalance(
                                                walletRepository.findByUserId(owner.getId()).map(Wallet::getBalance)
                                                                .orElse(BigDecimal.ZERO))
                                .walletCurrencyCode(
                                                walletRepository.findByUserId(owner.getId())
                                                                .map(Wallet::getCurrencyCode).orElse("VND"))
                                .build();
        }

        @Override
        @Transactional(readOnly = true)
        public BookingCreateContextResponse getCreateContext(HttpServletRequest request, Long providerId,
                        Long providerServiceId, String slotDate, String time) {
                AuthenticatedUser current = authService.requireAccessUser(request);
                return getCreateContext(current.userId(), providerId, providerServiceId, slotDate, time);
        }

        @Override
        public BookingAvailabilityResponse getAvailableDates(Long providerId, Long providerServiceId, LocalDate from,
                        Integer days) {
                LocalDate start = Optional.ofNullable(from).orElse(LocalDate.now(APP_ZONE));
                int range = Math.min(Math.max(Optional.ofNullable(days).orElse(14), 1), 60);
                ProviderService service = resolveActiveProviderService(providerId, providerServiceId);
                if (service == null) {
                        throw new ResourceNotFoundException("Không tìm thấy dịch vụ của nhà cung cấp");
                }
                Long resolvedProviderServiceId = service.getId();
                List<BookingSlotOptionResponse> dateOptions = new ArrayList<>();
                for (int i = 0; i < range; i++) {
                        LocalDate date = start.plusDays(i);
                        try {
                                BookingAvailabilityResponse availability = bookingAvailabilityService.getAvailability(
                                                providerId,
                                                resolvedProviderServiceId, date, service.getDurationMinutes());
                                boolean available = availability.slots() != null && availability.slots().stream()
                                                .anyMatch(slot -> "AVAILABLE".equalsIgnoreCase(slot.status())
                                                                && Optional.ofNullable(slot.capacityRemaining())
                                                                                .orElse(0) > 0);
                                dateOptions.add(BookingSlotOptionResponse.builder()
                                                .providerServiceId(resolvedProviderServiceId)
                                                .date(date.format(ISO_DATE))
                                                .label(date.format(ISO_DATE))
                                                .capacityRemaining(available ? 1 : 0)
                                                .selected(false)
                                                .status(available ? "AVAILABLE"
                                                                : firstNonBlank(availability.status(), "FULL"))
                                                .reason(available ? null
                                                                : firstNonBlank(availability.reason(),
                                                                                "Ngày này chưa có slot khả dụng"))
                                                .build());
                        } catch (RuntimeException ex) {
                                if (isUnexpectedAvailabilityFailure(ex)) {
                                        throw new BadRequestException(
                                                        "Không thể đọc lịch ngày " + date.format(ISO_DATE) + ": "
                                                                        + firstNonBlank(ex.getMessage(),
                                                                                        ex.getClass().getSimpleName()));
                                }
                                dateOptions.add(BookingSlotOptionResponse.builder()
                                                .providerServiceId(resolvedProviderServiceId)
                                                .date(date.format(ISO_DATE))
                                                .label(date.format(ISO_DATE))
                                                .capacityRemaining(0)
                                                .selected(false)
                                                .status("NOT_CONFIGURED")
                                                .reason(ex.getMessage())
                                                .build());
                        }
                }
                return BookingAvailabilityResponse.builder()
                                .providerId(providerId)
                                .providerServiceId(resolvedProviderServiceId)
                                .date(start.format(ISO_DATE))
                                .timezone(APP_ZONE.getId())
                                .durationMinutes(service.getDurationMinutes())
                                .status(dateOptions.stream().anyMatch(
                                                item -> "AVAILABLE".equalsIgnoreCase(item.status())) ? "AVAILABLE"
                                                                : "FULL")
                                .reason(dateOptions.stream()
                                                .anyMatch(item -> "AVAILABLE".equalsIgnoreCase(item.status())) ? null
                                                                : "Không có ngày khả dụng")
                                .slots(dateOptions)
                                .build();
        }

        @Override
        @Transactional(readOnly = true)
        public BookingAvailabilityResponse getAvailableSlots(Long providerId, Long providerServiceId, LocalDate date) {
                ProviderService service = resolveActiveProviderService(providerId, providerServiceId);
                return bookingAvailabilityService.getAvailability(providerId, service.getId(), date,
                                service.getDurationMinutes());
        }

        @Override
        @Transactional
        public BookingSummaryResponse createBooking(BookingCreateRequest request) {
                if (request.ownerUserId() == null) {
                        throw new BadRequestException("Thiếu thông tin người dùng đặt lịch.");
                }
                User owner = userRepository.findById(request.ownerUserId())
                                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng đặt lịch"));

                Pet pet = petRepository.findOwnedActivePet(request.ownerUserId(), request.petId())
                                .orElseThrow(() -> new BadRequestException(
                                                "Thú cưng không hợp lệ hoặc không thuộc người dùng này"));

                ProviderProfile provider = providerProfileRepository.findActiveById(request.providerId())
                                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhà cung cấp"));

                ProviderService providerService = providerServiceRepository
                                .findActiveDetailById(request.providerServiceId())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Không tìm thấy dịch vụ của nhà cung cấp"));

                if (!Objects.equals(providerService.getProvider().getId(), provider.getId())) {
                        throw new BadRequestException("Dịch vụ không thuộc nhà cung cấp đã chọn");
                }

                LocalDate appointmentDate = request.appointmentDate();
                LocalTime startTime = request.startTime();
                LocalTime endTime = Optional.ofNullable(request.endTime()).orElse(
                                startTime.plusMinutes(
                                                Optional.ofNullable(providerService.getDurationMinutes()).orElse(30)));
                validateGeneratedAvailability(provider, providerService, appointmentDate, startTime, endTime);

                String providerAddress = buildProviderAddress(provider);
                String serviceName = firstNonBlank(providerService.getCustomName(),
                                providerService.getService().getName(),
                                "Dịch vụ PetGo");
                String serviceDescription = abbreviate(firstNonBlank(
                                providerService.getShortDescription(),
                                providerService.getDescription(),
                                providerService.getService().getShortDescription(),
                                providerService.getService().getDescription()), 255);

                BigDecimal subtotal = defaultMoney(providerService.getPriceAmount());
                BigDecimal total = subtotal;

                Booking booking = new Booking();
                booking.setBookingCode(generateBookingCode(provider, owner));
                booking.setCustomerUser(owner);
                booking.setProvider(provider);
                booking.setPet(pet);
                booking.setProviderService(providerService);
                booking.setAvailabilitySlot(null);
                booking.setAppointmentDate(appointmentDate);
                booking.setStartTime(startTime);
                booking.setEndTime(endTime);
                booking.setTimezone(firstNonBlank(bookingTimezone, "Asia/Ho_Chi_Minh"));
                booking.setStatus("PENDING_PROVIDER_CONFIRMATION");
                booking.setCustomerNote(normalizeBlank(request.customerNote()));
                booking.setInternalNote(null);
                booking.setRescheduleCount(0);
                booking.setProviderNameSnapshot(provider.getBusinessName());
                booking.setProviderPhoneSnapshot(firstNonBlank(provider.getEmergencyPhone()));
                booking.setProviderAddressSnapshot(abbreviate(providerAddress, 255));
                booking.setServiceNameSnapshot(serviceName);
                booking.setServiceDescriptionSnapshot(serviceDescription);
                booking.setServiceDurationMinutesSnapshot(
                                Optional.ofNullable(providerService.getDurationMinutes()).orElse(30));
                booking.setPetNameSnapshot(pet.getName());
                booking.setPetBreedSnapshot(pet.getBreed());
                booking.setSubtotalAmount(subtotal);
                booking.setMembershipDiscountAmount(BigDecimal.ZERO);
                booking.setPromoDiscountAmount(BigDecimal.ZERO);
                booking.setTaxAmount(BigDecimal.ZERO);
                booking.setTotalAmount(total);
                booking.setCurrencyCode(
                                firstNonBlank(providerService.getCurrencyCode(), provider.getCurrencyCode(), "VND"));

                Booking saved = bookingRepository.save(booking);
                holdWalletEscrow(saved, owner, total);

                BookingStatusHistory history = new BookingStatusHistory();
                history.setBooking(saved);
                history.setFromStatus(null);
                history.setToStatus(saved.getStatus());
                history.setChangedByUser(owner);
                history.setNote("User đặt lịch, tiền ví đã được giữ trong escrow/admin hold, chờ provider xác nhận nhận lịch");
                bookingStatusHistoryRepository.save(history);

                bookingNotificationService.notifyProviderBookingCreated(saved);

                return mapSummary(saved);
        }

        @Override
        @Transactional
        public BookingSummaryResponse createBooking(HttpServletRequest request, BookingCreateRequest createRequest) {
                AuthenticatedUser current = authService.requireAccessUser(request);
                BookingCreateRequest authenticatedRequest = new BookingCreateRequest(
                                current.userId(),
                                createRequest.petId(),
                                createRequest.providerId(),
                                createRequest.providerServiceId(),
                                createRequest.slotId(),
                                createRequest.appointmentDate(),
                                createRequest.startTime(),
                                createRequest.endTime(),
                                createRequest.customerNote());
                return createBooking(authenticatedRequest);
        }

        @Override
        @Transactional(readOnly = true)
        public BookingSummaryResponse getBookingSummary(Long bookingId) {
                Booking booking = bookingRepository.findDetailedById(bookingId)
                                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy booking"));
                return mapSummary(booking);
        }

        @Override
        @Transactional
        public BookingMutationResponse confirmCompletedByUser(HttpServletRequest request, Long bookingId) {
                AuthenticatedUser current = authService.requireAccessUser(request);
                Booking booking = bookingRepository.findDetailedOwnedById(current.userId(), bookingId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Không tìm thấy booking của người dùng này"));
                String status = firstNonBlank(booking.getStatus(), "").toUpperCase(Locale.ROOT);
                if (!List.of("CONFIRMED", "IN_PROGRESS", "AWAITING_COMPLETION_CONFIRMATION", "COMPLETED_BY_PROVIDER")
                                .contains(status)) {
                        throw new BadRequestException("Booking hiện không thể xác nhận hoàn tất.");
                }
                String nextStatus = "COMPLETED_BY_PROVIDER".equals(status) ? "COMPLETED" : "COMPLETED_BY_USER";
                String previous = booking.getStatus();
                booking.setStatus(nextStatus);
                bookingRepository.save(booking);

                BookingStatusHistory history = new BookingStatusHistory();
                history.setBooking(booking);
                history.setFromStatus(previous);
                history.setToStatus(nextStatus);
                history.setChangedByUser(booking.getCustomerUser());
                history.setNote("User xác nhận hoàn tất dịch vụ");
                bookingStatusHistoryRepository.save(history);

                // Giải ngân escrow ngay khi booking hoàn tất
                if ("COMPLETED".equals(nextStatus)) {
                        releaseBookingEscrowIfCompleted(booking);
                }

                return BookingMutationResponse.builder()
                                .bookingId(booking.getId())
                                .bookingCode(booking.getBookingCode())
                                .status(booking.getStatus())
                                .statusLabel(mapStatusLabel(booking.getStatus()))
                                .appointmentDate(
                                                booking.getAppointmentDate() != null
                                                                ? booking.getAppointmentDate().format(ISO_DATE)
                                                                : null)
                                .appointmentDateDisplay(
                                                booking.getAppointmentDate() != null
                                                                ? booking.getAppointmentDate().format(VIEW_DATE)
                                                                : null)
                                .appointmentTime(formatTime(booking.getStartTime())
                                                + (booking.getEndTime() != null
                                                                ? " - " + formatTime(booking.getEndTime())
                                                                : ""))
                                .slotId(null)
                                .message("Đã xác nhận hoàn tất dịch vụ.")
                                .refundAmount(BigDecimal.ZERO)
                                .refundAmountDisplay(formatMoney(BigDecimal.ZERO))
                                .refundStatus(null)
                                .build();
        }

        @Override
        @Transactional
        public BookingMutationResponse createDispute(HttpServletRequest request, Long bookingId,
                        BookingDisputeRequest disputeRequest) {
                AuthenticatedUser current = authService.requireAccessUser(request);
                Booking booking = bookingRepository.findDetailedOwnedById(current.userId(), bookingId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Không tìm thấy booking của người dùng này"));
                String status = firstNonBlank(booking.getStatus(), "").toUpperCase(Locale.ROOT);
                if (!List.of("CONFIRMED", "IN_PROGRESS", "AWAITING_COMPLETION_CONFIRMATION", "COMPLETED_BY_USER",
                                "COMPLETED_BY_PROVIDER").contains(status)) {
                        throw new BadRequestException("Booking hiện không thể tạo khiếu nại.");
                }
                String previous = booking.getStatus();
                String reason = normalizeBlank(disputeRequest.reason());
                booking.setStatus("DISPUTED");
                booking.setInternalNote(abbreviate(firstNonBlank(booking.getInternalNote(), "")
                                + "\n[USER_DISPUTE] " + reason, 2000));
                bookingRepository.save(booking);

                BookingStatusHistory history = new BookingStatusHistory();
                history.setBooking(booking);
                history.setFromStatus(previous);
                history.setToStatus("DISPUTED");
                history.setChangedByUser(booking.getCustomerUser());
                history.setNote("User tạo khiếu nại: " + reason);
                bookingStatusHistoryRepository.save(history);

                chatService.ensureAdminBookingDisputeChat(booking.getId());
                bookingNotificationService.notifyBookingDisputed(booking, reason);

                return BookingMutationResponse.builder()
                                .bookingId(booking.getId())
                                .bookingCode(booking.getBookingCode())
                                .status(booking.getStatus())
                                .statusLabel(mapStatusLabel(booking.getStatus()))
                                .appointmentDate(
                                                booking.getAppointmentDate() != null
                                                                ? booking.getAppointmentDate().format(ISO_DATE)
                                                                : null)
                                .appointmentDateDisplay(
                                                booking.getAppointmentDate() != null
                                                                ? booking.getAppointmentDate().format(VIEW_DATE)
                                                                : null)
                                .appointmentTime(formatTime(booking.getStartTime())
                                                + (booking.getEndTime() != null
                                                                ? " - " + formatTime(booking.getEndTime())
                                                                : ""))
                                .slotId(null)
                                .message("Đã gửi khiếu nại, booking sẽ chờ admin xử lý.")
                                .refundAmount(BigDecimal.ZERO)
                                .refundAmountDisplay(formatMoney(BigDecimal.ZERO))
                                .refundStatus(null)
                                .build();
        }

        private void validateGeneratedAvailability(ProviderProfile provider,
                        ProviderService providerService,
                        LocalDate appointmentDate,
                        LocalTime startTime,
                        LocalTime endTime) {
                if (appointmentDate == null || startTime == null || endTime == null) {
                        throw new BadRequestException("Vui lòng chọn ngày và giờ hẹn hợp lệ");
                }
                BookingAvailabilityResponse availability = bookingAvailabilityService.getAvailability(provider.getId(),
                                providerService.getId(), appointmentDate, providerService.getDurationMinutes());
                boolean available = availability.slots() != null && availability.slots().stream()
                                .anyMatch(slot -> Objects.equals(parseFlexibleDate(slot.date()), appointmentDate)
                                                && Objects.equals(parseFlexibleTime(slot.startTime()), startTime)
                                                && Objects.equals(parseFlexibleTime(slot.endTime()), endTime)
                                                && Optional.ofNullable(slot.capacityRemaining()).orElse(0) > 0);
                if (!available) {
                        throw new BadRequestException("Khung giờ đã chọn không còn khả dụng");
                }
        }

        private void holdWalletEscrow(Booking booking, User owner, BigDecimal amount) {
                BigDecimal total = defaultMoney(amount);
                Wallet wallet = getOrCreateWalletWithLock(owner);
                if (!"ACTIVE".equalsIgnoreCase(Optional.ofNullable(wallet.getStatus()).orElse("ACTIVE"))) {
                        throw new BadRequestException("Ví PetGo của bạn hiện không khả dụng để thanh toán booking.");
                }
                BigDecimal before = defaultMoney(wallet.getBalance());
                if (before.compareTo(total) < 0) {
                        BigDecimal missing = total.subtract(before);
                        throw new InsufficientWalletBalanceException(total, before, missing, "/wallet");
                }
                wallet.setBalance(before.subtract(total));
                walletRepository.save(wallet);

                WalletTransaction tx = new WalletTransaction();
                tx.setTransactionCode(generateWalletTransactionCode("BOOKING_ESCROW"));
                tx.setWallet(wallet);
                tx.setUser(owner);
                tx.setType("BOOKING_ESCROW_HOLD");
                tx.setStatus("HELD_BY_ADMIN");
                tx.setAmount(total);
                tx.setGatewayTransactionId("BOOKING:" + booking.getId());
                tx.setBalanceBefore(before);
                tx.setBalanceAfter(wallet.getBalance());
                tx.setNote("Giữ tiền booking trong escrow/admin hold, chờ provider xác nhận và hoàn tất dịch vụ.");
                walletTransactionRepository.save(tx);
        }

        private void releaseBookingEscrowIfCompleted(Booking booking) {
                if (booking == null || booking.getProvider() == null || booking.getProvider().getUser() == null) {
                        return;
                }
                try {
                        WalletTransaction holdTx = walletTransactionRepository
                                        .findFirstWithLockByGatewayTransactionIdAndTypeAndStatusOrderByCreatedAtDescIdDesc(
                                                        "BOOKING:" + booking.getId(), "BOOKING_ESCROW_HOLD",
                                                        "HELD_BY_ADMIN")
                                        .orElse(null);
                        if (holdTx == null) {
                                return;
                        }
                        // Cộng tiền vào ví provider
                        Wallet providerWallet = getOrCreateWalletWithLock(booking.getProvider().getUser());
                        BigDecimal before = defaultMoney(providerWallet.getBalance());
                        providerWallet.setBalance(before.add(holdTx.getAmount()));
                        walletRepository.save(providerWallet);

                        // Tạo transaction credit cho provider
                        WalletTransaction creditTx = new WalletTransaction();
                        creditTx.setTransactionCode(generateWalletTransactionCode("BOOKING_RELEASE"));
                        creditTx.setWallet(providerWallet);
                        creditTx.setUser(booking.getProvider().getUser());
                        creditTx.setType("BOOKING_ESCROW_RELEASE");
                        creditTx.setStatus("COMPLETED");
                        creditTx.setAmount(holdTx.getAmount());
                        creditTx.setGatewayTransactionId("BOOKING:" + booking.getId());
                        creditTx.setBalanceBefore(before);
                        creditTx.setBalanceAfter(providerWallet.getBalance());
                        creditTx.setNote("Giải ngân escrow booking " + booking.getBookingCode() + " sau khi hoàn tất.");
                        walletTransactionRepository.save(creditTx);

                        // Cập nhật status hold transaction
                        holdTx.setStatus("RELEASED_TO_PROVIDER");
                        holdTx.setReviewNote("Auto released to provider when booking completed.");
                        walletTransactionRepository.save(holdTx);
                } catch (Exception e) {
                        // Log error nhưng không fail booking completion
                        System.err.println("Lỗi giải ngân escrow booking " + booking.getId() + ": " + e.getMessage());
                }
        }

        private Wallet getOrCreateWalletWithLock(User owner) {
                walletRepository.findByUserId(owner.getId()).orElseGet(() -> {
                        Wallet wallet = new Wallet();
                        wallet.setUser(owner);
                        return walletRepository.save(wallet);
                });
                return walletRepository.findWithLockByUserId(owner.getId())
                                .orElseThrow(() -> new ResourceNotFoundException("Ví chưa được khởi tạo."));
        }

        private String generateWalletTransactionCode(String prefix) {
                return prefix.replace("_", "") + "-"
                                + UUID.randomUUID().toString().replace("-", "").substring(0, 12)
                                                .toUpperCase(Locale.ROOT);
        }

        private BookingProviderOptionResponse mapProvider(ProviderProfile provider) {
                String image = firstNonBlank(provider.getMainImageUrl(), provider.getCoverImageUrl(),
                                firstProviderPhoto(provider.getId()));
                return BookingProviderOptionResponse.builder()
                                .id(provider.getId())
                                .name(provider.getBusinessName())
                                .headline(provider.getHeadline())
                                .address(buildProviderAddress(provider))
                                .rating(provider.getAverageRating())
                                .instantBooking(Boolean.TRUE.equals(provider.getAcceptsInstantBooking()))
                                .image(image)
                                .build();
        }

        private BookingPetOptionResponse mapPet(Pet pet) {
                String breed = firstNonBlank(pet.getBreed(), translateSpecies(pet.getSpecies()));
                String label = pet.getName() + (breed != null ? " (" + breed + ")" : "");
                return BookingPetOptionResponse.builder()
                                .id(pet.getId())
                                .name(pet.getName())
                                .species(pet.getSpecies())
                                .breed(pet.getBreed())
                                .avatarUrl(pet.getAvatarUrl())
                                .ageLabel(pet.getAgeLabel())
                                .label(label)
                                .build();
        }

        private BookingServiceOptionResponse mapService(ProviderService service) {
                return BookingServiceOptionResponse.builder()
                                .id(service.getId())
                                .serviceId(service.getService() != null ? service.getService().getId() : null)
                                .name(firstNonBlank(service.getCustomName(),
                                                service.getService() != null ? service.getService().getName() : null,
                                                "Dịch vụ"))
                                .description(firstNonBlank(service.getShortDescription(), service.getDescription(),
                                                service.getService() != null
                                                                ? service.getService().getShortDescription()
                                                                : null))
                                .durationMinutes(service.getDurationMinutes())
                                .durationLabel(formatDuration(service.getDurationMinutes()))
                                .priceAmount(service.getPriceAmount())
                                .priceDisplay(formatMoney(service.getPriceAmount()))
                                .currencyCode(firstNonBlank(service.getCurrencyCode(),
                                                service.getService() != null ? service.getService().getCurrencyCode()
                                                                : null,
                                                "VND"))
                                .priceUnit(service.getPriceUnit())
                                .featured(Boolean.TRUE.equals(service.getFeatured()))
                                .categoryId(service.getService() != null && service.getService().getCategory() != null
                                                ? service.getService().getCategory().getId()
                                                : null)
                                .categoryName(service.getService() != null && service.getService().getCategory() != null
                                                ? service.getService().getCategory().getName()
                                                : null)
                                .build();
        }

        private BookingSummaryResponse mapSummary(Booking booking) {
                return BookingSummaryResponse.builder()
                                .bookingId(booking.getId())
                                .bookingCode(booking.getBookingCode())
                                .status(booking.getStatus())
                                .ownerUserId(booking.getCustomerUser() != null ? booking.getCustomerUser().getId()
                                                : null)
                                .petId(booking.getPet() != null ? booking.getPet().getId() : null)
                                .providerId(booking.getProvider() != null ? booking.getProvider().getId() : null)
                                .providerServiceId(booking.getProviderService() != null
                                                ? booking.getProviderService().getId()
                                                : null)
                                .slotId(booking.getAvailabilitySlot() != null ? booking.getAvailabilitySlot().getId()
                                                : null)
                                .providerName(booking.getProviderNameSnapshot())
                                .providerPhone(booking.getProviderPhoneSnapshot())
                                .providerAddress(booking.getProviderAddressSnapshot())
                                .serviceName(booking.getServiceNameSnapshot())
                                .serviceDurationMinutes(booking.getServiceDurationMinutesSnapshot())
                                .petName(booking.getPetNameSnapshot())
                                .petBreed(booking.getPetBreedSnapshot())
                                .appointmentDate(
                                                booking.getAppointmentDate() != null
                                                                ? booking.getAppointmentDate().format(VIEW_DATE)
                                                                : null)
                                .startTime(formatTime(booking.getStartTime()))
                                .endTime(formatTime(booking.getEndTime()))
                                .subtotalAmount(booking.getSubtotalAmount())
                                .totalAmount(booking.getTotalAmount())
                                .totalAmountDisplay(formatMoney(booking.getTotalAmount()))
                                .currencyCode(booking.getCurrencyCode())
                                .customerNote(booking.getCustomerNote())
                                .createdAt(
                                                booking.getCreatedAt() != null
                                                                ? booking.getCreatedAt().atZone(APP_ZONE)
                                                                                .format(DateTimeFormatter.ofPattern(
                                                                                                "dd/MM/yyyy HH:mm"))
                                                                : null)
                                .build();
        }

        private Long resolveProviderServiceId(Long providerServiceId, List<ProviderService> services) {
                if (providerServiceId != null) {
                        boolean matchesProviderServiceId = services.stream()
                                        .anyMatch(service -> Objects.equals(service.getId(), providerServiceId));
                        if (matchesProviderServiceId) {
                                return providerServiceId;
                        }
                        return services.stream()
                                        .filter(service -> service.getService() != null
                                                        && Objects.equals(service.getService().getId(),
                                                                        providerServiceId))
                                        .map(ProviderService::getId)
                                        .findFirst()
                                        .orElse(providerServiceId);
                }
                return services.stream().filter(ps -> Boolean.TRUE.equals(ps.getFeatured())).map(ProviderService::getId)
                                .findFirst().orElse(null);
        }

        private ProviderService resolveActiveProviderService(Long providerId, Long providerServiceOrCatalogServiceId) {
                if (providerServiceOrCatalogServiceId == null) {
                        throw new BadRequestException("Thiếu thông tin dịch vụ để kiểm tra ngày hẹn");
                }
                ProviderService providerService = providerServiceRepository
                                .findActiveDetailById(providerServiceOrCatalogServiceId)
                                .orElse(null);
                if (providerService != null && providerService.getProvider() != null
                                && Objects.equals(providerService.getProvider().getId(), providerId)) {
                        return providerService;
                }
                return providerServiceRepository
                                .findActiveDetailsByProviderIdAndCatalogServiceId(providerId,
                                                providerServiceOrCatalogServiceId)
                                .stream()
                                .findFirst()
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Không tìm thấy dịch vụ của nhà cung cấp"));
        }

        private boolean isUnexpectedAvailabilityFailure(RuntimeException ex) {
                return !(ex instanceof BadRequestException) && !(ex instanceof ResourceNotFoundException);
        }

        private String buildProviderAddress(ProviderProfile provider) {
                List<String> parts = new ArrayList<>();
                addIfPresent(parts, provider.getPrimaryAddressLine1());
                addIfPresent(parts, provider.getWard());
                addIfPresent(parts, provider.getDistrict());
                addIfPresent(parts, provider.getCity());
                addIfPresent(parts, provider.getProvince());
                return String.join(", ", parts);
        }

        private String firstProviderPhoto(Long providerId) {
                return providerPhotoRepository.findImagesByProviderId(providerId).stream()
                                .map(ProviderPhoto::getPhotoUrl)
                                .filter(Objects::nonNull)
                                .filter(url -> !url.isBlank())
                                .findFirst()
                                .orElse(null);
        }

        private String formatDuration(Integer minutes) {
                if (minutes == null || minutes <= 0)
                        return "Chưa rõ";
                if (minutes < 60)
                        return minutes + " phút";
                int hours = minutes / 60;
                int remain = minutes % 60;
                return remain == 0 ? hours + " giờ" : hours + " giờ " + remain + " phút";
        }

        private String formatMoney(BigDecimal amount) {
                if (amount == null)
                        return "0";
                return String.format(Locale.forLanguageTag("vi-VN"), "%,.0f đ", amount);
        }

        private String formatTime(LocalTime time) {
                return time != null ? time.format(TIME_FORMATTER) : null;
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

        private String mapStatusLabel(String status) {
                if (status == null)
                        return "Chưa rõ";
                return switch (status.toUpperCase(Locale.ROOT)) {
                        case "PENDING_PROVIDER_CONFIRMATION" -> "Chờ provider xác nhận";
                        case "CONFIRMED" -> "Đã xác nhận";
                        case "IN_PROGRESS" -> "Đang phục vụ";
                        case "COMPLETED_BY_USER" -> "User đã xác nhận hoàn tất";
                        case "COMPLETED_BY_PROVIDER" -> "Provider đã xác nhận hoàn tất";
                        case "COMPLETED" -> "Hoàn thành";
                        case "REJECTED" -> "Provider từ chối";
                        case "CANCELLED" -> "Đã hủy";
                        default -> status;
                };
        }

        private void addIfPresent(List<String> parts, String value) {
                if (value != null && !value.isBlank()) {
                        parts.add(value.trim());
                }
        }

        private BigDecimal defaultMoney(BigDecimal value) {
                return value != null ? value : BigDecimal.ZERO;
        }

        private String abbreviate(String value, int max) {
                if (value == null)
                        return null;
                String trimmed = value.trim();
                return trimmed.length() <= max ? trimmed : trimmed.substring(0, max);
        }

        private String normalizeBlank(String value) {
                return value == null || value.isBlank() ? null : value.trim();
        }

        private LocalDate parseFlexibleDate(String value) {
                if (value == null || value.isBlank())
                        return null;
                for (DateTimeFormatter formatter : List.of(ISO_DATE, VIEW_DATE)) {
                        try {
                                return LocalDate.parse(value.trim(), formatter);
                        } catch (DateTimeParseException ignored) {
                        }
                }
                return null;
        }

        private LocalTime parseFlexibleTime(String value) {
                if (value == null || value.isBlank())
                        return null;
                String normalized = value.trim().toUpperCase(Locale.ROOT);
                List<DateTimeFormatter> formatters = List.of(
                                TIME_FORMATTER,
                                DateTimeFormatter.ofPattern("H:mm"),
                                DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH),
                                DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH));
                for (DateTimeFormatter formatter : formatters) {
                        try {
                                return LocalTime.parse(normalized, formatter);
                        } catch (DateTimeParseException ignored) {
                        }
                }
                return null;
        }

        private String translateSpecies(String species) {
                if (species == null)
                        return null;
                String normalized = Normalizer.normalize(species, Normalizer.Form.NFD)
                                .replaceAll("\\p{M}", "")
                                .toUpperCase(Locale.ROOT);
                return switch (normalized) {
                        case "DOG" -> "Chó";
                        case "CAT" -> "Mèo";
                        case "BIRD" -> "Chim";
                        case "RABBIT" -> "Thỏ";
                        case "HAMSTER" -> "Hamster";
                        case "REPTILE" -> "Bò sát";
                        default -> species;
                };
        }

        private String generateBookingCode(ProviderProfile provider, User owner) {
                String prefix = "BK";
                String providerCode = provider.getProviderCode() != null && provider.getProviderCode().length() >= 4
                                ? provider.getProviderCode()
                                                .substring(Math.max(0, provider.getProviderCode().length() - 4))
                                                .toUpperCase(Locale.ROOT)
                                : String.format(Locale.ROOT, "%04d", provider.getId());
                String userCode = String.format(Locale.ROOT, "%04d", owner.getId());
                String random = UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase(Locale.ROOT);
                return prefix + providerCode + userCode + random;
        }
}