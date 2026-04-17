-- PetGo full demo seed
-- Run after the base schema file from the user has been executed.
-- Goal: provide enough demo data to test functions 1 -> 10 end-to-end.
-- This script is written to be mostly idempotent.

SET NAMES utf8mb4;

-- =========================================================
-- 0) Roles (safe re-run)
-- =========================================================
INSERT INTO roles (code, name, description)
SELECT 'CUSTOMER', 'Customer / Pet Owner', 'Người dùng đặt dịch vụ cho thú cưng'
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE code = 'CUSTOMER');

INSERT INTO roles (code, name, description)
SELECT 'PROVIDER', 'Caregiver / Provider', 'Người cung cấp dịch vụ chăm sóc thú cưng'
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE code = 'PROVIDER');

INSERT INTO roles (code, name, description)
SELECT 'ADMIN', 'Administrator', 'Quản trị hệ thống'
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE code = 'ADMIN');

-- =========================================================
-- 1) Users + roles
-- Demo passwords:
--   customer1@petgo.local / customer123
--   customer2@petgo.local / customer123
--   provider1@petgo.local / provider123
--   provider2@petgo.local / provider123
--   provider3@petgo.local / provider123
--   provider4@petgo.local / provider123
--   admin@petgo.local     / admin123
-- =========================================================
INSERT INTO users (
    user_code, email, password_hash, full_name, phone_number, avatar_url,
    gender, city, province, country_code, status, email_verified_at, created_at, updated_at
)
SELECT 'USR-CUS-001', 'customer1@petgo.local', '$2b$12$M42GgYda9EoTlEYXfFZ/pOuPim5ejyig19F3/H2Sx1C0jl9I8WbDy',
       'Nguyễn Minh Anh', '0908000001',
       'https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&q=80&w=400',
       'FEMALE', 'Hồ Chí Minh', 'Hồ Chí Minh', 'VN', 'ACTIVE', NOW(), NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'customer1@petgo.local');

INSERT INTO users (
    user_code, email, password_hash, full_name, phone_number, avatar_url,
    gender, city, province, country_code, status, email_verified_at, created_at, updated_at
)
SELECT 'USR-CUS-002', 'customer2@petgo.local', '$2b$12$M42GgYda9EoTlEYXfFZ/pOuPim5ejyig19F3/H2Sx1C0jl9I8WbDy',
       'Trần Hoàng Nam', '0908000002',
       'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&q=80&w=400',
       'MALE', 'Hồ Chí Minh', 'Hồ Chí Minh', 'VN', 'ACTIVE', NOW(), NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'customer2@petgo.local');

INSERT INTO users (
    user_code, email, password_hash, full_name, phone_number, avatar_url,
    gender, city, province, country_code, status, email_verified_at, created_at, updated_at
)
SELECT 'USR-PROV-001', 'provider1@petgo.local', '$2b$12$LHMOF4HMC28fihCP4URKIuz9CpXEHh6towCZDam5UxBokvpfqOazm',
       'Paws Relax Owner', '0901000001',
       'https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?auto=format&fit=crop&q=80&w=400',
       'MALE', 'Hồ Chí Minh', 'Hồ Chí Minh', 'VN', 'ACTIVE', NOW(), NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'provider1@petgo.local');

INSERT INTO users (
    user_code, email, password_hash, full_name, phone_number, avatar_url,
    gender, city, province, country_code, status, email_verified_at, created_at, updated_at
)
SELECT 'USR-PROV-002', 'provider2@petgo.local', '$2b$12$LHMOF4HMC28fihCP4URKIuz9CpXEHh6towCZDam5UxBokvpfqOazm',
       'Happy Tails Owner', '0901000002',
       'https://images.unsplash.com/photo-1504593811423-6dd665756598?auto=format&fit=crop&q=80&w=400',
       'FEMALE', 'Hồ Chí Minh', 'Hồ Chí Minh', 'VN', 'ACTIVE', NOW(), NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'provider2@petgo.local');

INSERT INTO users (
    user_code, email, password_hash, full_name, phone_number, avatar_url,
    gender, city, province, country_code, status, email_verified_at, created_at, updated_at
)
SELECT 'USR-PROV-003', 'provider3@petgo.local', '$2b$12$LHMOF4HMC28fihCP4URKIuz9CpXEHh6towCZDam5UxBokvpfqOazm',
       'Pet Paradise Owner', '0901000003',
       'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&q=80&w=400',
       'MALE', 'Hồ Chí Minh', 'Hồ Chí Minh', 'VN', 'ACTIVE', NOW(), NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'provider3@petgo.local');

INSERT INTO users (
    user_code, email, password_hash, full_name, phone_number, avatar_url,
    gender, city, province, country_code, status, email_verified_at, created_at, updated_at
)
SELECT 'USR-PROV-004', 'provider4@petgo.local', '$2b$12$LHMOF4HMC28fihCP4URKIuz9CpXEHh6towCZDam5UxBokvpfqOazm',
       'Walkie Buddy Owner', '0901000004',
       'https://images.unsplash.com/photo-1546961329-78bef0414d7c?auto=format&fit=crop&q=80&w=400',
       'OTHER', 'Hồ Chí Minh', 'Hồ Chí Minh', 'VN', 'ACTIVE', NOW(), NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'provider4@petgo.local');

INSERT INTO users (
    user_code, email, password_hash, full_name, phone_number, avatar_url,
    gender, city, province, country_code, status, email_verified_at, created_at, updated_at
)
SELECT 'USR-ADM-001', 'admin@petgo.local', '$2b$12$UTZbmA3G3J78ai53lL6XjuYmWltxiDZJRs4qBB6pKT7ogPTAsZ1Mq',
       'PetGo Admin', '0908000099',
       'https://images.unsplash.com/photo-1607746882042-944635dfe10e?auto=format&fit=crop&q=80&w=400',
       'PREFER_NOT_TO_SAY', 'Hồ Chí Minh', 'Hồ Chí Minh', 'VN', 'ACTIVE', NOW(), NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'admin@petgo.local');

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u JOIN roles r ON r.code = 'CUSTOMER'
WHERE u.email IN ('customer1@petgo.local', 'customer2@petgo.local')
  AND NOT EXISTS (SELECT 1 FROM user_roles ur WHERE ur.user_id = u.id AND ur.role_id = r.id);

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u JOIN roles r ON r.code = 'PROVIDER'
WHERE u.email IN ('provider1@petgo.local', 'provider2@petgo.local', 'provider3@petgo.local', 'provider4@petgo.local')
  AND NOT EXISTS (SELECT 1 FROM user_roles ur WHERE ur.user_id = u.id AND ur.role_id = r.id);

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u JOIN roles r ON r.code = 'ADMIN'
WHERE u.email = 'admin@petgo.local'
  AND NOT EXISTS (SELECT 1 FROM user_roles ur WHERE ur.user_id = u.id AND ur.role_id = r.id);

