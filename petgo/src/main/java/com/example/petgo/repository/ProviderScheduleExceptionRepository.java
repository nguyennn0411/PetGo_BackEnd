package com.example.petgo.repository;

import com.example.petgo.entity.ProviderScheduleException;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ProviderScheduleExceptionRepository extends JpaRepository<ProviderScheduleException, Long> {
        List<ProviderScheduleException> findByProvider_IdAndLocalDateOrderByStartsAtLocalAscIdAsc(Long providerId,
                        LocalDate localDate);

        List<ProviderScheduleException> findByProvider_IdAndLocalDateBetweenOrderByLocalDateAscStartsAtLocalAscIdAsc(
                        Long providerId, LocalDate from, LocalDate to);

        Optional<ProviderScheduleException> findFirstByProvider_IdAndLocalDateOrderByIdAsc(Long providerId,
                        LocalDate localDate);

        void deleteByProvider_IdAndLocalDate(Long providerId, LocalDate localDate);
}