package com.example.petgo.service.partner.impl;

import com.example.petgo.dto.ReviewPhotoResponse;
import com.example.petgo.dto.partner.PartnerReviewListResponse;
import com.example.petgo.dto.partner.PartnerReviewSummaryResponse;
import com.example.petgo.entity.Booking;
import com.example.petgo.entity.Pet;
import com.example.petgo.entity.ProviderProfile;
import com.example.petgo.entity.Review;
import com.example.petgo.entity.ReviewPhoto;
import com.example.petgo.entity.User;
import com.example.petgo.exception.BadRequestException;
import com.example.petgo.repository.ReviewPhotoRepository;
import com.example.petgo.repository.ReviewRepository;
import com.example.petgo.service.partner.PartnerAccessService;
import com.example.petgo.service.partner.PartnerMappingSupport;
import com.example.petgo.service.partner.PartnerReviewManagementService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PartnerReviewManagementServiceImpl implements PartnerReviewManagementService {

    private static final int DEFAULT_PAGE_SIZE = 12;
    private static final int MAX_PAGE_SIZE = 50;

    private final PartnerAccessService partnerAccessService;
    private final PartnerMappingSupport mapper;
    private final ReviewRepository reviewRepository;
    private final ReviewPhotoRepository reviewPhotoRepository;

    @Override
    @Transactional(readOnly = true)
    public PartnerReviewListResponse listReviews(HttpServletRequest request, Integer rating, Long serviceId,
            String from,
            String to, String keyword, Integer page, Integer size) {
        ProviderProfile provider = partnerAccessService.requirePartnerContext(request).provider();
        LocalDate fromDate = parseOptionalDate(from);
        LocalDate toDate = parseOptionalDate(to);
        if (fromDate != null && toDate != null && toDate.isBefore(fromDate)) {
            throw new BadRequestException("Khoảng ngày lọc review không hợp lệ.");
        }
        Integer normalizedRating = normalizeRating(rating);
        String normalizedKeyword = mapper.normalizeBlank(keyword);
        int safePage = safePage(page);
        int safeSize = safeSize(size);

        List<Review> filteredReviews = reviewRepository.findVisibleDetailedByProviderId(provider.getId()).stream()
                .filter(review -> normalizedRating == null || Objects.equals(review.getRating(), normalizedRating))
                .filter(review -> serviceId == null || (review.getBooking() != null
                        && review.getBooking().getProviderService() != null
                        && Objects.equals(review.getBooking().getProviderService().getId(), serviceId)))
                .filter(review -> matchesDateRange(review, fromDate, toDate))
                .filter(review -> matchesKeyword(review, normalizedKeyword))
                .toList();

        int totalItems = filteredReviews.size();
        List<Review> pageReviews = slice(filteredReviews, safePage, safeSize);
        Map<Long, List<ReviewPhotoResponse>> photoMap = buildPhotoMap(pageReviews);

        return PartnerReviewListResponse.builder()
                .providerId(provider.getId())
                .businessName(provider.getBusinessName())
                .averageRating(calculateAverage(filteredReviews))
                .averageRatingDisplay(formatRating(calculateAverage(filteredReviews)))
                .totalReviews(totalItems)
                .ratingDistribution(buildDistribution(filteredReviews))
                .filterRating(normalizedRating)
                .filterServiceId(serviceId)
                .keyword(normalizedKeyword)
                .fromDate(mapper.formatIsoDate(fromDate))
                .toDate(mapper.formatIsoDate(toDate))
                .page(safePage)
                .size(safeSize)
                .totalItems(totalItems)
                .totalPages(totalPages(totalItems, safeSize))
                .reviews(pageReviews.stream().map(review -> mapReview(review, photoMap)).toList())
                .build();
    }

    @Override
    @Transactional
    public PartnerReviewSummaryResponse replyReview(HttpServletRequest request, Long reviewId, String reply) {
        ProviderProfile provider = partnerAccessService.requirePartnerContext(request).provider();
        String normalizedReply = mapper.normalizeBlank(reply);
        if (normalizedReply == null) {
            throw new BadRequestException("Nội dung phản hồi review không được để trống.");
        }
        if (normalizedReply.length() > 1000) {
            throw new BadRequestException("Nội dung phản hồi review tối đa 1000 ký tự.");
        }
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new BadRequestException("Không tìm thấy review."));
        if (review.getDeletedAt() != null || review.getProvider() == null
                || !Objects.equals(review.getProvider().getId(), provider.getId())) {
            throw new BadRequestException("Bạn không có quyền phản hồi review này.");
        }
        review.setProviderReply(normalizedReply);
        review.setProviderRepliedAt(LocalDateTime.now(PartnerMappingSupport.APP_ZONE));
        review.setProviderRepliedBy(provider.getUser());
        reviewRepository.save(review);
        return mapReview(review, buildPhotoMap(List.of(review)));
    }

    private PartnerReviewSummaryResponse mapReview(Review review, Map<Long, List<ReviewPhotoResponse>> photoMap) {
        Booking booking = review.getBooking();
        User customer = review.getCustomerUser();
        Pet pet = booking != null ? booking.getPet() : null;
        return PartnerReviewSummaryResponse.builder()
                .reviewId(review.getId())
                .rating(review.getRating())
                .comment(review.getComment())
                .status(review.getStatus())
                .createdAt(formatDateTime(review.getCreatedAt()))
                .customerUserId(customer != null ? customer.getId() : null)
                .customerName(
                        customer != null ? mapper.firstNonBlank(customer.getFullName(), customer.getEmail()) : null)
                .customerAvatarUrl(customer != null ? customer.getAvatarUrl() : null)
                .bookingId(booking != null ? booking.getId() : null)
                .bookingCode(booking != null ? booking.getBookingCode() : null)
                .bookingStatus(booking != null ? booking.getStatus() : null)
                .bookingStatusLabel(booking != null ? mapper.mapStatusLabel(booking.getStatus()) : null)
                .providerServiceId(booking != null && booking.getProviderService() != null
                        ? booking.getProviderService().getId()
                        : null)
                .serviceName(booking != null ? booking.getServiceNameSnapshot() : null)
                .petId(pet != null ? pet.getId() : null)
                .petName(booking != null ? mapper.firstNonBlank(pet != null ? pet.getName() : null,
                        booking.getPetNameSnapshot()) : null)
                .petBreed(booking != null ? mapper.firstNonBlank(pet != null ? pet.getBreed() : null,
                        booking.getPetBreedSnapshot()) : null)
                .appointmentDate(booking != null ? mapper.formatIsoDate(booking.getAppointmentDate()) : null)
                .appointmentDateDisplay(booking != null ? mapper.formatDate(booking.getAppointmentDate()) : null)
                .providerReply(review.getProviderReply())
                .providerRepliedAt(formatDateTime(review.getProviderRepliedAt()))
                .adminNote(review.getAdminNote())
                .adminReviewedAt(formatDateTime(review.getAdminReviewedAt()))
                .photos(photoMap.getOrDefault(review.getId(), List.of()))
                .build();
    }

    private Map<Long, List<ReviewPhotoResponse>> buildPhotoMap(List<Review> reviews) {
        List<Long> reviewIds = reviews.stream().map(Review::getId).filter(Objects::nonNull).toList();
        if (reviewIds.isEmpty()) {
            return Map.of();
        }
        return reviewPhotoRepository.findByReview_IdInOrderBySortOrderAscIdAsc(reviewIds).stream()
                .collect(Collectors.groupingBy(photo -> photo.getReview().getId(), LinkedHashMap::new,
                        Collectors.mapping(this::mapPhoto, Collectors.toList())));
    }

    private ReviewPhotoResponse mapPhoto(ReviewPhoto photo) {
        return ReviewPhotoResponse.builder()
                .photoUrl(photo.getPhotoUrl())
                .sortOrder(photo.getSortOrder())
                .build();
    }

    private boolean matchesDateRange(Review review, LocalDate fromDate, LocalDate toDate) {
        LocalDate reviewDate = reviewDate(review);
        if (reviewDate == null) {
            return true;
        }
        if (fromDate != null && reviewDate.isBefore(fromDate)) {
            return false;
        }
        return toDate == null || !reviewDate.isAfter(toDate);
    }

    private boolean matchesKeyword(Review review, String keyword) {
        if (keyword == null) {
            return true;
        }
        String normalized = keyword.toLowerCase(Locale.ROOT);
        Booking booking = review.getBooking();
        User customer = review.getCustomerUser();
        return contains(review.getComment(), normalized)
                || contains(customer != null ? customer.getFullName() : null, normalized)
                || contains(booking != null ? booking.getBookingCode() : null, normalized)
                || contains(booking != null ? booking.getServiceNameSnapshot() : null, normalized)
                || contains(booking != null ? booking.getPetNameSnapshot() : null, normalized)
                || contains(booking != null ? booking.getPetBreedSnapshot() : null, normalized);
    }

    private boolean contains(String value, String keyword) {
        return value != null && value.toLowerCase(Locale.ROOT).contains(keyword);
    }

    private Map<Integer, Long> buildDistribution(List<Review> reviews) {
        Map<Integer, Long> distribution = new LinkedHashMap<>();
        for (int value = 5; value >= 1; value--) {
            int rating = value;
            distribution.put(rating,
                    reviews.stream().filter(review -> Objects.equals(review.getRating(), rating)).count());
        }
        return distribution;
    }

    private BigDecimal calculateAverage(List<Review> reviews) {
        if (reviews.isEmpty()) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        BigDecimal sum = reviews.stream()
                .map(Review::getRating)
                .filter(Objects::nonNull)
                .map(BigDecimal::valueOf)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return sum.divide(BigDecimal.valueOf(reviews.size()), 2, RoundingMode.HALF_UP);
    }

    private String formatRating(BigDecimal rating) {
        return rating == null ? "0.00" : rating.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    private Integer normalizeRating(Integer rating) {
        if (rating == null) {
            return null;
        }
        if (rating < 1 || rating > 5) {
            throw new BadRequestException("Rating filter phải từ 1 đến 5.");
        }
        return rating;
    }

    private LocalDate parseOptionalDate(String value) {
        String normalized = mapper.normalizeBlank(value);
        if (normalized == null) {
            return null;
        }
        try {
            return LocalDate.parse(normalized, PartnerMappingSupport.ISO_DATE);
        } catch (DateTimeParseException ex) {
            throw new BadRequestException("Ngày filter cần định dạng yyyy-MM-dd.");
        }
    }

    private LocalDate reviewDate(Review review) {
        LocalDateTime createdAt = review.getCreatedAt();
        return createdAt != null ? createdAt.toLocalDate() : null;
    }

    private String formatDateTime(LocalDateTime value) {
        return value != null ? value.atZone(PartnerMappingSupport.APP_ZONE).format(PartnerMappingSupport.DATE_TIME_VIEW)
                : null;
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
}