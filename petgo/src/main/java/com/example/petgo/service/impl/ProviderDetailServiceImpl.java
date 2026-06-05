package com.example.petgo.service.impl;

import com.example.petgo.dto.*;
import com.example.petgo.entity.*;
import com.example.petgo.repository.*;
import com.example.petgo.service.ProviderDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ProviderDetailServiceImpl implements ProviderDetailService {

    private static final ZoneId APP_ZONE = ZoneId.of("Asia/Ho_Chi_Minh");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final ProviderProfileRepository providerProfileRepository;
    private final ProviderPhotoRepository providerPhotoRepository;
    private final ProviderServiceRepository providerServiceRepository;
    private final ProviderBusinessHourRepository providerBusinessHourRepository;
    private final ProviderAvailabilitySlotRepository providerAvailabilitySlotRepository;
    private final ReviewRepository reviewRepository;

    @Value("${app.providers.detail-slot-limit:6}")
    private int detailSlotLimit;

    @Value("${app.providers.detail-review-limit:8}")
    private int detailReviewLimit;

    @Value("${app.providers.slot-lookahead-days:7}")
    private int slotLookaheadDays;

    @Override
    public ProviderDetailResponse getProviderDetail(Long providerId, Double latitude, Double longitude) {
        ProviderProfile provider = providerProfileRepository.findActiveById(providerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy nhà cung cấp"));

        List<ProviderPhoto> photos = providerPhotoRepository.findImagesByProviderId(providerId);
        List<ProviderService> services = providerServiceRepository.findActiveDetailsByProviderId(providerId);
        List<ProviderBusinessHour> businessHours = providerBusinessHourRepository.findByProvider_IdOrderByWeekdayAscIdAsc(providerId);

        LocalDate today = LocalDate.now(APP_ZONE);
        List<ProviderAvailabilitySlot> allUpcomingSlots = providerAvailabilitySlotRepository.findUpcomingAvailableSlotsForProvider(
                providerId,
                today,
                today.plusDays(Math.max(slotLookaheadDays, 1))
        );
        List<Review> reviews = reviewRepository.findVisibleByProviderId(providerId, PageRequest.of(0, Math.max(detailReviewLimit, 1)));

        String bannerImage = firstNonBlank(provider.getCoverImageUrl(), provider.getMainImageUrl(), firstPhotoUrl(photos), fallbackProviderImage(provider));
        String mainImage = firstNonBlank(provider.getMainImageUrl(), firstPhotoUrl(photos), bannerImage, fallbackProviderImage(provider));
        List<String> gallery = buildGallery(photos, bannerImage, mainImage);
        List<ProviderDetailServiceItemResponse> serviceItems = services.stream().map(this::mapService).toList();
        List<ProviderBusinessHourDetailResponse> hourItems = businessHours.stream().map(this::mapHour).toList();
        List<ProviderSlotResponse> slotItems = allUpcomingSlots.stream().limit(Math.max(detailSlotLimit, 1)).map(this::mapSlot).toList();
        List<ProviderReviewResponse> reviewItems = reviews.stream().map(this::mapReview).toList();

        boolean openNow = isOpenNow(businessHours, LocalDateTime.now(APP_ZONE).toLocalTime(), LocalDate.now(APP_ZONE).getDayOfWeek());
        Double distanceKm = calculateDistanceKm(latitude, longitude, provider.getLatitude(), provider.getLongitude());
        BigDecimal priceFrom = resolveLowestPrice(provider, services);
        String nextAvailableSlot = slotItems.isEmpty() ? null : slotItems.get(0).date() + " " + slotItems.get(0).label();

        return ProviderDetailResponse.builder()
                .id(provider.getId())
                .slug(provider.getSlug())
                .name(provider.getBusinessName())
                .headline(provider.getHeadline())
                .description(provider.getDescription())
                .providerType(provider.getProviderType())
                .verificationStatus(provider.getVerificationStatus())
                .featured(Boolean.TRUE.equals(provider.getFeatured()))
                .hot(Boolean.TRUE.equals(provider.getHot()))
                .instantBooking(Boolean.TRUE.equals(provider.getAcceptsInstantBooking()))
                .acceptsMembership(Boolean.TRUE.equals(provider.getAcceptsMembership()))
                .yearsExperience(provider.getYearsExperience())
                .rating(provider.getAverageRating())
                .reviewsCount(provider.getTotalReviews())
                .address(buildAddress(provider))
                .city(provider.getCity())
                .province(provider.getProvince())
                .emergencyPhone(provider.getEmergencyPhone())
                .bannerImage(bannerImage)
                .mainImage(mainImage)
                .gallery(gallery)
                .services(serviceItems)
                .hours(hourItems)
                .slots(slotItems)
                .reviews(reviewItems)
                .summary(ProviderDetailSummaryResponse.builder()
                        .priceFrom(priceFrom)
                        .currencyCode(firstNonBlank(provider.getCurrencyCode(), services.stream().map(ProviderService::getCurrencyCode).filter(Objects::nonNull).findFirst().orElse("VND")))
                        .cancellationFreeHours(provider.getCancellationFreeHours())
                        .openNow(openNow)
                        .distance(formatDistance(distanceKm))
                        .distanceKm(distanceKm)
                        .totalServices(serviceItems.size())
                        .totalGalleryImages(gallery.size())
                        .totalReviews(provider.getTotalReviews())
                        .totalCompletedBookings(provider.getTotalCompletedBookings())
                        .nextAvailableSlot(nextAvailableSlot)
                        .build())
                .build();
    }

    private ProviderDetailServiceItemResponse mapService(ProviderService providerService) {
        String serviceName = firstNonBlank(providerService.getCustomName(), providerService.getService().getName(), "Dịch vụ");
        String description = firstNonBlank(providerService.getShortDescription(), providerService.getDescription(), providerService.getService().getShortDescription(), providerService.getService().getDescription());
        return ProviderDetailServiceItemResponse.builder()
                .id(providerService.getId())
                .name(serviceName)
                .desc(description)
                .price(providerService.getPriceAmount())
                .priceDisplay(formatMoney(providerService.getPriceAmount()))
                .currencyCode(firstNonBlank(providerService.getCurrencyCode(), providerService.getService().getCurrencyCode(), "VND"))
                .priceUnit(providerService.getPriceUnit())
                .durationMinutes(providerService.getDurationMinutes())
                .duration(formatDuration(providerService.getDurationMinutes()))
                .featured(Boolean.TRUE.equals(providerService.getFeatured()))
                .categoryName(providerService.getService().getCategory().getName())
                .categorySlug(providerService.getService().getCategory().getSlug())
                .build();
    }

    private ProviderBusinessHourDetailResponse mapHour(ProviderBusinessHour hour) {
        return ProviderBusinessHourDetailResponse.builder()
                .weekday(hour.getWeekday())
                .days(mapWeekdayLabel(hour.getWeekday()))
                .time(buildHourLabel(hour))
                .opensAt(formatTime(hour.getOpensAt()))
                .closesAt(formatTime(hour.getClosesAt()))
                .breakStartsAt(formatTime(hour.getBreakStartsAt()))
                .breakEndsAt(formatTime(hour.getBreakEndsAt()))
                .closed(Boolean.TRUE.equals(hour.getClosed()))
                .build();
    }

    private ProviderSlotResponse mapSlot(ProviderAvailabilitySlot slot) {
        String serviceName = slot.getProviderService() != null && slot.getProviderService().getService() != null
                ? firstNonBlank(slot.getProviderService().getCustomName(), slot.getProviderService().getService().getName())
                : null;
        int capacityRemaining = Math.max(0, Optional.ofNullable(slot.getCapacityTotal()).orElse(0) - Optional.ofNullable(slot.getCapacityBooked()).orElse(0));
        return ProviderSlotResponse.builder()
                .id(slot.getId())
                .providerServiceId(slot.getProviderService() != null ? slot.getProviderService().getId() : null)
                .serviceName(serviceName)
                .date(slot.getSlotDate().format(DATE_FORMATTER))
                .startTime(formatTime(slot.getStartTime()))
                .endTime(formatTime(slot.getEndTime()))
                .label(formatTime(slot.getStartTime()))
                .capacityRemaining(capacityRemaining)
                .build();
    }

    private ProviderReviewResponse mapReview(Review review) {
        Pet pet = review.getBooking() != null ? review.getBooking().getPet() : null;
        User user = review.getCustomerUser();
        return ProviderReviewResponse.builder()
                .id(review.getId())
                .user(user != null ? user.getFullName() : "Khách hàng PetGo")
                .avatar(user != null ? user.getAvatarUrl() : null)
                .rating(review.getRating())
                .comment(review.getComment())
                .date(review.getCreatedAt() != null ? review.getCreatedAt().toLocalDate().format(DATE_FORMATTER) : null)
                .petName(pet != null ? pet.getName() : null)
                .petBreed(pet != null ? pet.getBreed() : null)
                .build();
    }

    private String buildAddress(ProviderProfile provider) {
        List<String> parts = new ArrayList<>();
        addIfPresent(parts, provider.getPrimaryAddressLine1());
        addIfPresent(parts, provider.getWard());
        addIfPresent(parts, provider.getDistrict());
        addIfPresent(parts, provider.getCity());
        addIfPresent(parts, provider.getProvince());
        return String.join(", ", parts);
    }

    private void addIfPresent(List<String> parts, String value) {
        if (value != null && !value.isBlank()) {
            parts.add(value.trim());
        }
    }

    private BigDecimal resolveLowestPrice(ProviderProfile provider, List<ProviderService> services) {
        return services.stream()
                .map(ProviderService::getPriceAmount)
                .filter(Objects::nonNull)
                .min(Comparator.naturalOrder())
                .orElse(provider.getPriceFromAmount());
    }

    private List<String> buildGallery(List<ProviderPhoto> photos, String bannerImage, String mainImage) {
        LinkedHashSet<String> urls = new LinkedHashSet<>();
        if (bannerImage != null && !bannerImage.isBlank()) urls.add(bannerImage);
        if (mainImage != null && !mainImage.isBlank()) urls.add(mainImage);
        for (ProviderPhoto photo : photos) {
            if (photo.getPhotoUrl() != null && !photo.getPhotoUrl().isBlank()) {
                urls.add(photo.getPhotoUrl());
            }
        }
        return new ArrayList<>(urls);
    }

    private String firstPhotoUrl(List<ProviderPhoto> photos) {
        return photos.stream().map(ProviderPhoto::getPhotoUrl).filter(Objects::nonNull).filter(url -> !url.isBlank()).findFirst().orElse(null);
    }

    private String fallbackProviderImage(ProviderProfile provider) {
        String slug = provider.getSlug() == null || provider.getSlug().isBlank() ? "petgo" : provider.getSlug();
        return "https://images.unsplash.com/photo-1516734212186-a967f81ad0d7?auto=format&fit=crop&q=80&w=1200&sig=" + Math.abs(slug.hashCode());
    }

    private String buildHourLabel(ProviderBusinessHour hour) {
        if (Boolean.TRUE.equals(hour.getClosed())) {
            return "Closed";
        }
        String opens = formatTime(hour.getOpensAt());
        String closes = formatTime(hour.getClosesAt());
        if (opens == null || closes == null) {
            return "Chưa cập nhật";
        }
        return opens + " - " + closes;
    }

    private String mapWeekdayLabel(Integer weekday) {
        if (weekday == null) return "N/A";
        return switch (weekday) {
            case 1 -> "Mon";
            case 2 -> "Tue";
            case 3 -> "Wed";
            case 4 -> "Thu";
            case 5 -> "Fri";
            case 6 -> "Sat";
            case 7 -> "Sun";
            default -> "N/A";
        };
    }

    private boolean isOpenNow(List<ProviderBusinessHour> businessHours, LocalTime now, DayOfWeek dayOfWeek) {
        int weekday = dayOfWeek.getValue();
        return businessHours.stream()
                .filter(hour -> Objects.equals(hour.getWeekday(), weekday))
                .filter(hour -> !Boolean.TRUE.equals(hour.getClosed()))
                .anyMatch(hour -> isWithinHourRange(hour, now));
    }

    private boolean isWithinHourRange(ProviderBusinessHour hour, LocalTime now) {
        if (hour.getOpensAt() == null || hour.getClosesAt() == null) {
            return false;
        }
        boolean inOpenRange = !now.isBefore(hour.getOpensAt()) && !now.isAfter(hour.getClosesAt());
        if (!inOpenRange) {
            return false;
        }
        if (hour.getBreakStartsAt() != null && hour.getBreakEndsAt() != null) {
            boolean inBreak = !now.isBefore(hour.getBreakStartsAt()) && !now.isAfter(hour.getBreakEndsAt());
            if (inBreak) {
                return false;
            }
        }
        return true;
    }

    private Double calculateDistanceKm(Double userLat, Double userLng, BigDecimal providerLat, BigDecimal providerLng) {
        if (userLat == null || userLng == null || providerLat == null || providerLng == null) {
            return null;
        }
        double earthRadiusKm = 6371.0;
        double lat1 = Math.toRadians(userLat);
        double lon1 = Math.toRadians(userLng);
        double lat2 = Math.toRadians(providerLat.doubleValue());
        double lon2 = Math.toRadians(providerLng.doubleValue());

        double dLat = lat2 - lat1;
        double dLon = lon2 - lon1;
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(lat1) * Math.cos(lat2) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return BigDecimal.valueOf(earthRadiusKm * c).setScale(1, RoundingMode.HALF_UP).doubleValue();
    }

    private String formatDistance(Double distanceKm) {
        if (distanceKm == null) {
            return "--";
        }
        return BigDecimal.valueOf(distanceKm).setScale(1, RoundingMode.HALF_UP).toPlainString() + " km";
    }

    private String formatMoney(BigDecimal amount) {
        if (amount == null) {
            return null;
        }
        return amount.setScale(0, RoundingMode.HALF_UP).toPlainString();
    }

    private String formatDuration(Integer minutes) {
        if (minutes == null || minutes <= 0) {
            return null;
        }
        if (minutes % 60 == 0) {
            return (minutes / 60) + " hr";
        }
        return minutes + " min";
    }

    private String formatTime(LocalTime time) {
        return time == null ? null : time.format(TIME_FORMATTER);
    }

    private String firstNonBlank(String... values) {
        if (values == null) {
            return null;
        }
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value.trim();
            }
        }
        return null;
    }
}
