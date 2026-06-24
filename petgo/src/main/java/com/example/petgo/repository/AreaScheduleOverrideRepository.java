package com.example.petgo.repository;

import com.example.petgo.entity.AreaScheduleOverride;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AreaScheduleOverrideRepository extends JpaRepository<AreaScheduleOverride, Long> {

    List<AreaScheduleOverride> findByAreaIdAndOverrideDateBetweenOrderByOverrideDateAsc(Long areaId, LocalDate from, LocalDate to);

    Optional<AreaScheduleOverride> findByAreaIdAndOverrideDate(Long areaId, LocalDate overrideDate);

    boolean existsByAreaIdAndOverrideDate(Long areaId, LocalDate overrideDate);

    void deleteByAreaId(Long areaId);
}
