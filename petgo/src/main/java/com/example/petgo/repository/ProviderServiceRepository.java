package com.example.petgo.repository;

import com.example.petgo.entity.ProviderService;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ProviderServiceRepository extends JpaRepository<ProviderService, Long> {

    @EntityGraph(attributePaths = {"provider", "service", "service.category"})
    @Query("""
            select ps
            from ProviderService ps
            where ps.provider.id in :providerIds
              and ps.active = true
              and ps.provider.status = 'ACTIVE'
              and ps.provider.deletedAt is null
              and ps.service.active = true
              and ps.service.category.active = true
            order by ps.provider.id asc, ps.featured desc, ps.displayOrder asc, ps.id asc
            """)
    List<ProviderService> findActiveByProviderIds(@Param("providerIds") Collection<Long> providerIds);

    @EntityGraph(attributePaths = {"provider", "service", "service.category"})
    @Query("""
            select ps
            from ProviderService ps
            where ps.provider.id = :providerId
              and ps.active = true
              and ps.provider.status = 'ACTIVE'
              and ps.provider.deletedAt is null
              and ps.service.active = true
              and ps.service.category.active = true
            order by ps.featured desc, ps.displayOrder asc, ps.id asc
            """)
    List<ProviderService> findActiveDetailsByProviderId(@Param("providerId") Long providerId);


    @EntityGraph(attributePaths = {"provider", "service", "service.category"})
    @Query("""
            select ps
            from ProviderService ps
            where ps.id = :providerServiceId
              and ps.active = true
              and ps.provider.status = 'ACTIVE'
              and ps.provider.deletedAt is null
              and ps.service.active = true
              and ps.service.category.active = true
            """)
    Optional<ProviderService> findActiveDetailById(@Param("providerServiceId") Long providerServiceId);

    List<ProviderService> findByProvider_Id(Long providerId);
}