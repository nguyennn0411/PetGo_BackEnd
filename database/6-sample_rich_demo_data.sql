-- Query tạo dữ liệu demo mở rộng cho PetGo.
-- Chạy thủ công sau các file: 2-base_roles.sql, 3-sample_service_categories.sql, 4-sample_accounts.sql, 5-base_wallets.sql.
-- File idempotent theo code cố định: thêm ví tiền demo, nhiều service cho provider và booking/giao dịch ở nhiều trạng thái.

USE petgo_db;

SET @old_sql_safe_updates = @@SQL_SAFE_UPDATES;
SET SQL_SAFE_UPDATES = 0;

SET @sample_user_id = (SELECT id FROM users WHERE email = 'user@petgo.local' LIMIT 1);
SET @sample_provider_owner_id = (SELECT id FROM users WHERE email = 'provider@petgo.local' LIMIT 1);
SET @sample_admin_id = (SELECT id FROM users WHERE email = 'admin@petgo.local' LIMIT 1);
SET @sample_provider_id = (SELECT id FROM provider_profiles WHERE provider_code = 'PRV-SAMPLE-PROVIDER' LIMIT 1);

INSERT INTO wallets (user_id, balance, currency_code, status)
SELECT u.id, 0.00, 'VND', 'ACTIVE'
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

INSERT INTO pets (pet_code, owner_user_id, name, species, breed, gender, size, status, age_label, weight_kg, avatar_url, vaccination_notes, health_notes, behavior_notes)
SELECT 'PET-DEMO-COCO', @sample_user_id, 'Coco', 'DOG', 'Poodle', 'FEMALE', 'SMALL', 'ACTIVE', '2 tuổi', 5.40, 'https://images.unsplash.com/photo-1588421357574-87938a86fa28?auto=format&fit=crop&q=80&w=500', 'Đã tiêm phòng định kỳ.', 'Sức khỏe tốt.', 'Thân thiện, hơi sợ máy sấy.'
WHERE @sample_user_id IS NOT NULL AND NOT EXISTS (SELECT 1 FROM pets WHERE pet_code = 'PET-DEMO-COCO');

INSERT INTO pets (pet_code, owner_user_id, name, species, breed, gender, size, status, age_label, weight_kg, avatar_url, vaccination_notes, health_notes, behavior_notes)
SELECT 'PET-DEMO-MILO', @sample_user_id, 'Milo', 'CAT', 'British Shorthair', 'MALE', 'MEDIUM', 'ACTIVE', '3 tuổi', 4.80, 'https://images.unsplash.com/photo-1574158622682-e40e69881006?auto=format&fit=crop&q=80&w=500', 'Đã tẩy giun.', 'Dị ứng nhẹ với hải sản.', 'Hiền, không thích tiếng ồn lớn.'
WHERE @sample_user_id IS NOT NULL AND NOT EXISTS (SELECT 1 FROM pets WHERE pet_code = 'PET-DEMO-MILO');

SET @pet_coco_id = (SELECT id FROM pets WHERE pet_code = 'PET-DEMO-COCO' LIMIT 1);
SET @pet_milo_id = (SELECT id FROM pets WHERE pet_code = 'PET-DEMO-MILO' LIMIT 1);

INSERT INTO pet_photos (pet_id, photo_url, is_primary, sort_order)
SELECT @pet_coco_id, 'https://images.unsplash.com/photo-1588421357574-87938a86fa28?auto=format&fit=crop&q=80&w=500', TRUE, 0
WHERE @pet_coco_id IS NOT NULL AND NOT EXISTS (SELECT 1 FROM pet_photos WHERE pet_id = @pet_coco_id AND photo_url LIKE 'https://images.unsplash.com/photo-1588421357574%');

INSERT INTO pet_photos (pet_id, photo_url, is_primary, sort_order)
SELECT @pet_milo_id, 'https://images.unsplash.com/photo-1574158622682-e40e69881006?auto=format&fit=crop&q=80&w=500', TRUE, 0
WHERE @pet_milo_id IS NOT NULL AND NOT EXISTS (SELECT 1 FROM pet_photos WHERE pet_id = @pet_milo_id AND photo_url LIKE 'https://images.unsplash.com/photo-1574158622682%');

INSERT INTO services (service_code, category_id, name, slug, short_description, description, default_duration_minutes, base_price_amount, currency_code, price_unit, requires_consultation, is_active)
SELECT 'SVC-DEMO-BATH-BASIC', c.id, 'Tắm sấy cơ bản', 'tam-say-co-ban', 'Tắm, sấy, chải lông và vệ sinh nhẹ.', 'Gói tắm sấy phổ biến cho chó mèo kích thước nhỏ/vừa.', 60, 180000.00, 'VND', 'SESSION', FALSE, TRUE FROM service_categories c WHERE c.name = 'Tắm sấy chó mèo' AND NOT EXISTS (SELECT 1 FROM services WHERE service_code = 'SVC-DEMO-BATH-BASIC');

