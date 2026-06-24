-- Query tạo loạt mã giảm giá mẫu do admin tạo cho PetGo.
-- Chạy thủ công sau các file seed account, tối thiểu cần có admin@petgo.local từ 4-sample_accounts.sql.
-- File idempotent theo code cố định: chạy nhiều lần không tạo trùng mã.
-- Quy ước demo: mã giảm nhiều nhất tối đa 50.000đ; đa số mã giảm 10.000đ.

USE petgo_db;

SET @old_sql_safe_updates = @@SQL_SAFE_UPDATES;
SET SQL_SAFE_UPDATES = 0;

SET @sample_admin_id = (SELECT id FROM users WHERE email = 'admin@petgo.local' LIMIT 1);
SET @sample_grooming_category_id = (
    SELECT id
    FROM service_categories
    WHERE name IN ('Chăm sóc & spa thú cưng', 'Tắm gội & vệ sinh cơ bản', 'Tắm sấy chó mèo')
    ORDER BY FIELD(name, 'Tắm sấy chó mèo', 'Tắm gội & vệ sinh cơ bản', 'Chăm sóc & spa thú cưng')
    LIMIT 1
);

-- Mã phổ thông: giảm cố định 10.000đ cho booking từ 0đ.
INSERT INTO promo_codes (
    code, name, description, owner_type, created_by_user_id,
    promotion_type, target_type, discount_type, discount_value, max_discount_amount, min_order_amount,
    usage_limit_total, usage_limit_per_user, usage_count, is_stackable, is_auto_apply, priority,
    user_segment, min_completed_bookings, applicable_days_of_week, area_ids,
    service_category_ids, membership_plan_ids, badge_text, landing_page_url, terms_and_conditions,
    internal_note, starts_at, ends_at, is_active
)
SELECT 'PETGO10K', 'Giảm 10.000đ mọi booking',
       'Mã demo admin: giảm cố định 10.000đ cho đơn booking dịch vụ từ 0đ.',
       'ADMIN', @sample_admin_id, 'PROMO_CODE', 'BOOKING', 'FIXED_AMOUNT', 10000.00, 10000.00, 0.00,
       500, 1, 0, FALSE, FALSE, 10,
       'ALL', NULL, NULL, NULL, NULL, NULL, 'Giảm 10K', '/booking',
       'Áp dụng cho booking dịch vụ hợp lệ. Không quy đổi thành tiền mặt.',
       'Seed demo admin promotion.', NOW(), DATE_ADD(NOW(), INTERVAL 90 DAY), TRUE
WHERE @sample_admin_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM promo_codes WHERE code = 'PETGO10K');

-- Mã phần trăm: giảm 20%, tối đa 20.000đ, đơn tối thiểu 0đ.
INSERT INTO promo_codes (
    code, name, description, owner_type, created_by_user_id,
    promotion_type, target_type, discount_type, discount_value, max_discount_amount, min_order_amount,
    usage_limit_total, usage_limit_per_user, usage_count, is_stackable, is_auto_apply, priority,
    user_segment, min_completed_bookings, applicable_days_of_week, area_ids,
    service_category_ids, membership_plan_ids, badge_text, landing_page_url, terms_and_conditions,
    internal_note, starts_at, ends_at, is_active
)
SELECT 'PETGO20MAX20K', 'Giảm 20% tối đa 20.000đ',
       'Mã demo admin: giảm 20% cho booking, số tiền giảm tối đa 20.000đ, đơn từ 0đ.',
       'ADMIN', @sample_admin_id, 'PROMO_CODE', 'BOOKING', 'PERCENTAGE', 20.00, 20000.00, 0.00,
       300, 1, 0, FALSE, FALSE, 20,
       'ALL', NULL, NULL, NULL, NULL, NULL, 'Giảm 20%', '/booking',
       'Áp dụng cho booking dịch vụ hợp lệ. Mức giảm tối đa 20.000đ.',
       'Seed demo admin promotion.', NOW(), DATE_ADD(NOW(), INTERVAL 90 DAY), TRUE
WHERE @sample_admin_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM promo_codes WHERE code = 'PETGO20MAX20K');

