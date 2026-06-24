-- Query tạo tài khoản mẫu PetGo (mô hình 2 bên: User ↔ Admin).
-- Chạy thủ công khi cần seed tài khoản demo; backend không còn tự tạo các tài khoản này khi khởi động.
-- Yêu cầu chạy trước:
--   2-base_roles.sql
-- Sau khi chạy file này, chạy tiếp 5-base_wallets.sql để tạo ví cho tài khoản mẫu.
-- Mật khẩu mẫu cho tài khoản user và admin: petgo123
-- BCrypt hash bên dưới tương ứng với mật khẩu petgo123.

USE petgo_db;

SET @sample_password_hash = '$2a$10$n89Petg/CbVQPxO1Hzra7ug9/MjwIM5gqQBM2iz9CDnh5ZT6yWwoy';
SET @old_sql_safe_updates = @@SQL_SAFE_UPDATES;
SET SQL_SAFE_UPDATES = 0;

-- ========================
-- 1. Tài khoản user mẫu
-- ========================
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

-- ========================
-- 2. Tài khoản admin mẫu
-- ========================
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
WHERE email IN ('user@petgo.local', 'admin@petgo.local');