INSERT INTO services (service_code, category_id, name, slug, short_description, description, default_duration_minutes, base_price_amount, currency_code, price_unit, requires_consultation, is_active)
SELECT 'SVC-DEMO-GROOMING-STYLE', c.id, 'Cắt tỉa tạo kiểu theo giống', 'cat-tia-tao-kieu-theo-giong', 'Cắt tỉa lông, tạo kiểu theo giống và yêu cầu.', 'Gói grooming chuyên sâu cho thú cưng cần làm đẹp ngoại hình.', 90, 350000.00, 'VND', 'SESSION', FALSE, TRUE FROM service_categories c WHERE c.name = 'Cắt tỉa theo giống' AND NOT EXISTS (SELECT 1 FROM services WHERE service_code = 'SVC-DEMO-GROOMING-STYLE');

INSERT INTO services (service_code, category_id, name, slug, short_description, description, default_duration_minutes, base_price_amount, currency_code, price_unit, requires_consultation, is_active)
SELECT 'SVC-DEMO-HOTEL-DOG', c.id, 'Lưu trú chó qua đêm', 'luu-tru-cho-qua-dem', 'Phòng lưu trú, cho ăn và cập nhật tình trạng.', 'Dịch vụ khách sạn qua đêm dành cho chó.', 1440, 450000.00, 'VND', 'DAY', TRUE, TRUE FROM service_categories c WHERE c.name = 'Lưu trú chó' AND NOT EXISTS (SELECT 1 FROM services WHERE service_code = 'SVC-DEMO-HOTEL-DOG');

INSERT INTO services (service_code, category_id, name, slug, short_description, description, default_duration_minutes, base_price_amount, currency_code, price_unit, requires_consultation, is_active)
SELECT 'SVC-DEMO-VET-CHECKUP', c.id, 'Khám tổng quát thú cưng', 'kham-tong-quat-thu-cung', 'Kiểm tra thể trạng và tư vấn chăm sóc.', 'Dịch vụ khám tổng quát cơ bản cho chó mèo.', 45, 250000.00, 'VND', 'SESSION', FALSE, TRUE FROM service_categories c WHERE c.name = 'Khám tổng quát' AND NOT EXISTS (SELECT 1 FROM services WHERE service_code = 'SVC-DEMO-VET-CHECKUP');

INSERT INTO services (service_code, category_id, name, slug, short_description, description, default_duration_minutes, base_price_amount, currency_code, price_unit, requires_consultation, is_active)
SELECT 'SVC-DEMO-TRAINING-BASIC', c.id, 'Huấn luyện lệnh cơ bản', 'huan-luyen-lenh-co-ban', 'Dạy ngồi, nằm, gọi tên và đi dây dắt.', 'Buổi huấn luyện kỹ năng cơ bản cho chó.', 75, 300000.00, 'VND', 'SESSION', TRUE, TRUE FROM service_categories c WHERE c.name = 'Lệnh cơ bản cho chó' AND NOT EXISTS (SELECT 1 FROM services WHERE service_code = 'SVC-DEMO-TRAINING-BASIC');

INSERT INTO services (service_code, category_id, name, slug, short_description, description, default_duration_minutes, base_price_amount, currency_code, price_unit, requires_consultation, is_active)
SELECT 'SVC-DEMO-WALKING', c.id, 'Dắt chó đi dạo 60 phút', 'dat-cho-di-dao-60-phut', 'Dắt chó đi dạo, vận động và gửi cập nhật.', 'Dịch vụ hỗ trợ vận động cho chó trong khu vực gần provider.', 60, 120000.00, 'VND', 'SESSION', FALSE, TRUE FROM service_categories c WHERE c.name = 'Dắt chó đi dạo' AND NOT EXISTS (SELECT 1 FROM services WHERE service_code = 'SVC-DEMO-WALKING');

INSERT INTO provider_services (provider_id, service_id, custom_name, short_description, description, duration_minutes, duration_type, price_amount, currency_code, price_unit, is_featured, is_active, capacity_per_slot, booking_buffer_minutes, buffer_after_minutes, display_order, category_ids, photo_urls, approval_status)
SELECT @sample_provider_id, s.id, 'Gói tắm sấy nhanh PetGo', 'Tắm sấy cơ bản cho chó mèo nhỏ/vừa.', 'Bao gồm tắm, sấy, chải lông, vệ sinh tai móng nhẹ.', 60, 'MINUTES', 180000.00, 'VND', 'SESSION', TRUE, TRUE, 2, 0, 10, 10, CAST(s.category_id AS CHAR), 'https://images.unsplash.com/photo-1516734212186-a967f81ad0d7?auto=format&fit=crop&q=80&w=800', 'APPROVED' FROM services s WHERE s.service_code = 'SVC-DEMO-BATH-BASIC' AND @sample_provider_id IS NOT NULL AND NOT EXISTS (SELECT 1 FROM provider_services ps WHERE ps.provider_id = @sample_provider_id AND ps.service_id = s.id);

