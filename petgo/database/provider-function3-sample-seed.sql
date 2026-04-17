-- Sample seed for function 3: provider detail
-- Assumes provider_profiles, users, service_categories, services, provider_services already exist.

-- 1) Provider gallery images
INSERT INTO provider_photos (provider_id, photo_url, media_type, is_primary, sort_order)
SELECT p.id, 'https://images.unsplash.com/photo-1583337130417-3346a1be7dee?auto=format&fit=crop&q=80&w=800', 'IMAGE', TRUE, 1
FROM provider_profiles p
WHERE p.slug = 'paws-relax-luxury-spa'
  AND NOT EXISTS (
    SELECT 1 FROM provider_photos pp
    WHERE pp.provider_id = p.id
  );

INSERT INTO provider_photos (provider_id, photo_url, media_type, is_primary, sort_order)
SELECT p.id, 'https://images.unsplash.com/photo-1591768793355-74d7ca738055?auto=format&fit=crop&q=80&w=800', 'IMAGE', FALSE, 2
FROM provider_profiles p
WHERE p.slug = 'paws-relax-luxury-spa'
  AND NOT EXISTS (
    SELECT 1 FROM provider_photos pp
    WHERE pp.provider_id = p.id AND pp.sort_order = 2
  );

INSERT INTO provider_photos (provider_id, photo_url, media_type, is_primary, sort_order)
SELECT p.id, 'https://images.unsplash.com/photo-1535268647677-300dbf3d78d1?auto=format&fit=crop&q=80&w=800', 'IMAGE', FALSE, 3
FROM provider_profiles p
WHERE p.slug = 'paws-relax-luxury-spa'
  AND NOT EXISTS (
    SELECT 1 FROM provider_photos pp
    WHERE pp.provider_id = p.id AND pp.sort_order = 3
  );

-- 2) Business hours for all week
INSERT INTO provider_business_hours (provider_id, weekday, opens_at, closes_at, is_closed)
SELECT p.id, x.weekday, x.opens_at, x.closes_at, x.is_closed
FROM provider_profiles p
JOIN (
  SELECT 1 weekday, '09:00:00' opens_at, '19:00:00' closes_at, FALSE is_closed UNION ALL
  SELECT 2, '09:00:00', '19:00:00', FALSE UNION ALL
  SELECT 3, '09:00:00', '19:00:00', FALSE UNION ALL
  SELECT 4, '09:00:00', '19:00:00', FALSE UNION ALL
  SELECT 5, '09:00:00', '19:00:00', FALSE UNION ALL
  SELECT 6, '09:00:00', '17:00:00', FALSE UNION ALL
  SELECT 7, '09:00:00', '17:00:00', FALSE
) x
WHERE p.slug = 'paws-relax-luxury-spa'
  AND NOT EXISTS (
    SELECT 1 FROM provider_business_hours h
    WHERE h.provider_id = p.id AND h.weekday = x.weekday
  );

-- 3) Upcoming slots for next 2 days (requires provider_services)
INSERT INTO provider_availability_slots (provider_id, provider_service_id, slot_date, start_time, end_time, slot_status, capacity_total, capacity_booked, note)
SELECT ps.provider_id, ps.id, CURDATE(), '10:00:00', '10:45:00', 'AVAILABLE', 3, 0, 'Morning slot'
FROM provider_services ps
JOIN provider_profiles p ON p.id = ps.provider_id
WHERE p.slug = 'paws-relax-luxury-spa'
  AND NOT EXISTS (
    SELECT 1 FROM provider_availability_slots s
    WHERE s.provider_id = ps.provider_id
      AND s.provider_service_id = ps.id
      AND s.slot_date = CURDATE()
      AND s.start_time = '10:00:00'
  );

INSERT INTO provider_availability_slots (provider_id, provider_service_id, slot_date, start_time, end_time, slot_status, capacity_total, capacity_booked, note)
SELECT ps.provider_id, ps.id, CURDATE(), '14:00:00', '14:45:00', 'AVAILABLE', 3, 1, 'Afternoon slot'
FROM provider_services ps
JOIN provider_profiles p ON p.id = ps.provider_id
WHERE p.slug = 'paws-relax-luxury-spa'
  AND NOT EXISTS (
    SELECT 1 FROM provider_availability_slots s
    WHERE s.provider_id = ps.provider_id
      AND s.provider_service_id = ps.id
      AND s.slot_date = CURDATE()
      AND s.start_time = '14:00:00'
  );

-- 4) Minimal customers + pets + bookings + reviews for detail page
INSERT INTO users (user_code, email, password_hash, full_name, phone_number, status)
SELECT 'CUS-DEMO-001', 'minhanh@example.com', 'demo', 'Minh Anh', '0900000001', 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'minhanh@example.com');

INSERT INTO users (user_code, email, password_hash, full_name, phone_number, status)
SELECT 'CUS-DEMO-002', 'hoangnam@example.com', 'demo', 'Hoàng Nam', '0900000002', 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'hoangnam@example.com');

