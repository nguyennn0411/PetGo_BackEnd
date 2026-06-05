package com.example.petgo.config;

import com.example.petgo.entity.RoleType;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RoleDataInitializer implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;

    private static final String SAMPLE_PASSWORD = "petgo123";

    @Override
    @Transactional
    public void run(String... args) {
        for (RoleType roleType : RoleType.values()) {
            upsertRole(roleType);
        }

        migrateLegacyRoles(RoleType.USER, List.of("CUSTOMER"));
        migrateLegacyRoles(RoleType.SHOP, List.of("PROVIDER", "PARTNER"));

        for (RoleType roleType : RoleType.values()) {
            upsertRole(roleType);
        }

        initializeSampleAccounts();
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

    private void migrateLegacyRoles(RoleType targetRole, List<String> legacyCodes) {
        if (legacyCodes == null || legacyCodes.isEmpty()) {
            return;
        }

        String placeholders = String.join(", ", Collections.nCopies(legacyCodes.size(), "?"));

        List<Object> assignParams = new ArrayList<>();
        assignParams.add(targetRole.getCode());
        assignParams.addAll(legacyCodes);
        jdbcTemplate.update("""
                INSERT INTO user_roles (user_id, role_id)
                SELECT DISTINCT ur.user_id, target.id
                FROM user_roles ur
                JOIN roles legacy ON legacy.id = ur.role_id
                JOIN roles target ON target.code = ?
                WHERE legacy.code IN (%s)
                  AND NOT EXISTS (
                      SELECT 1
                      FROM user_roles existing
                      WHERE existing.user_id = ur.user_id
                        AND existing.role_id = target.id
                  )
                """.formatted(placeholders), assignParams.toArray());

        jdbcTemplate.update("""
                DELETE ur
                FROM user_roles ur
                JOIN roles legacy ON legacy.id = ur.role_id
                WHERE legacy.code IN (%s)
                """.formatted(placeholders), legacyCodes.toArray());

        jdbcTemplate.update("""
                DELETE FROM roles
                WHERE code IN (%s)
                """.formatted(placeholders), legacyCodes.toArray());
    }

    private void initializeSampleAccounts() {
        String passwordHash = passwordEncoder.encode(SAMPLE_PASSWORD);

        createSampleUser(
                "USR-SAMPLE-USER",
                "user@petgo.local",
                passwordHash,
                "PetGo Sample User",
                "0919000001",
                "https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&q=80&w=400");
        assignRole("user@petgo.local", RoleType.USER);

        createSampleUser(
                "USR-SAMPLE-SHOP",
                "shop@petgo.local",
                passwordHash,
                "PetGo Sample Shop Owner",
                "0919000002",
                "https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?auto=format&fit=crop&q=80&w=400");
        assignRole("shop@petgo.local", RoleType.SHOP);
        createSampleProviderProfile();

        createSampleUser(
                "USR-SAMPLE-ADMIN",
                "admin@petgo.local",
                passwordHash,
                "PetGo Sample Admin",
                "0919000003",
                "https://images.unsplash.com/photo-1607746882042-944635dfe10e?auto=format&fit=crop&q=80&w=400");
        assignRole("admin@petgo.local", RoleType.ADMIN);
    }

    private void createSampleUser(String userCode, String email, String passwordHash, String fullName,
            String phoneNumber, String avatarUrl) {
        jdbcTemplate.update("""
                INSERT INTO users (
                    user_code, email, password_hash, full_name, phone_number,
                    avatar_url, cover_url, city, province, country_code,
                    status, email_verified_at
                )
                SELECT ?, ?, ?, ?, ?, ?, ?, ?, ?, 'VN', 'ACTIVE', NOW()
                WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = ?)
                """,
                userCode,
                email,
                passwordHash,
                fullName,
                phoneNumber,
                avatarUrl,
                "https://images.unsplash.com/photo-1548199973-03cce0bbc87b?auto=format&fit=crop&q=80&w=1600",
                "Hồ Chí Minh",
                "Hồ Chí Minh",
                email);

        jdbcTemplate.update("""
                UPDATE users
                SET status = 'ACTIVE',
                    email_verified_at = COALESCE(email_verified_at, NOW()),
                    password_hash = ?
                WHERE email = ?
                """, passwordHash, email);
    }

    private void assignRole(String email, RoleType roleType) {
        jdbcTemplate.update("""
                INSERT INTO user_roles (user_id, role_id)
                SELECT u.id, r.id
                FROM users u
                JOIN roles r ON r.code = ?
                WHERE u.email = ?
                  AND NOT EXISTS (
                      SELECT 1
                      FROM user_roles ur
                      WHERE ur.user_id = u.id
                        AND ur.role_id = r.id
                  )
                """, roleType.getCode(), email);
    }

    private void createSampleProviderProfile() {
        jdbcTemplate.update("""
                INSERT INTO provider_profiles (
                    provider_code, user_id, business_name, slug, provider_type,
                    description, years_experience, verification_status,
                    is_featured, is_hot, accepts_instant_booking, accepts_membership,
                    average_rating, total_reviews, total_completed_bookings,
                    service_radius_km, cancellation_free_hours, emergency_phone,
                    primary_address_line1, district, city, country_code,
                    latitude, longitude, main_image_url, cover_image_url,
                    price_from_amount, currency_code, status
                )
                SELECT 'PRV-SAMPLE-SHOP', u.id, 'PetGo Sample Shop', 'petgo-sample-shop', 'BUSINESS',
                       'Tài khoản shop mẫu để kiểm thử luồng đối tác PetGo.',
                       3, 'VERIFIED', TRUE, TRUE, TRUE, TRUE,
                       ?, 25, 120, ?, 24, u.phone_number,
                       '123 PetGo Demo Street', 'Quận 1', 'Hồ Chí Minh', 'VN',
                       ?, ?,
                       'https://images.unsplash.com/photo-1516734212186-a967f81ad0d7?auto=format&fit=crop&q=80&w=800',
                       'https://images.unsplash.com/photo-1516734212186-a967f81ad0d7?auto=format&fit=crop&q=80&w=1400',
                       ?, 'VND', 'ACTIVE'
                FROM users u
                WHERE u.email = 'shop@petgo.local'
                  AND NOT EXISTS (
                      SELECT 1
                      FROM provider_profiles p
                      WHERE p.user_id = u.id
                         OR p.slug = 'petgo-sample-shop'
                         OR p.provider_code = 'PRV-SAMPLE-SHOP'
                  )
                """,
                new BigDecimal("4.80"),
                new BigDecimal("8.00"),
                new BigDecimal("10.7769000"),
                new BigDecimal("106.7009000"),
                new BigDecimal("200000.00"));
    }
}