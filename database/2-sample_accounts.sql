-- Query tạo tài khoản mẫu PetGo.
-- Chạy thủ công khi cần seed tài khoản demo; backend không còn tự tạo các tài khoản này khi khởi động.
-- Mật khẩu mẫu cho cả 3 tài khoản: petgo123
-- BCrypt hash bên dưới tương ứng với mật khẩu petgo123.

USE petgo_db;

SET @sample_password_hash = '$2a$10$n89Petg/CbVQPxO1Hzra7ug9/MjwIM5gqQBM2iz9CDnh5ZT6yWwoy';
SET @old_sql_safe_updates = @@SQL_SAFE_UPDATES;
SET SQL_SAFE_UPDATES = 0;

-- Đảm bảo role tồn tại trước khi gán user_roles.
-- Cần thiết nếu chạy file này khi backend chưa kịp seed role hoặc roles đang rỗng sau reset.
INSERT INTO roles (code, name, description)
SELECT 'USER', 'User', 'Người dùng hệ thống'
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE code = 'USER');

INSERT INTO roles (code, name, description)
SELECT 'PROVIDER', 'Provider', 'Đối tác cung cấp dịch vụ'
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE code = 'PROVIDER');

INSERT INTO roles (code, name, description)
SELECT 'ADMIN', 'Administrator', 'Quản trị hệ thống'
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE code = 'ADMIN');

UPDATE roles
SET name = 'User',
    description = 'Người dùng hệ thống'
WHERE code = 'USER'
  AND id > 0;

UPDATE roles
SET name = 'Provider',
    description = 'Đối tác cung cấp dịch vụ'
WHERE code = 'PROVIDER'
  AND id > 0;

UPDATE roles
SET name = 'Administrator',
    description = 'Quản trị hệ thống'
WHERE code = 'ADMIN'
  AND id > 0;

INSERT INTO users (
    user_code, email, password_hash, full_name, phone_number,
    avatar_url, cover_url, city, province, country_code,
    status, email_verified_at
)
SELECT 'USR-SAMPLE-USER', 'user@petgo.local', @sample_password_hash, 'PetGo Sample User', '0919000001',
       'https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&q=80&w=400',
       'https://images.unsplash.com/photo-1548199973-03cce0bbc87b?auto=format&fit=crop&q=80&w=1600',
       'Hồ Chí Minh', 'Hồ Chí Minh', 'VN', 'ACTIVE', NOW()
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'user@petgo.local');

UPDATE users
SET status = 'ACTIVE',
    email_verified_at = COALESCE(email_verified_at, NOW()),
    password_hash = @sample_password_hash
WHERE email = 'user@petgo.local';

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u
JOIN roles r ON r.code = 'USER'
WHERE u.email = 'user@petgo.local'
  AND NOT EXISTS (
      SELECT 1 FROM user_roles ur WHERE ur.user_id = u.id AND ur.role_id = r.id
  );

INSERT INTO users (
    user_code, email, password_hash, full_name, phone_number,
    avatar_url, cover_url, city, province, country_code,
    status, email_verified_at
)
SELECT 'USR-SAMPLE-PROVIDER', 'provider@petgo.local', @sample_password_hash, 'PetGo Sample Provider Owner', '0919000002',
       'https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?auto=format&fit=crop&q=80&w=400',
       'https://images.unsplash.com/photo-1548199973-03cce0bbc87b?auto=format&fit=crop&q=80&w=1600',
       'Hồ Chí Minh', 'Hồ Chí Minh', 'VN', 'ACTIVE', NOW()
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'provider@petgo.local');

UPDATE users
SET status = 'ACTIVE',
    email_verified_at = COALESCE(email_verified_at, NOW()),
    password_hash = @sample_password_hash
WHERE email = 'provider@petgo.local';

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u
JOIN roles r ON r.code = 'PROVIDER'
WHERE u.email = 'provider@petgo.local'
  AND NOT EXISTS (
      SELECT 1 FROM user_roles ur WHERE ur.user_id = u.id AND ur.role_id = r.id
  );