INSERT INTO provider_services (provider_id, service_id, custom_name, short_description, description, duration_minutes, duration_type, price_amount, currency_code, price_unit, is_featured, is_active, capacity_per_slot, booking_buffer_minutes, buffer_after_minutes, display_order, category_ids, photo_urls, approval_status)
SELECT @sample_provider_id, s.id, 'Grooming tạo kiểu premium', 'Cắt tỉa tạo kiểu theo giống.', 'Tư vấn kiểu lông, cắt tỉa, sấy tạo phồng và hoàn thiện ngoại hình.', 90, 'MINUTES', 350000.00, 'VND', 'SESSION', TRUE, TRUE, 1, 0, 15, 20, CAST(s.category_id AS CHAR), 'https://images.unsplash.com/photo-1560807707-8cc77767d783?auto=format&fit=crop&q=80&w=800', 'APPROVED' FROM services s WHERE s.service_code = 'SVC-DEMO-GROOMING-STYLE' AND @sample_provider_id IS NOT NULL AND NOT EXISTS (SELECT 1 FROM provider_services ps WHERE ps.provider_id = @sample_provider_id AND ps.service_id = s.id);

INSERT INTO provider_services (provider_id, service_id, custom_name, short_description, description, duration_minutes, duration_type, price_amount, currency_code, price_unit, is_featured, is_active, capacity_per_slot, booking_buffer_minutes, buffer_after_minutes, display_order, category_ids, photo_urls, approval_status)
SELECT @sample_provider_id, s.id, 'Khách sạn chó qua đêm', 'Lưu trú qua đêm có cập nhật ảnh.', 'Phòng sạch, cho ăn theo hướng dẫn, theo dõi và cập nhật cho chủ.', 1440, 'MINUTES', 450000.00, 'VND', 'DAY', FALSE, TRUE, 4, 0, 0, 30, CAST(s.category_id AS CHAR), 'https://images.unsplash.com/photo-1601758174114-e711c0cbaa69?auto=format&fit=crop&q=80&w=800', 'APPROVED' FROM services s WHERE s.service_code = 'SVC-DEMO-HOTEL-DOG' AND @sample_provider_id IS NOT NULL AND NOT EXISTS (SELECT 1 FROM provider_services ps WHERE ps.provider_id = @sample_provider_id AND ps.service_id = s.id);

UPDATE provider_services ps
JOIN services s ON s.id = ps.service_id AND s.service_code = 'SVC-DEMO-HOTEL-DOG'
SET ps.duration_minutes = 480,
    ps.duration_type = 'MINUTES',
    ps.description = 'Lưu trú trong ngày có cập nhật ảnh. Dữ liệu demo dùng 480 phút để tránh TIME vượt 24:00 khi hiển thị dashboard.'
WHERE ps.provider_id = @sample_provider_id
  AND ps.duration_minutes >= 1440
  AND ps.id > 0;

INSERT INTO provider_services (provider_id, service_id, custom_name, short_description, description, duration_minutes, duration_type, price_amount, currency_code, price_unit, is_featured, is_active, capacity_per_slot, booking_buffer_minutes, buffer_after_minutes, display_order, category_ids, photo_urls, approval_status)
SELECT @sample_provider_id, s.id, 'Khám tổng quát tại PetGo', 'Kiểm tra sức khỏe cơ bản.', 'Khám thể trạng, da lông, cân nặng và tư vấn chăm sóc.', 45, 'MINUTES', 250000.00, 'VND', 'SESSION', FALSE, TRUE, 1, 0, 10, 40, CAST(s.category_id AS CHAR), 'https://images.unsplash.com/photo-1628009368231-7bb7cfcb0def?auto=format&fit=crop&q=80&w=800', 'APPROVED' FROM services s WHERE s.service_code = 'SVC-DEMO-VET-CHECKUP' AND @sample_provider_id IS NOT NULL AND NOT EXISTS (SELECT 1 FROM provider_services ps WHERE ps.provider_id = @sample_provider_id AND ps.service_id = s.id);

INSERT INTO provider_services (provider_id, service_id, custom_name, short_description, description, duration_minutes, duration_type, price_amount, currency_code, price_unit, is_featured, is_active, capacity_per_slot, booking_buffer_minutes, buffer_after_minutes, display_order, category_ids, photo_urls, approval_status)
SELECT @sample_provider_id, s.id, 'Buổi huấn luyện lệnh cơ bản', 'Huấn luyện kỹ năng cơ bản cho chó.', 'Dạy ngồi, nằm, gọi tên, đi dây dắt và phản hồi hiệu lệnh.', 75, 'MINUTES', 300000.00, 'VND', 'SESSION', FALSE, TRUE, 1, 0, 15, 50, CAST(s.category_id AS CHAR), 'https://images.unsplash.com/photo-1601758123927-196fefb5b65e?auto=format&fit=crop&q=80&w=800', 'APPROVED' FROM services s WHERE s.service_code = 'SVC-DEMO-TRAINING-BASIC' AND @sample_provider_id IS NOT NULL AND NOT EXISTS (SELECT 1 FROM provider_services ps WHERE ps.provider_id = @sample_provider_id AND ps.service_id = s.id);