-- =========================================================
-- 2) Extra catalog/services missing from base seed
-- =========================================================
INSERT INTO services (service_code, category_id, name, slug, short_description, description, default_duration_minutes, base_price_amount, currency_code, price_unit)
SELECT 'SVC-BOARD-STAY', c.id, 'Lưu trú theo ngày', 'boarding-stay', 'Khách sạn thú cưng theo ngày', 'Dịch vụ lưu trú có giám sát.', 1440, 350000, 'VND', 'PER_DAY'
FROM service_categories c
WHERE c.slug = 'boarding'
  AND NOT EXISTS (SELECT 1 FROM services s WHERE s.slug = 'boarding-stay');

INSERT INTO services (service_code, category_id, name, slug, short_description, description, default_duration_minutes, base_price_amount, currency_code, price_unit)
SELECT 'SVC-WALK-60', c.id, 'Dắt đi dạo 60 phút', 'walk-60', 'Đi dạo và vận động ngoài trời', 'Dịch vụ dắt thú cưng đi dạo 60 phút.', 60, 120000, 'VND', 'PER_VISIT'
FROM service_categories c
WHERE c.slug = 'walking'
  AND NOT EXISTS (SELECT 1 FROM services s WHERE s.slug = 'walk-60');

INSERT INTO services (service_code, category_id, name, slug, short_description, description, default_duration_minutes, base_price_amount, currency_code, price_unit)
SELECT 'SVC-TRAIN-BASIC', c.id, 'Huấn luyện cơ bản', 'basic-training', 'Huấn luyện lệnh cơ bản', 'Huấn luyện ngồi, nằm, chờ, đi cạnh.', 90, 280000, 'VND', 'PER_SESSION'
FROM service_categories c
WHERE c.slug = 'training'
  AND NOT EXISTS (SELECT 1 FROM services s WHERE s.slug = 'basic-training');

-- =========================================================
-- 3) Provider profiles
-- =========================================================
INSERT INTO provider_profiles (
    provider_code, user_id, business_name, slug, provider_type, headline, description, years_experience,
    verification_status, is_featured, is_hot, accepts_instant_booking, accepts_membership,
    average_rating, total_reviews, total_completed_bookings, service_radius_km, cancellation_free_hours,
    emergency_phone, primary_address_line1, district, city, province, country_code, latitude, longitude,
    main_image_url, cover_image_url, price_from_amount, currency_code, status, created_at, updated_at
)
SELECT 'PRV-001', u.id, 'Paws & Relax Luxury Spa', 'paws-relax-luxury-spa', 'SPA',
       'Spa & Grooming cao cấp', 'Không gian spa sạch đẹp cho chó mèo, có đón trả nội thành.', 6,
       'VERIFIED', TRUE, TRUE, TRUE, TRUE,
       4.90, 156, 420, 8.00, 24,
       '0909000001', '12 Nguyễn Huệ', 'Quận 1', 'Hồ Chí Minh', 'Hồ Chí Minh', 'VN', 10.7769, 106.7009,
       'https://images.unsplash.com/photo-1516734212186-a967f81ad0d7?auto=format&fit=crop&q=80&w=800',
       'https://images.unsplash.com/photo-1516734212186-a967f81ad0d7?auto=format&fit=crop&q=80&w=1400',
       200000, 'VND', 'ACTIVE', NOW(), NOW()
FROM users u
WHERE u.email = 'provider1@petgo.local'
  AND NOT EXISTS (SELECT 1 FROM provider_profiles p WHERE p.slug = 'paws-relax-luxury-spa');

INSERT INTO provider_profiles (
    provider_code, user_id, business_name, slug, provider_type, headline, description, years_experience,
    verification_status, is_featured, is_hot, accepts_instant_booking, accepts_membership,
    average_rating, total_reviews, total_completed_bookings, service_radius_km, cancellation_free_hours,
    emergency_phone, primary_address_line1, district, city, province, country_code, latitude, longitude,
    main_image_url, cover_image_url, price_from_amount, currency_code, status, created_at, updated_at
)
SELECT 'PRV-002', u.id, 'Happy Tails Veterinary Clinic', 'happy-tails-veterinary-clinic', 'CLINIC',
       'Khám tổng quát & tư vấn sức khỏe', 'Phòng khám thú y thân thiện, tiếp nhận khám nhanh và tư vấn dinh dưỡng.', 8,
       'VERIFIED', TRUE, FALSE, TRUE, TRUE,
       4.80, 92, 310, 10.00, 24,
       '0909000002', '45 Nguyễn Thị Thập', 'Quận 7', 'Hồ Chí Minh', 'Hồ Chí Minh', 'VN', 10.7342, 106.7218,
       'https://images.unsplash.com/photo-1583337130417-3346a1be7dee?auto=format&fit=crop&q=80&w=800',
       'https://images.unsplash.com/photo-1583337130417-3346a1be7dee?auto=format&fit=crop&q=80&w=1400',
       150000, 'VND', 'ACTIVE', NOW(), NOW()
FROM users u
WHERE u.email = 'provider2@petgo.local'
  AND NOT EXISTS (SELECT 1 FROM provider_profiles p WHERE p.slug = 'happy-tails-veterinary-clinic');

INSERT INTO provider_profiles (
    provider_code, user_id, business_name, slug, provider_type, headline, description, years_experience,
    verification_status, is_featured, is_hot, accepts_instant_booking, accepts_membership,
    average_rating, total_reviews, total_completed_bookings, service_radius_km, cancellation_free_hours,
    emergency_phone, primary_address_line1, district, city, province, country_code, latitude, longitude,
    main_image_url, cover_image_url, price_from_amount, currency_code, status, created_at, updated_at
)
SELECT 'PRV-003', u.id, 'Pet Paradise Resort', 'pet-paradise-resort', 'BOARDING',
       'Khách sạn thú cưng & lưu trú dài ngày', 'Lưu trú và chăm sóc trọn gói, có camera và báo cáo hằng ngày.', 5,
       'VERIFIED', FALSE, FALSE, TRUE, TRUE,
       4.70, 210, 280, 12.00, 24,
       '0909000003', '120 Điện Biên Phủ', 'Bình Thạnh', 'Hồ Chí Minh', 'Hồ Chí Minh', 'VN', 10.8036, 106.7131,
       'https://images.unsplash.com/photo-1591768793355-74d7ca738055?auto=format&fit=crop&q=80&w=800',
       'https://images.unsplash.com/photo-1591768793355-74d7ca738055?auto=format&fit=crop&q=80&w=1400',
       350000, 'VND', 'ACTIVE', NOW(), NOW()
FROM users u
WHERE u.email = 'provider3@petgo.local'
  AND NOT EXISTS (SELECT 1 FROM provider_profiles p WHERE p.slug = 'pet-paradise-resort');