INSERT INTO pets (pet_code, owner_user_id, name, species, breed, status)
SELECT 'PET-DEMO-001', u.id, 'Mochi', 'DOG', 'Poodle', 'ACTIVE'
FROM users u
WHERE u.email = 'minhanh@example.com'
  AND NOT EXISTS (SELECT 1 FROM pets WHERE pet_code = 'PET-DEMO-001');

INSERT INTO pets (pet_code, owner_user_id, name, species, breed, status)
SELECT 'PET-DEMO-002', u.id, 'Bơ', 'CAT', 'British Shorthair', 'ACTIVE'
FROM users u
WHERE u.email = 'hoangnam@example.com'
  AND NOT EXISTS (SELECT 1 FROM pets WHERE pet_code = 'PET-DEMO-002');

INSERT INTO bookings (
  booking_code, customer_user_id, provider_id, pet_id, provider_service_id,
  appointment_date, start_time, end_time, timezone, status,
  provider_name_snapshot, provider_phone_snapshot, provider_address_snapshot,
  service_name_snapshot, service_description_snapshot, service_duration_minutes_snapshot,
  pet_name_snapshot, pet_breed_snapshot,
  subtotal_amount, membership_discount_amount, promo_discount_amount, tax_amount, total_amount, currency_code
)
SELECT
  'BOOK-DEMO-001', u.id, p.id, pet.id, ps.id,
  CURDATE() - INTERVAL 2 DAY, '10:00:00', '10:45:00', 'Asia/Ho_Chi_Minh', 'COMPLETED',
  p.business_name, p.emergency_phone, p.primary_address_line1,
  COALESCE(ps.custom_name, s.name), COALESCE(ps.short_description, s.short_description), ps.duration_minutes,
  pet.name, pet.breed,
  ps.price_amount, 0, 0, 0, ps.price_amount, ps.currency_code
FROM users u
JOIN pets pet ON pet.owner_user_id = u.id AND pet.pet_code = 'PET-DEMO-001'
JOIN provider_profiles p ON p.slug = 'paws-relax-luxury-spa'
JOIN provider_services ps ON ps.provider_id = p.id
JOIN services s ON s.id = ps.service_id
WHERE u.email = 'minhanh@example.com'
  AND NOT EXISTS (SELECT 1 FROM bookings WHERE booking_code = 'BOOK-DEMO-001')
LIMIT 1;

INSERT INTO bookings (
  booking_code, customer_user_id, provider_id, pet_id, provider_service_id,
  appointment_date, start_time, end_time, timezone, status,
  provider_name_snapshot, provider_phone_snapshot, provider_address_snapshot,
  service_name_snapshot, service_description_snapshot, service_duration_minutes_snapshot,
  pet_name_snapshot, pet_breed_snapshot,
  subtotal_amount, membership_discount_amount, promo_discount_amount, tax_amount, total_amount, currency_code
)
SELECT
  'BOOK-DEMO-002', u.id, p.id, pet.id, ps.id,
  CURDATE() - INTERVAL 3 DAY, '14:00:00', '14:45:00', 'Asia/Ho_Chi_Minh', 'COMPLETED',
  p.business_name, p.emergency_phone, p.primary_address_line1,
  COALESCE(ps.custom_name, s.name), COALESCE(ps.short_description, s.short_description), ps.duration_minutes,
  pet.name, pet.breed,
  ps.price_amount, 0, 0, 0, ps.price_amount, ps.currency_code
FROM users u
JOIN pets pet ON pet.owner_user_id = u.id AND pet.pet_code = 'PET-DEMO-002'
JOIN provider_profiles p ON p.slug = 'paws-relax-luxury-spa'
JOIN provider_services ps ON ps.provider_id = p.id
JOIN services s ON s.id = ps.service_id
WHERE u.email = 'hoangnam@example.com'
  AND NOT EXISTS (SELECT 1 FROM bookings WHERE booking_code = 'BOOK-DEMO-002')
LIMIT 1;

INSERT INTO reviews (booking_id, customer_user_id, provider_id, rating, comment, status)
SELECT b.id, u.id, p.id, 5, 'Dịch vụ rất tốt, nhân viên nhiệt tình và yêu thương chó mèo. Sẽ quay lại!', 'VISIBLE'
FROM bookings b
JOIN users u ON u.id = b.customer_user_id
JOIN provider_profiles p ON p.id = b.provider_id
WHERE b.booking_code = 'BOOK-DEMO-001'
  AND NOT EXISTS (SELECT 1 FROM reviews r WHERE r.booking_id = b.id);

INSERT INTO reviews (booking_id, customer_user_id, provider_id, rating, comment, status)
SELECT b.id, u.id, p.id, 4, 'Spa sạch sẽ, cắt tỉa rất đẹp. Bé nhà mình rất thích.', 'VISIBLE'
FROM bookings b
JOIN users u ON u.id = b.customer_user_id
JOIN provider_profiles p ON p.id = b.provider_id
WHERE b.booking_code = 'BOOK-DEMO-002'
  AND NOT EXISTS (SELECT 1 FROM reviews r WHERE r.booking_id = b.id);
