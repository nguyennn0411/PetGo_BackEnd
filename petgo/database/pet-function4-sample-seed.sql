-- Function 4 sample seed for Pet CRUD
-- Lưu ý: chạy sau khi đã có user thật trong bảng users.

-- Ví dụ tạo thú cưng cho user id = 1
INSERT INTO pets (
    pet_code, owner_user_id, name, species, breed, gender, age_label, weight_kg,
    size, avatar_url, health_notes, allergy_notes, behavior_notes, vaccination_notes,
    status, created_at, updated_at
) VALUES
('PET-DEMO-001', 1, 'Mochi', 'DOG', 'Golden Retriever', 'MALE', '2 tuổi', 18.50,
 'L', 'https://images.unsplash.com/photo-1552053831-71594a27632d?auto=format&fit=crop&q=80&w=800',
 'Đã tiêm phòng đầy đủ', 'Không có', 'Thân thiện, năng động', 'Mũi 7in1 đã hoàn tất',
 'ACTIVE', NOW(), NOW()),
('PET-DEMO-002', 1, 'Luna', 'CAT', 'British Shorthair', 'FEMALE', '1 tuổi', 4.20,
 'S', 'https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?auto=format&fit=crop&q=80&w=800',
 'Sức khỏe tốt', 'Nhạy cảm với hải sản', 'Hiền, hơi nhút nhát', 'Đã tiêm nhắc lại định kỳ',
 'ACTIVE', NOW(), NOW());

INSERT INTO pet_photos (pet_id, photo_url, is_primary, sort_order, created_at, updated_at)
SELECT id, avatar_url, TRUE, 0, NOW(), NOW() FROM pets WHERE pet_code IN ('PET-DEMO-001', 'PET-DEMO-002');