-- Mã giảm mạnh nhất demo: tối đa 50.000đ.
INSERT INTO promo_codes (
    code, name, description, owner_type, created_by_user_id,
    promotion_type, target_type, discount_type, discount_value, max_discount_amount, min_order_amount,
    usage_limit_total, usage_limit_per_user, usage_count, is_stackable, is_auto_apply, priority,
    user_segment, min_completed_bookings, applicable_days_of_week, area_ids,
    service_category_ids, membership_plan_ids, badge_text, landing_page_url, terms_and_conditions,
    internal_note, starts_at, ends_at, is_active
)
SELECT 'PETGO50K', 'Giảm 50.000đ đơn từ 300.000đ',
       'Mã demo admin giảm cao nhất: giảm cố định 50.000đ cho booking có giá trị tối thiểu 300.000đ.',
       'ADMIN', @sample_admin_id, 'SEASONAL', 'BOOKING', 'FIXED_AMOUNT', 50000.00, 50000.00, 300000.00,
       100, 1, 0, FALSE, FALSE, 30,
       'ALL', NULL, NULL, NULL, NULL, NULL, 'Giảm 50K', '/search',
       'Áp dụng cho booking dịch vụ từ 300.000đ. Đây là mã giảm cao nhất trong bộ demo.',
       'Seed demo admin promotion. Max discount policy: 50.000đ.', NOW(), DATE_ADD(NOW(), INTERVAL 60 DAY), TRUE
WHERE @sample_admin_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM promo_codes WHERE code = 'PETGO50K');

-- Mã cho khách mới: giảm 10.000đ, giới hạn theo user segment.
INSERT INTO promo_codes (
    code, name, description, owner_type, created_by_user_id,
    promotion_type, target_type, discount_type, discount_value, max_discount_amount, min_order_amount,
    usage_limit_total, usage_limit_per_user, usage_count, is_stackable, is_auto_apply, priority,
    user_segment, min_completed_bookings, applicable_days_of_week, area_ids,
    service_category_ids, membership_plan_ids, badge_text, landing_page_url, terms_and_conditions,
    internal_note, starts_at, ends_at, is_active
)
SELECT 'NEWUSER10K', 'Khách mới giảm 10.000đ',
       'Mã demo admin dành cho khách mới, giảm 10.000đ cho booking từ 0đ.',
       'ADMIN', @sample_admin_id, 'FIRST_BOOKING', 'BOOKING', 'FIXED_AMOUNT', 10000.00, 10000.00, 0.00,
       300, 1, 0, FALSE, FALSE, 15,
       'NEW_USER', NULL, NULL, NULL, NULL, NULL, 'Khách mới', '/booking',
       'Chỉ áp dụng cho khách mới theo rule backend.',
       'Seed demo admin promotion for NEW_USER.', NOW(), DATE_ADD(NOW(), INTERVAL 90 DAY), TRUE
WHERE @sample_admin_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM promo_codes WHERE code = 'NEWUSER10K');

-- Mã cho khách quay lại: giảm 10.000đ, yêu cầu ít nhất 1 booking hoàn thành.
INSERT INTO promo_codes (
    code, name, description, owner_type, created_by_user_id,
    promotion_type, target_type, discount_type, discount_value, max_discount_amount, min_order_amount,
    usage_limit_total, usage_limit_per_user, usage_count, is_stackable, is_auto_apply, priority,
    user_segment, min_completed_bookings, applicable_days_of_week, area_ids,
    service_category_ids, membership_plan_ids, badge_text, landing_page_url, terms_and_conditions,
    internal_note, starts_at, ends_at, is_active
)
SELECT 'LOYAL10K', 'Khách quay lại giảm 10.000đ',
       'Mã demo admin cho khách đã có booking hoàn thành, giảm 10.000đ.',
       'ADMIN', @sample_admin_id, 'LOYALTY', 'BOOKING', 'FIXED_AMOUNT', 10000.00, 10000.00, 0.00,
       300, 1, 0, FALSE, FALSE, 15,
       'RETURNING_USER', 1, NULL, NULL, NULL, NULL, 'Khách thân thiết', '/booking',
       'Áp dụng cho khách có ít nhất 1 booking hoàn thành.',
       'Seed demo admin promotion for RETURNING_USER.', NOW(), DATE_ADD(NOW(), INTERVAL 90 DAY), TRUE
WHERE @sample_admin_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM promo_codes WHERE code = 'LOYAL10K');

