package com.example.petgo.service.impl;

import com.example.petgo.dto.*;
import com.example.petgo.entity.*;
import com.example.petgo.exception.BadRequestException;
import com.example.petgo.exception.ResourceNotFoundException;
import com.example.petgo.repository.*;
import com.example.petgo.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.Normalizer;
import java.time.LocalDate;
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
    private final ProviderAvailabilitySlotRepository providerAvailabilitySlotRepository;
    private final ProviderPhotoRepository providerPhotoRepository;
    private final BookingRepository bookingRepository;
    private final BookingStatusHistoryRepository bookingStatusHistoryRepository;
    private final BookingNotificationService bookingNotificationService;

    @Value("${app.providers.slot-lookahead-days:7}")
    private int slotLookaheadDays;

    @Value("${app.bookings.timezone:Asia/Ho_Chi_Minh}")
    private String bookingTimezone;

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

        LocalDate today = LocalDate.now(APP_ZONE);
        List<ProviderAvailabilitySlot> upcomingSlots = providerAvailabilitySlotRepository
                .findUpcomingAvailableSlotsForProvider(
                        providerId,
                        today,
                        today.plusDays(Math.max(slotLookaheadDays, 1)));

        final Long initialResolvedServiceId = resolvedServiceId;
        if (initialResolvedServiceId != null) {
            upcomingSlots = upcomingSlots.stream()
                    .filter(slot -> slot.getProviderService() != null
                            && Objects.equals(slot.getProviderService().getId(), initialResolvedServiceId))
                    .toList();
        }

        Long selectedSlotId = null;
        if (resolvedDate != null || resolvedTime != null) {
            for (ProviderAvailabilitySlot slot : upcomingSlots) {
                boolean matchDate = resolvedDate == null || Objects.equals(slot.getSlotDate(), resolvedDate);
                boolean matchTime = resolvedTime == null || Objects.equals(slot.getStartTime(), resolvedTime);
                if (matchDate && matchTime) {
                    selectedSlotId = slot.getId();
                    resolvedDate = slot.getSlotDate();
                    resolvedTime = slot.getStartTime();
                    if (resolvedServiceId == null && slot.getProviderService() != null) {
                        resolvedServiceId = slot.getProviderService().getId();
                    }
                    break;
                }
            }
        }

        if (resolvedServiceId == null) {
            resolvedServiceId = services.get(0).getId();
        }

        final Long finalResolvedServiceId = resolvedServiceId;

        List<ProviderAvailabilitySlot> visibleSlots = upcomingSlots;
        if (finalResolvedServiceId != null) {
            visibleSlots = visibleSlots.stream()
                    .filter(slot -> slot.getProviderService() != null
                            && Objects.equals(slot.getProviderService().getId(), finalResolvedServiceId))
                    .toList();
        }

        if (resolvedDate == null && !visibleSlots.isEmpty()) {
            resolvedDate = visibleSlots.get(0).getSlotDate();
        }

        final Long finalSelectedSlotId = selectedSlotId;

        List<BookingSlotOptionResponse> slotResponses = visibleSlots.stream()
                .map(slot -> mapSlot(slot, finalSelectedSlotId))
                .toList();

        List<String> availableDates = visibleSlots.stream()
                .map(ProviderAvailabilitySlot::getSlotDate)
                .filter(Objects::nonNull)
                .distinct()
                .sorted()
                .map(ISO_DATE::format)
                .toList();

        return BookingCreateContextResponse.builder()
                .ownerUserId(owner.getId())
                .provider(mapProvider(provider))
                .pets(pets.stream().map(this::mapPet).toList())
                .services(services.stream().map(this::mapService).toList())
                .availableDates(availableDates)
                .slots(slotResponses)
                .selectedProviderId(provider.getId())
                .selectedProviderServiceId(finalResolvedServiceId)
                .selectedDate(resolvedDate != null ? resolvedDate.format(ISO_DATE) : null)
                .selectedTime(resolvedTime != null ? resolvedTime.format(TIME_FORMATTER) : null)
                .selectedSlotId(finalSelectedSlotId)
                .build();
    }

    @Override
    @Transactional
    public BookingSummaryResponse createBooking(BookingCreateRequest request) {
        User owner = userRepository.findById(request.ownerUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng đặt lịch"));

        Pet pet = petRepository.findOwnedActivePet(request.ownerUserId(), request.petId())
                .orElseThrow(() -> new BadRequestException("Thú cưng không hợp lệ hoặc không thuộc người dùng này"));

        ProviderProfile provider = providerProfileRepository.findActiveById(request.providerId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhà cung cấp"));

        ProviderService providerService = providerServiceRepository.findActiveDetailById(request.providerServiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy dịch vụ của nhà cung cấp"));

        if (!Objects.equals(providerService.getProvider().getId(), provider.getId())) {
            throw new BadRequestException("Dịch vụ không thuộc nhà cung cấp đã chọn");
        }

        ProviderAvailabilitySlot slot = resolveSlot(request, provider, providerService);

        LocalDate appointmentDate = slot != null ? slot.getSlotDate() : request.appointmentDate();
        LocalTime startTime = slot != null ? slot.getStartTime() : request.startTime();
        LocalTime endTime = slot != null
                ? slot.getEndTime()
                : Optional.ofNullable(request.endTime()).orElse(
                        startTime.plusMinutes(Optional.ofNullable(providerService.getDurationMinutes()).orElse(30)));

        String providerAddress = buildProviderAddress(provider);
        String serviceName = firstNonBlank(providerService.getCustomName(), providerService.getService().getName(),
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
        booking.setAvailabilitySlot(slot);
        booking.setAppointmentDate(appointmentDate);
        booking.setStartTime(startTime);
        booking.setEndTime(endTime);
        booking.setTimezone(firstNonBlank(bookingTimezone, "Asia/Ho_Chi_Minh"));
        booking.setStatus("PENDING_CONFIRMATION");
        booking.setCustomerNote(normalizeBlank(request.customerNote()));
        booking.setInternalNote(null);
        booking.setRescheduleCount(0);
        booking.setProviderNameSnapshot(provider.getBusinessName());
        booking.setProviderPhoneSnapshot(firstNonBlank(provider.getEmergencyPhone()));
        booking.setProviderAddressSnapshot(abbreviate(providerAddress, 255));
        booking.setServiceNameSnapshot(serviceName);
        booking.setServiceDescriptionSnapshot(serviceDescription);
        booking.setServiceDurationMinutesSnapshot(Optional.ofNullable(providerService.getDurationMinutes()).orElse(30));
        booking.setPetNameSnapshot(pet.getName());
        booking.setPetBreedSnapshot(pet.getBreed());
        booking.setSubtotalAmount(subtotal);
        booking.setMembershipDiscountAmount(BigDecimal.ZERO);
        booking.setPromoDiscountAmount(BigDecimal.ZERO);
        booking.setTaxAmount(BigDecimal.ZERO);
        booking.setTotalAmount(total);
        booking.setCurrencyCode(firstNonBlank(providerService.getCurrencyCode(), provider.getCurrencyCode(), "VND"));

        Booking saved = bookingRepository.save(booking);

        if (slot != null) {
            slot.setCapacityBooked(Optional.ofNullable(slot.getCapacityBooked()).orElse(0) + 1);
            if (slot.getCapacityBooked() >= Optional.ofNullable(slot.getCapacityTotal()).orElse(1)) {
                slot.setSlotStatus("BOOKED");
            }
            providerAvailabilitySlotRepository.save(slot);
        }

        BookingStatusHistory history = new BookingStatusHistory();
        history.setBooking(saved);
        history.setFromStatus(null);
        history.setToStatus(saved.getStatus());
        history.setChangedByUser(owner);
        history.setNote("Owner gửi yêu cầu đặt lịch từ BookingPage, chờ nhà cung cấp duyệt/xếp lịch");
        bookingStatusHistoryRepository.save(history);

        bookingNotificationService.notifyProviderBookingCreated(saved);

        return mapSummary(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingSummaryResponse getBookingSummary(Long bookingId) {
        Booking booking = bookingRepository.findDetailedById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy booking"));
        return mapSummary(booking);
    }

    private ProviderAvailabilitySlot resolveSlot(BookingCreateRequest request,
            ProviderProfile provider,
            ProviderService providerService) {
        if (request.slotId() != null) {
            ProviderAvailabilitySlot slot = providerAvailabilitySlotRepository.findDetailedById(request.slotId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy slot đã chọn"));
            validateSlot(slot, provider, providerService, request.appointmentDate(), request.startTime());
            return slot;
        }

        LocalDate today = LocalDate.now(APP_ZONE);
        List<ProviderAvailabilitySlot> slots = providerAvailabilitySlotRepository.findUpcomingAvailableSlotsForProvider(
                provider.getId(),
                today,
                today.plusDays(Math.max(slotLookaheadDays, 1)));
        return slots.stream()
                .filter(slot -> slot.getProviderService() != null
                        && Objects.equals(slot.getProviderService().getId(), providerService.getId()))
                .filter(slot -> Objects.equals(slot.getSlotDate(), request.appointmentDate()))
                .filter(slot -> Objects.equals(slot.getStartTime(), request.startTime()))
                .findFirst()
                .map(slot -> {
                    validateSlot(slot, provider, providerService, request.appointmentDate(), request.startTime());
                    return slot;
                })
                .orElseThrow(() -> new BadRequestException("Khung giờ đã chọn không còn khả dụng"));
    }

    private void validateSlot(ProviderAvailabilitySlot slot,
            ProviderProfile provider,
            ProviderService providerService,
            LocalDate appointmentDate,
            LocalTime startTime) {
        if (!Objects.equals(slot.getProvider().getId(), provider.getId())) {
            throw new BadRequestException("Slot không thuộc nhà cung cấp đã chọn");
        }
        if (slot.getProviderService() == null
                || !Objects.equals(slot.getProviderService().getId(), providerService.getId())) {
            throw new BadRequestException("Slot không thuộc dịch vụ đã chọn");
        }
        if (!"AVAILABLE".equalsIgnoreCase(Optional.ofNullable(slot.getSlotStatus()).orElse(""))) {
            throw new BadRequestException("Slot không còn khả dụng");
        }
        if (Optional.ofNullable(slot.getCapacityBooked()).orElse(0) >= Optional.ofNullable(slot.getCapacityTotal())
                .orElse(1)) {
            throw new BadRequestException("Slot đã đầy");
        }
        if (appointmentDate != null && !Objects.equals(slot.getSlotDate(), appointmentDate)) {
            throw new BadRequestException("Ngày hẹn không khớp slot đã chọn");
        }
        if (startTime != null && !Objects.equals(slot.getStartTime(), startTime)) {
            throw new BadRequestException("Giờ hẹn không khớp slot đã chọn");
        }
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
                        service.getService() != null ? service.getService().getName() : null, "Dịch vụ"))
                .description(firstNonBlank(service.getShortDescription(), service.getDescription(),
                        service.getService() != null ? service.getService().getShortDescription() : null))
                .durationMinutes(service.getDurationMinutes())
                .durationLabel(formatDuration(service.getDurationMinutes()))
                .priceAmount(service.getPriceAmount())
                .priceDisplay(formatMoney(service.getPriceAmount()))
                .currencyCode(firstNonBlank(service.getCurrencyCode(),
                        service.getService() != null ? service.getService().getCurrencyCode() : null, "VND"))
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

    private BookingSlotOptionResponse mapSlot(ProviderAvailabilitySlot slot, Long selectedSlotId) {
        int capacityRemaining = Math.max(0, Optional.ofNullable(slot.getCapacityTotal()).orElse(0)
                - Optional.ofNullable(slot.getCapacityBooked()).orElse(0));
        String serviceName = slot.getProviderService() != null
                ? firstNonBlank(slot.getProviderService().getCustomName(),
                        slot.getProviderService().getService() != null
                                ? slot.getProviderService().getService().getName()
                                : null)
                : null;
        return BookingSlotOptionResponse.builder()
                .slotId(slot.getId())
                .providerServiceId(slot.getProviderService() != null ? slot.getProviderService().getId() : null)
                .serviceName(serviceName)
                .date(slot.getSlotDate() != null ? slot.getSlotDate().format(ISO_DATE) : null)
                .startTime(formatTime(slot.getStartTime()))
                .endTime(formatTime(slot.getEndTime()))
                .label(formatTime(slot.getStartTime()))
                .capacityRemaining(capacityRemaining)
                .selected(Objects.equals(selectedSlotId, slot.getId()))
                .build();
    }

    private BookingSummaryResponse mapSummary(Booking booking) {
        return BookingSummaryResponse.builder()
                .bookingId(booking.getId())
                .bookingCode(booking.getBookingCode())
                .status(booking.getStatus())
                .ownerUserId(booking.getCustomerUser() != null ? booking.getCustomerUser().getId() : null)
                .petId(booking.getPet() != null ? booking.getPet().getId() : null)
                .providerId(booking.getProvider() != null ? booking.getProvider().getId() : null)
                .providerServiceId(booking.getProviderService() != null ? booking.getProviderService().getId() : null)
                .slotId(booking.getAvailabilitySlot() != null ? booking.getAvailabilitySlot().getId() : null)
                .providerName(booking.getProviderNameSnapshot())
                .providerPhone(booking.getProviderPhoneSnapshot())
                .providerAddress(booking.getProviderAddressSnapshot())
                .serviceName(booking.getServiceNameSnapshot())
                .serviceDurationMinutes(booking.getServiceDurationMinutesSnapshot())
                .petName(booking.getPetNameSnapshot())
                .petBreed(booking.getPetBreedSnapshot())
                .appointmentDate(
                        booking.getAppointmentDate() != null ? booking.getAppointmentDate().format(VIEW_DATE) : null)
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
                                        .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                                : null)
                .build();
    }

    private Long resolveProviderServiceId(Long providerServiceId, List<ProviderService> services) {
        if (providerServiceId != null)
            return providerServiceId;
        return services.stream().filter(ps -> Boolean.TRUE.equals(ps.getFeatured())).map(ProviderService::getId)
                .findFirst().orElse(null);
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
                ? provider.getProviderCode().substring(Math.max(0, provider.getProviderCode().length() - 4))
                        .toUpperCase(Locale.ROOT)
                : String.format(Locale.ROOT, "%04d", provider.getId());
        String userCode = String.format(Locale.ROOT, "%04d", owner.getId());
        String random = UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase(Locale.ROOT);
        return prefix + providerCode + userCode + random;
    }
}