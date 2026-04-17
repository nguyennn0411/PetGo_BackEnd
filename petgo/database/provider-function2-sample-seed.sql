-- Sample seed for Function 2: Provider list + Search + Filter
-- Run after your base schema.sql

INSERT INTO users (user_code, email, password_hash, full_name, phone_number, status) VALUES
('USR-PROV-001', 'provider1@petgo.local', '$2a$10$demo', 'Paws Relax Owner', '0901000001', 'ACTIVE'),
('USR-PROV-002', 'provider2@petgo.local', '$2a$10$demo', 'Happy Tails Owner', '0901000002', 'ACTIVE'),
('USR-PROV-003', 'provider3@petgo.local', '$2a$10$demo', 'Paradise Resort Owner', '0901000003', 'ACTIVE');

INSERT INTO provider_profiles (
    provider_code, user_id, business_name, slug, provider_type, headline, description, years_experience,
    verification_status, is_featured, is_hot, accepts_instant_booking, accepts_membership, average_rating,
    total_reviews, total_completed_bookings, service_radius_km, cancellation_free_hours, emergency_phone,
    primary_address_line1, district, city, province, country_code, latitude, longitude, main_image_url,
    cover_image_url, price_from_amount, currency_code, status
) VALUES
('PRV-001', 1, 'Paws & Relax Luxury Spa', 'paws-relax-luxury-spa', 'SPA', 'Spa & Grooming cao cấp', 'Không gian spa sạch đẹp cho chó mèo.', 6,
 'VERIFIED', TRUE, TRUE, TRUE, TRUE, 4.90, 156, 420, 8.00, 24, '0909000001', '12 Nguyễn Huệ', 'Quận 1', 'Hồ Chí Minh', 'Hồ Chí Minh', 'VN', 10.7769, 106.7009,
 'https://images.unsplash.com/photo-1516734212186-a967f81ad0d7?auto=format&fit=crop&q=80&w=600',
 'https://images.unsplash.com/photo-1516734212186-a967f81ad0d7?auto=format&fit=crop&q=80&w=1200', 200000, 'VND', 'ACTIVE'),
('PRV-002', 2, 'Happy Tails Veterinary Clinic', 'happy-tails-veterinary-clinic', 'CLINIC', 'Khám tổng quát & tư vấn sức khỏe', 'Phòng khám thú y thân thiện.', 8,
 'VERIFIED', TRUE, FALSE, TRUE, TRUE, 4.80, 92, 310, 10.00, 24, '0909000002', '45 Nguyễn Thị Thập', 'Quận 7', 'Hồ Chí Minh', 'Hồ Chí Minh', 'VN', 10.7342, 106.7218,
 'https://images.unsplash.com/photo-1583337130417-3346a1be7dee?auto=format&fit=crop&q=80&w=600',
 'https://images.unsplash.com/photo-1583337130417-3346a1be7dee?auto=format&fit=crop&q=80&w=1200', 150000, 'VND', 'ACTIVE'),
('PRV-003', 3, 'Pet Paradise Resort', 'pet-paradise-resort', 'BOARDING', 'Khách sạn thú cưng & lưu trú dài ngày', 'Lưu trú và chăm sóc trọn gói.', 5,
 'VERIFIED', FALSE, FALSE, TRUE, TRUE, 4.70, 210, 280, 12.00, 24, '0909000003', '120 Điện Biên Phủ', 'Bình Thạnh', 'Hồ Chí Minh', 'Hồ Chí Minh', 'VN', 10.8036, 106.7131,
 'https://images.unsplash.com/photo-1591768793355-74d7ca738055?auto=format&fit=crop&q=80&w=600',
 'https://images.unsplash.com/photo-1591768793355-74d7ca738055?auto=format&fit=crop&q=80&w=1200', 350000, 'VND', 'ACTIVE');

INSERT INTO provider_business_hours (provider_id, weekday, opens_at, closes_at, is_closed) VALUES
(1,1,'08:00:00','20:00:00',FALSE),(1,2,'08:00:00','20:00:00',FALSE),(1,3,'08:00:00','20:00:00',FALSE),(1,4,'08:00:00','20:00:00',FALSE),(1,5,'08:00:00','20:00:00',FALSE),(1,6,'08:00:00','21:00:00',FALSE),(1,7,'08:00:00','18:00:00',FALSE),
(2,1,'08:00:00','19:00:00',FALSE),(2,2,'08:00:00','19:00:00',FALSE),(2,3,'08:00:00','19:00:00',FALSE),(2,4,'08:00:00','19:00:00',FALSE),(2,5,'08:00:00','19:00:00',FALSE),(2,6,'08:00:00','17:00:00',FALSE),(2,7,NULL,NULL,TRUE),
(3,1,'07:00:00','21:00:00',FALSE),(3,2,'07:00:00','21:00:00',FALSE),(3,3,'07:00:00','21:00:00',FALSE),(3,4,'07:00:00','21:00:00',FALSE),(3,5,'07:00:00','21:00:00',FALSE),(3,6,'07:00:00','21:00:00',FALSE),(3,7,'07:00:00','21:00:00',FALSE);

INSERT INTO provider_services (provider_id, service_id, custom_name, short_description, description, duration_minutes, price_amount, currency_code, price_unit, is_featured, is_active, capacity_per_slot, booking_buffer_minutes, display_order)
SELECT 1, s.id, 'Tắm & Cắt tỉa', 'Grooming trọn gói', 'Tắm, sấy, vệ sinh tai, cắt tỉa tạo kiểu.', 90, 200000, 'VND', 'PER_SESSION', TRUE, TRUE, 1, 0, 1 FROM services s WHERE s.slug = 'groom-style';

