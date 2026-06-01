package com.example.petgo.repository;

import com.example.petgo.entity.Review;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

  @EntityGraph(attributePaths = { "customerUser", "booking", "booking.pet" })
  List<Review> findByStatusAndDeletedAtIsNullOrderByCreatedAtDesc(String status, Pageable pageable);

  @EntityGraph(attributePaths = { "customerUser", "booking", "booking.pet" })
  @Query("""
      select r
      from Review r
      where r.provider.id = :providerId
        and r.status = 'VISIBLE'
        and r.deletedAt is null
      order by r.createdAt desc, r.id desc
      """)
  List<Review> findVisibleByProviderId(@Param("providerId") Long providerId, Pageable pageable);

  @EntityGraph(attributePaths = { "customerUser", "booking", "booking.pet", "booking.providerService",
      "booking.providerService.service" })
  @Query("""
      select r
      from Review r
      where r.provider.id = :providerId
        and r.status = 'VISIBLE'
        and r.deletedAt is null
      order by r.createdAt desc, r.id desc
      """)
  List<Review> findVisibleDetailedByProviderId(@Param("providerId") Long providerId);

  @EntityGraph(attributePaths = { "customerUser", "provider", "booking", "booking.pet" })
  @Query("""
      select r
      from Review r
      where r.customerUser.id = :ownerUserId
        and r.deletedAt is null
      order by r.createdAt desc, r.id desc
      """)
  List<Review> findByOwnerUserId(@Param("ownerUserId") Long ownerUserId);

  @EntityGraph(attributePaths = { "customerUser", "provider", "booking", "booking.pet" })
  @Query("""
      select r
      from Review r
      where r.booking.id = :bookingId
        and r.deletedAt is null
      """)
  Optional<Review> findActiveByBookingId(@Param("bookingId") Long bookingId);

  boolean existsByBooking_IdAndDeletedAtIsNull(Long bookingId);

  @Query("""
      select count(r)
      from Review r
      where r.provider.id = :providerId
        and r.status = 'VISIBLE'
        and r.deletedAt is null
      """)
  long countVisibleByProviderId(@Param("providerId") Long providerId);

  @Query("""
      select avg(r.rating)
      from Review r
      where r.provider.id = :providerId
        and r.status = 'VISIBLE'
        and r.deletedAt is null
      """)
  BigDecimal averageVisibleRatingByProviderId(@Param("providerId") Long providerId);

  long countByCustomerUser_IdAndDeletedAtIsNull(Long ownerUserId);
}