INSERT INTO provider_profiles (
    provider_code, user_id, business_name, slug, provider_type, headline, description, years_experience,
    verification_status, is_featured, is_hot, accepts_instant_booking, accepts_membership,
    average_rating, total_reviews, total_completed_bookings, service_radius_km, cancellation_free_hours,
    emergency_phone, primary_address_line1, district, city, province, country_code, latitude, longitude,
    main_image_url, cover_image_url, price_from_amount, currency_code, status, created_at, updated_at
)
SELECT 'PRV-004', u.id, 'Walkie Buddy', 'walkie-buddy', 'WALKER',
       'Dắt thú cưng đi dạo & vận động', 'Nhận dắt chó đi dạo theo lịch linh hoạt, phù hợp người bận rộn.', 4,
       'VERIFIED', FALSE, TRUE, TRUE, TRUE,
       4.60, 54, 120, 6.00, 12,
       '0909000004', '88 Phan Xích Long', 'Phú Nhuận', 'Hồ Chí Minh', 'Hồ Chí Minh', 'VN', 10.8010, 106.6868,
       'https://images.unsplash.com/photo-1548199973-03cce0bbc87b?auto=format&fit=crop&q=80&w=800',
       'https://images.unsplash.com/photo-1548199973-03cce0bbc87b?auto=format&fit=crop&q=80&w=1400',
       120000, 'VND', 'ACTIVE', NOW(), NOW()
FROM users u
WHERE u.email = 'provider4@petgo.local'
  AND NOT EXISTS (SELECT 1 FROM provider_profiles p WHERE p.slug = 'walkie-buddy');

-- =========================================================
-- 4) Provider photos
-- =========================================================
INSERT INTO provider_photos (provider_id, photo_url, media_type, is_primary, sort_order)
SELECT p.id, 'https://images.unsplash.com/photo-1583337130417-3346a1be7dee?auto=format&fit=crop&q=80&w=900', 'IMAGE', TRUE, 1
FROM provider_profiles p
WHERE p.slug = 'paws-relax-luxury-spa'
  AND NOT EXISTS (SELECT 1 FROM provider_photos pp WHERE pp.provider_id = p.id AND pp.sort_order = 1);

INSERT INTO provider_photos (provider_id, photo_url, media_type, is_primary, sort_order)
SELECT p.id, 'https://images.unsplash.com/photo-1591768793355-74d7ca738055?auto=format&fit=crop&q=80&w=900', 'IMAGE', FALSE, 2
FROM provider_profiles p
WHERE p.slug = 'paws-relax-luxury-spa'
  AND NOT EXISTS (SELECT 1 FROM provider_photos pp WHERE pp.provider_id = p.id AND pp.sort_order = 2);

INSERT INTO provider_photos (provider_id, photo_url, media_type, is_primary, sort_order)
SELECT p.id, 'https://images.unsplash.com/photo-1517849845537-4d257902454a?auto=format&fit=crop&q=80&w=900', 'IMAGE', FALSE, 3
FROM provider_profiles p
WHERE p.slug = 'paws-relax-luxury-spa'
  AND NOT EXISTS (SELECT 1 FROM provider_photos pp WHERE pp.provider_id = p.id AND pp.sort_order = 3);

INSERT INTO provider_photos (provider_id, photo_url, media_type, is_primary, sort_order)
SELECT p.id, p.main_image_url, 'IMAGE', TRUE, 1
FROM provider_profiles p
WHERE p.slug IN ('happy-tails-veterinary-clinic', 'pet-paradise-resort', 'walkie-buddy')
  AND NOT EXISTS (SELECT 1 FROM provider_photos pp WHERE pp.provider_id = p.id AND pp.sort_order = 1);

-- =========================================================
-- 5) Business hours
-- =========================================================
INSERT INTO provider_business_hours (provider_id, weekday, opens_at, closes_at, is_closed)
SELECT p.id, x.weekday, x.opens_at, x.closes_at, x.is_closed
FROM provider_profiles p
JOIN (
    SELECT 1 weekday, '08:00:00' opens_at, '20:00:00' closes_at, FALSE is_closed UNION ALL
    SELECT 2, '08:00:00', '20:00:00', FALSE UNION ALL
    SELECT 3, '08:00:00', '20:00:00', FALSE UNION ALL
    SELECT 4, '08:00:00', '20:00:00', FALSE UNION ALL
    SELECT 5, '08:00:00', '20:00:00', FALSE UNION ALL
    SELECT 6, '08:00:00', '21:00:00', FALSE UNION ALL
    SELECT 7, '08:00:00', '18:00:00', FALSE
) x
WHERE p.slug = 'paws-relax-luxury-spa'
  AND NOT EXISTS (SELECT 1 FROM provider_business_hours h WHERE h.provider_id = p.id AND h.weekday = x.weekday);

INSERT INTO provider_business_hours (provider_id, weekday, opens_at, closes_at, is_closed)
SELECT p.id, x.weekday, x.opens_at, x.closes_at, x.is_closed
FROM provider_profiles p
JOIN (
    SELECT 1 weekday, '08:00:00' opens_at, '19:00:00' closes_at, FALSE is_closed UNION ALL
    SELECT 2, '08:00:00', '19:00:00', FALSE UNION ALL
    SELECT 3, '08:00:00', '19:00:00', FALSE UNION ALL
    SELECT 4, '08:00:00', '19:00:00', FALSE UNION ALL
    SELECT 5, '08:00:00', '19:00:00', FALSE UNION ALL
    SELECT 6, '08:00:00', '17:00:00', FALSE UNION ALL
    SELECT 7, NULL, NULL, TRUE
) x
WHERE p.slug = 'happy-tails-veterinary-clinic'
  AND NOT EXISTS (SELECT 1 FROM provider_business_hours h WHERE h.provider_id = p.id AND h.weekday = x.weekday);

INSERT INTO provider_business_hours (provider_id, weekday, opens_at, closes_at, is_closed)
SELECT p.id, x.weekday, x.opens_at, x.closes_at, x.is_closed
FROM provider_profiles p
JOIN (
    SELECT 1 weekday, '07:00:00' opens_at, '21:00:00' closes_at, FALSE is_closed UNION ALL
    SELECT 2, '07:00:00', '21:00:00', FALSE UNION ALL
    SELECT 3, '07:00:00', '21:00:00', FALSE UNION ALL
    SELECT 4, '07:00:00', '21:00:00', FALSE UNION ALL
    SELECT 5, '07:00:00', '21:00:00', FALSE UNION ALL
    SELECT 6, '07:00:00', '21:00:00', FALSE UNION ALL
    SELECT 7, '07:00:00', '21:00:00', FALSE
) x
WHERE p.slug = 'pet-paradise-resort'
  AND NOT EXISTS (SELECT 1 FROM provider_business_hours h WHERE h.provider_id = p.id AND h.weekday = x.weekday);

INSERT INTO provider_business_hours (provider_id, weekday, opens_at, closes_at, is_closed)
SELECT p.id, x.weekday, x.opens_at, x.closes_at, x.is_closed
FROM provider_profiles p
JOIN (
    SELECT 1 weekday, '06:30:00' opens_at, '19:00:00' closes_at, FALSE is_closed UNION ALL
    SELECT 2, '06:30:00', '19:00:00', FALSE UNION ALL
    SELECT 3, '06:30:00', '19:00:00', FALSE UNION ALL
    SELECT 4, '06:30:00', '19:00:00', FALSE UNION ALL
    SELECT 5, '06:30:00', '19:00:00', FALSE UNION ALL
    SELECT 6, '07:00:00', '18:00:00', FALSE UNION ALL
    SELECT 7, '07:00:00', '10:00:00', FALSE
) x
WHERE p.slug = 'walkie-buddy'
  AND NOT EXISTS (SELECT 1 FROM provider_business_hours h WHERE h.provider_id = p.id AND h.weekday = x.weekday);

