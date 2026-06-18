package com.example.petgo.service.partner.impl;

import com.example.petgo.dto.BookingLockRequest;
import com.example.petgo.dto.BookingLockResponse;
import com.example.petgo.dto.partner.PartnerBusinessHourRequest;
import com.example.petgo.dto.partner.PartnerScheduleExceptionRequest;
import com.example.petgo.dto.partner.PartnerScheduleExceptionResponse;
import com.example.petgo.dto.partner.PartnerScheduleResponse;
import com.example.petgo.dto.partner.PartnerWeeklyScheduleRequest;
import com.example.petgo.entity.BookingLock;
import com.example.petgo.entity.ProviderBusinessHour;
import com.example.petgo.entity.ProviderProfile;
import com.example.petgo.entity.ProviderScheduleException;
import com.example.petgo.entity.ProviderService;
import com.example.petgo.entity.User;
import com.example.petgo.exception.BadRequestException;
import com.example.petgo.exception.ResourceNotFoundException;
import com.example.petgo.repository.BookingLockRepository;
import com.example.petgo.repository.BookingRepository;
import com.example.petgo.repository.ProviderAvailabilitySlotRepository;
import com.example.petgo.repository.ProviderBusinessHourRepository;
import com.example.petgo.repository.ProviderServiceRepository;
import com.example.petgo.repository.ProviderScheduleExceptionRepository;
import com.example.petgo.service.impl.BookingAvailabilityServiceImpl;
import com.example.petgo.service.partner.PartnerAccessService;
import com.example.petgo.service.partner.PartnerMappingSupport;
import com.example.petgo.service.partner.PartnerScheduleService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.HashSet;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PartnerScheduleServiceImpl implements PartnerScheduleService {

    private final PartnerAccessService partnerAccessService;
    private final PartnerMappingSupport mapper;
    private final BookingRepository bookingRepository;
    private final ProviderBusinessHourRepository providerBusinessHourRepository;
    private final ProviderServiceRepository providerServiceRepository;
    private final ProviderScheduleExceptionRepository providerScheduleExceptionRepository;
    private final BookingLockRepository bookingLockRepository;
    private final BookingAvailabilityServiceImpl bookingAvailabilityService;

    @Override
    @Transactional(readOnly = true)
    public PartnerScheduleResponse getSchedule(HttpServletRequest request, String from, String to) {
        ProviderProfile provider = partnerAccessService.requirePartnerContext(request).provider();
        return PartnerScheduleResponse.builder()
                .providerId(provider.getId())
                .weeklyHours(providerBusinessHourRepository.findByProvider_IdOrderByWeekdayAscIdAsc(provider.getId())
                        .stream()
                        .map(mapper::mapHour)
                        .toList())
                .dayOverrides(loadDayOverrides(provider.getId(), from, to))
                .slots(List.of())
                .build();
    }

    @Override
    @Transactional
    public PartnerScheduleResponse updateWeeklySchedule(HttpServletRequest request,
            PartnerWeeklyScheduleRequest requestBody) {
        ProviderProfile provider = partnerAccessService.requirePartnerContext(request).provider();
        validateWeeklySchedule(requestBody);
        Map<Integer, ProviderBusinessHour> existingByWeekday = providerBusinessHourRepository
                .findByProvider_IdOrderByWeekdayAscIdAsc(provider.getId()).stream()
                .collect(
                        Collectors.toMap(ProviderBusinessHour::getWeekday, Function.identity(), (left, right) -> left));

        for (PartnerBusinessHourRequest hourRequest : requestBody.weeklyHours()) {
            ProviderBusinessHour hour = existingByWeekday.getOrDefault(hourRequest.weekday(),
                    new ProviderBusinessHour());
            if (hour.getId() == null) {
                hour.setProvider(provider);
                hour.setWeekday(hourRequest.weekday());
            }
            boolean closed = Boolean.TRUE.equals(hourRequest.closed());
            hour.setClosed(closed);
            hour.setOpensAt(closed ? null : parseTime(hourRequest.opensAt(), "Giờ mở cửa không hợp lệ."));
            hour.setClosesAt(closed ? null : parseTime(hourRequest.closesAt(), "Giờ đóng cửa không hợp lệ."));
            hour.setBreakStartsAt(
                    closed ? null : parseOptionalTime(hourRequest.breakStartsAt(), "Giờ bắt đầu nghỉ không hợp lệ."));
            hour.setBreakEndsAt(
                    closed ? null : parseOptionalTime(hourRequest.breakEndsAt(), "Giờ kết thúc nghỉ không hợp lệ."));
            if (!closed && hour.getOpensAt() != null && hour.getClosesAt() != null
                    && !hour.getOpensAt().isBefore(hour.getClosesAt())) {
                throw new BadRequestException("Giờ mở cửa phải trước giờ đóng cửa.");
            }
            providerBusinessHourRepository.save(hour);
        }

        return getSchedule(request, null, null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PartnerScheduleExceptionResponse> listDayOverrides(HttpServletRequest request, String from, String to) {
        ProviderProfile provider = partnerAccessService.requirePartnerContext(request).provider();
        return loadDayOverrides(provider.getId(), from, to);
    }

    @Override
    @Transactional(readOnly = true)
    public PartnerScheduleExceptionResponse getDayOverride(HttpServletRequest request, String date) {
        ProviderProfile provider = partnerAccessService.requirePartnerContext(request).provider();
        LocalDate localDate = parseDate(date);
        return providerScheduleExceptionRepository
                .findFirstByProvider_IdAndLocalDateOrderByIdAsc(provider.getId(), localDate)
                .map(this::mapOverride)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy override cho ngày này."));
    }

    @Override
    @Transactional
    public PartnerScheduleExceptionResponse upsertDayOverride(HttpServletRequest request, String date,
            PartnerScheduleExceptionRequest requestBody) {
        ProviderProfile provider = partnerAccessService.requirePartnerContext(request).provider();
        LocalDate localDate = parseDate(date);
        ProviderScheduleException exception = providerScheduleExceptionRepository
                .findFirstByProvider_IdAndLocalDateOrderByIdAsc(provider.getId(), localDate)
                .orElseGet(ProviderScheduleException::new);
        if (exception.getId() == null) {
            exception.setProvider(provider);
            exception.setLocalDate(localDate);
        }
        applyOverride(exception, requestBody, localDate);
        ProviderScheduleException saved = providerScheduleExceptionRepository.save(exception);
        bookingAvailabilityService.invalidateAvailabilityCache(provider.getId(), null, localDate);
        return mapOverride(saved);
    }

    @Override
    @Transactional
    public void deleteDayOverride(HttpServletRequest request, String date) {
        ProviderProfile provider = partnerAccessService.requirePartnerContext(request).provider();
        LocalDate localDate = parseDate(date);
        providerScheduleExceptionRepository.deleteByProvider_IdAndLocalDate(provider.getId(), localDate);
        bookingAvailabilityService.invalidateAvailabilityCache(provider.getId(), null, localDate);
    }

    @Override
    @Transactional
    public List<PartnerScheduleExceptionResponse> copyDay(HttpServletRequest request, String sourceDate,
            List<String> targetDates) {
        ProviderProfile provider = partnerAccessService.requirePartnerContext(request).provider();
        LocalDate source = parseDate(sourceDate);
        ProviderScheduleException sourceOverride = providerScheduleExceptionRepository
                .findFirstByProvider_IdAndLocalDateOrderByIdAsc(provider.getId(), source)
                .orElseThrow(() -> new ResourceNotFoundException("Ngày nguồn chưa có override để copy."));
        if (targetDates == null || targetDates.isEmpty()) {
            throw new BadRequestException("Vui lòng chọn ngày đích.");
        }
        return targetDates.stream()
                .map(this::parseDate)
                .map(target -> copyOverride(provider, sourceOverride, target))
                .map(this::mapOverride)
                .toList();
    }

    @Override
    @Transactional
    public List<PartnerScheduleExceptionResponse> copyMonth(HttpServletRequest request, String sourceMonth,
            String targetMonth) {
        ProviderProfile provider = partnerAccessService.requirePartnerContext(request).provider();
        YearMonth source = YearMonth.parse(mapper.normalizeBlank(sourceMonth));
        YearMonth target = YearMonth.parse(mapper.normalizeBlank(targetMonth));
        return providerScheduleExceptionRepository
                .findByProvider_IdAndLocalDateBetweenOrderByLocalDateAscStartsAtLocalAscIdAsc(provider.getId(),
                        source.atDay(1), source.atEndOfMonth())
                .stream()
                .map(item -> copyOverride(provider, item,
                        target.atDay(Math.min(item.getLocalDate().getDayOfMonth(), target.lengthOfMonth()))))
                .map(this::mapOverride)
                .toList();
    }

    @Override
    @Transactional
    public List<PartnerScheduleExceptionResponse> copyYear(HttpServletRequest request, String sourceYear,
            String targetYear) {
        ProviderProfile provider = partnerAccessService.requirePartnerContext(request).provider();
        Year source = Year.parse(mapper.normalizeBlank(sourceYear));
        Year target = Year.parse(mapper.normalizeBlank(targetYear));
        return providerScheduleExceptionRepository
                .findByProvider_IdAndLocalDateBetweenOrderByLocalDateAscStartsAtLocalAscIdAsc(provider.getId(),
                        source.atDay(1), source.atDay(source.length()))
                .stream()
                .map(item -> copyOverride(provider, item,
                        LocalDate.of(target.getValue(), item.getLocalDate().getMonth(),
                                Math.min(item.getLocalDate().getDayOfMonth(),
                                        YearMonth.of(target.getValue(), item.getLocalDate().getMonth())
                                                .lengthOfMonth()))))
                .map(this::mapOverride)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingLockResponse> listBookingLocks(HttpServletRequest request, boolean activeOnly) {
        ProviderProfile provider = partnerAccessService.requirePartnerContext(request).provider();
        return bookingLockRepository
                .findByProviderForManagement(provider.getId(), activeOnly, LocalDateTime.now(ZoneId.of("UTC")))
                .stream()
                .map(this::mapLock)
                .toList();
    }

    @Override
    @Transactional
    public BookingLockResponse createBookingLock(HttpServletRequest request, BookingLockRequest requestBody) {
        ProviderProfile provider = partnerAccessService.requirePartnerContext(request).provider();
        ProviderService service = providerServiceRepository.findActiveDetailById(requestBody.providerServiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy dịch vụ của nhà cung cấp."));
        if (service.getProvider() == null || !provider.getId().equals(service.getProvider().getId())) {
            throw new BadRequestException("Dịch vụ không thuộc provider hiện tại.");
        }
        User partnerUser = provider.getUser();
        BookingLockRequest normalized = new BookingLockRequest(
                partnerUser.getId(),
                provider.getId(),
                requestBody.providerServiceId(),
                requestBody.appointmentDate(),
                requestBody.startTime(),
                requestBody.endTime(),
                requestBody.durationMinutes(),
                requestBody.lockType());
        return bookingAvailabilityService.createLock(normalized);
    }

    @Override
    @Transactional
    public BookingLockResponse unlockBookingLock(HttpServletRequest request, Long lockId) {
        ProviderProfile provider = partnerAccessService.requirePartnerContext(request).provider();
        BookingLock lock = bookingLockRepository.findDetailedById(lockId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy booking lock."));
        if (lock.getProvider() == null || !provider.getId().equals(lock.getProvider().getId())) {
            throw new BadRequestException("Lock không thuộc provider hiện tại.");
        }
        lock.setStatus("UNLOCKED");
        BookingLock saved = bookingLockRepository.save(lock);
        bookingAvailabilityService.invalidateAvailabilityCache(provider.getId(),
                saved.getProviderService() != null ? saved.getProviderService().getId() : null,
                saved.getAppointmentDate());
        return mapLock(saved);
    }

    @Override
    @Transactional
    public BookingLockResponse extendBookingLock(HttpServletRequest request, Long lockId, Integer durationMinutes) {
        ProviderProfile provider = partnerAccessService.requirePartnerContext(request).provider();
        BookingLock lock = requireOwnedLock(provider.getId(), lockId);
        lock.setExpiresAtUtc(LocalDateTime.now(ZoneId.of("UTC"))
                .plusMinutes(Math.max(durationMinutes != null ? durationMinutes : 60, 1)));
        lock.setStatus("ACTIVE");
        BookingLock saved = bookingLockRepository.save(lock);
        bookingAvailabilityService.invalidateAvailabilityCache(provider.getId(),
                saved.getProviderService() != null ? saved.getProviderService().getId() : null,
                saved.getAppointmentDate());
        return mapLock(saved);
    }

    @Override
    @Transactional
    public void deleteBookingLock(HttpServletRequest request, Long lockId) {
        ProviderProfile provider = partnerAccessService.requirePartnerContext(request).provider();
        BookingLock lock = requireOwnedLock(provider.getId(), lockId);
        bookingLockRepository.delete(lock);
        bookingAvailabilityService.invalidateAvailabilityCache(provider.getId(),
                lock.getProviderService() != null ? lock.getProviderService().getId() : null,
                lock.getAppointmentDate());
    }

    private BookingLock requireOwnedLock(Long providerId, Long lockId) {
        BookingLock lock = bookingLockRepository.findDetailedById(lockId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy booking lock."));
        if (lock.getProvider() == null || !providerId.equals(lock.getProvider().getId())) {
            throw new BadRequestException("Lock không thuộc provider hiện tại.");
        }
        return lock;
    }

    private List<PartnerScheduleExceptionResponse> loadDayOverrides(Long providerId, String from, String to) {
        if (mapper.normalizeBlank(from) == null || mapper.normalizeBlank(to) == null) {
            LocalDate today = LocalDate.now(PartnerMappingSupport.APP_ZONE);
            return providerScheduleExceptionRepository
                    .findByProvider_IdAndLocalDateBetweenOrderByLocalDateAscStartsAtLocalAscIdAsc(providerId, today,
                            today.plusDays(30))
                    .stream().map(this::mapOverride).toList();
        }
        return providerScheduleExceptionRepository
                .findByProvider_IdAndLocalDateBetweenOrderByLocalDateAscStartsAtLocalAscIdAsc(providerId,
                        parseDate(from), parseDate(to))
                .stream().map(this::mapOverride).toList();
    }

    private ProviderScheduleException copyOverride(ProviderProfile provider, ProviderScheduleException source,
            LocalDate targetDate) {
        ProviderScheduleException target = providerScheduleExceptionRepository
                .findFirstByProvider_IdAndLocalDateOrderByIdAsc(provider.getId(), targetDate)
                .orElseGet(ProviderScheduleException::new);
        if (target.getId() == null) {
            target.setProvider(provider);
            target.setLocalDate(targetDate);
        }
        target.setType(source.getType());
        target.setStartsAtLocal(source.getStartsAtLocal());
        target.setEndsAtLocal(source.getEndsAtLocal());
        target.setMaxConcurrentOverride(source.getMaxConcurrentOverride());
        target.setReason(source.getReason());
        ProviderScheduleException saved = providerScheduleExceptionRepository.save(target);
        bookingAvailabilityService.invalidateAvailabilityCache(provider.getId(), null, targetDate);
        return saved;
    }

    private void applyOverride(ProviderScheduleException exception, PartnerScheduleExceptionRequest requestBody,
            LocalDate pathDate) {
        if (requestBody == null) {
            throw new BadRequestException("Thiếu dữ liệu override.");
        }
        LocalDate bodyDate = parseDate(requestBody.localDate());
        if (!pathDate.equals(bodyDate)) {
            throw new BadRequestException("Ngày override không khớp URL.");
        }
        String type = mapper.firstNonBlank(requestBody.type(), "").toUpperCase(java.util.Locale.ROOT);
        if (!List.of("CLOSED", "PARTIAL_BLOCK", "OPEN_OVERRIDE", "CAPACITY_OVERRIDE").contains(type)) {
            throw new BadRequestException("Loại override không hợp lệ.");
        }
        LocalTime startsAt = parseOptionalTime(requestBody.startsAt(), "Giờ bắt đầu override không hợp lệ.");
        LocalTime endsAt = parseOptionalTime(requestBody.endsAt(), "Giờ kết thúc override không hợp lệ.");
        if ((startsAt == null) != (endsAt == null)) {
            throw new BadRequestException("Cần nhập đủ giờ bắt đầu và kết thúc override.");
        }
        if (startsAt != null && !startsAt.isBefore(endsAt)) {
            throw new BadRequestException("Giờ bắt đầu override phải trước giờ kết thúc.");
        }
        validateNoActiveBookingConflict(exception.getProvider().getId(), pathDate, type, startsAt, endsAt);
        exception.setType(type);
        exception.setStartsAtLocal(startsAt);
        exception.setEndsAtLocal(endsAt);
        exception.setMaxConcurrentOverride(requestBody.maxConcurrentOverride());
        exception.setReason(mapper.normalizeBlank(requestBody.reason()));
    }

    private PartnerScheduleExceptionResponse mapOverride(ProviderScheduleException exception) {
        return PartnerScheduleExceptionResponse.builder()
                .id(exception.getId())
                .localDate(exception.getLocalDate() != null
                        ? exception.getLocalDate().format(PartnerMappingSupport.ISO_DATE)
                        : null)
                .type(exception.getType())
                .startsAt(exception.getStartsAtLocal() != null
                        ? exception.getStartsAtLocal().format(PartnerMappingSupport.TIME_VIEW)
                        : null)
                .endsAt(exception.getEndsAtLocal() != null
                        ? exception.getEndsAtLocal().format(PartnerMappingSupport.TIME_VIEW)
                        : null)
                .maxConcurrentOverride(exception.getMaxConcurrentOverride())
                .reason(exception.getReason())
                .build();
    }

    private void validateNoActiveBookingConflict(Long providerId, LocalDate date, String type, LocalTime startsAt,
            LocalTime endsAt) {
        if (!List.of("CLOSED", "PARTIAL_BLOCK").contains(type)) {
            return;
        }
        long activeBookings = bookingRepository.countActiveProviderBookingsOnDate(providerId, date, startsAt, endsAt);
        if (activeBookings > 0) {
            throw new BadRequestException("Không thể đóng/chặn lịch vì đang có booking active trong khoảng này.");
        }
    }

    private BookingLockResponse mapLock(BookingLock lock) {
        return BookingLockResponse.builder()
                .lockId(lock.getId())
                .ownerUserId(lock.getUser() != null ? lock.getUser().getId() : null)
                .providerId(lock.getProvider() != null ? lock.getProvider().getId() : null)
                .providerServiceId(lock.getProviderService() != null ? lock.getProviderService().getId() : null)
                .appointmentDate(lock.getAppointmentDate() != null
                        ? lock.getAppointmentDate().format(PartnerMappingSupport.ISO_DATE)
                        : null)
                .startTime(lock.getStartTime() != null ? lock.getStartTime().format(PartnerMappingSupport.TIME_VIEW)
                        : null)
                .endTime(lock.getEndTime() != null ? lock.getEndTime().format(PartnerMappingSupport.TIME_VIEW) : null)
                .expiresAtUtc(lock.getExpiresAtUtc() != null ? lock.getExpiresAtUtc().toString() : null)
                .expiresInSeconds(lock.getExpiresAtUtc() != null
                        ? Math.max(0, (int) java.time.Duration
                                .between(LocalDateTime.now(ZoneId.of("UTC")), lock.getExpiresAtUtc()).toSeconds())
                        : 0)
                .status(lock.getStatus())
                .message("Booking lock " + mapper.firstNonBlank(lock.getStatus(), "UNKNOWN"))
                .build();
    }

    private LocalDate parseDate(String value) {
        try {
            return LocalDate.parse(value, PartnerMappingSupport.ISO_DATE);
        } catch (DateTimeParseException ex) {
            throw new BadRequestException("Ngày cần định dạng yyyy-MM-dd.");
        }
    }

    private void validateWeeklySchedule(PartnerWeeklyScheduleRequest requestBody) {
        if (requestBody == null || requestBody.weeklyHours() == null || requestBody.weeklyHours().isEmpty()) {
            throw new BadRequestException("Vui lòng cấu hình ít nhất một ngày.");
        }
        Set<Integer> weekdays = new HashSet<>();
        boolean hasOpenDay = false;
        for (PartnerBusinessHourRequest hourRequest : requestBody.weeklyHours()) {
            if (hourRequest.weekday() == null || hourRequest.weekday() < 1 || hourRequest.weekday() > 7) {
                throw new BadRequestException("weekday phải từ 1 đến 7.");
            }
            if (!weekdays.add(hourRequest.weekday())) {
                throw new BadRequestException("Không được cấu hình trùng ngày trong tuần.");
            }
            if (Boolean.TRUE.equals(hourRequest.closed())) {
                continue;
            }
            hasOpenDay = true;
            LocalTime opensAt = parseTime(hourRequest.opensAt(), "Giờ mở cửa không hợp lệ.");
            LocalTime closesAt = parseTime(hourRequest.closesAt(), "Giờ đóng cửa không hợp lệ.");
            if (!opensAt.isBefore(closesAt)) {
                throw new BadRequestException("Giờ mở cửa phải trước giờ đóng cửa.");
            }
            LocalTime breakStartsAt = parseOptionalTime(hourRequest.breakStartsAt(), "Giờ bắt đầu nghỉ không hợp lệ.");
            LocalTime breakEndsAt = parseOptionalTime(hourRequest.breakEndsAt(), "Giờ kết thúc nghỉ không hợp lệ.");
            if ((breakStartsAt == null) != (breakEndsAt == null)) {
                throw new BadRequestException("Cần nhập đủ giờ bắt đầu và kết thúc nghỉ giữa ca.");
            }
            if (breakStartsAt != null) {
                if (!breakStartsAt.isBefore(breakEndsAt)) {
                    throw new BadRequestException("Giờ bắt đầu nghỉ phải trước giờ kết thúc nghỉ.");
                }
                if (breakStartsAt.isBefore(opensAt) || breakEndsAt.isAfter(closesAt)) {
                    throw new BadRequestException("Giờ nghỉ phải nằm trong giờ mở cửa.");
                }
            }
        }
        if (!hasOpenDay) {
            throw new BadRequestException("Vui lòng mở ít nhất một ngày trong tuần.");
        }
    }

    private LocalTime parseTime(String value, String message) {
        LocalTime parsed = parseOptionalTime(value, message);
        if (parsed == null) {
            throw new BadRequestException(message);
        }
        return parsed;
    }

    private LocalTime parseOptionalTime(String value, String message) {
        String normalized = mapper.normalizeBlank(value);
        if (normalized == null)
            return null;
        try {
            return LocalTime.parse(normalized, PartnerMappingSupport.TIME_VIEW);
        } catch (DateTimeParseException ex) {
            throw new BadRequestException(message);
        }
    }
}