INSERT INTO provider_services (provider_id, service_id, custom_name, short_description, description, duration_minutes, duration_type, price_amount, currency_code, price_unit, is_featured, is_active, capacity_per_slot, booking_buffer_minutes, buffer_after_minutes, display_order, category_ids, photo_urls, approval_status)
SELECT @sample_provider_id, s.id, 'Dắt chó đi dạo khu vực Quận 1', 'Đi dạo 60 phút kèm cập nhật.', 'Đưa chó đi dạo, vận động nhẹ và gửi ghi chú sau buổi.', 60, 'MINUTES', 120000.00, 'VND', 'SESSION', FALSE, TRUE, 3, 0, 5, 60, CAST(s.category_id AS CHAR), 'https://images.unsplash.com/photo-1548199973-03cce0bbc87b?auto=format&fit=crop&q=80&w=800', 'APPROVED' FROM services s WHERE s.service_code = 'SVC-DEMO-WALKING' AND @sample_provider_id IS NOT NULL AND NOT EXISTS (SELECT 1 FROM provider_services ps WHERE ps.provider_id = @sample_provider_id AND ps.service_id = s.id);

UPDATE provider_profiles SET price_from_amount = 120000.00, average_rating = 4.86, total_reviews = 42, total_completed_bookings = 186, is_featured = TRUE, is_hot = TRUE, accepts_instant_booking = TRUE, accepts_membership = TRUE, status = 'ACTIVE', verification_status = 'VERIFIED' WHERE id = @sample_provider_id AND id > 0;

INSERT INTO provider_booking_policies (provider_id, allow_user_reschedule, cancel_fee_amount, cancel_fee_applies_after_hours, cancel_fee_type, cancel_window_hours, max_reschedules_per_booking, reschedule_window_hours, timezone)
SELECT @sample_provider_id, TRUE, 50000.00, 12, 'FIXED', 24, 2, 12, 'Asia/Ho_Chi_Minh' WHERE @sample_provider_id IS NOT NULL AND NOT EXISTS (SELECT 1 FROM provider_booking_policies WHERE provider_id = @sample_provider_id);

INSERT INTO provider_availability_slots (provider_id, provider_service_id, slot_date, start_time, end_time, capacity_total, capacity_booked, slot_status, note)
SELECT @sample_provider_id, ps.id, DATE_ADD(CURDATE(), INTERVAL d.day_offset DAY), t.start_time,
       CASE
           WHEN TIME_TO_SEC(t.start_time) + (ps.duration_minutes * 60) >= 86400 THEN '23:59:00'
           ELSE ADDTIME(t.start_time, SEC_TO_TIME(ps.duration_minutes * 60))
       END,
       ps.capacity_per_slot, 0, 'AVAILABLE', 'Seed demo slot khả dụng'
FROM provider_services ps JOIN services s ON s.id = ps.service_id
JOIN (SELECT 1 AS day_offset UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 5 UNION ALL SELECT 7) d
JOIN (SELECT '09:00:00' AS start_time UNION ALL SELECT '14:00:00') t
WHERE ps.provider_id = @sample_provider_id AND s.service_code IN ('SVC-DEMO-BATH-BASIC', 'SVC-DEMO-GROOMING-STYLE', 'SVC-DEMO-VET-CHECKUP', 'SVC-DEMO-WALKING')
  AND NOT EXISTS (SELECT 1 FROM provider_availability_slots existing WHERE existing.provider_id = ps.provider_id AND existing.provider_service_id = ps.id AND existing.slot_date = DATE_ADD(CURDATE(), INTERVAL d.day_offset DAY) AND existing.start_time = t.start_time);

UPDATE provider_availability_slots
SET end_time = '23:59:00',
    note = CONCAT(COALESCE(note, ''), ' | Fixed demo end_time vượt 24h')
WHERE CAST(end_time AS CHAR) >= '24:00:00'
  AND id > 0;

DROP TEMPORARY TABLE IF EXISTS demo_booking_seed;
CREATE TEMPORARY TABLE demo_booking_seed (booking_code VARCHAR(32) PRIMARY KEY, service_code VARCHAR(32) NOT NULL, pet_code VARCHAR(32) NOT NULL, appointment_date DATE NOT NULL, start_time TIME NOT NULL, status VARCHAR(30) NOT NULL, customer_note TEXT, internal_note TEXT, promo_discount DECIMAL(12,2) NOT NULL DEFAULT 0.00, tax_amount DECIMAL(12,2) NOT NULL DEFAULT 0.00, reschedule_count INT NOT NULL DEFAULT 0);

