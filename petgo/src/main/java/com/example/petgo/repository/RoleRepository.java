package com.example.petgo.repository;

import com.example.petgo.entity.Role;
import com.example.petgo.entity.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByCode(RoleType code);
}
