-- Reset toàn bộ dữ liệu cũ trong database PetGo.
-- File này chỉ tạo query, KHÔNG tự chạy.
-- CẢNH BÁO: Chạy file này sẽ xóa dữ liệu trong các bảng bên dưới.
-- Sau khi reset, chạy lại PetGo_BackEnd/database/2-base_roles.sql để seed role USER/ADMIN.
-- Nếu cần tài khoản demo, chạy tiếp 4-sample_accounts.sql (tạo user@petgo.local + admin@petgo.local).
-- Sau khi có user, chạy PetGo_BackEnd/database/5-base_wallets.sql để tạo ví và cấu hình ví mặc định.
-- Danh sách TRUNCATE bên dưới đã được đối chiếu với entity backend và các file SQL seed/sample hiện có.

USE petgo_db;

SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE notification_recipients;
TRUNCATE TABLE notifications;

TRUNCATE TABLE promo_code_redemptions;
TRUNCATE TABLE promo_codes;

TRUNCATE TABLE payments;
TRUNCATE TABLE invoice_items;
TRUNCATE TABLE invoices;

-- Shop/cart tables hiện chưa có dữ liệu seed, nhưng vẫn reset để dọn dữ liệu phát sinh khi test.
TRUNCATE TABLE cart_items;
TRUNCATE TABLE shop_order_status_history;
TRUNCATE TABLE shop_order_items;
TRUNCATE TABLE shop_orders;

TRUNCATE TABLE pet_photos;
TRUNCATE TABLE pets;

TRUNCATE TABLE products;
TRUNCATE TABLE product_categories;
TRUNCATE TABLE service_category_mapping;
TRUNCATE TABLE services;
TRUNCATE TABLE service_categories;
-- Bảng home_sliders hiện chưa có dữ liệu seed, nhưng là bảng nội dung trang chủ nên vẫn reset nếu đã test thủ công.
TRUNCATE TABLE home_sliders;

-- Membership tables hiện chưa có dữ liệu seed, nhưng vẫn reset để đồng bộ schema hiện có.
TRUNCATE TABLE membership_subscriptions;
TRUNCATE TABLE membership_plan_features;
TRUNCATE TABLE membership_plans;

TRUNCATE TABLE wallet_transactions;
TRUNCATE TABLE wallets;
TRUNCATE TABLE wallet_settings;

TRUNCATE TABLE refresh_tokens;
TRUNCATE TABLE user_roles;
TRUNCATE TABLE users;
TRUNCATE TABLE roles;

TRUNCATE TABLE messages;
TRUNCATE TABLE conversations;
TRUNCATE TABLE booking_status_histories;
TRUNCATE TABLE shipping_fee_configs;
TRUNCATE TABLE area_schedule_overrides;
TRUNCATE TABLE area_schedules;
TRUNCATE TABLE area_service_configs;
TRUNCATE TABLE booking_disputes;
TRUNCATE TABLE shipping_bookings;
TRUNCATE TABLE areas;

-- Xóa column cũ category_id (single FK) nếu còn sót từ schema cũ trước khi chuyển sang ManyToMany
-- ALTER TABLE services DROP FOREIGN KEY IF EXISTS fk_services_category;
-- ALTER TABLE services DROP COLUMN IF EXISTS category_id;

SET FOREIGN_KEY_CHECKS = 1;