-- =========================================================
-- 6) Provider services
-- =========================================================
INSERT INTO provider_services (provider_id, service_id, custom_name, short_description, description, duration_minutes, price_amount, currency_code, price_unit, is_featured, is_active, capacity_per_slot, booking_buffer_minutes, display_order)
SELECT p.id, s.id, 'Tắm & Cắt tỉa', 'Grooming trọn gói', 'Tắm, sấy, vệ sinh tai, cắt tỉa tạo kiểu.', 90, 200000, 'VND', 'PER_SESSION', TRUE, TRUE, 1, 0, 1
FROM provider_profiles p JOIN services s ON s.slug = 'groom-style'
WHERE p.slug = 'paws-relax-luxury-spa'
  AND NOT EXISTS (SELECT 1 FROM provider_services ps WHERE ps.provider_id = p.id AND ps.service_id = s.id AND COALESCE(ps.custom_name,'') = 'Tắm & Cắt tỉa');

INSERT INTO provider_services (provider_id, service_id, custom_name, short_description, description, duration_minutes, price_amount, currency_code, price_unit, is_featured, is_active, capacity_per_slot, booking_buffer_minutes, display_order)
SELECT p.id, s.id, 'Spa thư giãn', 'Tắm thư giãn cho thú cưng', 'Gói spa cơ bản, sấy và chải lông.', 45, 220000, 'VND', 'PER_SESSION', FALSE, TRUE, 1, 0, 2
FROM provider_profiles p JOIN services s ON s.slug = 'relax-bath'
WHERE p.slug = 'paws-relax-luxury-spa'
  AND NOT EXISTS (SELECT 1 FROM provider_services ps WHERE ps.provider_id = p.id AND ps.service_id = s.id AND COALESCE(ps.custom_name,'') = 'Spa thư giãn');

INSERT INTO provider_services (provider_id, service_id, custom_name, short_description, description, duration_minutes, price_amount, currency_code, price_unit, is_featured, is_active, capacity_per_slot, booking_buffer_minutes, display_order)
SELECT p.id, s.id, 'Khám tổng quát', 'Checkup cơ bản', 'Khám sức khỏe tổng quát, soi tai và tư vấn dinh dưỡng.', 30, 150000, 'VND', 'PER_VISIT', TRUE, TRUE, 1, 0, 1
FROM provider_profiles p JOIN services s ON s.slug = 'general-checkup'
WHERE p.slug = 'happy-tails-veterinary-clinic'
  AND NOT EXISTS (SELECT 1 FROM provider_services ps WHERE ps.provider_id = p.id AND ps.service_id = s.id AND COALESCE(ps.custom_name,'') = 'Khám tổng quát');

INSERT INTO provider_services (provider_id, service_id, custom_name, short_description, description, duration_minutes, price_amount, currency_code, price_unit, is_featured, is_active, capacity_per_slot, booking_buffer_minutes, display_order)
SELECT p.id, s.id, 'Khách sạn thú cưng', 'Lưu trú theo ngày', 'Lưu trú sạch sẽ và có camera.', 1440, 350000, 'VND', 'PER_DAY', TRUE, TRUE, 4, 0, 1
FROM provider_profiles p JOIN services s ON s.slug = 'boarding-stay'
WHERE p.slug = 'pet-paradise-resort'
  AND NOT EXISTS (SELECT 1 FROM provider_services ps WHERE ps.provider_id = p.id AND ps.service_id = s.id AND COALESCE(ps.custom_name,'') = 'Khách sạn thú cưng');

INSERT INTO provider_services (provider_id, service_id, custom_name, short_description, description, duration_minutes, price_amount, currency_code, price_unit, is_featured, is_active, capacity_per_slot, booking_buffer_minutes, display_order)
SELECT p.id, s.id, 'Dắt đi dạo 60 phút', 'Vận động ngoài trời', 'Dắt cún đi dạo, chơi và vận động trong 60 phút.', 60, 120000, 'VND', 'PER_VISIT', TRUE, TRUE, 2, 0, 1
FROM provider_profiles p JOIN services s ON s.slug = 'walk-60'
WHERE p.slug = 'walkie-buddy'
  AND NOT EXISTS (SELECT 1 FROM provider_services ps WHERE ps.provider_id = p.id AND ps.service_id = s.id AND COALESCE(ps.custom_name,'') = 'Dắt đi dạo 60 phút');

-- =========================================================
-- 7) Availability slots for next 7 days
-- =========================================================
INSERT INTO provider_availability_slots (provider_id, provider_service_id, slot_date, start_time, end_time, slot_status, capacity_total, capacity_booked, note)
SELECT ps.provider_id, ps.id, CURDATE() + INTERVAL d.day_offset DAY, t.start_time, t.end_time, 'AVAILABLE', t.capacity_total, 0, NULL
FROM provider_services ps
JOIN provider_profiles p ON p.id = ps.provider_id
JOIN (
    SELECT 0 day_offset UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6
) d
JOIN (
    SELECT '09:00:00' start_time, '10:30:00' end_time, 1 capacity_total UNION ALL
    SELECT '14:00:00', '15:30:00', 1
) t
WHERE p.slug = 'paws-relax-luxury-spa'
  AND NOT EXISTS (
      SELECT 1 FROM provider_availability_slots s
      WHERE s.provider_service_id = ps.id
        AND s.slot_date = CURDATE() + INTERVAL d.day_offset DAY
        AND s.start_time = t.start_time
  );

INSERT INTO provider_availability_slots (provider_id, provider_service_id, slot_date, start_time, end_time, slot_status, capacity_total, capacity_booked, note)
SELECT ps.provider_id, ps.id, CURDATE() + INTERVAL d.day_offset DAY, t.start_time, t.end_time, 'AVAILABLE', 1, 0, NULL
FROM provider_services ps
JOIN provider_profiles p ON p.id = ps.provider_id
JOIN (
    SELECT 0 day_offset UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5
) d
JOIN (
    SELECT '09:00:00' start_time, '09:30:00' end_time UNION ALL
    SELECT '11:00:00', '11:30:00' UNION ALL
    SELECT '16:00:00', '16:30:00'
) t
WHERE p.slug = 'happy-tails-veterinary-clinic'
  AND NOT EXISTS (
      SELECT 1 FROM provider_availability_slots s
      WHERE s.provider_service_id = ps.id
        AND s.slot_date = CURDATE() + INTERVAL d.day_offset DAY
        AND s.start_time = t.start_time
  );

INSERT INTO provider_availability_slots (provider_id, provider_service_id, slot_date, start_time, end_time, slot_status, capacity_total, capacity_booked, note)
SELECT ps.provider_id, ps.id, CURDATE() + INTERVAL d.day_offset DAY, '08:00:00', '09:00:00', 'AVAILABLE', 4, 0, NULL
FROM provider_services ps
JOIN provider_profiles p ON p.id = ps.provider_id
JOIN (
    SELECT 1 day_offset UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6
) d
WHERE p.slug = 'pet-paradise-resort'
  AND NOT EXISTS (
      SELECT 1 FROM provider_availability_slots s
      WHERE s.provider_service_id = ps.id
        AND s.slot_date = CURDATE() + INTERVAL d.day_offset DAY
        AND s.start_time = '08:00:00'
  );