INSERT INTO provider_profiles (
    provider_code, user_id, business_name, slug, provider_type,
    headline, description, years_experience, verification_status,
    is_featured, is_hot, accepts_instant_booking, accepts_membership,
    average_rating, total_reviews, total_completed_bookings,
    service_radius_km, cancellation_free_hours, emergency_phone,
    primary_address_line1, ward, district, city, province, country_code,
    latitude, longitude, main_image_url, cover_image_url,
    price_from_amount, currency_code, status
)
SELECT 'PRV-SAMPLE-PROVIDER', u.id, 'PetGo Sample Provider', 'petgo-sample-provider', 'BUSINESS',
       'Spa, grooming và chăm sóc thú cưng mẫu',
       'Tài khoản nhà cung cấp mẫu để kiểm thử luồng đối tác PetGo. Có dịch vụ đã duyệt để hiển thị đúng ở trang tìm kiếm.',
       3, 'VERIFIED', TRUE, TRUE, TRUE, TRUE,
       4.80, 25, 120, 8.00, 24, u.phone_number,
       '123 PetGo Demo Street', 'Phường Bến Nghé', 'Quận 1', 'Hồ Chí Minh', 'Hồ Chí Minh', 'VN',
       10.7769000, 106.7009000,
       'https://images.unsplash.com/photo-1601758124510-52d02ddb7cbd?auto=format&fit=crop&q=80&w=800',
       'https://images.unsplash.com/photo-1601758125946-6ec2ef64daf8?auto=format&fit=crop&q=80&w=1400',
       180000.00, 'VND', 'ACTIVE'
FROM users u
WHERE u.email = 'provider@petgo.local'
  AND NOT EXISTS (
      SELECT 1
      FROM provider_profiles p
      WHERE p.user_id = u.id
         OR p.slug = 'petgo-sample-provider'
         OR p.provider_code = 'PRV-SAMPLE-PROVIDER'
  );

UPDATE provider_profiles p
JOIN users u ON u.id = p.user_id
SET p.business_name = 'PetGo Sample Provider',
    p.headline = 'Spa, grooming và chăm sóc thú cưng mẫu',
    p.description = 'Tài khoản nhà cung cấp mẫu để kiểm thử luồng đối tác PetGo. Có dịch vụ đã duyệt để hiển thị đúng ở trang tìm kiếm.',
    p.verification_status = 'VERIFIED',
    p.is_featured = TRUE,
    p.is_hot = TRUE,
    p.accepts_instant_booking = TRUE,
    p.accepts_membership = TRUE,
    p.average_rating = 4.80,
    p.total_reviews = 25,
    p.total_completed_bookings = 120,
    p.service_radius_km = 8.00,
    p.cancellation_free_hours = 24,
    p.emergency_phone = u.phone_number,
    p.primary_address_line1 = '123 PetGo Demo Street',
    p.ward = 'Phường Bến Nghé',
    p.district = 'Quận 1',
    p.city = 'Hồ Chí Minh',
    p.province = 'Hồ Chí Minh',
    p.country_code = 'VN',
    p.latitude = 10.7769000,
    p.longitude = 106.7009000,
    p.main_image_url = 'https://images.unsplash.com/photo-1601758124510-52d02ddb7cbd?auto=format&fit=crop&q=80&w=800',
    p.cover_image_url = 'https://images.unsplash.com/photo-1601758125946-6ec2ef64daf8?auto=format&fit=crop&q=80&w=1400',
    p.price_from_amount = 180000.00,
    p.currency_code = 'VND',
    p.status = 'ACTIVE',
    p.deleted_at = NULL
WHERE u.email = 'provider@petgo.local'
  AND p.provider_code = 'PRV-SAMPLE-PROVIDER'
  AND p.id > 0;

-- Từ luồng search hiện tại, provider chỉ nên xuất hiện khi có ít nhất 1 dịch vụ active + approved.
-- Seed thêm catalog service và provider service mẫu để tài khoản provider demo không bị "thừa"/rỗng ở trang /search.
INSERT INTO services (
    service_code, category_id, name, slug, short_description, description,
    default_duration_minutes, base_price_amount, currency_code, price_unit,
    requires_consultation, is_active
)
SELECT 'SVC-SAMPLE-GROOMING', c.id, 'Tắm spa thú cưng mẫu', 'tam-spa-thu-cung-mau',
       'Tắm, sấy, vệ sinh tai móng và chăm sóc lông cơ bản.',
       'Dịch vụ mẫu dùng cho tài khoản provider demo, phù hợp kiểm thử tìm kiếm, booking và quản lý dịch vụ partner.',
       60, 180000.00, 'VND', 'SESSION', FALSE, TRUE
