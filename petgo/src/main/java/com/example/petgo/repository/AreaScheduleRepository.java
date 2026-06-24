package com.example.petgo.repository;

import com.example.petgo.entity.AreaSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AreaScheduleRepository extends JpaRepository<AreaSchedule, Long> {

    List<AreaSchedule> findByAreaIdOrderByDayOfWeekAsc(Long areaId);

    Optional<AreaSchedule> findByAreaIdAndDayOfWeek(Long areaId, Integer dayOfWeek);

    List<AreaSchedule> findByAreaIdAndActiveTrue(Long areaId);

    void deleteByAreaId(Long areaId);
}
