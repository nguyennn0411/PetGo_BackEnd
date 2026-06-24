package com.example.petgo.repository;

import com.example.petgo.entity.Area;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AreaRepository extends JpaRepository<Area, Long> {

    List<Area> findAllByOrderByNameAsc();
}
