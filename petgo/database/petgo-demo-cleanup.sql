-- Cleanup only demo data inserted by petgo-full-demo-seed.sql
-- Run this if you want to reset demo rows without dropping the whole schema.

SET FOREIGN_KEY_CHECKS = 0;

DELETE rp FROM review_photos rp
JOIN reviews r ON r.id = rp.review_id
JOIN bookings b ON b.id = r.booking_id
WHERE b.booking_code IN ('BOOK-DEMO-001','BOOK-DEMO-002','BOOK-DEMO-003','BOOK-DEMO-004');

DELETE r FROM reviews r
JOIN bookings b ON b.id = r.booking_id
WHERE b.booking_code IN ('BOOK-DEMO-001','BOOK-DEMO-002','BOOK-DEMO-003','BOOK-DEMO-004');

DELETE FROM favorites
WHERE user_id IN (SELECT id FROM users WHERE email IN ('customer1@petgo.local','customer2@petgo.local'));

DELETE ii FROM invoice_items ii
JOIN invoices i ON i.id = ii.invoice_id
WHERE i.invoice_number IN ('INV-BOOK-001','INV-MEM-001');

DELETE FROM payments WHERE payment_code IN ('PAY-BOOK-001','PAY-MEM-001');
DELETE FROM invoices WHERE invoice_number IN ('INV-BOOK-001','INV-MEM-001');
DELETE FROM booking_reschedules WHERE booking_id IN (SELECT id FROM bookings WHERE booking_code IN ('BOOK-DEMO-001','BOOK-DEMO-002','BOOK-DEMO-003','BOOK-DEMO-004'));
DELETE FROM booking_cancellations WHERE booking_id IN (SELECT id FROM bookings WHERE booking_code IN ('BOOK-DEMO-001','BOOK-DEMO-002','BOOK-DEMO-003','BOOK-DEMO-004'));
DELETE FROM booking_status_history WHERE booking_id IN (SELECT id FROM bookings WHERE booking_code IN ('BOOK-DEMO-001','BOOK-DEMO-002','BOOK-DEMO-003','BOOK-DEMO-004'));
DELETE FROM promo_code_redemptions WHERE booking_id IN (SELECT id FROM bookings WHERE booking_code IN ('BOOK-DEMO-001','BOOK-DEMO-002','BOOK-DEMO-003','BOOK-DEMO-004'));
DELETE FROM bookings WHERE booking_code IN ('BOOK-DEMO-001','BOOK-DEMO-002','BOOK-DEMO-003','BOOK-DEMO-004');

DELETE FROM notifications WHERE user_id IN (SELECT id FROM users WHERE email IN ('customer1@petgo.local','customer2@petgo.local'));
DELETE FROM user_notification_settings WHERE user_id IN (SELECT id FROM users WHERE email IN ('customer1@petgo.local','customer2@petgo.local'));
DELETE FROM membership_subscriptions WHERE subscription_code = 'SUB-DEMO-001';
DELETE FROM promo_codes WHERE code IN ('PETGO50','PETGO20','MEMPRO10');
DELETE FROM pet_photos WHERE pet_id IN (SELECT id FROM pets WHERE pet_code IN ('PET-DEMO-001','PET-DEMO-002','PET-DEMO-003'));
DELETE FROM pets WHERE pet_code IN ('PET-DEMO-001','PET-DEMO-002','PET-DEMO-003');
DELETE FROM provider_availability_slots WHERE provider_id IN (SELECT id FROM provider_profiles WHERE provider_code IN ('PRV-001','PRV-002','PRV-003','PRV-004'));
DELETE FROM provider_services WHERE provider_id IN (SELECT id FROM provider_profiles WHERE provider_code IN ('PRV-001','PRV-002','PRV-003','PRV-004'));
DELETE FROM provider_business_hours WHERE provider_id IN (SELECT id FROM provider_profiles WHERE provider_code IN ('PRV-001','PRV-002','PRV-003','PRV-004'));
DELETE FROM provider_photos WHERE provider_id IN (SELECT id FROM provider_profiles WHERE provider_code IN ('PRV-001','PRV-002','PRV-003','PRV-004'));
DELETE FROM provider_profiles WHERE provider_code IN ('PRV-001','PRV-002','PRV-003','PRV-004');
DELETE FROM services WHERE service_code IN ('SVC-BOARD-STAY','SVC-WALK-60','SVC-TRAIN-BASIC');
DELETE FROM refresh_tokens WHERE user_id IN (SELECT id FROM users WHERE email LIKE '%@petgo.local');
DELETE FROM user_roles WHERE user_id IN (SELECT id FROM users WHERE email LIKE '%@petgo.local');
DELETE FROM users WHERE email IN (
  'customer1@petgo.local','customer2@petgo.local',
  'provider1@petgo.local','provider2@petgo.local','provider3@petgo.local','provider4@petgo.local',
  'admin@petgo.local'
);

SET FOREIGN_KEY_CHECKS = 1;