INSERT INTO demo_booking_seed VALUES
('BK-DEMO-PENDING-001', 'SVC-DEMO-BATH-BASIC', 'PET-DEMO-COCO', DATE_ADD(CURDATE(), INTERVAL 1 DAY), '09:00:00', 'PENDING_PROVIDER_CONFIRMATION', 'User vừa đặt lịch, chờ provider xác nhận.', NULL, 0.00, 0.00, 0),
('BK-DEMO-CONFIRM-001', 'SVC-DEMO-GROOMING-STYLE', 'PET-DEMO-COCO', DATE_ADD(CURDATE(), INTERVAL 2 DAY), '14:00:00', 'CONFIRMED', 'Đã thống nhất kiểu cắt teddy.', NULL, 0.00, 0.00, 0),
('BK-DEMO-INPROG-001', 'SVC-DEMO-WALKING', 'PET-DEMO-COCO', CURDATE(), '08:30:00', 'IN_PROGRESS', 'Đang thực hiện dịch vụ đi dạo.', NULL, 0.00, 0.00, 0),
('BK-DEMO-COMPLETED-001', 'SVC-DEMO-VET-CHECKUP', 'PET-DEMO-MILO', DATE_SUB(CURDATE(), INTERVAL 5 DAY), '10:00:00', 'COMPLETED', 'Khám tổng quát cho Milo.', 'Đã giải ngân cho provider sau khi hoàn tất.', 0.00, 0.00, 0),
('BK-DEMO-USERDONE-001', 'SVC-DEMO-BATH-BASIC', 'PET-DEMO-MILO', DATE_SUB(CURDATE(), INTERVAL 2 DAY), '15:00:00', 'COMPLETED_BY_USER', 'User xác nhận xong, chờ provider xác nhận đối soát.', NULL, 0.00, 0.00, 0),
('BK-DEMO-PROVDONE-001', 'SVC-DEMO-GROOMING-STYLE', 'PET-DEMO-COCO', DATE_SUB(CURDATE(), INTERVAL 1 DAY), '11:00:00', 'COMPLETED_BY_PROVIDER', 'Provider báo đã hoàn tất, chờ user xác nhận.', NULL, 0.00, 0.00, 0),
('BK-DEMO-DISPUTE-001', 'SVC-DEMO-HOTEL-DOG', 'PET-DEMO-COCO', DATE_SUB(CURDATE(), INTERVAL 3 DAY), '09:00:00', 'DISPUTED', 'User khiếu nại vì thời gian bàn giao trễ.', '[USER_DISPUTE] Bàn giao trễ và thiếu ảnh cập nhật.', 0.00, 0.00, 0),
('BK-DEMO-CANCEL-001', 'SVC-DEMO-TRAINING-BASIC', 'PET-DEMO-COCO', DATE_ADD(CURDATE(), INTERVAL 4 DAY), '16:00:00', 'CANCELLED', 'User hủy do đổi lịch cá nhân.', 'Đã hoàn tiền demo vào ví user.', 0.00, 0.00, 0),
('BK-DEMO-REJECT-001', 'SVC-DEMO-VET-CHECKUP', 'PET-DEMO-MILO', DATE_ADD(CURDATE(), INTERVAL 6 DAY), '13:00:00', 'REJECTED', 'Provider từ chối do bác sĩ nghỉ.', 'Provider từ chối booking.', 0.00, 0.00, 0),
('BK-DEMO-RESCHED-001', 'SVC-DEMO-BATH-BASIC', 'PET-DEMO-COCO', DATE_ADD(CURDATE(), INTERVAL 8 DAY), '10:00:00', 'CONFIRMED', 'Booking đã đổi lịch một lần.', 'Đã reschedule từ ngày cũ sang ngày mới.', 0.00, 0.00, 1),
('BK-DEMO-PAYPEND-001', 'SVC-DEMO-GROOMING-STYLE', 'PET-DEMO-MILO', DATE_ADD(CURDATE(), INTERVAL 9 DAY), '09:30:00', 'PENDING_PAYMENT', 'Booking checkout ngoài ví đang chờ thanh toán.', NULL, 0.00, 0.00, 0);

INSERT INTO bookings (booking_code, customer_user_id, provider_id, pet_id, provider_service_id, appointment_date, start_time, end_time, timezone, status, customer_note, internal_note, reschedule_count, provider_name_snapshot, provider_phone_snapshot, provider_address_snapshot, service_name_snapshot, service_description_snapshot, service_duration_minutes_snapshot, pet_name_snapshot, pet_breed_snapshot, subtotal_amount, membership_discount_amount, promo_discount_amount, tax_amount, total_amount, currency_code)
SELECT seed.booking_code, @sample_user_id, @sample_provider_id, p.id, ps.id, seed.appointment_date, seed.start_time,
       CASE
           WHEN TIME_TO_SEC(seed.start_time) + (ps.duration_minutes * 60) >= 86400 THEN '23:59:00'
           ELSE ADDTIME(seed.start_time, SEC_TO_TIME(ps.duration_minutes * 60))
       END,
       'Asia/Ho_Chi_Minh', seed.status, seed.customer_note, seed.internal_note, seed.reschedule_count, pp.business_name, pp.emergency_phone, CONCAT_WS(', ', pp.primary_address_line1, pp.ward, pp.district, pp.city, pp.province), COALESCE(ps.custom_name, s.name), LEFT(COALESCE(ps.short_description, ps.description, s.short_description, s.description), 255), ps.duration_minutes, p.name, p.breed, ps.price_amount, 0.00, seed.promo_discount, seed.tax_amount, ps.price_amount - seed.promo_discount + seed.tax_amount, 'VND'
