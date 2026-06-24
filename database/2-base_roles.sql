-- Query tạo dữ liệu role bắt buộc cho PetGo (mô hình 2 bên: User ↔ Admin).
-- Chạy thủ công sau khi backend đã sinh bảng từ database rỗng.
-- File này idempotent: chạy nhiều lần không tạo trùng role theo code.

USE petgo_db;

SET @old_sql_safe_updates = @@SQL_SAFE_UPDATES;
SET SQL_SAFE_UPDATES = 0;

INSERT INTO roles (code, name, description)
SELECT 'USER', 'User', 'Người dùng hệ thống'
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE code = 'USER');

INSERT INTO roles (code, name, description)
SELECT 'ADMIN', 'Administrator', 'Quản trị hệ thống'
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE code = 'ADMIN');

UPDATE roles
SET name = 'User',
    description = 'Người dùng hệ thống'
WHERE code = 'USER'
  AND id > 0;

UPDATE roles
SET name = 'Administrator',
    description = 'Quản trị hệ thống'
WHERE code = 'ADMIN'
  AND id > 0;

SET SQL_SAFE_UPDATES = @old_sql_safe_updates;

SELECT code, name, description
FROM roles
WHERE code IN ('USER', 'ADMIN')
ORDER BY code;