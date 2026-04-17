-- Function 8 sample seed: Favorites + Reviews
-- Assumes at least 1 customer user, 1 provider, and 1 completed booking already exist.

INSERT INTO favorites (user_id, provider_id)
SELECT u.id, p.id
FROM users u
JOIN provider_profiles p ON p.status = 'ACTIVE' AND p.deleted_at IS NULL
WHERE u.deleted_at IS NULL
ORDER BY u.id ASC, p.id ASC
LIMIT 1
ON DUPLICATE KEY UPDATE provider_id = provider_id;

INSERT INTO reviews (booking_id, customer_user_id, provider_id, rating, comment, status)
SELECT b.id, b.customer_user_id, b.provider_id, 5, 'Dịch vụ rất tốt, thú cưng được chăm sóc kỹ.', 'VISIBLE'
FROM bookings b
LEFT JOIN reviews r ON r.booking_id = b.id AND r.deleted_at IS NULL
WHERE b.status = 'COMPLETED'
  AND r.id IS NULL
ORDER BY b.id ASC
LIMIT 1;