FROM demo_booking_seed seed JOIN pets p ON p.pet_code = seed.pet_code JOIN services s ON s.service_code = seed.service_code JOIN provider_services ps ON ps.service_id = s.id AND ps.provider_id = @sample_provider_id JOIN provider_profiles pp ON pp.id = @sample_provider_id
WHERE @sample_user_id IS NOT NULL AND @sample_provider_id IS NOT NULL AND NOT EXISTS (SELECT 1 FROM bookings b WHERE b.booking_code = seed.booking_code);

UPDATE bookings
SET end_time = '23:59:00',
    internal_note = CONCAT(COALESCE(internal_note, ''), ' | Fixed demo end_time vượt 24h')
WHERE CAST(end_time AS CHAR) >= '24:00:00'
  AND id > 0;

INSERT INTO booking_status_history (booking_id, from_status, to_status, changed_by_user_id, note)
SELECT b.id, NULL, 'PENDING_PROVIDER_CONFIRMATION', @sample_user_id, 'Seed demo: user tạo booking và hệ thống giữ tiền ví.' FROM bookings b WHERE b.booking_code LIKE 'BK-DEMO-%' AND NOT EXISTS (SELECT 1 FROM booking_status_history h WHERE h.booking_id = b.id AND h.from_status IS NULL AND h.to_status = 'PENDING_PROVIDER_CONFIRMATION');

INSERT INTO booking_status_history (booking_id, from_status, to_status, changed_by_user_id, note)
SELECT b.id, 'PENDING_PROVIDER_CONFIRMATION', b.status, @sample_provider_owner_id, CONCAT('Seed demo: chuyển sang trạng thái ', b.status) FROM bookings b WHERE b.booking_code LIKE 'BK-DEMO-%' AND b.status <> 'PENDING_PROVIDER_CONFIRMATION' AND NOT EXISTS (SELECT 1 FROM booking_status_history h WHERE h.booking_id = b.id AND h.to_status = b.status);

INSERT INTO booking_cancellations (booking_id, cancelled_by_user_id, cancelled_at, reason_code, reason_text, refund_amount, refund_status)
SELECT b.id, @sample_user_id, NOW(), 'USER_CHANGED_PLAN', 'Seed demo: user đổi lịch cá nhân.', b.total_amount, 'COMPLETED' FROM bookings b WHERE b.booking_code = 'BK-DEMO-CANCEL-001' AND NOT EXISTS (SELECT 1 FROM booking_cancellations c WHERE c.booking_id = b.id);

INSERT INTO booking_reschedules (booking_id, requested_by_user_id, old_appointment_date, old_start_time, old_end_time, new_appointment_date, new_start_time, new_end_time, fee_amount, note, status)
SELECT b.id, @sample_user_id, DATE_SUB(b.appointment_date, INTERVAL 2 DAY), '09:00:00', '10:00:00', b.appointment_date, b.start_time, b.end_time, 0.00, 'Seed demo: user đổi lịch sang khung giờ mới.', 'APPROVED' FROM bookings b WHERE b.booking_code = 'BK-DEMO-RESCHED-001' AND NOT EXISTS (SELECT 1 FROM booking_reschedules r WHERE r.booking_id = b.id AND r.status = 'APPROVED');

INSERT INTO invoices (invoice_number, user_id, booking_id, invoice_type, status, billing_name, billing_email, billing_phone, billing_address, subtotal_amount, discount_amount, tax_amount, total_amount, currency_code, issued_at, due_at, paid_at, note)
SELECT CONCAT('INV-', b.booking_code), @sample_user_id, b.id, 'BOOKING', CASE WHEN b.status IN ('COMPLETED','COMPLETED_BY_USER','COMPLETED_BY_PROVIDER','CONFIRMED','IN_PROGRESS','DISPUTED') THEN 'PAID' WHEN b.status = 'PENDING_PAYMENT' THEN 'ISSUED' WHEN b.status IN ('CANCELLED','REJECTED') THEN 'VOID' ELSE 'ISSUED' END, u.full_name, u.email, u.phone_number, CONCAT_WS(', ', u.address_line1, u.ward, u.district, u.city, u.province), b.subtotal_amount, b.promo_discount_amount + b.membership_discount_amount, b.tax_amount, b.total_amount, b.currency_code, NOW(), DATE_ADD(NOW(), INTERVAL 24 HOUR), CASE WHEN b.status IN ('COMPLETED','COMPLETED_BY_USER','COMPLETED_BY_PROVIDER','CONFIRMED','IN_PROGRESS','DISPUTED') THEN NOW() ELSE NULL END, CONCAT('Seed demo invoice cho booking ', b.booking_code)
FROM bookings b JOIN users u ON u.id = @sample_user_id WHERE b.booking_code LIKE 'BK-DEMO-%' AND NOT EXISTS (SELECT 1 FROM invoices i WHERE i.invoice_number = CONCAT('INV-', b.booking_code));

