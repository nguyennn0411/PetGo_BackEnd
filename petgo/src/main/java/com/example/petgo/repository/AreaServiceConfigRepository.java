package com.example.petgo.repository;

import com.example.petgo.entity.AreaServiceConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AreaServiceConfigRepository extends JpaRepository<AreaServiceConfig, Long> {

    List<AreaServiceConfig> findByAreaId(Long areaId);

    Optional<AreaServiceConfig> findByAreaIdAndServiceId(Long areaId, Long serviceId);

    @Query("SELECT asc FROM AreaServiceConfig asc JOIN FETCH asc.service WHERE asc.area.id = :areaId AND asc.active = true")
    List<AreaServiceConfig> findActiveByAreaIdWithService(@Param("areaId") Long areaId);

    boolean existsByAreaIdAndServiceId(Long areaId, Long serviceId);

    @Query("SELECT asc FROM AreaServiceConfig asc JOIN FETCH asc.area WHERE asc.service.id = :serviceId AND asc.active = true")
    List<AreaServiceConfig> findActiveByServiceIdWithArea(@Param("serviceId") Long serviceId);

    void deleteByServiceId(Long serviceId);

    void deleteByAreaId(Long areaId);

    @Query("SELECT DISTINCT asc.service.id FROM AreaServiceConfig asc WHERE asc.active = true")
    List<Long> findDistinctServiceIdsByActiveTrue();

    @Query("SELECT asc.service.id, asc.area.id FROM AreaServiceConfig asc WHERE asc.active = true")
    List<Object[]> findActiveServiceAreaPairs();
}
