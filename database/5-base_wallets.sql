-- Query tạo dữ liệu ví bắt buộc cho PetGo.
-- Chạy thủ công sau khi backend đã sinh bảng và sau khi đã có dữ liệu users nếu cần tạo ví cho user hiện có.
-- File này idempotent: chạy nhiều lần không tạo trùng wallet theo user_id.

USE petgo_db;

INSERT INTO wallet_settings (setting_key, setting_value)
SELECT 'WALLET_AUTO_CONFIRM_TOP_UP', 'false'
WHERE NOT EXISTS (
    SELECT 1
    FROM wallet_settings
    WHERE setting_key = 'WALLET_AUTO_CONFIRM_TOP_UP'
);

INSERT INTO wallets (user_id, balance, currency_code, status)
SELECT u.id, 0.00, 'VND', 'ACTIVE'
FROM users u
WHERE NOT EXISTS (
    SELECT 1
    FROM wallets w
    WHERE w.user_id = u.id
);

SELECT setting_key, setting_value
FROM wallet_settings
WHERE setting_key = 'WALLET_AUTO_CONFIRM_TOP_UP';

SELECT u.email, w.balance, w.currency_code, w.status
FROM wallets w
JOIN users u ON u.id = w.user_id
ORDER BY u.email;