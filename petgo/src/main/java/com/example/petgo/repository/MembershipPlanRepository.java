package com.example.petgo.repository;

import com.example.petgo.entity.MembershipPlan;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MembershipPlanRepository extends JpaRepository<MembershipPlan, Long> {

    @EntityGraph(attributePaths = "features")
    List<MembershipPlan> findByActiveTrueOrderByPopularDescSortOrderAscIdAsc(Pageable pageable);

    @EntityGraph(attributePaths = "features")
    List<MembershipPlan> findByActiveTrueOrderByPopularDescSortOrderAscIdAsc();

    @EntityGraph(attributePaths = "features")
    java.util.Optional<MembershipPlan> findBySlugAndActiveTrue(String slug);
}