-- Mã cuối tuần: giảm 10.000đ, chỉ áp dụng thứ bảy/chủ nhật.
INSERT INTO promo_codes (
    code, name, description, owner_type, created_by_user_id,
    promotion_type, target_type, discount_type, discount_value, max_discount_amount, min_order_amount,
    usage_limit_total, usage_limit_per_user, usage_count, is_stackable, is_auto_apply, priority,
    user_segment, min_completed_bookings, applicable_days_of_week, area_ids,
    service_category_ids, membership_plan_ids, badge_text, landing_page_url, terms_and_conditions,
    internal_note, starts_at, ends_at, is_active
)
SELECT 'WEEKEND10K', 'Cuối tuần giảm 10.000đ',
       'Mã demo admin: giảm 10.000đ cho booking vào thứ bảy hoặc chủ nhật.',
       'ADMIN', @sample_admin_id, 'SEASONAL', 'BOOKING', 'FIXED_AMOUNT', 10000.00, 10000.00, 0.00,
       300, 1, 0, FALSE, FALSE, 12,
       'ALL', NULL, 'SATURDAY,SUNDAY', NULL, NULL, NULL, 'Cuối tuần', '/search',
       'Chỉ áp dụng cho lịch hẹn rơi vào thứ bảy hoặc chủ nhật.',
       'Seed demo admin weekend promotion.', NOW(), DATE_ADD(NOW(), INTERVAL 90 DAY), TRUE
WHERE @sample_admin_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM promo_codes WHERE code = 'WEEKEND10K');

-- Mã giới hạn nhóm dịch vụ grooming/spa: giảm 10.000đ.
INSERT INTO promo_codes (
    code, name, description, owner_type, created_by_user_id,
    promotion_type, target_type, discount_type, discount_value, max_discount_amount, min_order_amount,
    usage_limit_total, usage_limit_per_user, usage_count, is_stackable, is_auto_apply, priority,
    user_segment, min_completed_bookings, applicable_days_of_week, area_ids,
    service_category_ids, membership_plan_ids, badge_text, landing_page_url, terms_and_conditions,
    internal_note, starts_at, ends_at, is_active
)
SELECT 'SPA10K', 'Spa thú cưng giảm 10.000đ',
       'Mã demo admin giới hạn cho nhóm dịch vụ spa/grooming.',
       'ADMIN', @sample_admin_id, 'PROMO_CODE', 'BOOKING', 'FIXED_AMOUNT', 10000.00, 10000.00, 0.00,
       300, 1, 0, FALSE, FALSE, 12,
       'ALL', NULL, NULL, NULL, CAST(@sample_grooming_category_id AS CHAR), NULL, 'Spa deal', '/search',
       'Áp dụng cho nhóm dịch vụ spa/grooming nếu category tồn tại.',
       'Seed demo admin category-scoped promotion.', NOW(), DATE_ADD(NOW(), INTERVAL 90 DAY), TRUE
WHERE @sample_admin_id IS NOT NULL
  AND @sample_grooming_category_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM promo_codes WHERE code = 'SPA10K');

-- Mã membership: giảm 10.000đ khi mua gói hội viên.
INSERT INTO promo_codes (
    code, name, description, owner_type, created_by_user_id,
    promotion_type, target_type, discount_type, discount_value, max_discount_amount, min_order_amount,
    usage_limit_total, usage_limit_per_user, usage_count, is_stackable, is_auto_apply, priority,
    user_segment, min_completed_bookings, applicable_days_of_week, area_ids,
    service_category_ids, membership_plan_ids, badge_text, landing_page_url, terms_and_conditions,
    internal_note, starts_at, ends_at, is_active
)
SELECT 'MEMBER10K', 'Membership giảm 10.000đ',
       'Mã demo admin giảm 10.000đ khi checkout membership.',
       'ADMIN', @sample_admin_id, 'MEMBERSHIP', 'MEMBERSHIP', 'FIXED_AMOUNT', 10000.00, 10000.00, 0.00,
       200, 1, 0, FALSE, FALSE, 12,
       'ALL', NULL, NULL, NULL, NULL, NULL, 'Hội viên', '/membership',
       'Áp dụng cho checkout membership hợp lệ.',
       'Seed demo admin membership promotion.', NOW(), DATE_ADD(NOW(), INTERVAL 90 DAY), TRUE
WHERE @sample_admin_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM promo_codes WHERE code = 'MEMBER10K');

SET SQL_SAFE_UPDATES = @old_sql_safe_updates;

SELECT
    code,
    name,
    owner_type,
    target_type,
    discount_type,
    discount_value,
    max_discount_amount,
    min_order_amount,
    user_segment,
    usage_limit_total,
    usage_limit_per_user,
    is_active
FROM promo_codes
WHERE code IN (
    'PETGO10K', 'PETGO20MAX20K', 'PETGO50K', 'NEWUSER10K', 'LOYAL10K',
    'WEEKEND10K', 'SPA10K', 'MEMBER10K'
)
ORDER BY discount_value, code;