INSERT INTO invoice_items (invoice_id, item_type, item_name, description, quantity, unit_price, line_total, sort_order)
SELECT i.id, 'BOOKING_SERVICE', b.service_name_snapshot, CONCAT(DATE_FORMAT(b.appointment_date, '%d/%m/%Y'), ' • ', TIME_FORMAT(b.start_time, '%H:%i'), ' - ', TIME_FORMAT(b.end_time, '%H:%i')), 1, b.subtotal_amount, b.subtotal_amount, 1 FROM invoices i JOIN bookings b ON b.id = i.booking_id WHERE b.booking_code LIKE 'BK-DEMO-%' AND NOT EXISTS (SELECT 1 FROM invoice_items item WHERE item.invoice_id = i.id AND item.item_type = 'BOOKING_SERVICE');

INSERT INTO payments (payment_code, invoice_id, payer_user_id, amount, currency_code, payment_method, gateway_name, gateway_transaction_id, status, paid_at, failure_reason, metadata_json)
SELECT CONCAT('PAY-', b.booking_code), i.id, @sample_user_id, i.total_amount, i.currency_code, CASE b.status WHEN 'PENDING_PAYMENT' THEN 'BANK_TRANSFER' WHEN 'CANCELLED' THEN 'WALLET' WHEN 'REJECTED' THEN 'WALLET' ELSE 'WALLET' END, CASE b.status WHEN 'PENDING_PAYMENT' THEN 'Bank Transfer' ELSE 'PetGo Wallet' END, CONCAT('DEMO-TXN-', b.booking_code), CASE WHEN b.status = 'PENDING_PAYMENT' THEN 'PENDING' WHEN b.status IN ('CANCELLED','REJECTED') THEN 'REFUNDED' ELSE 'SUCCEEDED' END, CASE WHEN b.status = 'PENDING_PAYMENT' THEN NULL ELSE NOW() END, CASE WHEN b.status = 'REJECTED' THEN 'Provider từ chối booking, hoàn tiền ví.' ELSE NULL END, JSON_OBJECT('source', 'seed-demo', 'bookingCode', b.booking_code)
FROM bookings b JOIN invoices i ON i.booking_id = b.id WHERE b.booking_code LIKE 'BK-DEMO-%' AND NOT EXISTS (SELECT 1 FROM payments p WHERE p.payment_code = CONCAT('PAY-', b.booking_code));

INSERT INTO wallet_transactions (transaction_code, wallet_id, user_id, counterparty_user_id, type, status, amount, balance_before, balance_after, gateway_transaction_id, note)
SELECT CONCAT('DEMO-ESCROW-', b.booking_code), wu.id, @sample_user_id, @sample_provider_owner_id, 'BOOKING_ESCROW_HOLD', CASE WHEN b.status IN ('CANCELLED','REJECTED') THEN 'REFUNDED' WHEN b.status = 'COMPLETED' THEN 'RELEASED' ELSE 'HELD_BY_ADMIN' END, b.total_amount, 10000000.00, 10000000.00 - b.total_amount, CONCAT('BOOKING:', b.id), CONCAT('Seed demo escrow cho booking ', b.booking_code)
FROM bookings b JOIN wallets wu ON wu.user_id = @sample_user_id WHERE b.booking_code LIKE 'BK-DEMO-%' AND b.status <> 'PENDING_PAYMENT' AND NOT EXISTS (SELECT 1 FROM wallet_transactions wt WHERE wt.transaction_code = CONCAT('DEMO-ESCROW-', b.booking_code));

INSERT INTO wallet_transactions (transaction_code, wallet_id, user_id, counterparty_user_id, type, status, amount, balance_before, balance_after, gateway_transaction_id, note)
SELECT 'DEMO-SETTLE-BK-COMPLETED-001', wp.id, @sample_provider_owner_id, @sample_user_id, 'BOOKING_ESCROW_RELEASE', 'COMPLETED', b.total_amount, 0.00, b.total_amount, CONCAT('BOOKING:', b.id), 'Seed demo: admin giải ngân tiền booking hoàn thành cho provider.' FROM bookings b JOIN wallets wp ON wp.user_id = @sample_provider_owner_id WHERE b.booking_code = 'BK-DEMO-COMPLETED-001' AND NOT EXISTS (SELECT 1 FROM wallet_transactions WHERE transaction_code = 'DEMO-SETTLE-BK-COMPLETED-001');

