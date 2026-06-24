package com.example.petgo.repository;

import com.example.petgo.entity.ProviderServiceChangeRequest;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProviderServiceChangeRequestRepository extends JpaRepository<ProviderServiceChangeRequest, Long> {

    @EntityGraph(attributePaths = {
            "provider", "provider.user",
            "providerService", "providerService.service", "providerService.service.category", "providerService.service.category.parent",
            "reviewer"
    })
    @Query("""
            select request
            from ProviderServiceChangeRequest request
            where request.provider.id = :providerId
            order by request.updatedAt desc, request.createdAt desc, request.id desc
            """)
    List<ProviderServiceChangeRequest> findDetailedByProviderId(@Param("providerId") Long providerId);

    @EntityGraph(attributePaths = {
            "provider", "provider.user",
            "providerService", "providerService.service", "providerService.service.category", "providerService.service.category.parent",
            "reviewer"
    })
    @Query("""
            select request
            from ProviderServiceChangeRequest request
            where request.provider.id = :providerId
              and request.id = :requestId
            """)
    Optional<ProviderServiceChangeRequest> findDetailedByProviderIdAndId(@Param("providerId") Long providerId,
            @Param("requestId") Long requestId);

    @EntityGraph(attributePaths = {
            "provider", "provider.user",
            "providerService", "providerService.service", "providerService.service.category", "providerService.service.category.parent",
            "reviewer"
    })
    @Query("""
            select request
            from ProviderServiceChangeRequest request
            where request.id = :requestId
            """)
    Optional<ProviderServiceChangeRequest> findDetailedById(@Param("requestId") Long requestId);

    @EntityGraph(attributePaths = {
            "provider", "provider.user",
            "providerService", "providerService.service", "providerService.service.category", "providerService.service.category.parent",
            "reviewer"
    })
    @Query("""
            select request
            from ProviderServiceChangeRequest request
            where (:status is null or upper(request.status) = upper(:status))
            order by request.submittedAt asc, request.createdAt asc, request.id asc
            """)
    List<ProviderServiceChangeRequest> findDetailedForAdmin(@Param("status") String status);

    long countByProvider_IdAndStatus(Long providerId, String status);

    boolean existsByProvider_IdAndProviderService_IdAndStatus(Long providerId, Long providerServiceId, String status);

    boolean existsByProvider_IdAndProviderService_IdAndStatusAndIdNot(Long providerId, Long providerServiceId,
            String status, Long id);
}