INSERT INTO provider_availability_slots (provider_id, provider_service_id, slot_date, start_time, end_time, slot_status, capacity_total, capacity_booked, note)
SELECT ps.provider_id, ps.id, CURDATE() + INTERVAL d.day_offset DAY, '06:30:00', '07:30:00', 'AVAILABLE', 2, 0, NULL
FROM provider_services ps
JOIN provider_profiles p ON p.id = ps.provider_id
JOIN (
    SELECT 0 day_offset UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4
) d
WHERE p.slug = 'walkie-buddy'
  AND NOT EXISTS (
      SELECT 1 FROM provider_availability_slots s
      WHERE s.provider_service_id = ps.id
        AND s.slot_date = CURDATE() + INTERVAL d.day_offset DAY
        AND s.start_time = '06:30:00'
  );

-- =========================================================
-- 8) Pets + photos
-- =========================================================
INSERT INTO pets (
    pet_code, owner_user_id, name, species, breed, gender, age_label, weight_kg, color, size,
    avatar_url, health_notes, allergy_notes, behavior_notes, vaccination_notes, status, created_at, updated_at
)
SELECT 'PET-DEMO-001', u.id, 'Mochi', 'DOG', 'Golden Retriever', 'MALE', '2 tuổi', 18.50, 'Golden', 'L',
       'https://images.unsplash.com/photo-1552053831-71594a27632d?auto=format&fit=crop&q=80&w=900',
       'Đã tiêm phòng đầy đủ', 'Không có', 'Thân thiện, năng động', 'Mũi 7in1 đã hoàn tất', 'ACTIVE', NOW(), NOW()
FROM users u
WHERE u.email = 'customer1@petgo.local'
  AND NOT EXISTS (SELECT 1 FROM pets WHERE pet_code = 'PET-DEMO-001');

INSERT INTO pets (
    pet_code, owner_user_id, name, species, breed, gender, age_label, weight_kg, color, size,
    avatar_url, health_notes, allergy_notes, behavior_notes, vaccination_notes, status, created_at, updated_at
)
SELECT 'PET-DEMO-002', u.id, 'Luna', 'CAT', 'British Shorthair', 'FEMALE', '1 tuổi', 4.20, 'Gray', 'S',
       'https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?auto=format&fit=crop&q=80&w=900',
       'Sức khỏe tốt', 'Nhạy cảm với hải sản', 'Hiền, hơi nhút nhát', 'Đã tiêm nhắc lại định kỳ', 'ACTIVE', NOW(), NOW()
FROM users u
WHERE u.email = 'customer1@petgo.local'
  AND NOT EXISTS (SELECT 1 FROM pets WHERE pet_code = 'PET-DEMO-002');

INSERT INTO pets (
    pet_code, owner_user_id, name, species, breed, gender, age_label, weight_kg, color, size,
    avatar_url, health_notes, allergy_notes, behavior_notes, vaccination_notes, status, created_at, updated_at
)
SELECT 'PET-DEMO-003', u.id, 'Bơ', 'DOG', 'Poodle', 'MALE', '3 tuổi', 6.10, 'White', 'S',
       'https://images.unsplash.com/photo-1517849845537-4d257902454a?auto=format&fit=crop&q=80&w=900',
       'Sức khỏe ổn định', 'Không có', 'Vui vẻ, thích đi dạo', 'Đã tiêm đầy đủ', 'ACTIVE', NOW(), NOW()
FROM users u
WHERE u.email = 'customer2@petgo.local'
  AND NOT EXISTS (SELECT 1 FROM pets WHERE pet_code = 'PET-DEMO-003');

INSERT INTO pet_photos (pet_id, photo_url, is_primary, sort_order, created_at, updated_at)
SELECT p.id, p.avatar_url, TRUE, 0, NOW(), NOW()
FROM pets p
WHERE p.pet_code IN ('PET-DEMO-001', 'PET-DEMO-002', 'PET-DEMO-003')
  AND NOT EXISTS (SELECT 1 FROM pet_photos pp WHERE pp.pet_id = p.id AND pp.is_primary = TRUE);

-- =========================================================
-- 9) Promo codes
-- =========================================================
INSERT INTO promo_codes (code, target_type, discount_type, discount_value, max_discount_amount, min_order_amount, usage_limit_total, usage_limit_per_user, starts_at, ends_at, is_active)
SELECT 'PETGO50', 'BOOKING', 'FIXED_AMOUNT', 50000, NULL, 0, 1000, 10, NOW(), DATE_ADD(NOW(), INTERVAL 365 DAY), TRUE
WHERE NOT EXISTS (SELECT 1 FROM promo_codes WHERE UPPER(code) = 'PETGO50');

INSERT INTO promo_codes (code, target_type, discount_type, discount_value, max_discount_amount, min_order_amount, usage_limit_total, usage_limit_per_user, starts_at, ends_at, is_active)
SELECT 'PETGO20', 'BOTH', 'FIXED_AMOUNT', 20000, NULL, 0, 10000, 1, NOW(), DATE_ADD(NOW(), INTERVAL 365 DAY), TRUE
WHERE NOT EXISTS (SELECT 1 FROM promo_codes WHERE UPPER(code) = 'PETGO20');

INSERT INTO promo_codes (code, target_type, discount_type, discount_value, max_discount_amount, min_order_amount, usage_limit_total, usage_limit_per_user, starts_at, ends_at, is_active)
SELECT 'MEMPRO10', 'MEMBERSHIP', 'PERCENTAGE', 10, 50000, 0, 10000, 1, NOW(), DATE_ADD(NOW(), INTERVAL 365 DAY), TRUE
WHERE NOT EXISTS (SELECT 1 FROM promo_codes WHERE UPPER(code) = 'MEMPRO10');

-- =========================================================
-- 10) Booking demo data (pending, confirmed, completed, cancelled)
-- =========================================================
INSERT INTO bookings (
    booking_code, customer_user_id, provider_id, pet_id, provider_service_id, availability_slot_id,
    appointment_date, start_time, end_time, timezone, status, customer_note, internal_note,
    provider_name_snapshot, provider_phone_snapshot, provider_address_snapshot,
    service_name_snapshot, service_description_snapshot, service_duration_minutes_snapshot,
    pet_name_snapshot, pet_breed_snapshot,
    subtotal_amount, membership_discount_amount, promo_discount_amount, tax_amount, total_amount, currency_code,
    created_at, updated_at
)
SELECT
    'BOOK-DEMO-001', u.id, p.id, pet.id, ps.id, s.id,
    s.slot_date, s.start_time, s.end_time, 'Asia/Ho_Chi_Minh', 'PENDING_CONFIRMATION', 'Bé hơi nhát, xin hỗ trợ nhẹ nhàng', NULL,
    p.business_name, p.emergency_phone, p.primary_address_line1,
    COALESCE(ps.custom_name, svc.name), COALESCE(ps.short_description, svc.short_description), ps.duration_minutes,
    pet.name, pet.breed,
    ps.price_amount, 0, 50000, 0, GREATEST(ps.price_amount - 50000, 0), ps.currency_code,
    NOW(), NOW()
