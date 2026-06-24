package com.example.petgo.repository;

import com.example.petgo.entity.ReviewPhoto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface ReviewPhotoRepository extends JpaRepository<ReviewPhoto, Long> {
    List<ReviewPhoto> findByReview_IdInOrderBySortOrderAscIdAsc(Collection<Long> reviewIds);
    List<ReviewPhoto> findByReview_IdOrderBySortOrderAscIdAsc(Long reviewId);
}
