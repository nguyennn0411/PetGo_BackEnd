package com.example.petgo.service.impl;

import com.example.petgo.dto.CreateReviewRequest;
import com.example.petgo.dto.ReviewResponse;
import com.example.petgo.config.AuthenticatedUser;
import com.example.petgo.entity.*;
import com.example.petgo.exception.BadRequestException;
import com.example.petgo.exception.ResourceNotFoundException;
import com.example.petgo.repository.*;
import com.example.petgo.service.AuthService;
import com.example.petgo.service.ReviewService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ServiceReviewRepository reviewRepository;
    private final ShippingBookingRepository bookingRepository;
    private final CatalogServiceRepository catalogServiceRepository;
    private final UserRepository userRepository;
    private final AuthService authService;

    @Override
    @Transactional
    public ReviewResponse createReview(HttpServletRequest request, CreateReviewRequest req) {
        AuthenticatedUser current = authService.requireAccessUser(request);
        User user = userRepository.findById(current.userId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng."));
        ShippingBooking booking = bookingRepository.findDetailedById(req.bookingId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy booking."));

        if (!booking.getUser().getId().equals(user.getId()))
            throw new BadRequestException("Bạn không thể đánh giá booking của người khác.");
        if (!"COMPLETED".equals(booking.getStatus()))
            throw new BadRequestException("Chỉ được đánh giá sau khi dịch vụ hoàn thành.");
        if (reviewRepository.existsByBookingId(req.bookingId()))
            throw new BadRequestException("Bạn đã đánh giá booking này rồi.");

        ServiceReview review = new ServiceReview();
        review.setBooking(booking);
        review.setUser(user);
        review.setService(booking.getService());
        review.setRating(req.rating());
        review.setContent(req.content() != null ? req.content().trim() : null);
        reviewRepository.save(review);

        updateServiceRating(booking.getService().getId());

        return mapReview(review);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponse> getReviewsByService(Long serviceId) {
        return reviewRepository.findByServiceIdAndHiddenFalseOrderByCreatedAtDesc(serviceId)
                .stream().map(this::mapReview).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasReviewed(HttpServletRequest request, Long bookingId) {
        authService.requireAccessUser(request);
        return reviewRepository.existsByBookingId(bookingId);
    }

    public void updateServiceRating(Long serviceId) {
        List<ServiceReview> reviews = reviewRepository.findByServiceIdAndHiddenFalseOrderByCreatedAtDesc(serviceId);
        int total = reviews.size();
        BigDecimal avg = BigDecimal.ZERO;
        if (total > 0) {
            int sum = reviews.stream().mapToInt(ServiceReview::getRating).sum();
            avg = BigDecimal.valueOf(sum).divide(BigDecimal.valueOf(total), 2, RoundingMode.HALF_UP);
        }
        CatalogService service = catalogServiceRepository.findById(serviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy dịch vụ."));
        service.setAverageRating(avg);
        service.setTotalReviews(total);
        catalogServiceRepository.save(service);
    }

    public ReviewResponse mapReview(ServiceReview review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .bookingId(review.getBooking().getId())
                .userId(review.getUser().getId())
                .userName(review.getUser().getFullName())
                .userAvatar(review.getUser().getAvatarUrl())
                .serviceId(review.getService().getId())
                .serviceName(review.getService().getName())
                .rating(review.getRating())
                .content(review.getContent())
                .hidden(review.getHidden() != null && review.getHidden())
                .reply(review.getReply())
                .createdAt(review.getCreatedAt())
                .build();
    }
}
