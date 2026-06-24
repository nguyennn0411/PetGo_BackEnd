package com.example.petgo.service.impl;

import com.example.petgo.dto.*;
import com.example.petgo.entity.Booking;
import com.example.petgo.entity.ProviderProfile;
import com.example.petgo.entity.Review;
import com.example.petgo.entity.ReviewPhoto;
import com.example.petgo.exception.BadRequestException;
import com.example.petgo.exception.ResourceNotFoundException;
import com.example.petgo.repository.*;
import com.example.petgo.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private static final ZoneId APP_ZONE = ZoneId.of("Asia/Ho_Chi_Minh");
    private static final DateTimeFormatter DATE_VIEW = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATE_TIME_VIEW = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewPhotoRepository reviewPhotoRepository;
    private final ProviderProfileRepository providerProfileRepository;

    @Override
    @Transactional(readOnly = true)
    public ReviewContextResponse getReviewContext(Long ownerUserId, Long bookingId) {
        userRepository.findById(ownerUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));
        Booking booking = bookingRepository.findDetailedOwnedById(ownerUserId, bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy booking của người dùng này"));

        Optional<Review> existingReview = reviewRepository.findActiveByBookingId(bookingId);
        boolean canReview = canReview(booking, existingReview.orElse(null));

        return ReviewContextResponse.builder()
                .ownerUserId(ownerUserId)
                .bookingId(booking.getId())
                .bookingCode(booking.getBookingCode())
                .providerId(booking.getProvider() != null ? booking.getProvider().getId() : null)
                .providerName(booking.getProviderNameSnapshot())
                .providerImage(resolveProviderImage(booking))
                .serviceName(booking.getServiceNameSnapshot())
                .appointmentDate(booking.getAppointmentDate() != null ? booking.getAppointmentDate().toString() : null)
                .appointmentDateDisplay(booking.getAppointmentDate() != null ? booking.getAppointmentDate().format(DATE_VIEW) : null)
                .canReview(canReview)
                .existingReviewId(existingReview.map(Review::getId).orElse(null))
                .note(canReview ? "Bạn có thể gửi đánh giá cho booking này." : buildBlockedNote(booking, existingReview.orElse(null)))
                .build();
    }

    @Override
    @Transactional
    public ReviewCreateResponse createReview(Long ownerUserId, Long bookingId, ReviewCreateRequest request) {
        if (!Objects.equals(ownerUserId, request.ownerUserId())) {
            throw new BadRequestException("ownerUserId không khớp với đường dẫn");
        }

        userRepository.findById(ownerUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));
        Booking booking = bookingRepository.findDetailedOwnedById(ownerUserId, bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy booking của người dùng này"));

        Review existingReview = reviewRepository.findActiveByBookingId(bookingId).orElse(null);
        if (!canReview(booking, existingReview)) {
            throw new BadRequestException(buildBlockedNote(booking, existingReview));
        }

        Review review = new Review();
        review.setBooking(booking);
        review.setCustomerUser(booking.getCustomerUser());
        review.setProvider(booking.getProvider());
        review.setRating(request.rating());
        review.setComment(normalizeBlank(request.comment()));
        review.setStatus("VISIBLE");
        reviewRepository.save(review);

        List<String> photoUrls = Optional.ofNullable(request.photoUrls()).orElse(List.of()).stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(url -> !url.isBlank())
                .distinct()
                .limit(5)
                .toList();

        int sortOrder = 0;
        for (String photoUrl : photoUrls) {
            ReviewPhoto photo = new ReviewPhoto();
            photo.setReview(review);
            photo.setPhotoUrl(photoUrl);
            photo.setSortOrder(sortOrder++);
            reviewPhotoRepository.save(photo);
        }

        syncProviderReviewStats(booking.getProvider().getId());

        return ReviewCreateResponse.builder()
                .reviewId(review.getId())
                .bookingId(bookingId)
                .providerId(booking.getProvider() != null ? booking.getProvider().getId() : null)
                .rating(review.getRating())
                .status(review.getStatus())
                .createdAt(review.getCreatedAt() != null ? review.getCreatedAt().atZone(APP_ZONE).format(DATE_TIME_VIEW) : null)
                .message("Gửi đánh giá thành công")
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public UserReviewListResponse getMyReviews(Long ownerUserId) {
        userRepository.findById(ownerUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));

        List<Review> reviews = reviewRepository.findByOwnerUserId(ownerUserId);
        List<Long> reviewIds = reviews.stream().map(Review::getId).toList();
        Map<Long, List<ReviewPhotoResponse>> photoMap = reviewIds.isEmpty()
                ? Map.of()
                : reviewPhotoRepository.findByReview_IdInOrderBySortOrderAscIdAsc(reviewIds).stream()
                .collect(Collectors.groupingBy(photo -> photo.getReview().getId(), LinkedHashMap::new, Collectors.mapping(photo ->
                                ReviewPhotoResponse.builder()
                                        .photoUrl(photo.getPhotoUrl())
                                        .sortOrder(photo.getSortOrder())
                                        .build(),
                        Collectors.toList()
                )));

        List<UserReviewResponse> items = reviews.stream()
                .map(review -> UserReviewResponse.builder()
                        .reviewId(review.getId())
                        .bookingId(review.getBooking() != null ? review.getBooking().getId() : null)
                        .providerId(review.getProvider() != null ? review.getProvider().getId() : null)
                        .providerName(review.getProvider() != null ? review.getProvider().getBusinessName() : null)
                        .providerImage(resolveProviderImage(review.getBooking()))
                        .serviceName(review.getBooking() != null ? review.getBooking().getServiceNameSnapshot() : null)
                        .petName(review.getBooking() != null ? review.getBooking().getPetNameSnapshot() : null)
                        .rating(review.getRating())
                        .comment(review.getComment())
                        .status(review.getStatus())
                        .createdAt(review.getCreatedAt() != null ? review.getCreatedAt().atZone(APP_ZONE).format(DATE_TIME_VIEW) : null)
                        .photos(photoMap.getOrDefault(review.getId(), List.of()))
                        .build())
                .toList();

        return UserReviewListResponse.builder()
                .ownerUserId(ownerUserId)
                .reviews(items)
                .totalItems(items.size())
                .build();
    }

    private boolean canReview(Booking booking, Review existingReview) {
        return booking != null
                && "COMPLETED".equalsIgnoreCase(firstNonBlank(booking.getStatus(), ""))
                && existingReview == null;
    }

    private String buildBlockedNote(Booking booking, Review existingReview) {
        if (existingReview != null) {
            return "Booking này đã được đánh giá trước đó.";
        }
        if (booking == null) {
            return "Không tìm thấy booking để đánh giá.";
        }
        if (!"COMPLETED".equalsIgnoreCase(firstNonBlank(booking.getStatus(), ""))) {
            return "Chỉ có thể đánh giá booking đã hoàn thành.";
        }
        return "Booking hiện không thể đánh giá.";
    }

    private void syncProviderReviewStats(Long providerId) {
        if (providerId == null) return;
        ProviderProfile provider = providerProfileRepository.findById(providerId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhà cung cấp"));
        long totalReviews = reviewRepository.countVisibleByProviderId(providerId);
        BigDecimal averageRating = reviewRepository.averageVisibleRatingByProviderId(providerId);
        provider.setTotalReviews((int) totalReviews);
        provider.setAverageRating(averageRating != null ? averageRating.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
        providerProfileRepository.save(provider);
    }

    private String resolveProviderImage(Booking booking) {
        if (booking == null || booking.getProvider() == null) return null;
        return firstNonBlank(booking.getProvider().getMainImageUrl(), booking.getProvider().getCoverImageUrl());
    }

    private String normalizeBlank(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private String firstNonBlank(String... values) {
        if (values == null) return null;
        for (String value : values) {
            if (value != null && !value.isBlank()) return value;
        }
        return null;
    }
}