INSERT INTO wallet_transactions (transaction_code, wallet_id, user_id, counterparty_user_id, type, status, amount, balance_before, balance_after, gateway_transaction_id, note)
SELECT 'DEMO-REFUND-BK-CANCEL-001', wu.id, @sample_user_id, @sample_provider_owner_id, 'BOOKING_REFUND', 'COMPLETED', b.total_amount, 10000000.00 - b.total_amount, 10000000.00, CONCAT('BOOKING:', b.id), 'Seed demo: hoàn tiền booking đã hủy về ví user.' FROM bookings b JOIN wallets wu ON wu.user_id = @sample_user_id WHERE b.booking_code = 'BK-DEMO-CANCEL-001' AND NOT EXISTS (SELECT 1 FROM wallet_transactions WHERE transaction_code = 'DEMO-REFUND-BK-CANCEL-001');

INSERT INTO wallet_transactions (transaction_code, wallet_id, user_id, type, status, amount, balance_before, balance_after, bank_name, bank_account_number, bank_account_holder, note)
SELECT 'DEMO-WITHDRAW-PROVIDER-PENDING', wp.id, @sample_provider_owner_id, 'WITHDRAW', 'PENDING_ADMIN_APPROVAL', 200000.00, wp.balance, wp.balance - 200000.00, 'VCB', '0123456789', 'PETGO SAMPLE PROVIDER OWNER', 'Seed demo: provider yêu cầu rút tiền chờ admin duyệt.' FROM wallets wp WHERE wp.user_id = @sample_provider_owner_id AND NOT EXISTS (SELECT 1 FROM wallet_transactions WHERE transaction_code = 'DEMO-WITHDRAW-PROVIDER-PENDING');

INSERT INTO wallet_transactions (transaction_code, wallet_id, user_id, type, status, amount, gateway_name, gateway_transaction_id, checkout_url, qr_code_text, note, review_note)
SELECT 'DEMO-TOPUP-USER-FAILED', wu.id, @sample_user_id, 'TOP_UP', 'FAILED', 150000.00, 'PayOS', 'DEMO-PAYOS-FAILED-001', 'https://pay.payos.vn/web/demo-failed', 'DEMO-QR-CODE-TEXT', 'Seed demo: giao dịch nạp ví thất bại để admin xử lý.', 'Mã thanh toán demo đã hết hạn.' FROM wallets wu WHERE wu.user_id = @sample_user_id AND NOT EXISTS (SELECT 1 FROM wallet_transactions WHERE transaction_code = 'DEMO-TOPUP-USER-FAILED');

INSERT INTO reviews (booking_id, customer_user_id, provider_id, rating, comment, status)
SELECT b.id, @sample_user_id, @sample_provider_id, 5, 'Dịch vụ rất tốt, provider chăm sóc bé kỹ và cập nhật rõ ràng.', 'APPROVED' FROM bookings b WHERE b.booking_code = 'BK-DEMO-COMPLETED-001' AND NOT EXISTS (SELECT 1 FROM reviews r WHERE r.booking_id = b.id);

INSERT INTO review_photos (review_id, photo_url, sort_order)
SELECT r.id, 'https://images.unsplash.com/photo-1601758124510-52d02ddb7cbd?auto=format&fit=crop&q=80&w=800', 0 FROM reviews r JOIN bookings b ON b.id = r.booking_id AND b.booking_code = 'BK-DEMO-COMPLETED-001' WHERE NOT EXISTS (SELECT 1 FROM review_photos rp WHERE rp.review_id = r.id AND rp.photo_url LIKE 'https://images.unsplash.com/photo-1601758124510%');

INSERT INTO favorites (user_id, provider_id)
SELECT @sample_user_id, @sample_provider_id WHERE @sample_user_id IS NOT NULL AND @sample_provider_id IS NOT NULL AND NOT EXISTS (SELECT 1 FROM favorites WHERE user_id = @sample_user_id AND provider_id = @sample_provider_id);

SET SQL_SAFE_UPDATES = @old_sql_safe_updates;

SELECT u.email, w.balance, w.currency_code, w.status FROM wallets w JOIN users u ON u.id = w.user_id WHERE u.email IN ('user@petgo.local', 'provider@petgo.local', 'admin@petgo.local') ORDER BY u.email;
SELECT ps.id AS provider_service_id, s.service_code, COALESCE(ps.custom_name, s.name) AS provider_service_name, ps.price_amount, ps.approval_status, ps.is_active FROM provider_services ps JOIN services s ON s.id = ps.service_id WHERE ps.provider_id = @sample_provider_id ORDER BY ps.display_order, ps.id;
SELECT booking_code, status, service_name_snapshot, pet_name_snapshot, appointment_date, start_time, total_amount FROM bookings WHERE booking_code LIKE 'BK-DEMO-%' ORDER BY appointment_date, start_time;
SELECT transaction_code, type, status, amount, note FROM wallet_transactions WHERE transaction_code LIKE 'DEMO-%' ORDER BY id;