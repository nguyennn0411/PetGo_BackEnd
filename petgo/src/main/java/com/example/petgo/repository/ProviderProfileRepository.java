package com.example.petgo.repository;

import com.example.petgo.entity.ProviderProfile;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProviderProfileRepository extends JpaRepository<ProviderProfile, Long> {

    @Query("""
            select p
            from ProviderProfile p
            where p.status = 'ACTIVE'
              and p.deletedAt is null
            order by p.featured desc, p.hot desc, p.averageRating desc, p.totalReviews desc, p.id asc
            """)
    List<ProviderProfile> findFeaturedProviders(Pageable pageable);

    @Query("""
            select p
            from ProviderProfile p
            where p.status = 'ACTIVE'
              and p.deletedAt is null
            order by p.averageRating desc, p.totalReviews desc, p.totalCompletedBookings desc, p.id asc
            """)
    List<ProviderProfile> findNearbyProviders(Pageable pageable);

    @Query("""
            select p
            from ProviderProfile p
            where p.status = 'ACTIVE'
              and p.deletedAt is null
            order by p.featured desc, p.hot desc, p.averageRating desc, p.totalReviews desc, p.id asc
            """)
    List<ProviderProfile> findActiveProviders();

    @Query("""
            select distinct p.city
            from ProviderProfile p
            where p.status = 'ACTIVE'
              and p.deletedAt is null
              and p.city is not null
              and trim(p.city) <> ''
            order by p.city asc
            """)
    List<String> findDistinctActiveCities();

    @Query("""
            select p
            from ProviderProfile p
            where p.id = :providerId
              and p.status = 'ACTIVE'
              and p.deletedAt is null
            """)
    Optional<ProviderProfile> findActiveById(@Param("providerId") Long providerId);

    List<ProviderProfile> findByVerificationStatus(String status);
}