INSERT INTO provider_services (provider_id, service_id, custom_name, short_description, description, duration_minutes, price_amount, currency_code, price_unit, is_featured, is_active, capacity_per_slot, booking_buffer_minutes, display_order)
SELECT 1, s.id, 'Spa thư giãn', 'Tắm thư giãn cho thú cưng', 'Gói spa cơ bản.', 45, 220000, 'VND', 'PER_SESSION', FALSE, TRUE, 1, 0, 2 FROM services s WHERE s.slug = 'relax-bath';

INSERT INTO provider_services (provider_id, service_id, custom_name, short_description, description, duration_minutes, price_amount, currency_code, price_unit, is_featured, is_active, capacity_per_slot, booking_buffer_minutes, display_order)
SELECT 2, s.id, 'Khám tổng quát', 'Checkup cơ bản', 'Khám sức khỏe tổng quát.', 30, 150000, 'VND', 'PER_VISIT', TRUE, TRUE, 1, 0, 1 FROM services s WHERE s.slug = 'general-checkup';


INSERT INTO services (service_code, category_id, name, slug, short_description, description, default_duration_minutes, base_price_amount, currency_code, price_unit)
SELECT 'SVC-BOARD-STAY', id, 'Lưu trú theo ngày', 'boarding-stay', 'Khách sạn thú cưng theo ngày', 'Dịch vụ lưu trú có giám sát.', 1440, 350000, 'VND', 'PER_DAY' FROM service_categories WHERE slug='boarding';

INSERT INTO provider_services (provider_id, service_id, custom_name, short_description, description, duration_minutes, price_amount, currency_code, price_unit, is_featured, is_active, capacity_per_slot, booking_buffer_minutes, display_order)
SELECT 3, s.id, 'Khách sạn thú cưng', 'Lưu trú theo ngày', 'Lưu trú sạch sẽ và có camera.', 1440, 350000, 'VND', 'PER_DAY', TRUE, TRUE, 4, 0, 1 FROM services s WHERE s.slug = 'boarding-stay';

INSERT INTO provider_availability_slots (provider_id, provider_service_id, slot_date, start_time, end_time, slot_status, capacity_total, capacity_booked, note)
SELECT ps.provider_id, ps.id, CURDATE(), '10:00:00', '11:30:00', 'AVAILABLE', 1, 0, NULL
FROM provider_services ps WHERE ps.provider_id = 1 AND ps.custom_name = 'Tắm & Cắt tỉa';

INSERT INTO provider_availability_slots (provider_id, provider_service_id, slot_date, start_time, end_time, slot_status, capacity_total, capacity_booked, note)
SELECT ps.provider_id, ps.id, CURDATE(), '14:00:00', '15:30:00', 'AVAILABLE', 1, 0, NULL
FROM provider_services ps WHERE ps.provider_id = 1 AND ps.custom_name = 'Tắm & Cắt tỉa';

INSERT INTO provider_availability_slots (provider_id, provider_service_id, slot_date, start_time, end_time, slot_status, capacity_total, capacity_booked, note)
SELECT ps.provider_id, ps.id, DATE_ADD(CURDATE(), INTERVAL 1 DAY), '09:00:00', '09:45:00', 'AVAILABLE', 1, 0, NULL
FROM provider_services ps WHERE ps.provider_id = 1 AND ps.custom_name = 'Spa thư giãn';

INSERT INTO provider_availability_slots (provider_id, provider_service_id, slot_date, start_time, end_time, slot_status, capacity_total, capacity_booked, note)
SELECT ps.provider_id, ps.id, CURDATE(), '09:00:00', '09:30:00', 'AVAILABLE', 1, 0, NULL
FROM provider_services ps WHERE ps.provider_id = 2 AND ps.custom_name = 'Khám tổng quát';

INSERT INTO provider_availability_slots (provider_id, provider_service_id, slot_date, start_time, end_time, slot_status, capacity_total, capacity_booked, note)
SELECT ps.provider_id, ps.id, CURDATE(), '11:00:00', '11:30:00', 'AVAILABLE', 1, 0, NULL
FROM provider_services ps WHERE ps.provider_id = 2 AND ps.custom_name = 'Khám tổng quát';

INSERT INTO provider_availability_slots (provider_id, provider_service_id, slot_date, start_time, end_time, slot_status, capacity_total, capacity_booked, note)
SELECT ps.provider_id, ps.id, CURDATE(), '16:00:00', '16:30:00', 'AVAILABLE', 1, 0, NULL
FROM provider_services ps WHERE ps.provider_id = 2 AND ps.custom_name = 'Khám tổng quát';

INSERT INTO provider_availability_slots (provider_id, provider_service_id, slot_date, start_time, end_time, slot_status, capacity_total, capacity_booked, note)
SELECT ps.provider_id, ps.id, DATE_ADD(CURDATE(), INTERVAL 1 DAY), '08:00:00', '09:00:00', 'AVAILABLE', 4, 1, NULL
FROM provider_services ps WHERE ps.provider_id = 3 AND ps.custom_name = 'Khách sạn thú cưng';

INSERT INTO provider_availability_slots (provider_id, provider_service_id, slot_date, start_time, end_time, slot_status, capacity_total, capacity_booked, note)
SELECT ps.provider_id, ps.id, DATE_ADD(CURDATE(), INTERVAL 2 DAY), '13:00:00', '14:00:00', 'AVAILABLE', 4, 0, NULL
FROM provider_services ps WHERE ps.provider_id = 3 AND ps.custom_name = 'Khách sạn thú cưng';
