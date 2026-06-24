-- Query tạo dữ liệu demo mở rộng cho PetGo.
-- Chạy thủ công sau các file: 2-base_roles.sql, 4-sample_accounts.sql, 5-base_wallets.sql.
-- File idempotent theo code cố định: thêm ví tiền demo và pet mẫu.

USE petgo_db;

SET @old_sql_safe_updates = @@SQL_SAFE_UPDATES;
SET SQL_SAFE_UPDATES = 0;

SET @sample_user_id = (SELECT id FROM users WHERE email = 'user@petgo.local' LIMIT 1);
SET @sample_admin_id = (SELECT id FROM users WHERE email = 'admin@petgo.local' LIMIT 1);

-- ========================
-- 1. Nạp tiền ví demo
-- ========================
INSERT INTO wallets (user_id, balance, held_balance, currency_code, status, is_system)
SELECT u.id, 0.00, 0.00, 'VND', 'ACTIVE', FALSE
FROM users u
WHERE NOT EXISTS (SELECT 1 FROM wallets w WHERE w.user_id = u.id);

UPDATE wallets w
JOIN users u ON u.id = w.user_id
SET w.balance = CASE
        WHEN u.email = 'admin@petgo.local' THEN 1000000000.00
        WHEN u.email = 'user@petgo.local' THEN 10000000.00
        ELSE w.balance
    END,
    w.currency_code = 'VND',
    w.status = 'ACTIVE'
WHERE u.email IN ('admin@petgo.local', 'user@petgo.local')
  AND w.id > 0;

INSERT INTO wallet_transactions (transaction_code, wallet_id, user_id, type, status, amount, balance_before, balance_after, reviewed_by_admin_id, reviewed_at, note)
SELECT 'DEMO-TOPUP-ADMIN-1B', w.id, u.id, 'TOP_UP', 'COMPLETED', 1000000000.00, 0.00, 1000000000.00, @sample_admin_id, NOW(), 'Seed demo: nạp 1 tỷ vào ví admin.'
FROM wallets w
JOIN users u ON u.id = w.user_id AND u.email = 'admin@petgo.local'
WHERE NOT EXISTS (SELECT 1 FROM wallet_transactions WHERE transaction_code = 'DEMO-TOPUP-ADMIN-1B');

INSERT INTO wallet_transactions (transaction_code, wallet_id, user_id, type, status, amount, balance_before, balance_after, reviewed_by_admin_id, reviewed_at, note)
SELECT 'DEMO-TOPUP-USER-10M', w.id, u.id, 'TOP_UP', 'COMPLETED', 10000000.00, 0.00, 10000000.00, @sample_admin_id, NOW(), 'Seed demo: nạp 10 triệu vào ví user.'
FROM wallets w
JOIN users u ON u.id = w.user_id AND u.email = 'user@petgo.local'
WHERE NOT EXISTS (SELECT 1 FROM wallet_transactions WHERE transaction_code = 'DEMO-TOPUP-USER-10M');

-- ========================
-- 2. Pet mẫu
-- ========================
INSERT INTO pets (pet_code, owner_user_id, name, species, breed, gender, size, status, age_label, weight_kg, avatar_url, vaccination_notes, health_notes, behavior_notes)
SELECT 'PET-DEMO-COCO', @sample_user_id, 'Coco', 'DOG', 'Poodle', 'FEMALE', 'SMALL', 'ACTIVE', '2 tuổi', 5.40, 'https://images.unsplash.com/photo-1588421357574-87938a86fa28?auto=format&fit=crop&q=80&w=500', 'Đã tiêm phòng định kỳ.', 'Sức khỏe tốt.', 'Thân thiện, hơi sợ máy sấy.'
WHERE @sample_user_id IS NOT NULL AND NOT EXISTS (SELECT 1 FROM pets WHERE pet_code = 'PET-DEMO-COCO');

INSERT INTO pets (pet_code, owner_user_id, name, species, breed, gender, size, status, age_label, weight_kg, avatar_url, vaccination_notes, health_notes, behavior_notes)
SELECT 'PET-DEMO-MILO', @sample_user_id, 'Milo', 'CAT', 'British Shorthair', 'MALE', 'MEDIUM', 'ACTIVE', '3 tuổi', 4.80, 'https://images.unsplash.com/photo-1574158622682-e40e69881006?auto=format&fit=crop&q=80&w=500', 'Đã tẩy giun.', 'Dị ứng nhẹ với hải sản.', 'Hiền, không thích tiếng ồn lớn.'
WHERE @sample_user_id IS NOT NULL AND NOT EXISTS (SELECT 1 FROM pets WHERE pet_code = 'PET-DEMO-MILO');

INSERT INTO pet_photos (pet_id, photo_url, is_primary, sort_order)
SELECT p.id, 'https://images.unsplash.com/photo-1588421357574-87938a86fa28?auto=format&fit=crop&q=80&w=500', TRUE, 0
FROM pets p WHERE p.pet_code = 'PET-DEMO-COCO' AND NOT EXISTS (SELECT 1 FROM pet_photos pp WHERE pp.pet_id = p.id AND pp.photo_url LIKE 'https://images.unsplash.com/photo-1588421357574%');

INSERT INTO pet_photos (pet_id, photo_url, is_primary, sort_order)
SELECT p.id, 'https://images.unsplash.com/photo-1574158622682-e40e69881006?auto=format&fit=crop&q=80&w=500', TRUE, 0
FROM pets p WHERE p.pet_code = 'PET-DEMO-MILO' AND NOT EXISTS (SELECT 1 FROM pet_photos pp WHERE pp.pet_id = p.id AND pp.photo_url LIKE 'https://images.unsplash.com/photo-1574158622682%');

SET SQL_SAFE_UPDATES = @old_sql_safe_updates;

SELECT u.email, w.balance, w.currency_code, w.status FROM wallets w JOIN users u ON u.id = w.user_id WHERE u.email IN ('user@petgo.local', 'admin@petgo.local') ORDER BY u.email;
SELECT p.pet_code, p.name, p.species, p.breed FROM pets p WHERE p.pet_code IN ('PET-DEMO-COCO', 'PET-DEMO-MILO') ORDER BY p.pet_code;
