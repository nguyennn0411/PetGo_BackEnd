package com.example.petgo.repository;

import com.example.petgo.entity.ProviderBusinessHour;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface ProviderBusinessHourRepository extends JpaRepository<ProviderBusinessHour, Long> {
    List<ProviderBusinessHour> findByProvider_IdIn(Collection<Long> providerIds);

    List<ProviderBusinessHour> findByProvider_IdOrderByWeekdayAscIdAsc(Long providerId);
}
