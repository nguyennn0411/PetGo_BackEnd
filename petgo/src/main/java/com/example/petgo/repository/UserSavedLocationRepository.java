package com.example.petgo.repository;

import com.example.petgo.entity.UserSavedLocation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserSavedLocationRepository extends JpaRepository<UserSavedLocation, Long> {
    List<UserSavedLocation> findByUserIdOrderByCreatedAtDesc(Long userId);
}