FROM users u
JOIN pets pet ON pet.owner_user_id = u.id AND pet.pet_code = 'PET-DEMO-001'
JOIN provider_profiles p ON p.slug = 'paws-relax-luxury-spa'
JOIN provider_services ps ON ps.provider_id = p.id AND ps.custom_name = 'Tắm & Cắt tỉa'
JOIN services svc ON svc.id = ps.service_id
JOIN provider_availability_slots s ON s.provider_service_id = ps.id AND s.slot_date = CURDATE() + INTERVAL 1 DAY AND s.start_time = '09:00:00'
WHERE u.email = 'customer1@petgo.local'
  AND NOT EXISTS (SELECT 1 FROM bookings WHERE booking_code = 'BOOK-DEMO-001')
LIMIT 1;

INSERT INTO bookings (
    booking_code, customer_user_id, provider_id, pet_id, provider_service_id, availability_slot_id,
    appointment_date, start_time, end_time, timezone, status, customer_note, internal_note,
    provider_name_snapshot, provider_phone_snapshot, provider_address_snapshot,
    service_name_snapshot, service_description_snapshot, service_duration_minutes_snapshot,
    pet_name_snapshot, pet_breed_snapshot,
    subtotal_amount, membership_discount_amount, promo_discount_amount, tax_amount, total_amount, currency_code,
    created_at, updated_at
)
SELECT
    'BOOK-DEMO-002', u.id, p.id, pet.id, ps.id, s.id,
    s.slot_date, s.start_time, s.end_time, 'Asia/Ho_Chi_Minh', 'CONFIRMED', 'Xin gọi trước khi đến.', 'Provider đã xác nhận',
    p.business_name, p.emergency_phone, p.primary_address_line1,
    COALESCE(ps.custom_name, svc.name), COALESCE(ps.short_description, svc.short_description), ps.duration_minutes,
    pet.name, pet.breed,
    ps.price_amount, 0, 0, 0, ps.price_amount, ps.currency_code,
    NOW(), NOW()
FROM users u
JOIN pets pet ON pet.owner_user_id = u.id AND pet.pet_code = 'PET-DEMO-002'
JOIN provider_profiles p ON p.slug = 'happy-tails-veterinary-clinic'
JOIN provider_services ps ON ps.provider_id = p.id AND ps.custom_name = 'Khám tổng quát'
JOIN services svc ON svc.id = ps.service_id
JOIN provider_availability_slots s ON s.provider_service_id = ps.id AND s.slot_date = CURDATE() + INTERVAL 2 DAY AND s.start_time = '11:00:00'
WHERE u.email = 'customer1@petgo.local'
  AND NOT EXISTS (SELECT 1 FROM bookings WHERE booking_code = 'BOOK-DEMO-002')
LIMIT 1;

INSERT INTO bookings (
    booking_code, customer_user_id, provider_id, pet_id, provider_service_id,
    appointment_date, start_time, end_time, timezone, status, customer_note,
    provider_name_snapshot, provider_phone_snapshot, provider_address_snapshot,
    service_name_snapshot, service_description_snapshot, service_duration_minutes_snapshot,
    pet_name_snapshot, pet_breed_snapshot,
    subtotal_amount, membership_discount_amount, promo_discount_amount, tax_amount, total_amount, currency_code,
    created_at, updated_at
)
SELECT
    'BOOK-DEMO-003', u.id, p.id, pet.id, ps.id,
    CURDATE() - INTERVAL 3 DAY, '10:00:00', '10:45:00', 'Asia/Ho_Chi_Minh', 'COMPLETED', 'Tắm nhẹ vì bé hơi sợ nước',
    p.business_name, p.emergency_phone, p.primary_address_line1,
    COALESCE(ps.custom_name, svc.name), COALESCE(ps.short_description, svc.short_description), ps.duration_minutes,
    pet.name, pet.breed,
    ps.price_amount, 0, 0, 0, ps.price_amount, ps.currency_code,
    NOW(), NOW()
FROM users u
JOIN pets pet ON pet.owner_user_id = u.id AND pet.pet_code = 'PET-DEMO-001'
JOIN provider_profiles p ON p.slug = 'paws-relax-luxury-spa'
JOIN provider_services ps ON ps.provider_id = p.id AND ps.custom_name = 'Spa thư giãn'
JOIN services svc ON svc.id = ps.service_id
WHERE u.email = 'customer1@petgo.local'
  AND NOT EXISTS (SELECT 1 FROM bookings WHERE booking_code = 'BOOK-DEMO-003')
LIMIT 1;

INSERT INTO bookings (
    booking_code, customer_user_id, provider_id, pet_id, provider_service_id,
    appointment_date, start_time, end_time, timezone, status, customer_note,
    provider_name_snapshot, provider_phone_snapshot, provider_address_snapshot,
    service_name_snapshot, service_description_snapshot, service_duration_minutes_snapshot,
    pet_name_snapshot, pet_breed_snapshot,
    subtotal_amount, membership_discount_amount, promo_discount_amount, tax_amount, total_amount, currency_code,
    created_at, updated_at
)
SELECT
    'BOOK-DEMO-004', u.id, p.id, pet.id, ps.id,
    CURDATE() - INTERVAL 1 DAY, '06:30:00', '07:30:00', 'Asia/Ho_Chi_Minh', 'CANCELLED', 'Có việc đột xuất',
    p.business_name, p.emergency_phone, p.primary_address_line1,
    COALESCE(ps.custom_name, svc.name), COALESCE(ps.short_description, svc.short_description), ps.duration_minutes,
    pet.name, pet.breed,
    ps.price_amount, 0, 0, 0, ps.price_amount, ps.currency_code,
    NOW(), NOW()
FROM users u
JOIN pets pet ON pet.owner_user_id = u.id AND pet.pet_code = 'PET-DEMO-003'
JOIN provider_profiles p ON p.slug = 'walkie-buddy'
JOIN provider_services ps ON ps.provider_id = p.id AND ps.custom_name = 'Dắt đi dạo 60 phút'
JOIN services svc ON svc.id = ps.service_id
WHERE u.email = 'customer2@petgo.local'
  AND NOT EXISTS (SELECT 1 FROM bookings WHERE booking_code = 'BOOK-DEMO-004')
LIMIT 1;

-- Mark booked capacities for the future slots used above.
UPDATE provider_availability_slots s
JOIN bookings b ON b.availability_slot_id = s.id
SET s.capacity_booked = GREATEST(s.capacity_booked, 1),
    s.slot_status = CASE WHEN GREATEST(s.capacity_booked, 1) >= s.capacity_total THEN 'BOOKED' ELSE 'AVAILABLE' END
WHERE b.booking_code IN ('BOOK-DEMO-001', 'BOOK-DEMO-002');

