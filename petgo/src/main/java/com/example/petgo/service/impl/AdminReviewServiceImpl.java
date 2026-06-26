package com.example.petgo.service.impl;

import com.example.petgo.dto.ReviewResponse;
import com.example.petgo.entity.ServiceReview;
import com.example.petgo.exception.ResourceNotFoundException;
import com.example.petgo.repository.ServiceReviewRepository;
import com.example.petgo.service.AdminReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class AdminReviewServiceImpl implements AdminReviewService {

    private final ServiceReviewRepository reviewRepository;
    private final ReviewServiceImpl reviewService;

    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponse> getAllReviews(String search, Integer rating, Boolean hidden) {
        Stream<ServiceReview> stream = reviewRepository.findAll().stream();

        if (search != null && !search.isBlank()) {
            String q = search.toLowerCase();
            stream = stream.filter(r ->
                    (r.getUser().getFullName() != null && r.getUser().getFullName().toLowerCase().contains(q)) ||
                    (r.getService().getName() != null && r.getService().getName().toLowerCase().contains(q)) ||
                    (r.getContent() != null && r.getContent().toLowerCase().contains(q)));
        }
        if (rating != null) {
            stream = stream.filter(r -> r.getRating().equals(rating));
        }
        if (hidden != null) {
            stream = stream.filter(r -> hidden.equals(r.getHidden()));
        }

        return stream
                .sorted(Comparator.comparing(ServiceReview::getCreatedAt).reversed())
                .map(reviewService::mapReview)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ReviewResponse getReviewDetail(Long id) {
        ServiceReview review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đánh giá."));
        return reviewService.mapReview(review);
    }

    @Override
    @Transactional
    public ReviewResponse toggleHidden(Long id) {
        ServiceReview review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đánh giá."));
        review.setHidden(review.getHidden() == null || !review.getHidden());
        reviewRepository.save(review);
        reviewService.updateServiceRating(review.getService().getId());
        return reviewService.mapReview(review);
    }

    @Override
    @Transactional
    public ReviewResponse reply(Long id, String reply) {
        ServiceReview review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đánh giá."));
        review.setReply(reply != null ? reply.trim() : null);
        reviewRepository.save(review);
        return reviewService.mapReview(review);
    }

    @Override
    @Transactional
    public void deleteReview(Long id) {
        ServiceReview review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đánh giá."));
        Long serviceId = review.getService().getId();
        reviewRepository.delete(review);
        reviewService.updateServiceRating(serviceId);
    }
}
