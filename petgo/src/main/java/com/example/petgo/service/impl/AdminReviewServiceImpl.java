package com.example.petgo.service.impl;

import com.example.petgo.config.AuthenticatedUser;
import com.example.petgo.dto.*;
import com.example.petgo.entity.*;
import com.example.petgo.exception.BadRequestException;
import com.example.petgo.exception.ResourceNotFoundException;
import com.example.petgo.exception.UnauthorizedException;
import com.example.petgo.repository.*;
import com.example.petgo.service.AdminReviewService;
import com.example.petgo.service.AuthService;
import com.example.petgo.service.partner.PartnerMappingSupport;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminReviewServiceImpl implements AdminReviewService {
    private final AuthService authService;
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewPhotoRepository reviewPhotoRepository;

    @Override
    @Transactional(readOnly = true)
    public AdminReviewListResponse listReviews(HttpServletRequest request, String status, String keyword) {
        requireAdmin(request);
        String normalizedStatus = normalizeBlank(status);
        String normalizedKeyword = normalizeBlank(keyword);
        List<Review> reviews = reviewRepository.findAllActiveDetailed().stream()
                .filter(r -> normalizedStatus == null || normalizedStatus.equalsIgnoreCase(r.getStatus()))
                .filter(r -> matchesKeyword(r, normalizedKeyword))
                .toList();
        Map<Long, List<ReviewPhotoResponse>> photoMap = buildPhotoMap(reviews);
        return AdminReviewListResponse.builder()
                .totalItems(reviews.size())
                .reviews(reviews.stream().map(r -> mapReview(r, photoMap)).toList())
                .build();
    }

    @Override
    @Transactional
    public AdminReviewResponse moderateReview(HttpServletRequest request, Long reviewId,
            AdminReviewModerationRequest requestBody) {
        User admin = requireAdmin(request);
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy review."));
        String status = normalizeBlank(requestBody.status());
        if (status != null) {
            String upper = status.toUpperCase(Locale.ROOT);
            if (!List.of("VISIBLE", "HIDDEN", "REPORTED").contains(upper))
                throw new BadRequestException("Trạng thái review không hợp lệ.");
            review.setStatus(upper);
        }
        review.setAdminNote(normalizeBlank(requestBody.adminNote()));
        review.setAdminReviewedAt(LocalDateTime.now(PartnerMappingSupport.APP_ZONE));
        review.setAdminReviewedBy(admin);
        reviewRepository.save(review);
        return mapReview(review, buildPhotoMap(List.of(review)));
    }

    private AdminReviewResponse mapReview(Review review, Map<Long, List<ReviewPhotoResponse>> photoMap) {
        User customer = review.getCustomerUser();
        ProviderProfile provider = review.getProvider();
        Booking booking = review.getBooking();
        return AdminReviewResponse.builder()
                .reviewId(review.getId()).rating(review.getRating()).comment(review.getComment())
                .status(review.getStatus())
                .createdAt(formatDateTime(review.getCreatedAt()))
                .customerUserId(customer != null ? customer.getId() : null)
                .customerName(customer != null ? firstNonBlank(customer.getFullName(), customer.getEmail()) : null)
                .providerId(provider != null ? provider.getId() : null)
                .providerName(provider != null ? provider.getBusinessName() : null)
                .bookingId(booking != null ? booking.getId() : null)
                .bookingCode(booking != null ? booking.getBookingCode() : null)
                .serviceName(booking != null ? booking.getServiceNameSnapshot() : null)
                .providerReply(review.getProviderReply())
                .providerRepliedAt(formatDateTime(review.getProviderRepliedAt()))
                .adminNote(review.getAdminNote()).adminReviewedAt(formatDateTime(review.getAdminReviewedAt()))
                .photos(photoMap.getOrDefault(review.getId(), List.of())).build();
    }

    private Map<Long, List<ReviewPhotoResponse>> buildPhotoMap(List<Review> reviews) {
        List<Long> ids = reviews.stream().map(Review::getId).filter(Objects::nonNull).toList();
        if (ids.isEmpty())
            return Map.of();
        return reviewPhotoRepository.findByReview_IdInOrderBySortOrderAscIdAsc(ids).stream()
                .collect(
                        Collectors
                                .groupingBy(p -> p.getReview().getId(), LinkedHashMap::new,
                                        Collectors
                                                .mapping(
                                                        p -> ReviewPhotoResponse.builder().photoUrl(p.getPhotoUrl())
                                                                .sortOrder(p.getSortOrder()).build(),
                                                        Collectors.toList())));
    }

    private boolean matchesKeyword(Review r, String keyword) {
        if (keyword == null)
            return true;
        String k = keyword.toLowerCase(Locale.ROOT);
        return contains(r.getComment(), k) || contains(r.getProviderReply(), k)
                || contains(r.getCustomerUser() != null ? r.getCustomerUser().getFullName() : null, k)
                || contains(r.getProvider() != null ? r.getProvider().getBusinessName() : null, k)
                || contains(r.getBooking() != null ? r.getBooking().getBookingCode() : null, k);
    }

    private boolean contains(String value, String keyword) {
        return value != null && value.toLowerCase(Locale.ROOT).contains(keyword);
    }

    private String normalizeBlank(String value) {
        return value == null || value.trim().isEmpty() ? null : value.trim();
    }

    private String firstNonBlank(String... values) {
        for (String v : values)
            if (normalizeBlank(v) != null)
                return v.trim();
        return null;
    }

    private String formatDateTime(LocalDateTime value) {
        return value != null ? value.atZone(PartnerMappingSupport.APP_ZONE).format(PartnerMappingSupport.DATE_TIME_VIEW)
                : null;
    }

    private User requireAdmin(HttpServletRequest request) {
        AuthenticatedUser current = authService.requireAccessUser(request);
        User user = userRepository.findById(current.userId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng."));
        boolean admin = userRoleRepository.findByUser_Id(user.getId()).stream().anyMatch(ur -> ur.getRole() != null
                && ur.getRole().getCode() != null && "ADMIN".equalsIgnoreCase(ur.getRole().getCode().getCode()));
        if (!admin)
            throw new UnauthorizedException("Bạn không có quyền admin.");
        return user;
    }
}