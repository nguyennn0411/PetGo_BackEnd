-- Migration: Tạo system wallet
-- File idempotent: chạy nhiều lần không lỗi.
--
-- Thời điểm chạy: sau khi backend đã startup lần đầu (JPA tạo bảng xong).
-- JPA tự tạo cột is_system + user_id NULL từ entity, không cần ALTER.

USE petgo_db;

-- Tạo system wallet nếu chưa tồn tại
INSERT INTO wallets (user_id, balance, held_balance, currency_code, status, is_system)
SELECT NULL, 0.00, 0.00, 'VND', 'ACTIVE', TRUE
WHERE NOT EXISTS (
    SELECT 1 FROM wallets WHERE is_system = TRUE
);
