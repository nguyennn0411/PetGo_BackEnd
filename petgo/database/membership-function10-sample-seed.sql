-- Optional sample seed for function 10 (Membership thật)
-- Assumes roles/plans from main schema already exist.

-- Sample promo dedicated to membership
INSERT INTO promo_codes (code, target_type, discount_type, discount_value, max_discount_amount, min_order_amount, usage_limit_total, usage_limit_per_user, starts_at, ends_at, is_active)
SELECT 'MEMPRO10', 'MEMBERSHIP', 'PERCENTAGE', 10, 50000, 0, 10000, 1, NOW(), DATE_ADD(NOW(), INTERVAL 365 DAY), TRUE
WHERE NOT EXISTS (
    SELECT 1 FROM promo_codes WHERE code = 'MEMPRO10'
);
