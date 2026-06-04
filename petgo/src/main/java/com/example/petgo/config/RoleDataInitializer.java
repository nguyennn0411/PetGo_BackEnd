package com.example.petgo.config;

import com.example.petgo.entity.RoleType;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class RoleDataInitializer implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public void run(String... args) {
        for (RoleType roleType : RoleType.values()) {
            upsertRole(roleType);
        }
    }

    private void upsertRole(RoleType roleType) {
        jdbcTemplate.update("""
                INSERT INTO roles (code, name, description)
                SELECT ?, ?, ?
                WHERE NOT EXISTS (SELECT 1 FROM roles WHERE code = ?)
                """, roleType.getCode(), roleType.getDisplayName(), roleType.getDescription(), roleType.getCode());
        jdbcTemplate.update("""
                UPDATE roles
                SET name = ?, description = ?
                WHERE code = ?
                """, roleType.getDisplayName(), roleType.getDescription(), roleType.getCode());
    }
}