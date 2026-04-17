-- Function 7 sample seed
-- Optional examples for booking management screens

-- Mark one paid booking as CONFIRMED
UPDATE bookings
SET status = 'CONFIRMED'
WHERE id = 1;

-- Optional completed example
UPDATE bookings
SET status = 'COMPLETED'
WHERE id = 2;

-- Status history examples
INSERT INTO booking_status_history (booking_id, from_status, to_status, changed_by_user_id, note)
SELECT id, 'PENDING_PAYMENT', 'PENDING_CONFIRMATION', customer_user_id, 'Thanh toán thành công'
FROM bookings
WHERE id = 1
  AND NOT EXISTS (SELECT 1 FROM booking_status_history h WHERE h.booking_id = bookings.id AND h.to_status = 'PENDING_CONFIRMATION');

INSERT INTO booking_status_history (booking_id, from_status, to_status, changed_by_user_id, note)
SELECT id, 'PENDING_CONFIRMATION', 'CONFIRMED', customer_user_id, 'Provider đã xác nhận lịch hẹn'
FROM bookings
WHERE id = 1
  AND NOT EXISTS (SELECT 1 FROM booking_status_history h WHERE h.booking_id = bookings.id AND h.to_status = 'CONFIRMED');
