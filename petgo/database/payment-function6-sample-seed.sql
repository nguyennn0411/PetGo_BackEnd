-- Sample promo code for Function 6 testing
INSERT INTO promo_codes (
    code,
    target_type,
    discount_type,
    discount_value,
    max_discount_amount,
    min_order_amount,
    usage_limit_total,
    usage_limit_per_user,
    starts_at,
    ends_at,
    is_active
)
SELECT
    'PETGO50',
    'BOOKING',
    'FIXED_AMOUNT',
    50000,
    NULL,
    0,
    1000,
    10,
    NOW(),
    DATE_ADD(NOW(), INTERVAL 365 DAY),
    TRUE
WHERE NOT EXISTS (
    SELECT 1 FROM promo_codes WHERE UPPER(code) = 'PETGO50'
);