-- =========================================================
-- 11) Booking status history + cancellations/reschedules
-- =========================================================
INSERT INTO booking_status_history (booking_id, from_status, to_status, changed_by_user_id, note)
SELECT b.id, NULL, 'PENDING_PAYMENT', b.customer_user_id, 'Booking được tạo'
FROM bookings b
WHERE b.booking_code IN ('BOOK-DEMO-001', 'BOOK-DEMO-002', 'BOOK-DEMO-003', 'BOOK-DEMO-004')
  AND NOT EXISTS (SELECT 1 FROM booking_status_history h WHERE h.booking_id = b.id AND h.to_status = 'PENDING_PAYMENT');

INSERT INTO booking_status_history (booking_id, from_status, to_status, changed_by_user_id, note)
SELECT b.id, 'PENDING_PAYMENT', 'PENDING_CONFIRMATION', b.customer_user_id, 'Thanh toán thành công'
FROM bookings b
WHERE b.booking_code = 'BOOK-DEMO-001'
  AND NOT EXISTS (SELECT 1 FROM booking_status_history h WHERE h.booking_id = b.id AND h.to_status = 'PENDING_CONFIRMATION');

INSERT INTO booking_status_history (booking_id, from_status, to_status, changed_by_user_id, note)
SELECT b.id, 'PENDING_PAYMENT', 'PENDING_CONFIRMATION', b.customer_user_id, 'Thanh toán thành công'
FROM bookings b
WHERE b.booking_code = 'BOOK-DEMO-002'
  AND NOT EXISTS (SELECT 1 FROM booking_status_history h WHERE h.booking_id = b.id AND h.to_status = 'PENDING_CONFIRMATION');

INSERT INTO booking_status_history (booking_id, from_status, to_status, changed_by_user_id, note)
SELECT b.id, 'PENDING_CONFIRMATION', 'CONFIRMED', b.customer_user_id, 'Provider đã xác nhận lịch hẹn'
FROM bookings b
WHERE b.booking_code = 'BOOK-DEMO-002'
  AND NOT EXISTS (SELECT 1 FROM booking_status_history h WHERE h.booking_id = b.id AND h.to_status = 'CONFIRMED');

INSERT INTO booking_status_history (booking_id, from_status, to_status, changed_by_user_id, note)
SELECT b.id, 'PENDING_PAYMENT', 'COMPLETED', b.customer_user_id, 'Đã hoàn thành dịch vụ'
FROM bookings b
WHERE b.booking_code = 'BOOK-DEMO-003'
  AND NOT EXISTS (SELECT 1 FROM booking_status_history h WHERE h.booking_id = b.id AND h.to_status = 'COMPLETED');

INSERT INTO booking_status_history (booking_id, from_status, to_status, changed_by_user_id, note)
SELECT b.id, 'PENDING_PAYMENT', 'CANCELLED', b.customer_user_id, 'Khách hàng hủy lịch'
FROM bookings b
WHERE b.booking_code = 'BOOK-DEMO-004'
  AND NOT EXISTS (SELECT 1 FROM booking_status_history h WHERE h.booking_id = b.id AND h.to_status = 'CANCELLED');

INSERT INTO booking_cancellations (booking_id, cancelled_by_user_id, reason_code, reason_text, refund_status, refund_amount, cancelled_at)
SELECT b.id, b.customer_user_id, 'CHANGE_OF_PLANS', 'Có việc đột xuất', 'FULL', b.total_amount, NOW()
FROM bookings b
WHERE b.booking_code = 'BOOK-DEMO-004'
  AND NOT EXISTS (SELECT 1 FROM booking_cancellations c WHERE c.booking_id = b.id);

INSERT INTO booking_reschedules (
    booking_id, requested_by_user_id,
    old_appointment_date, old_start_time, old_end_time,
    new_appointment_date, new_start_time, new_end_time,
    fee_amount, status, note, created_at, updated_at
)
SELECT b.id, b.customer_user_id,
       b.appointment_date, b.start_time, b.end_time,
       b.appointment_date + INTERVAL 1 DAY, b.start_time, b.end_time,
       0, 'APPROVED', 'Ví dụ lịch sử đổi lịch', NOW(), NOW()
FROM bookings b
WHERE b.booking_code = 'BOOK-DEMO-002'
  AND NOT EXISTS (SELECT 1 FROM booking_reschedules r WHERE r.booking_id = b.id);

-- =========================================================
-- 12) Reviews + photos + provider aggregates
-- =========================================================
INSERT INTO reviews (booking_id, customer_user_id, provider_id, rating, comment, status, created_at, updated_at)
SELECT b.id, b.customer_user_id, b.provider_id, 5, 'Dịch vụ rất tốt, thú cưng được chăm sóc kỹ và nhân viên rất nhiệt tình.', 'VISIBLE', NOW(), NOW()
FROM bookings b
WHERE b.booking_code = 'BOOK-DEMO-003'
  AND NOT EXISTS (SELECT 1 FROM reviews r WHERE r.booking_id = b.id);

INSERT INTO review_photos (review_id, photo_url, sort_order, created_at)
SELECT r.id,
       'https://images.unsplash.com/photo-1517849845537-4d257902454a?auto=format&fit=crop&q=80&w=900',
       1,
       NOW()
FROM reviews r
JOIN bookings b ON b.id = r.booking_id
WHERE b.booking_code = 'BOOK-DEMO-003'
  AND NOT EXISTS (SELECT 1 FROM review_photos rp WHERE rp.review_id = r.id AND rp.sort_order = 1);

UPDATE provider_profiles p
JOIN (
    SELECT provider_id,
           ROUND(AVG(rating), 2) AS avg_rating,
           COUNT(*) AS total_reviews_calc
    FROM reviews
    WHERE deleted_at IS NULL AND status = 'VISIBLE'
    GROUP BY provider_id
) x ON x.provider_id = p.id
SET p.average_rating = x.avg_rating,
    p.total_reviews = x.total_reviews_calc;

-- =========================================================
-- 13) Favorites
-- =========================================================
INSERT INTO favorites (user_id, provider_id, created_at)
SELECT u.id, p.id, NOW()
FROM users u
JOIN provider_profiles p ON p.slug IN ('paws-relax-luxury-spa', 'walkie-buddy')
WHERE u.email = 'customer1@petgo.local'
  AND NOT EXISTS (SELECT 1 FROM favorites f WHERE f.user_id = u.id AND f.provider_id = p.id);

-- =========================================================
-- 14) Membership subscription + invoice/payment
-- =========================================================
INSERT INTO membership_subscriptions (
    subscription_code, user_id, membership_plan_id, status, auto_renew,
    started_at, expires_at, next_billing_at, created_at, updated_at
)
SELECT 'SUB-DEMO-001', u.id, mp.id, 'ACTIVE', TRUE,
       NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY), DATE_ADD(NOW(), INTERVAL 30 DAY), NOW(), NOW()
FROM users u
JOIN membership_plans mp ON mp.plan_code = 'PRO'
WHERE u.email = 'customer1@petgo.local'
  AND NOT EXISTS (SELECT 1 FROM membership_subscriptions s WHERE s.subscription_code = 'SUB-DEMO-001');

