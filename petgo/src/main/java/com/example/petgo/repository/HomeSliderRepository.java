package com.example.petgo.repository;

import com.example.petgo.entity.HomeSlider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HomeSliderRepository extends JpaRepository<HomeSlider, Long> {
    List<HomeSlider> findAllByOrderBySortOrderAscIdAsc();

    List<HomeSlider> findByActiveTrueOrderBySortOrderAscIdAsc();
}