FROM service_categories c
WHERE c.name = 'Tắm gội & vệ sinh cơ bản'
  AND NOT EXISTS (SELECT 1 FROM services s WHERE s.service_code = 'SVC-SAMPLE-GROOMING');

UPDATE services s
JOIN service_categories c ON c.id = s.category_id
SET s.name = 'Tắm spa thú cưng mẫu',
    s.slug = 'tam-spa-thu-cung-mau',
    s.short_description = 'Tắm, sấy, vệ sinh tai móng và chăm sóc lông cơ bản.',
    s.description = 'Dịch vụ mẫu dùng cho tài khoản provider demo, phù hợp kiểm thử tìm kiếm, booking và quản lý dịch vụ partner.',
    s.default_duration_minutes = 60,
    s.base_price_amount = 180000.00,
    s.currency_code = 'VND',
    s.price_unit = 'SESSION',
    s.requires_consultation = FALSE,
    s.is_active = TRUE
WHERE s.service_code = 'SVC-SAMPLE-GROOMING'
  AND c.name = 'Tắm gội & vệ sinh cơ bản'
  AND s.id > 0;

INSERT INTO provider_services (
    provider_id, service_id, custom_name, short_description, description,
    duration_minutes, duration_type, price_amount, currency_code, price_unit,
    is_featured, is_active, capacity_per_slot, booking_buffer_minutes,
    buffer_after_minutes, display_order, category_ids, photo_urls, approval_status
)
SELECT p.id, s.id, 'Gói tắm spa tiêu chuẩn',
       'Tắm, sấy, chải lông và vệ sinh tai móng cho chó mèo.',
       'Dịch vụ mẫu đã được duyệt để provider demo xuất hiện đúng ở trang tìm kiếm và có thể đặt lịch thử.',
       60, 'MINUTES', 180000.00, 'VND', 'SESSION',
       TRUE, TRUE, 1, 0, 0, 1, CAST(s.category_id AS CHAR),
       'https://images.unsplash.com/photo-1516734212186-a967f81ad0d7?auto=format&fit=crop&q=80&w=800',
       'APPROVED'
FROM provider_profiles p
JOIN users u ON u.id = p.user_id AND u.email = 'provider@petgo.local'
JOIN services s ON s.service_code = 'SVC-SAMPLE-GROOMING'
WHERE p.provider_code = 'PRV-SAMPLE-PROVIDER'
  AND NOT EXISTS (
      SELECT 1 FROM provider_services ps WHERE ps.provider_id = p.id AND ps.service_id = s.id
  );

UPDATE provider_services ps
JOIN provider_profiles p ON p.id = ps.provider_id AND p.provider_code = 'PRV-SAMPLE-PROVIDER'
JOIN users u ON u.id = p.user_id AND u.email = 'provider@petgo.local'
JOIN services s ON s.id = ps.service_id AND s.service_code = 'SVC-SAMPLE-GROOMING'
SET ps.custom_name = 'Gói tắm spa tiêu chuẩn',
    ps.short_description = 'Tắm, sấy, chải lông và vệ sinh tai móng cho chó mèo.',
    ps.description = 'Dịch vụ mẫu đã được duyệt để provider demo xuất hiện đúng ở trang tìm kiếm và có thể đặt lịch thử.',
    ps.duration_minutes = 60,
    ps.duration_type = 'MINUTES',
    ps.price_amount = 180000.00,
    ps.currency_code = 'VND',
    ps.price_unit = 'SESSION',
    ps.is_featured = TRUE,
    ps.is_active = TRUE,
    ps.capacity_per_slot = 1,
    ps.booking_buffer_minutes = 0,
    ps.buffer_after_minutes = 0,
    ps.display_order = 1,
    ps.category_ids = CAST(s.category_id AS CHAR),
    ps.photo_urls = 'https://images.unsplash.com/photo-1516734212186-a967f81ad0d7?auto=format&fit=crop&q=80&w=800',
    ps.approval_status = 'APPROVED'
