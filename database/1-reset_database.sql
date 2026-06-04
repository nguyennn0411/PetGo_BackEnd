-- Reset toàn bộ dữ liệu cũ trong database PetGo.
-- File này chỉ tạo query, KHÔNG tự chạy.
-- CẢNH BÁO: Chạy file này sẽ xóa dữ liệu trong các bảng bên dưới.
-- Sau khi reset, backend sẽ tự seed lại role USER/PROVIDER/ADMIN khi khởi động.
-- Nếu cần tài khoản demo, chạy thêm: PetGo_BackEnd/petgo/database/sample_accounts.sql

USE petgo_db;

SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE notification_recipients;
TRUNCATE TABLE notifications;

TRUNCATE TABLE promo_code_redemptions;
TRUNCATE TABLE promo_codes;

TRUNCATE TABLE payments;
TRUNCATE TABLE invoice_items;
TRUNCATE TABLE invoices;

TRUNCATE TABLE booking_cancellations;
TRUNCATE TABLE booking_reschedules;
TRUNCATE TABLE booking_status_history;
TRUNCATE TABLE booking_locks;
TRUNCATE TABLE bookings;

TRUNCATE TABLE review_photos;
TRUNCATE TABLE reviews;

TRUNCATE TABLE cart_items;
TRUNCATE TABLE shop_order_status_history;
TRUNCATE TABLE shop_order_items;
TRUNCATE TABLE shop_orders;

TRUNCATE TABLE pet_photos;
TRUNCATE TABLE pets;

TRUNCATE TABLE provider_service_change_requests;
TRUNCATE TABLE provider_services;
TRUNCATE TABLE provider_availability_slots;
TRUNCATE TABLE provider_schedule_exceptions;
TRUNCATE TABLE provider_business_hours;
TRUNCATE TABLE provider_booking_policies;
TRUNCATE TABLE provider_photos;
TRUNCATE TABLE provider_profiles;

TRUNCATE TABLE products;
TRUNCATE TABLE product_categories;
TRUNCATE TABLE services;
TRUNCATE TABLE service_categories;
TRUNCATE TABLE home_sliders;

TRUNCATE TABLE membership_subscriptions;
TRUNCATE TABLE membership_plan_features;
TRUNCATE TABLE membership_plans;

TRUNCATE TABLE refresh_tokens;
TRUNCATE TABLE user_roles;
TRUNCATE TABLE users;
TRUNCATE TABLE roles;

SET FOREIGN_KEY_CHECKS = 1;