-- =========================================================
-- 15) Invoices + payments for booking and membership
-- =========================================================
INSERT INTO invoices (
    invoice_number, user_id, booking_id, invoice_type, status,
    billing_name, billing_email, billing_phone, billing_address,
    subtotal_amount, discount_amount, tax_amount, total_amount, currency_code,
    issued_at, paid_at, note, created_at, updated_at
)
SELECT 'INV-BOOK-001', b.customer_user_id, b.id, 'BOOKING', 'PAID',
       u.full_name, u.email, u.phone_number, CONCAT_WS(', ', u.address_line1, u.district, u.city),
       b.subtotal_amount, b.promo_discount_amount + b.membership_discount_amount, b.tax_amount, b.total_amount, b.currency_code,
       NOW(), NOW(), 'Thanh toán demo cho booking', NOW(), NOW()
FROM bookings b
JOIN users u ON u.id = b.customer_user_id
WHERE b.booking_code = 'BOOK-DEMO-001'
  AND NOT EXISTS (SELECT 1 FROM invoices i WHERE i.invoice_number = 'INV-BOOK-001');

INSERT INTO invoice_items (invoice_id, item_type, item_name, description, quantity, unit_price, line_total, sort_order, created_at, updated_at)
SELECT i.id, 'BOOKING_SERVICE', b.service_name_snapshot, b.service_description_snapshot, 1, b.subtotal_amount, b.subtotal_amount, 1, NOW(), NOW()
FROM invoices i
JOIN bookings b ON b.id = i.booking_id
WHERE i.invoice_number = 'INV-BOOK-001'
  AND NOT EXISTS (SELECT 1 FROM invoice_items ii WHERE ii.invoice_id = i.id AND ii.item_type = 'BOOKING_SERVICE');

INSERT INTO invoice_items (invoice_id, item_type, item_name, description, quantity, unit_price, line_total, sort_order, created_at, updated_at)
SELECT i.id, 'DISCOUNT', 'Khuyến mãi PETGO50', 'Áp mã giảm giá', 1, -1 * b.promo_discount_amount, -1 * b.promo_discount_amount, 2, NOW(), NOW()
FROM invoices i
JOIN bookings b ON b.id = i.booking_id
WHERE i.invoice_number = 'INV-BOOK-001'
  AND b.promo_discount_amount > 0
  AND NOT EXISTS (SELECT 1 FROM invoice_items ii WHERE ii.invoice_id = i.id AND ii.item_type = 'DISCOUNT');

INSERT INTO payments (
    payment_code, invoice_id, payer_user_id, amount, currency_code, payment_method,
    gateway_name, gateway_transaction_id, status, paid_at, created_at, updated_at
)
SELECT 'PAY-BOOK-001', i.id, i.user_id, i.total_amount, i.currency_code, 'MOMO',
       'SIMULATED', 'TXN-BOOK-001', 'SUCCEEDED', NOW(), NOW(), NOW()
FROM invoices i
WHERE i.invoice_number = 'INV-BOOK-001'
  AND NOT EXISTS (SELECT 1 FROM payments p WHERE p.payment_code = 'PAY-BOOK-001');

INSERT INTO invoices (
    invoice_number, user_id, membership_subscription_id, invoice_type, status,
    billing_name, billing_email, billing_phone, billing_address,
    subtotal_amount, discount_amount, tax_amount, total_amount, currency_code,
    issued_at, paid_at, note, created_at, updated_at
)
SELECT 'INV-MEM-001', s.user_id, s.id, 'MEMBERSHIP', 'PAID',
       u.full_name, u.email, u.phone_number, CONCAT_WS(', ', u.address_line1, u.district, u.city),
       mp.price_amount, 0, 0, mp.price_amount, mp.currency_code,
       NOW(), NOW(), 'Thanh toán demo cho membership', NOW(), NOW()
FROM membership_subscriptions s
JOIN users u ON u.id = s.user_id
JOIN membership_plans mp ON mp.id = s.membership_plan_id
WHERE s.subscription_code = 'SUB-DEMO-001'
  AND NOT EXISTS (SELECT 1 FROM invoices i WHERE i.invoice_number = 'INV-MEM-001');

INSERT INTO invoice_items (invoice_id, item_type, item_name, description, quantity, unit_price, line_total, sort_order, created_at, updated_at)
SELECT i.id, 'MEMBERSHIP_PLAN', mp.name, mp.description, 1, mp.price_amount, mp.price_amount, 1, NOW(), NOW()
FROM invoices i
JOIN membership_subscriptions s ON s.id = i.membership_subscription_id
JOIN membership_plans mp ON mp.id = s.membership_plan_id
WHERE i.invoice_number = 'INV-MEM-001'
  AND NOT EXISTS (SELECT 1 FROM invoice_items ii WHERE ii.invoice_id = i.id AND ii.item_type = 'MEMBERSHIP_PLAN');

INSERT INTO payments (
    payment_code, invoice_id, payer_user_id, amount, currency_code, payment_method,
    gateway_name, gateway_transaction_id, status, paid_at, created_at, updated_at
)
SELECT 'PAY-MEM-001', i.id, i.user_id, i.total_amount, i.currency_code, 'CARD',
       'SIMULATED', 'TXN-MEM-001', 'SUCCEEDED', NOW(), NOW(), NOW()
FROM invoices i
WHERE i.invoice_number = 'INV-MEM-001'
  AND NOT EXISTS (SELECT 1 FROM payments p WHERE p.payment_code = 'PAY-MEM-001');

-- =========================================================
-- 16) Notification settings + notifications
-- =========================================================
INSERT INTO user_notification_settings (user_id, email_booking_updates, email_promotions, push_booking_updates, push_reminders, sms_booking_updates)
SELECT u.id, TRUE, TRUE, TRUE, TRUE, FALSE
FROM users u
WHERE u.email IN ('customer1@petgo.local', 'customer2@petgo.local')
  AND NOT EXISTS (SELECT 1 FROM user_notification_settings ns WHERE ns.user_id = u.id);

INSERT INTO notifications (user_id, notification_type, title, message, reference_type, reference_id, is_read, sent_at, created_at, updated_at)
SELECT u.id, 'BOOKING_CONFIRMED', 'Lịch hẹn đã được xác nhận', 'Booking demo của bạn đã được provider xác nhận.', 'BOOKING', b.id, FALSE, NOW(), NOW(), NOW()
FROM users u
JOIN bookings b ON b.customer_user_id = u.id AND b.booking_code = 'BOOK-DEMO-002'
WHERE u.email = 'customer1@petgo.local'
  AND NOT EXISTS (SELECT 1 FROM notifications n WHERE n.user_id = u.id AND n.reference_type = 'BOOKING' AND n.reference_id = b.id AND n.notification_type = 'BOOKING_CONFIRMED');

-- =========================================================
-- 17) Final summary queries (optional)
-- SELECT * FROM users WHERE email LIKE '%@petgo.local';
-- SELECT * FROM provider_profiles;
-- SELECT * FROM bookings;
-- SELECT * FROM membership_subscriptions;
-- =========================================================
