package com.example.petgo.service.partner.impl;

import com.example.petgo.dto.partner.PartnerBookingSummaryResponse;
import com.example.petgo.dto.partner.PartnerCustomerDetailResponse;
import com.example.petgo.dto.partner.PartnerCustomerListResponse;
import com.example.petgo.dto.partner.PartnerCustomerPetSummaryResponse;
import com.example.petgo.dto.partner.PartnerCustomerSummaryResponse;
import com.example.petgo.entity.Booking;
import com.example.petgo.entity.Pet;
import com.example.petgo.entity.ProviderProfile;
import com.example.petgo.entity.User;
import com.example.petgo.exception.ResourceNotFoundException;
import com.example.petgo.repository.BookingRepository;
import com.example.petgo.service.partner.PartnerAccessService;
import com.example.petgo.service.partner.PartnerCustomerManagementService;
import com.example.petgo.service.partner.PartnerMappingSupport;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PartnerCustomerManagementServiceImpl implements PartnerCustomerManagementService {

    private static final int DEFAULT_PAGE_SIZE = 12;
    private static final int MAX_PAGE_SIZE = 50;

    private final PartnerAccessService partnerAccessService;
    private final PartnerMappingSupport mapper;
    private final BookingRepository bookingRepository;

    @Override
    @Transactional(readOnly = true)
    public PartnerCustomerListResponse listCustomers(HttpServletRequest request, String keyword, String status,
            Integer page, Integer size) {
        ProviderProfile provider = partnerAccessService.requirePartnerContext(request).provider();
        String normalizedKeyword = mapper.normalizeBlank(keyword);
        String normalizedStatus = normalizeStatus(status);
        int safePage = safePage(page);
        int safeSize = safeSize(size);

        List<CustomerAggregate> aggregates = buildAggregates(provider.getId()).stream()
                .filter(aggregate -> matchesStatusFilter(aggregate.bookings(), normalizedStatus))
                .filter(aggregate -> matchesKeyword(aggregate, normalizedKeyword))
                .sorted(Comparator.comparing((CustomerAggregate aggregate) -> latestBooking(aggregate.bookings()),
                        Comparator.nullsLast(this::compareBookingDesc)))
                .toList();

        int totalItems = aggregates.size();
        List<CustomerAggregate> pageItems = slice(aggregates, safePage, safeSize);

        return PartnerCustomerListResponse.builder()
                .providerId(provider.getId())
                .businessName(provider.getBusinessName())
                .keyword(normalizedKeyword)
                .filterStatus(normalizedStatus)
                .page(safePage)
                .size(safeSize)
                .totalItems(totalItems)
                .totalPages(totalPages(totalItems, safeSize))
                .customers(pageItems.stream().map(this::mapCustomerSummary).toList())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public PartnerCustomerDetailResponse getCustomerDetail(HttpServletRequest request, Long customerUserId) {
        ProviderProfile provider = partnerAccessService.requirePartnerContext(request).provider();
        CustomerAggregate aggregate = buildAggregates(provider.getId()).stream()
                .filter(item -> item.customer() != null && Objects.equals(item.customer().getId(), customerUserId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khách hàng thuộc shop hiện tại."));

        PartnerCustomerSummaryResponse summary = mapCustomerSummary(aggregate);
        List<Booking> bookings = aggregate.bookings().stream()
                .sorted(this::compareBookingDesc)
                .toList();

        return PartnerCustomerDetailResponse.builder()
                .providerId(provider.getId())
                .businessName(provider.getBusinessName())
                .customerUserId(summary.customerUserId())
                .customerName(summary.customerName())
                .customerAvatarUrl(summary.customerAvatarUrl())
                .maskedPhone(summary.maskedPhone())
                .maskedEmail(summary.maskedEmail())
                .bookingCount(summary.bookingCount())
                .completedBookingCount(summary.completedBookingCount())
                .cancelledBookingCount(bookings.stream().filter(booking -> statusEquals(booking, "CANCELLED")).count())
                .totalSpent(summary.totalSpent())
                .totalSpentDisplay(summary.totalSpentDisplay())
                .lastBookingId(summary.lastBookingId())
                .lastBookingCode(summary.lastBookingCode())
                .lastBookingStatus(summary.lastBookingStatus())
                .lastBookingStatusLabel(summary.lastBookingStatusLabel())
                .lastBookingDate(summary.lastBookingDate())
                .lastBookingDateDisplay(summary.lastBookingDateDisplay())
                .pets(summary.pets())
                .bookings(bookings.stream().map(mapper::mapBookingSummary).toList())
                .build();
    }

    private List<CustomerAggregate> buildAggregates(Long providerId) {
        Map<Long, List<Booking>> byCustomer = bookingRepository.findDetailedByProviderId(providerId).stream()
                .filter(booking -> booking.getCustomerUser() != null && booking.getCustomerUser().getId() != null)
                .collect(Collectors.groupingBy(booking -> booking.getCustomerUser().getId(), LinkedHashMap::new,
                        Collectors.toList()));

        List<CustomerAggregate> aggregates = new ArrayList<>();
        byCustomer.forEach((customerId, bookings) -> {
            Booking first = bookings.stream().findFirst().orElse(null);
            if (first != null) {
                aggregates.add(new CustomerAggregate(first.getCustomerUser(), bookings));
            }
        });
        return aggregates;
    }

    private PartnerCustomerSummaryResponse mapCustomerSummary(CustomerAggregate aggregate) {
        User customer = aggregate.customer();
        List<Booking> bookings = aggregate.bookings();
        Booking latest = latestBooking(bookings);
        BigDecimal totalSpent = bookings.stream()
                .filter(booking -> statusEquals(booking, "COMPLETED"))
                .map(Booking::getTotalAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return PartnerCustomerSummaryResponse.builder()
                .customerUserId(customer != null ? customer.getId() : null)
                .customerName(
                        customer != null ? mapper.firstNonBlank(customer.getFullName(), customer.getEmail()) : null)
                .customerAvatarUrl(customer != null ? customer.getAvatarUrl() : null)
                .maskedPhone(maskPhone(customer != null ? customer.getPhoneNumber() : null))
                .maskedEmail(maskEmail(customer != null ? customer.getEmail() : null))
                .bookingCount(bookings.size())
                .completedBookingCount(bookings.stream().filter(booking -> statusEquals(booking, "COMPLETED")).count())
                .totalSpent(totalSpent)
                .totalSpentDisplay(mapper.formatMoney(totalSpent))
                .lastBookingId(latest != null ? latest.getId() : null)
                .lastBookingCode(latest != null ? latest.getBookingCode() : null)
                .lastBookingStatus(latest != null ? latest.getStatus() : null)
                .lastBookingStatusLabel(latest != null ? mapper.mapStatusLabel(latest.getStatus()) : null)
                .lastBookingDate(latest != null ? mapper.formatIsoDate(latest.getAppointmentDate()) : null)
                .lastBookingDateDisplay(latest != null ? mapper.formatDate(latest.getAppointmentDate()) : null)
                .lastServiceName(latest != null ? latest.getServiceNameSnapshot() : null)
                .pets(buildPetSummaries(bookings))
                .build();
    }

    private List<PartnerCustomerPetSummaryResponse> buildPetSummaries(List<Booking> bookings) {
        Map<Long, List<Booking>> byPet = bookings.stream()
                .collect(Collectors.groupingBy(booking -> booking.getPet() != null && booking.getPet().getId() != null
                        ? booking.getPet().getId()
                        : 0L, LinkedHashMap::new, Collectors.toList()));
        return byPet.values().stream()
                .map(petBookings -> {
                    Booking first = petBookings.get(0);
                    Booking latest = latestBooking(petBookings);
                    Pet pet = first.getPet();
                    return PartnerCustomerPetSummaryResponse.builder()
                            .petId(pet != null ? pet.getId() : null)
                            .petName(mapper.firstNonBlank(pet != null ? pet.getName() : null,
                                    first.getPetNameSnapshot()))
                            .species(pet != null ? pet.getSpecies() : null)
                            .breed(mapper.firstNonBlank(pet != null ? pet.getBreed() : null,
                                    first.getPetBreedSnapshot()))
                            .avatarUrl(pet != null ? pet.getAvatarUrl() : null)
                            .bookingCount(petBookings.size())
                            .lastBookingDate(latest != null ? mapper.formatIsoDate(latest.getAppointmentDate()) : null)
                            .lastBookingDateDisplay(
                                    latest != null ? mapper.formatDate(latest.getAppointmentDate()) : null)
                            .build();
                })
                .sorted(Comparator.comparing(PartnerCustomerPetSummaryResponse::bookingCount).reversed())
                .toList();
    }

    private boolean matchesKeyword(CustomerAggregate aggregate, String keyword) {
        if (keyword == null) {
            return true;
        }
        String normalized = keyword.toLowerCase(Locale.ROOT);
        User customer = aggregate.customer();
        if (contains(customer != null ? customer.getFullName() : null, normalized)
                || contains(customer != null ? customer.getEmail() : null, normalized)
                || contains(customer != null ? customer.getPhoneNumber() : null, normalized)) {
            return true;
        }
        return aggregate.bookings().stream().anyMatch(booking -> contains(booking.getBookingCode(), normalized)
                || contains(booking.getServiceNameSnapshot(), normalized)
                || contains(booking.getPetNameSnapshot(), normalized)
                || contains(booking.getPetBreedSnapshot(), normalized));
    }

    private boolean matchesStatusFilter(List<Booking> bookings, String filter) {
        if ("ALL".equals(filter)) {
            return true;
        }
        if ("ACTIVE".equals(filter)) {
            return bookings.stream().anyMatch(booking -> !List.of("COMPLETED", "CANCELLED", "NO_SHOW", "REFUNDED")
                    .contains(mapper.firstNonBlank(booking.getStatus(), "").toUpperCase(Locale.ROOT)));
        }
        return bookings.stream().anyMatch(booking -> statusEquals(booking, filter));
    }

    private boolean contains(String value, String normalizedKeyword) {
        return value != null && value.toLowerCase(Locale.ROOT).contains(normalizedKeyword);
    }

    private Booking latestBooking(List<Booking> bookings) {
        return bookings.stream().max(this::compareBookingAsc).orElse(null);
    }

    private int compareBookingAsc(Booking left, Booking right) {
        int dateCompare = compareNullable(left.getAppointmentDate(), right.getAppointmentDate());
        if (dateCompare != 0) {
            return dateCompare;
        }
        int timeCompare = compareNullable(left.getStartTime(), right.getStartTime());
        if (timeCompare != 0) {
            return timeCompare;
        }
        return compareNullable(left.getId(), right.getId());
    }

    private int compareBookingDesc(Booking left, Booking right) {
        return -compareBookingAsc(left, right);
    }

    private <T extends Comparable<T>> int compareNullable(T left, T right) {
        if (left == null && right == null) {
            return 0;
        }
        if (left == null) {
            return -1;
        }
        if (right == null) {
            return 1;
        }
        return left.compareTo(right);
    }

    private boolean statusEquals(Booking booking, String expected) {
        return expected.equalsIgnoreCase(mapper.firstNonBlank(booking.getStatus(), ""));
    }

    private String normalizeStatus(String status) {
        String normalized = mapper.firstNonBlank(status, "ALL").toUpperCase(Locale.ROOT);
        return List.of("ALL", "ACTIVE", "COMPLETED", "CANCELLED", "PENDING_CONFIRMATION", "CONFIRMED", "IN_PROGRESS")
                .contains(normalized) ? normalized : "ALL";
    }

    private int safePage(Integer page) {
        return Math.max(0, page == null ? 0 : page);
    }

    private int safeSize(Integer size) {
        return Math.min(MAX_PAGE_SIZE, Math.max(1, size == null ? DEFAULT_PAGE_SIZE : size));
    }

    private int totalPages(int totalItems, int size) {
        return totalItems == 0 ? 0 : (int) Math.ceil((double) totalItems / size);
    }

    private <T> List<T> slice(List<T> items, int page, int size) {
        int fromIndex = Math.min(items.size(), page * size);
        int toIndex = Math.min(items.size(), fromIndex + size);
        return items.subList(fromIndex, toIndex);
    }

    private String maskEmail(String email) {
        String normalized = mapper.normalizeBlank(email);
        if (normalized == null) {
            return null;
        }
        int atIndex = normalized.indexOf('@');
        if (atIndex <= 1) {
            return "***" + (atIndex >= 0 ? normalized.substring(atIndex) : "");
        }
        return normalized.charAt(0) + "***" + normalized.substring(atIndex);
    }

    private String maskPhone(String phone) {
        String normalized = mapper.normalizeBlank(phone);
        if (normalized == null) {
            return null;
        }
        String digits = normalized.replaceAll("\\D", "");
        if (digits.length() <= 3) {
            return "***";
        }
        return "***" + digits.substring(digits.length() - 3);
    }

    private record CustomerAggregate(User customer, List<Booking> bookings) {
    }
}