WHERE ps.id > 0;

INSERT INTO provider_photos (provider_id, photo_url, media_type, is_primary, sort_order)
SELECT p.id, photo.photo_url, 'IMAGE', photo.is_primary, photo.sort_order
FROM provider_profiles p
JOIN users u ON u.id = p.user_id AND u.email = 'provider@petgo.local'
JOIN (
    SELECT 'https://images.unsplash.com/photo-1601758124510-52d02ddb7cbd?auto=format&fit=crop&q=80&w=800' AS photo_url, TRUE AS is_primary, 0 AS sort_order
    UNION ALL
    SELECT 'https://images.unsplash.com/photo-1516734212186-a967f81ad0d7?auto=format&fit=crop&q=80&w=800', FALSE, 1
    UNION ALL
    SELECT 'https://images.unsplash.com/photo-1586816001966-79b736744398?auto=format&fit=crop&q=80&w=800', FALSE, 2
) photo
WHERE p.provider_code = 'PRV-SAMPLE-PROVIDER'
  AND NOT EXISTS (
      SELECT 1 FROM provider_photos pp WHERE pp.provider_id = p.id AND pp.photo_url = photo.photo_url
  );

INSERT INTO provider_business_hours (provider_id, weekday, opens_at, closes_at, break_starts_at, break_ends_at, is_closed)
SELECT p.id, days.weekday,
       CASE WHEN days.weekday = 7 THEN NULL ELSE '08:00:00' END,
       CASE WHEN days.weekday = 7 THEN NULL ELSE '18:00:00' END,
       NULL, NULL,
       CASE WHEN days.weekday = 7 THEN TRUE ELSE FALSE END
FROM provider_profiles p
JOIN users u ON u.id = p.user_id AND u.email = 'provider@petgo.local'
JOIN (
    SELECT 1 AS weekday UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7
) days
WHERE p.provider_code = 'PRV-SAMPLE-PROVIDER'
  AND NOT EXISTS (
      SELECT 1 FROM provider_business_hours bh WHERE bh.provider_id = p.id AND bh.weekday = days.weekday
  );

UPDATE provider_business_hours bh
JOIN provider_profiles p ON p.id = bh.provider_id AND p.provider_code = 'PRV-SAMPLE-PROVIDER'
JOIN users u ON u.id = p.user_id AND u.email = 'provider@petgo.local'
SET bh.opens_at = CASE WHEN bh.weekday = 7 THEN NULL ELSE '08:00:00' END,
    bh.closes_at = CASE WHEN bh.weekday = 7 THEN NULL ELSE '18:00:00' END,
    bh.break_starts_at = NULL,
    bh.break_ends_at = NULL,
    bh.is_closed = CASE WHEN bh.weekday = 7 THEN TRUE ELSE FALSE END
WHERE bh.id > 0;

INSERT INTO users (
    user_code, email, password_hash, full_name, phone_number,
    avatar_url, cover_url, city, province, country_code,
    status, email_verified_at
)
SELECT 'USR-SAMPLE-ADMIN', 'admin@petgo.local', @sample_password_hash, 'PetGo Sample Admin', '0919000003',
       'https://images.unsplash.com/photo-1607746882042-944635dfe10e?auto=format&fit=crop&q=80&w=400',
       'https://images.unsplash.com/photo-1548199973-03cce0bbc87b?auto=format&fit=crop&q=80&w=1600',
       'Hồ Chí Minh', 'Hồ Chí Minh', 'VN', 'ACTIVE', NOW()
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'admin@petgo.local');

UPDATE users
SET status = 'ACTIVE',
    email_verified_at = COALESCE(email_verified_at, NOW()),
    password_hash = @sample_password_hash
WHERE email = 'admin@petgo.local';

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u
JOIN roles r ON r.code = 'ADMIN'
WHERE u.email = 'admin@petgo.local'
  AND NOT EXISTS (
      SELECT 1 FROM user_roles ur WHERE ur.user_id = u.id AND ur.role_id = r.id
  );

SET SQL_SAFE_UPDATES = @old_sql_safe_updates;

SELECT email, status, email_verified_at, password_hash
FROM users
WHERE email IN ('user@petgo.local', 'provider@petgo.local', 'admin@petgo.local');