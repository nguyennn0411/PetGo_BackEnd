-- ==========================================================
-- PetGo full product + service data seed
-- Generated for the schema in the uploaded MySQL dump: petgo_db
-- Safe to run multiple times: inserts are guarded by slug/product_code/service_code/plan_code checks.
-- Run this AFTER schema/base demo dump has been imported.
-- ==========================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 1;
START TRANSACTION;


-- 1) Product categories

INSERT INTO product_categories (created_at, updated_at, is_active, description, icon_key, name, slug, sort_order, parent_id)
SELECT NOW(6), NOW(6), b'1', 'Thức ăn hạt, pate, snack, sữa và sản phẩm bổ sung dinh dưỡng cho chó mèo.', 'bowl-food', 'Thức ăn & dinh dưỡng', 'thuc-an-dinh-duong', 10, NULL
WHERE NOT EXISTS (SELECT 1 FROM product_categories WHERE slug = 'thuc-an-dinh-duong');

SET @pc_thuc_an_dinh_duong = (SELECT id FROM product_categories WHERE slug = 'thuc-an-dinh-duong' ORDER BY id LIMIT 1);

INSERT INTO product_categories (created_at, updated_at, is_active, description, icon_key, name, slug, sort_order, parent_id)
SELECT NOW(6), NOW(6), b'1', 'Sản phẩm tắm gội, chăm sóc lông, cắt móng và vệ sinh môi trường sống.', 'grooming', 'Vệ sinh & grooming', 've-sinh-grooming', 20, NULL
WHERE NOT EXISTS (SELECT 1 FROM product_categories WHERE slug = 've-sinh-grooming');

SET @pc_ve_sinh_grooming = (SELECT id FROM product_categories WHERE slug = 've-sinh-grooming' ORDER BY id LIMIT 1);

INSERT INTO product_categories (created_at, updated_at, is_active, description, icon_key, name, slug, sort_order, parent_id)
SELECT NOW(6), NOW(6), b'1', 'Đồ chơi vận động, đồ chơi trí tuệ và dụng cụ hỗ trợ huấn luyện thú cưng.', 'toy', 'Đồ chơi & huấn luyện', 'do-choi-huan-luyen', 30, NULL
WHERE NOT EXISTS (SELECT 1 FROM product_categories WHERE slug = 'do-choi-huan-luyen');

SET @pc_do_choi_huan_luyen = (SELECT id FROM product_categories WHERE slug = 'do-choi-huan-luyen' ORDER BY id LIMIT 1);

INSERT INTO product_categories (created_at, updated_at, is_active, description, icon_key, name, slug, sort_order, parent_id)
SELECT NOW(6), NOW(6), b'1', 'Vòng cổ, dây dắt, bát ăn, nệm, chuồng và túi vận chuyển cho thú cưng.', 'accessories', 'Phụ kiện & đồ dùng', 'phu-kien-do-dung', 40, NULL
WHERE NOT EXISTS (SELECT 1 FROM product_categories WHERE slug = 'phu-kien-do-dung');

SET @pc_phu_kien_do_dung = (SELECT id FROM product_categories WHERE slug = 'phu-kien-do-dung' ORDER BY id LIMIT 1);

INSERT INTO product_categories (created_at, updated_at, is_active, description, icon_key, name, slug, sort_order, parent_id)
SELECT NOW(6), NOW(6), b'1', 'Sản phẩm hỗ trợ chăm sóc sức khỏe, răng miệng, phòng ve rận và bảo vệ thú cưng.', 'health', 'Sức khỏe & an toàn', 'suc-khoe-an-toan', 50, NULL
WHERE NOT EXISTS (SELECT 1 FROM product_categories WHERE slug = 'suc-khoe-an-toan');

SET @pc_suc_khoe_an_toan = (SELECT id FROM product_categories WHERE slug = 'suc-khoe-an-toan' ORDER BY id LIMIT 1);

INSERT INTO product_categories (created_at, updated_at, is_active, description, icon_key, name, slug, sort_order, parent_id)
SELECT NOW(6), NOW(6), b'1', 'Thức ăn hạt, pate và khẩu phần dinh dưỡng dành cho chó.', 'dog-food', 'Thức ăn cho chó', 'thuc-an-cho-cho', 11, @pc_thuc_an_dinh_duong
WHERE @pc_thuc_an_dinh_duong IS NOT NULL AND NOT EXISTS (SELECT 1 FROM product_categories WHERE slug = 'thuc-an-cho-cho');

SET @pc_thuc_an_cho_cho = (SELECT id FROM product_categories WHERE slug = 'thuc-an-cho-cho' ORDER BY id LIMIT 1);

INSERT INTO product_categories (created_at, updated_at, is_active, description, icon_key, name, slug, sort_order, parent_id)
SELECT NOW(6), NOW(6), b'1', 'Thức ăn hạt, pate và khẩu phần dinh dưỡng dành cho mèo.', 'cat-food', 'Thức ăn cho mèo', 'thuc-an-cho-meo', 12, @pc_thuc_an_dinh_duong
WHERE @pc_thuc_an_dinh_duong IS NOT NULL AND NOT EXISTS (SELECT 1 FROM product_categories WHERE slug = 'thuc-an-cho-meo');

SET @pc_thuc_an_cho_meo = (SELECT id FROM product_categories WHERE slug = 'thuc-an-cho-meo' ORDER BY id LIMIT 1);

INSERT INTO product_categories (created_at, updated_at, is_active, description, icon_key, name, slug, sort_order, parent_id)
SELECT NOW(6), NOW(6), b'1', 'Pate, bánh thưởng, snack huấn luyện và đồ gặm cho chó mèo.', 'snack', 'Pate, snack & thưởng', 'pate-snack-thuong', 13, @pc_thuc_an_dinh_duong
WHERE @pc_thuc_an_dinh_duong IS NOT NULL AND NOT EXISTS (SELECT 1 FROM product_categories WHERE slug = 'pate-snack-thuong');

SET @pc_pate_snack_thuong = (SELECT id FROM product_categories WHERE slug = 'pate-snack-thuong' ORDER BY id LIMIT 1);

INSERT INTO product_categories (created_at, updated_at, is_active, description, icon_key, name, slug, sort_order, parent_id)
SELECT NOW(6), NOW(6), b'1', 'Sữa bột, vitamin, dầu cá và sản phẩm hỗ trợ da lông.', 'vitamin', 'Sữa, vitamin & bổ sung', 'sua-vitamin-bo-sung', 14, @pc_thuc_an_dinh_duong
WHERE @pc_thuc_an_dinh_duong IS NOT NULL AND NOT EXISTS (SELECT 1 FROM product_categories WHERE slug = 'sua-vitamin-bo-sung');

SET @pc_sua_vitamin_bo_sung = (SELECT id FROM product_categories WHERE slug = 'sua-vitamin-bo-sung' ORDER BY id LIMIT 1);

INSERT INTO product_categories (created_at, updated_at, is_active, description, icon_key, name, slug, sort_order, parent_id)
SELECT NOW(6), NOW(6), b'1', 'Sữa tắm, dầu xả, xịt dưỡng và khử mùi an toàn cho thú cưng.', 'shampoo', 'Sữa tắm & dầu xả', 'sua-tam-dau-xa', 21, @pc_ve_sinh_grooming
WHERE @pc_ve_sinh_grooming IS NOT NULL AND NOT EXISTS (SELECT 1 FROM product_categories WHERE slug = 'sua-tam-dau-xa');

SET @pc_sua_tam_dau_xa = (SELECT id FROM product_categories WHERE slug = 'sua-tam-dau-xa' ORDER BY id LIMIT 1);

INSERT INTO product_categories (created_at, updated_at, is_active, description, icon_key, name, slug, sort_order, parent_id)
SELECT NOW(6), NOW(6), b'1', 'Dụng cụ chải lông, cắt móng, tông đơ và kéo grooming tại nhà.', 'clipper', 'Lược, tông đơ & kéo', 'luoc-tong-do-keo', 22, @pc_ve_sinh_grooming
WHERE @pc_ve_sinh_grooming IS NOT NULL AND NOT EXISTS (SELECT 1 FROM product_categories WHERE slug = 'luoc-tong-do-keo');

SET @pc_luoc_tong_do_keo = (SELECT id FROM product_categories WHERE slug = 'luoc-tong-do-keo' ORDER BY id LIMIT 1);

INSERT INTO product_categories (created_at, updated_at, is_active, description, icon_key, name, slug, sort_order, parent_id)
SELECT NOW(6), NOW(6), b'1', 'Cát mèo, khử mùi, khăn ướt và sản phẩm vệ sinh không gian sống.', 'litter', 'Khay cát & vệ sinh nhà', 'khay-cat-ve-sinh-nha', 23, @pc_ve_sinh_grooming
WHERE @pc_ve_sinh_grooming IS NOT NULL AND NOT EXISTS (SELECT 1 FROM product_categories WHERE slug = 'khay-cat-ve-sinh-nha');

SET @pc_khay_cat_ve_sinh_nha = (SELECT id FROM product_categories WHERE slug = 'khay-cat-ve-sinh-nha' ORDER BY id LIMIT 1);

INSERT INTO product_categories (created_at, updated_at, is_active, description, icon_key, name, slug, sort_order, parent_id)
SELECT NOW(6), NOW(6), b'1', 'Bóng, dây thừng, đồ chơi gặm và sản phẩm vận động cho chó.', 'ball', 'Đồ chơi gặm & bóng', 'do-choi-gam-bong', 31, @pc_do_choi_huan_luyen
WHERE @pc_do_choi_huan_luyen IS NOT NULL AND NOT EXISTS (SELECT 1 FROM product_categories WHERE slug = 'do-choi-gam-bong');

SET @pc_do_choi_gam_bong = (SELECT id FROM product_categories WHERE slug = 'do-choi-gam-bong' ORDER BY id LIMIT 1);

INSERT INTO product_categories (created_at, updated_at, is_active, description, icon_key, name, slug, sort_order, parent_id)
SELECT NOW(6), NOW(6), b'1', 'Cần câu, trụ cào móng, bóng chuông và đồ chơi kích thích vận động cho mèo.', 'cat-toy', 'Đồ chơi mèo', 'do-choi-meo', 32, @pc_do_choi_huan_luyen
WHERE @pc_do_choi_huan_luyen IS NOT NULL AND NOT EXISTS (SELECT 1 FROM product_categories WHERE slug = 'do-choi-meo');

SET @pc_do_choi_meo = (SELECT id FROM product_categories WHERE slug = 'do-choi-meo' ORDER BY id LIMIT 1);

INSERT INTO product_categories (created_at, updated_at, is_active, description, icon_key, name, slug, sort_order, parent_id)
SELECT NOW(6), NOW(6), b'1', 'Clicker, tấm lót, túi thưởng và đồ dùng hỗ trợ huấn luyện.', 'training', 'Dụng cụ huấn luyện', 'dung-cu-huan-luyen', 33, @pc_do_choi_huan_luyen
WHERE @pc_do_choi_huan_luyen IS NOT NULL AND NOT EXISTS (SELECT 1 FROM product_categories WHERE slug = 'dung-cu-huan-luyen');

SET @pc_dung_cu_huan_luyen = (SELECT id FROM product_categories WHERE slug = 'dung-cu-huan-luyen' ORDER BY id LIMIT 1);

INSERT INTO product_categories (created_at, updated_at, is_active, description, icon_key, name, slug, sort_order, parent_id)
SELECT NOW(6), NOW(6), b'1', 'Vòng cổ, dây dắt, yếm ngực và phụ kiện đi dạo.', 'leash', 'Vòng cổ, dây dắt & yếm', 'vong-co-day-dat-yem', 41, @pc_phu_kien_do_dung
WHERE @pc_phu_kien_do_dung IS NOT NULL AND NOT EXISTS (SELECT 1 FROM product_categories WHERE slug = 'vong-co-day-dat-yem');

SET @pc_vong_co_day_dat_yem = (SELECT id FROM product_categories WHERE slug = 'vong-co-day-dat-yem' ORDER BY id LIMIT 1);

INSERT INTO product_categories (created_at, updated_at, is_active, description, icon_key, name, slug, sort_order, parent_id)
SELECT NOW(6), NOW(6), b'1', 'Bát ăn, bình nước tự động, bình nước du lịch và phụ kiện ăn uống.', 'bowl', 'Bát ăn, bình nước', 'bat-an-binh-nuoc', 42, @pc_phu_kien_do_dung
WHERE @pc_phu_kien_do_dung IS NOT NULL AND NOT EXISTS (SELECT 1 FROM product_categories WHERE slug = 'bat-an-binh-nuoc');

SET @pc_bat_an_binh_nuoc = (SELECT id FROM product_categories WHERE slug = 'bat-an-binh-nuoc' ORDER BY id LIMIT 1);

INSERT INTO product_categories (created_at, updated_at, is_active, description, icon_key, name, slug, sort_order, parent_id)
SELECT NOW(6), NOW(6), b'1', 'Nệm ngủ, chuồng, lồng, balo và túi vận chuyển thú cưng.', 'bed-carrier', 'Chuồng, nệm & túi vận chuyển', 'chuong-nem-tui-van-chuyen', 43, @pc_phu_kien_do_dung
WHERE @pc_phu_kien_do_dung IS NOT NULL AND NOT EXISTS (SELECT 1 FROM product_categories WHERE slug = 'chuong-nem-tui-van-chuyen');

SET @pc_chuong_nem_tui_van_chuyen = (SELECT id FROM product_categories WHERE slug = 'chuong-nem-tui-van-chuyen' ORDER BY id LIMIT 1);

INSERT INTO product_categories (created_at, updated_at, is_active, description, icon_key, name, slug, sort_order, parent_id)
SELECT NOW(6), NOW(6), b'1', 'Bàn chải, gel vệ sinh răng, xương gặm và sản phẩm làm sạch răng miệng.', 'dental', 'Chăm sóc răng miệng', 'rang-mieng-cham-soc', 51, @pc_suc_khoe_an_toan
WHERE @pc_suc_khoe_an_toan IS NOT NULL AND NOT EXISTS (SELECT 1 FROM product_categories WHERE slug = 'rang-mieng-cham-soc');

SET @pc_rang_mieng_cham_soc = (SELECT id FROM product_categories WHERE slug = 'rang-mieng-cham-soc' ORDER BY id LIMIT 1);

INSERT INTO product_categories (created_at, updated_at, is_active, description, icon_key, name, slug, sort_order, parent_id)
SELECT NOW(6), NOW(6), b'1', 'Sản phẩm hỗ trợ phòng ve rận, sát khuẩn nhẹ và bảo vệ khi ra ngoài.', 'shield', 'Phòng ve rận & bảo vệ', 'phong-ve-ran-bao-ve', 52, @pc_suc_khoe_an_toan
WHERE @pc_suc_khoe_an_toan IS NOT NULL AND NOT EXISTS (SELECT 1 FROM product_categories WHERE slug = 'phong-ve-ran-bao-ve');

SET @pc_phong_ve_ran_bao_ve = (SELECT id FROM product_categories WHERE slug = 'phong-ve-ran-bao-ve' ORDER BY id LIMIT 1);


-- 2) Products

INSERT INTO products (created_at, updated_at, is_active, average_rating, barcode, brand, currency_code, deleted_at, description, is_featured, is_hot, main_image_url, name, price_amount, product_code, sale_price_amount, short_description, sku, slug, sold_quantity, status, stock_quantity, target_species, total_reviews, weight_gram, category_id)
SELECT NOW(6), NOW(6), b'1', 4.80, NULL, 'Royal Canin', 'VND', NULL, 'Công thức hạt dành cho chó Poodle trưởng thành, hỗ trợ da lông và tiêu hóa hằng ngày.', b'1', b'1', 'https://images.unsplash.com/photo-1589924691995-400dc9ecc119?auto=format&fit=crop&q=80&w=800', 'Royal Canin Poodle Adult 1.5kg', 355000.00, 'PRD-DOG-FOOD-001', 329000.00, 'Hạt dinh dưỡng cho Poodle trưởng thành.', 'DOG-FOOD-RC-POODLE-15KG', 'dog-food-rc-poodle-15kg', 36, 'ACTIVE', 80, 'DOG', 18, 1500, @pc_thuc_an_cho_cho
WHERE @pc_thuc_an_cho_cho IS NOT NULL AND NOT EXISTS (SELECT 1 FROM products WHERE product_code = 'PRD-DOG-FOOD-001');

INSERT INTO products (created_at, updated_at, is_active, average_rating, barcode, brand, currency_code, deleted_at, description, is_featured, is_hot, main_image_url, name, price_amount, product_code, sale_price_amount, short_description, sku, slug, sold_quantity, status, stock_quantity, target_species, total_reviews, weight_gram, category_id)
SELECT NOW(6), NOW(6), b'1', 4.60, NULL, 'Pedigree', 'VND', NULL, 'Gói hạt phổ thông cho chó trưởng thành, phù hợp dùng hằng ngày với mức giá dễ tiếp cận.', b'1', b'0', 'https://images.unsplash.com/photo-1601758228041-f3b2795255f1?auto=format&fit=crop&q=80&w=800', 'Pedigree vị bò cho chó trưởng thành 3kg', 285000.00, 'PRD-DOG-FOOD-002', 259000.00, 'Thức ăn hạt vị bò cho chó trưởng thành.', 'DOG-FOOD-PED-BEEF-3KG', 'dog-food-ped-beef-3kg', 58, 'ACTIVE', 120, 'DOG', 21, 3000, @pc_thuc_an_cho_cho
WHERE @pc_thuc_an_cho_cho IS NOT NULL AND NOT EXISTS (SELECT 1 FROM products WHERE product_code = 'PRD-DOG-FOOD-002');

INSERT INTO products (created_at, updated_at, is_active, average_rating, barcode, brand, currency_code, deleted_at, description, is_featured, is_hot, main_image_url, name, price_amount, product_code, sale_price_amount, short_description, sku, slug, sold_quantity, status, stock_quantity, target_species, total_reviews, weight_gram, category_id)
SELECT NOW(6), NOW(6), b'1', 4.70, NULL, 'SmartHeart', 'VND', NULL, 'Dành cho chó con đang phát triển, hỗ trợ năng lượng, tiêu hóa và tăng trưởng khỏe mạnh.', b'0', b'1', 'https://images.unsplash.com/photo-1583337130417-3346a1be7dee?auto=format&fit=crop&q=80&w=800', 'SmartHeart Puppy Milk & Egg 1.5kg', 185000.00, 'PRD-DOG-FOOD-003', 169000.00, 'Hạt cho chó con vị sữa trứng.', 'DOG-FOOD-SH-PUPPY-15KG', 'dog-food-sh-puppy-15kg', 44, 'ACTIVE', 95, 'DOG', 15, 1500, @pc_thuc_an_cho_cho
WHERE @pc_thuc_an_cho_cho IS NOT NULL AND NOT EXISTS (SELECT 1 FROM products WHERE product_code = 'PRD-DOG-FOOD-003');

INSERT INTO products (created_at, updated_at, is_active, average_rating, barcode, brand, currency_code, deleted_at, description, is_featured, is_hot, main_image_url, name, price_amount, product_code, sale_price_amount, short_description, sku, slug, sold_quantity, status, stock_quantity, target_species, total_reviews, weight_gram, category_id)
SELECT NOW(6), NOW(6), b'1', 4.50, NULL, 'Ganador', 'VND', NULL, 'Lựa chọn hợp lý cho chó trưởng thành, vị cá hồi dễ ăn, hỗ trợ bữa ăn hằng ngày.', b'0', b'0', 'https://images.unsplash.com/photo-1568640347023-a616a30bc3bd?auto=format&fit=crop&q=80&w=800', 'Ganador Adult Salmon & Rice 1.5kg', 135000.00, 'PRD-DOG-FOOD-004', 125000.00, 'Hạt cá hồi và gạo cho chó trưởng thành.', 'DOG-FOOD-GANADOR-SALMON-15KG', 'dog-food-ganador-salmon-15kg', 27, 'ACTIVE', 100, 'DOG', 12, 1500, @pc_thuc_an_cho_cho
WHERE @pc_thuc_an_cho_cho IS NOT NULL AND NOT EXISTS (SELECT 1 FROM products WHERE product_code = 'PRD-DOG-FOOD-004');

INSERT INTO products (created_at, updated_at, is_active, average_rating, barcode, brand, currency_code, deleted_at, description, is_featured, is_hot, main_image_url, name, price_amount, product_code, sale_price_amount, short_description, sku, slug, sold_quantity, status, stock_quantity, target_species, total_reviews, weight_gram, category_id)
SELECT NOW(6), NOW(6), b'1', 4.80, NULL, 'JerHigh', 'VND', NULL, 'Miếng snack gà mềm, dùng làm phần thưởng khi huấn luyện hoặc bổ sung bữa phụ.', b'0', b'1', 'https://images.unsplash.com/photo-1535294435445-d7249524ef2e?auto=format&fit=crop&q=80&w=800', 'JerHigh Chicken Jerky 70g', 45000.00, 'PRD-DOG-SNACK-001', 39000.00, 'Snack gà thưởng cho chó.', 'DOG-SNACK-JERHIGH-70G', 'dog-snack-jerhigh-70g', 122, 'ACTIVE', 200, 'DOG', 33, 70, @pc_pate_snack_thuong
WHERE @pc_pate_snack_thuong IS NOT NULL AND NOT EXISTS (SELECT 1 FROM products WHERE product_code = 'PRD-DOG-SNACK-001');

INSERT INTO products (created_at, updated_at, is_active, average_rating, barcode, brand, currency_code, deleted_at, description, is_featured, is_hot, main_image_url, name, price_amount, product_code, sale_price_amount, short_description, sku, slug, sold_quantity, status, stock_quantity, target_species, total_reviews, weight_gram, category_id)
SELECT NOW(6), NOW(6), b'1', 4.90, NULL, 'Royal Canin', 'VND', NULL, 'Công thức hỗ trợ mèo con phát triển, dễ tiêu hóa và phù hợp giai đoạn tăng trưởng.', b'1', b'1', 'https://images.unsplash.com/photo-1574158622682-e40e69881006?auto=format&fit=crop&q=80&w=800', 'Royal Canin Kitten 2kg', 520000.00, 'PRD-CAT-FOOD-001', 489000.00, 'Hạt cho mèo con dưới 12 tháng.', 'CAT-FOOD-RC-KITTEN-2KG', 'cat-food-rc-kitten-2kg', 32, 'ACTIVE', 60, 'CAT', 24, 2000, @pc_thuc_an_cho_meo
WHERE @pc_thuc_an_cho_meo IS NOT NULL AND NOT EXISTS (SELECT 1 FROM products WHERE product_code = 'PRD-CAT-FOOD-001');

INSERT INTO products (created_at, updated_at, is_active, average_rating, barcode, brand, currency_code, deleted_at, description, is_featured, is_hot, main_image_url, name, price_amount, product_code, sale_price_amount, short_description, sku, slug, sold_quantity, status, stock_quantity, target_species, total_reviews, weight_gram, category_id)
SELECT NOW(6), NOW(6), b'1', 4.60, NULL, 'Whiskas', 'VND', NULL, 'Thức ăn khô vị cá biển, phù hợp bữa ăn hằng ngày cho mèo trưởng thành.', b'1', b'0', 'https://images.unsplash.com/photo-1513360371669-4adf3dd7dff8?auto=format&fit=crop&q=80&w=800', 'Whiskas Ocean Fish 1.2kg', 155000.00, 'PRD-CAT-FOOD-002', 139000.00, 'Hạt vị cá biển cho mèo trưởng thành.', 'CAT-FOOD-WHISKAS-FISH-12KG', 'cat-food-whiskas-fish-12kg', 76, 'ACTIVE', 130, 'CAT', 19, 1200, @pc_thuc_an_cho_meo
WHERE @pc_thuc_an_cho_meo IS NOT NULL AND NOT EXISTS (SELECT 1 FROM products WHERE product_code = 'PRD-CAT-FOOD-002');

INSERT INTO products (created_at, updated_at, is_active, average_rating, barcode, brand, currency_code, deleted_at, description, is_featured, is_hot, main_image_url, name, price_amount, product_code, sale_price_amount, short_description, sku, slug, sold_quantity, status, stock_quantity, target_species, total_reviews, weight_gram, category_id)
SELECT NOW(6), NOW(6), b'1', 4.50, NULL, 'Me-O', 'VND', NULL, 'Công thức hỗ trợ giảm búi lông, phù hợp mèo lông dài và mèo nuôi trong nhà.', b'0', b'0', 'https://images.unsplash.com/photo-1592194996308-7b43878e84a6?auto=format&fit=crop&q=80&w=800', 'Me-O Persian 1.1kg', 145000.00, 'PRD-CAT-FOOD-003', 132000.00, 'Hạt cho mèo Ba Tư và mèo lông dài.', 'CAT-FOOD-MEO-PERSIAN-11KG', 'cat-food-meo-persian-11kg', 49, 'ACTIVE', 110, 'CAT', 14, 1100, @pc_thuc_an_cho_meo
WHERE @pc_thuc_an_cho_meo IS NOT NULL AND NOT EXISTS (SELECT 1 FROM products WHERE product_code = 'PRD-CAT-FOOD-003');

INSERT INTO products (created_at, updated_at, is_active, average_rating, barcode, brand, currency_code, deleted_at, description, is_featured, is_hot, main_image_url, name, price_amount, product_code, sale_price_amount, short_description, sku, slug, sold_quantity, status, stock_quantity, target_species, total_reviews, weight_gram, category_id)
SELECT NOW(6), NOW(6), b'1', 4.70, NULL, 'Catsrang', 'VND', NULL, 'Gói lớn tiết kiệm cho gia đình nuôi nhiều mèo, vị dễ ăn và tiện bảo quản.', b'1', b'0', 'https://images.unsplash.com/photo-1519052537078-e6302a4968d4?auto=format&fit=crop&q=80&w=800', 'Catsrang Adult 5kg', 465000.00, 'PRD-CAT-FOOD-004', 439000.00, 'Bao hạt kinh tế cho mèo trưởng thành.', 'CAT-FOOD-CATSRANG-5KG', 'cat-food-catsrang-5kg', 21, 'ACTIVE', 55, 'CAT', 10, 5000, @pc_thuc_an_cho_meo
WHERE @pc_thuc_an_cho_meo IS NOT NULL AND NOT EXISTS (SELECT 1 FROM products WHERE product_code = 'PRD-CAT-FOOD-004');

INSERT INTO products (created_at, updated_at, is_active, average_rating, barcode, brand, currency_code, deleted_at, description, is_featured, is_hot, main_image_url, name, price_amount, product_code, sale_price_amount, short_description, sku, slug, sold_quantity, status, stock_quantity, target_species, total_reviews, weight_gram, category_id)
SELECT NOW(6), NOW(6), b'1', 4.80, NULL, 'Wanpy', 'VND', NULL, 'Pate mềm thơm, bổ sung nước và làm bữa phụ hấp dẫn cho mèo.', b'0', b'1', 'https://images.unsplash.com/photo-1545249390-6bdfa286032f?auto=format&fit=crop&q=80&w=800', 'Pate Wanpy Tuna & Chicken 80g', 25000.00, 'PRD-CAT-PATE-001', 22000.00, 'Pate cá ngừ gà cho mèo.', 'CAT-PATE-WANPY-80G', 'cat-pate-wanpy-80g', 180, 'ACTIVE', 300, 'CAT', 41, 80, @pc_pate_snack_thuong
WHERE @pc_pate_snack_thuong IS NOT NULL AND NOT EXISTS (SELECT 1 FROM products WHERE product_code = 'PRD-CAT-PATE-001');

INSERT INTO products (created_at, updated_at, is_active, average_rating, barcode, brand, currency_code, deleted_at, description, is_featured, is_hot, main_image_url, name, price_amount, product_code, sale_price_amount, short_description, sku, slug, sold_quantity, status, stock_quantity, target_species, total_reviews, weight_gram, category_id)
SELECT NOW(6), NOW(6), b'1', 4.90, NULL, 'Ciao', 'VND', NULL, 'Snack dạng súp mềm, dễ dùng để thưởng, dụ ăn hoặc hỗ trợ bổ sung nước.', b'0', b'1', 'https://images.unsplash.com/photo-1494256997604-768d1f608cac?auto=format&fit=crop&q=80&w=800', 'Ciao Churu Tuna 4 thanh', 59000.00, 'PRD-CAT-SNACK-001', 54000.00, 'Súp thưởng dạng thanh cho mèo.', 'CAT-SNACK-CIAO-CHURU-4T', 'cat-snack-ciao-churu-4t', 97, 'ACTIVE', 180, 'CAT', 29, 56, @pc_pate_snack_thuong
WHERE @pc_pate_snack_thuong IS NOT NULL AND NOT EXISTS (SELECT 1 FROM products WHERE product_code = 'PRD-CAT-SNACK-001');

INSERT INTO products (created_at, updated_at, is_active, average_rating, barcode, brand, currency_code, deleted_at, description, is_featured, is_hot, main_image_url, name, price_amount, product_code, sale_price_amount, short_description, sku, slug, sold_quantity, status, stock_quantity, target_species, total_reviews, weight_gram, category_id)
SELECT NOW(6), NOW(6), b'1', 4.60, NULL, 'Orgo', 'VND', NULL, 'Đồ gặm giúp chó giải tỏa năng lượng và hỗ trợ làm sạch mảng bám nhẹ.', b'0', b'0', 'https://images.unsplash.com/photo-1558788353-f76d92427f16?auto=format&fit=crop&q=80&w=800', 'Xương gặm sạch răng Orgo 7 cây', 69000.00, 'PRD-DOG-DENTAL-001', 62000.00, 'Xương gặm hỗ trợ làm sạch răng.', 'DOG-DENTAL-BONE-7PCS', 'dog-dental-bone-7pcs', 70, 'ACTIVE', 160, 'DOG', 17, 210, @pc_rang_mieng_cham_soc
WHERE @pc_rang_mieng_cham_soc IS NOT NULL AND NOT EXISTS (SELECT 1 FROM products WHERE product_code = 'PRD-DOG-DENTAL-001');

INSERT INTO products (created_at, updated_at, is_active, average_rating, barcode, brand, currency_code, deleted_at, description, is_featured, is_hot, main_image_url, name, price_amount, product_code, sale_price_amount, short_description, sku, slug, sold_quantity, status, stock_quantity, target_species, total_reviews, weight_gram, category_id)
SELECT NOW(6), NOW(6), b'1', 4.70, NULL, 'PetGo Select', 'VND', NULL, 'Hạt thưởng kích thước nhỏ, tiện bỏ túi khi dạy lệnh và đi dạo.', b'0', b'0', 'https://images.unsplash.com/photo-1560743641-3914f2c45636?auto=format&fit=crop&q=80&w=800', 'Bánh thưởng huấn luyện vị bò 200g', 78000.00, 'PRD-DOG-TRAIN-001', 69000.00, 'Bánh thưởng nhỏ dùng khi huấn luyện.', 'DOG-TRAIN-TREAT-200G', 'dog-train-treat-200g', 63, 'ACTIVE', 140, 'DOG', 20, 200, @pc_pate_snack_thuong
WHERE @pc_pate_snack_thuong IS NOT NULL AND NOT EXISTS (SELECT 1 FROM products WHERE product_code = 'PRD-DOG-TRAIN-001');

INSERT INTO products (created_at, updated_at, is_active, average_rating, barcode, brand, currency_code, deleted_at, description, is_featured, is_hot, main_image_url, name, price_amount, product_code, sale_price_amount, short_description, sku, slug, sold_quantity, status, stock_quantity, target_species, total_reviews, weight_gram, category_id)
SELECT NOW(6), NOW(6), b'1', 4.50, NULL, 'Bio Milk', 'VND', NULL, 'Dùng cho thú cưng cần bổ sung năng lượng, chó mèo con hoặc giai đoạn phục hồi.', b'0', b'0', 'https://images.unsplash.com/photo-1601758123927-196fefb5b65e?auto=format&fit=crop&q=80&w=800', 'Sữa bột Bio Milk cho chó mèo 300g', 165000.00, 'PRD-ALL-MILK-001', 149000.00, 'Sữa bột bổ sung cho chó mèo.', 'ALL-MILK-BIO-300G', 'all-milk-bio-300g', 31, 'ACTIVE', 90, 'ALL', 11, 300, @pc_sua_vitamin_bo_sung
WHERE @pc_sua_vitamin_bo_sung IS NOT NULL AND NOT EXISTS (SELECT 1 FROM products WHERE product_code = 'PRD-ALL-MILK-001');

INSERT INTO products (created_at, updated_at, is_active, average_rating, barcode, brand, currency_code, deleted_at, description, is_featured, is_hot, main_image_url, name, price_amount, product_code, sale_price_amount, short_description, sku, slug, sold_quantity, status, stock_quantity, target_species, total_reviews, weight_gram, category_id)
SELECT NOW(6), NOW(6), b'1', 4.40, NULL, 'Bioline', 'VND', NULL, 'Sản phẩm bổ sung hỗ trợ chăm sóc da lông, phù hợp chó mèo rụng lông theo mùa.', b'0', b'0', 'https://images.unsplash.com/photo-1601758063541-d2f50b4aafb2?auto=format&fit=crop&q=80&w=800', 'Vitamin da lông Bioline 50g', 125000.00, 'PRD-ALL-VITAMIN-001', 115000.00, 'Hỗ trợ da lông bóng khỏe.', 'ALL-VIT-BIOLINE-SKIN-50G', 'all-vit-bioline-skin-50g', 22, 'ACTIVE', 85, 'ALL', 9, 50, @pc_sua_vitamin_bo_sung
WHERE @pc_sua_vitamin_bo_sung IS NOT NULL AND NOT EXISTS (SELECT 1 FROM products WHERE product_code = 'PRD-ALL-VITAMIN-001');

INSERT INTO products (created_at, updated_at, is_active, average_rating, barcode, brand, currency_code, deleted_at, description, is_featured, is_hot, main_image_url, name, price_amount, product_code, sale_price_amount, short_description, sku, slug, sold_quantity, status, stock_quantity, target_species, total_reviews, weight_gram, category_id)
SELECT NOW(6), NOW(6), b'1', 4.70, NULL, 'Joyce & Dolls', 'VND', NULL, 'Làm sạch nhẹ nhàng, hỗ trợ dưỡng lông mềm và lưu hương dễ chịu sau khi tắm.', b'1', b'0', 'https://images.unsplash.com/photo-1516734212186-a967f81ad0d7?auto=format&fit=crop&q=80&w=800', 'Sữa tắm Joyce & Dolls mềm lông 500ml', 195000.00, 'PRD-ALL-SHAMPOO-001', 179000.00, 'Sữa tắm dưỡng mềm lông cho chó mèo.', 'ALL-SHAMPOO-JD-500ML', 'all-shampoo-jd-500ml', 39, 'ACTIVE', 100, 'ALL', 16, 500, @pc_sua_tam_dau_xa
WHERE @pc_sua_tam_dau_xa IS NOT NULL AND NOT EXISTS (SELECT 1 FROM products WHERE product_code = 'PRD-ALL-SHAMPOO-001');

INSERT INTO products (created_at, updated_at, is_active, average_rating, barcode, brand, currency_code, deleted_at, description, is_featured, is_hot, main_image_url, name, price_amount, product_code, sale_price_amount, short_description, sku, slug, sold_quantity, status, stock_quantity, target_species, total_reviews, weight_gram, category_id)
SELECT NOW(6), NOW(6), b'1', 4.60, NULL, 'SOS', 'VND', NULL, 'Công thức làm sạch mùi cơ thể, phù hợp dùng định kỳ tại nhà.', b'0', b'1', 'https://images.unsplash.com/photo-1601758124510-52d02ddb7cbd?auto=format&fit=crop&q=80&w=800', 'Sữa tắm SOS khử mùi 530ml', 135000.00, 'PRD-ALL-SHAMPOO-002', 125000.00, 'Sữa tắm khử mùi cho chó mèo.', 'ALL-SHAMPOO-SOS-530ML', 'all-shampoo-sos-530ml', 65, 'ACTIVE', 150, 'ALL', 18, 530, @pc_sua_tam_dau_xa
WHERE @pc_sua_tam_dau_xa IS NOT NULL AND NOT EXISTS (SELECT 1 FROM products WHERE product_code = 'PRD-ALL-SHAMPOO-002');

INSERT INTO products (created_at, updated_at, is_active, average_rating, barcode, brand, currency_code, deleted_at, description, is_featured, is_hot, main_image_url, name, price_amount, product_code, sale_price_amount, short_description, sku, slug, sold_quantity, status, stock_quantity, target_species, total_reviews, weight_gram, category_id)
SELECT NOW(6), NOW(6), b'1', 4.80, NULL, 'Bio-Groom', 'VND', NULL, 'Phù hợp thú cưng lông dài, giúp lông mềm hơn sau khi tắm sấy.', b'0', b'0', 'https://images.unsplash.com/photo-1544568100-847a948585b9?auto=format&fit=crop&q=80&w=800', 'Dầu xả dưỡng lông Bio-Groom 355ml', 265000.00, 'PRD-ALL-CONDITIONER-001', 239000.00, 'Dầu xả hỗ trợ gỡ rối và dưỡng lông.', 'ALL-CONDITIONER-BG-355ML', 'all-conditioner-bg-355ml', 14, 'ACTIVE', 55, 'ALL', 7, 355, @pc_sua_tam_dau_xa
WHERE @pc_sua_tam_dau_xa IS NOT NULL AND NOT EXISTS (SELECT 1 FROM products WHERE product_code = 'PRD-ALL-CONDITIONER-001');

INSERT INTO products (created_at, updated_at, is_active, average_rating, barcode, brand, currency_code, deleted_at, description, is_featured, is_hot, main_image_url, name, price_amount, product_code, sale_price_amount, short_description, sku, slug, sold_quantity, status, stock_quantity, target_species, total_reviews, weight_gram, category_id)
SELECT NOW(6), NOW(6), b'1', 4.70, NULL, 'PetGo Select', 'VND', NULL, 'Thiết kế dễ vệ sinh, phù hợp chải lông rụng cho chó mèo tại nhà.', b'1', b'1', 'https://images.unsplash.com/photo-1583512603805-3cc6b41f3edb?auto=format&fit=crop&q=80&w=800', 'Lược chải lông tự làm sạch', 99000.00, 'PRD-ALL-COMB-001', 89000.00, 'Lược gỡ lông rụng có nút đẩy.', 'ALL-COMB-SELF-CLEAN', 'all-comb-self-clean', 81, 'ACTIVE', 170, 'ALL', 26, 180, @pc_luoc_tong_do_keo
WHERE @pc_luoc_tong_do_keo IS NOT NULL AND NOT EXISTS (SELECT 1 FROM products WHERE product_code = 'PRD-ALL-COMB-001');

INSERT INTO products (created_at, updated_at, is_active, average_rating, barcode, brand, currency_code, deleted_at, description, is_featured, is_hot, main_image_url, name, price_amount, product_code, sale_price_amount, short_description, sku, slug, sold_quantity, status, stock_quantity, target_species, total_reviews, weight_gram, category_id)
SELECT NOW(6), NOW(6), b'1', 4.50, NULL, 'PetGo Select', 'VND', NULL, 'Máy cắt lông nhỏ gọn, dùng cho vùng chân, bụng hoặc tỉa nhẹ tại nhà.', b'1', b'0', 'https://images.unsplash.com/photo-1583336663277-620dc1996580?auto=format&fit=crop&q=80&w=800', 'Tông đơ cắt lông PetGo Mini', 389000.00, 'PRD-ALL-CLIPPER-001', 349000.00, 'Tông đơ mini cho grooming tại nhà.', 'ALL-CLIPPER-MINI', 'all-clipper-mini', 18, 'ACTIVE', 45, 'ALL', 8, 420, @pc_luoc_tong_do_keo
WHERE @pc_luoc_tong_do_keo IS NOT NULL AND NOT EXISTS (SELECT 1 FROM products WHERE product_code = 'PRD-ALL-CLIPPER-001');

INSERT INTO products (created_at, updated_at, is_active, average_rating, barcode, brand, currency_code, deleted_at, description, is_featured, is_hot, main_image_url, name, price_amount, product_code, sale_price_amount, short_description, sku, slug, sold_quantity, status, stock_quantity, target_species, total_reviews, weight_gram, category_id)
SELECT NOW(6), NOW(6), b'1', 4.50, NULL, 'PetGo Select', 'VND', NULL, 'Dụng cụ cắt móng cơ bản, lưỡi sắc và tay cầm chống trượt.', b'0', b'0', 'https://images.unsplash.com/photo-1598133894008-61f7fdb8cc3a?auto=format&fit=crop&q=80&w=800', 'Kìm cắt móng thú cưng chống trượt', 79000.00, 'PRD-ALL-NAIL-001', 69000.00, 'Kìm cắt móng cầm chắc tay.', 'ALL-NAIL-CLIPPER', 'all-nail-clipper', 47, 'ACTIVE', 130, 'ALL', 15, 120, @pc_luoc_tong_do_keo
WHERE @pc_luoc_tong_do_keo IS NOT NULL AND NOT EXISTS (SELECT 1 FROM products WHERE product_code = 'PRD-ALL-NAIL-001');

INSERT INTO products (created_at, updated_at, is_active, average_rating, barcode, brand, currency_code, deleted_at, description, is_featured, is_hot, main_image_url, name, price_amount, product_code, sale_price_amount, short_description, sku, slug, sold_quantity, status, stock_quantity, target_species, total_reviews, weight_gram, category_id)
SELECT NOW(6), NOW(6), b'1', 4.40, NULL, 'PetGo Select', 'VND', NULL, 'Đầu silicon mềm giúp vệ sinh răng miệng nhẹ nhàng, phù hợp làm quen từ từ.', b'0', b'0', 'https://images.unsplash.com/photo-1601758176175-45914394491c?auto=format&fit=crop&q=80&w=800', 'Bàn chải đánh răng silicon cho thú cưng', 49000.00, 'PRD-ALL-TOOTHBRUSH-001', 42000.00, 'Bàn chải mềm cho chó mèo.', 'ALL-TOOTHBRUSH-SILICONE', 'all-toothbrush-silicone', 103, 'ACTIVE', 220, 'ALL', 19, 50, @pc_rang_mieng_cham_soc
WHERE @pc_rang_mieng_cham_soc IS NOT NULL AND NOT EXISTS (SELECT 1 FROM products WHERE product_code = 'PRD-ALL-TOOTHBRUSH-001');

INSERT INTO products (created_at, updated_at, is_active, average_rating, barcode, brand, currency_code, deleted_at, description, is_featured, is_hot, main_image_url, name, price_amount, product_code, sale_price_amount, short_description, sku, slug, sold_quantity, status, stock_quantity, target_species, total_reviews, weight_gram, category_id)
SELECT NOW(6), NOW(6), b'1', 4.60, NULL, 'PetGo Select', 'VND', NULL, 'Cát mèo đậu nành dễ dọn, mùi nhẹ, phù hợp căn hộ và phòng kín.', b'1', b'1', 'https://images.unsplash.com/photo-1573865526739-10659fec78a5?auto=format&fit=crop&q=80&w=800', 'Cát mèo đậu nành hương trà xanh 6L', 115000.00, 'PRD-CAT-LITTER-001', 105000.00, 'Cát đậu nành vón nhanh, ít bụi.', 'CAT-LITTER-TOFU-6L', 'cat-litter-tofu-6l', 69, 'ACTIVE', 140, 'CAT', 22, 2500, @pc_khay_cat_ve_sinh_nha
WHERE @pc_khay_cat_ve_sinh_nha IS NOT NULL AND NOT EXISTS (SELECT 1 FROM products WHERE product_code = 'PRD-CAT-LITTER-001');

INSERT INTO products (created_at, updated_at, is_active, average_rating, barcode, brand, currency_code, deleted_at, description, is_featured, is_hot, main_image_url, name, price_amount, product_code, sale_price_amount, short_description, sku, slug, sold_quantity, status, stock_quantity, target_species, total_reviews, weight_gram, category_id)
SELECT NOW(6), NOW(6), b'1', 4.50, NULL, 'PetGo Select', 'VND', NULL, 'Hạt cát vón tốt, hỗ trợ kiểm soát mùi cho khay vệ sinh mèo.', b'0', b'0', 'https://images.unsplash.com/photo-1570824104453-508955ab713e?auto=format&fit=crop&q=80&w=800', 'Cát bentonite lavender 5L', 89000.00, 'PRD-CAT-LITTER-002', 79000.00, 'Cát vệ sinh mèo hương lavender.', 'CAT-LITTER-BENTONITE-5L', 'cat-litter-bentonite-5l', 94, 'ACTIVE', 180, 'CAT', 18, 4500, @pc_khay_cat_ve_sinh_nha
WHERE @pc_khay_cat_ve_sinh_nha IS NOT NULL AND NOT EXISTS (SELECT 1 FROM products WHERE product_code = 'PRD-CAT-LITTER-002');

INSERT INTO products (created_at, updated_at, is_active, average_rating, barcode, brand, currency_code, deleted_at, description, is_featured, is_hot, main_image_url, name, price_amount, product_code, sale_price_amount, short_description, sku, slug, sold_quantity, status, stock_quantity, target_species, total_reviews, weight_gram, category_id)
SELECT NOW(6), NOW(6), b'1', 4.30, NULL, 'PetGo Select', 'VND', NULL, 'Hỗ trợ làm sạch mùi chuồng, nệm, khay vệ sinh và khu vực sinh hoạt của thú cưng.', b'0', b'0', 'https://images.unsplash.com/photo-1581578731548-c64695cc6952?auto=format&fit=crop&q=80&w=800', 'Xịt khử mùi chuồng trại 500ml', 99000.00, 'PRD-ALL-CLEAN-001', 89000.00, 'Xịt khử mùi khu vực thú cưng.', 'ALL-CLEAN-DEODOR-500ML', 'all-clean-deodor-500ml', 34, 'ACTIVE', 100, 'ALL', 10, 500, @pc_khay_cat_ve_sinh_nha
WHERE @pc_khay_cat_ve_sinh_nha IS NOT NULL AND NOT EXISTS (SELECT 1 FROM products WHERE product_code = 'PRD-ALL-CLEAN-001');

INSERT INTO products (created_at, updated_at, is_active, average_rating, barcode, brand, currency_code, deleted_at, description, is_featured, is_hot, main_image_url, name, price_amount, product_code, sale_price_amount, short_description, sku, slug, sold_quantity, status, stock_quantity, target_species, total_reviews, weight_gram, category_id)
SELECT NOW(6), NOW(6), b'1', 4.60, NULL, 'PetGo Select', 'VND', NULL, 'Đồ chơi ném bắt, có âm thanh nhẹ để kích thích chó vận động và chơi cùng chủ.', b'0', b'1', 'https://images.unsplash.com/photo-1558788353-f76d92427f16?auto=format&fit=crop&q=80&w=800', 'Bóng cao su phát âm thanh cho chó', 59000.00, 'PRD-DOG-TOY-001', 52000.00, 'Bóng cao su giúp chó vận động.', 'DOG-TOY-RUBBER-BALL', 'dog-toy-rubber-ball', 78, 'ACTIVE', 160, 'DOG', 21, 160, @pc_do_choi_gam_bong
WHERE @pc_do_choi_gam_bong IS NOT NULL AND NOT EXISTS (SELECT 1 FROM products WHERE product_code = 'PRD-DOG-TOY-001');

INSERT INTO products (created_at, updated_at, is_active, average_rating, barcode, brand, currency_code, deleted_at, description, is_featured, is_hot, main_image_url, name, price_amount, product_code, sale_price_amount, short_description, sku, slug, sold_quantity, status, stock_quantity, target_species, total_reviews, weight_gram, category_id)
SELECT NOW(6), NOW(6), b'1', 4.50, NULL, 'PetGo Select', 'VND', NULL, 'Đồ chơi dây thừng giúp chó giải tỏa năng lượng và hỗ trợ vệ sinh răng nhẹ.', b'0', b'0', 'https://images.unsplash.com/photo-1548199973-03cce0bbc87b?auto=format&fit=crop&q=80&w=800', 'Dây thừng gặm làm sạch răng', 69000.00, 'PRD-DOG-TOY-002', 59000.00, 'Dây thừng gặm và kéo co.', 'DOG-TOY-ROPE-DENTAL', 'dog-toy-rope-dental', 62, 'ACTIVE', 130, 'DOG', 16, 220, @pc_do_choi_gam_bong
WHERE @pc_do_choi_gam_bong IS NOT NULL AND NOT EXISTS (SELECT 1 FROM products WHERE product_code = 'PRD-DOG-TOY-002');

INSERT INTO products (created_at, updated_at, is_active, average_rating, barcode, brand, currency_code, deleted_at, description, is_featured, is_hot, main_image_url, name, price_amount, product_code, sale_price_amount, short_description, sku, slug, sold_quantity, status, stock_quantity, target_species, total_reviews, weight_gram, category_id)
SELECT NOW(6), NOW(6), b'1', 4.70, NULL, 'PetGo Select', 'VND', NULL, 'Cần câu lông vũ giúp mèo vận động, săn bắt giả lập và giảm buồn chán.', b'0', b'1', 'https://images.unsplash.com/photo-1548366086-7f1b76106622?auto=format&fit=crop&q=80&w=800', 'Cần câu mèo lông vũ', 45000.00, 'PRD-CAT-TOY-001', 39000.00, 'Đồ chơi tương tác cho mèo.', 'CAT-TOY-FEATHER-WAND', 'cat-toy-feather-wand', 133, 'ACTIVE', 210, 'CAT', 35, 60, @pc_do_choi_meo
WHERE @pc_do_choi_meo IS NOT NULL AND NOT EXISTS (SELECT 1 FROM products WHERE product_code = 'PRD-CAT-TOY-001');

INSERT INTO products (created_at, updated_at, is_active, average_rating, barcode, brand, currency_code, deleted_at, description, is_featured, is_hot, main_image_url, name, price_amount, product_code, sale_price_amount, short_description, sku, slug, sold_quantity, status, stock_quantity, target_species, total_reviews, weight_gram, category_id)
SELECT NOW(6), NOW(6), b'1', 4.60, NULL, 'PetGo Select', 'VND', NULL, 'Giúp mèo mài móng đúng chỗ, bảo vệ sofa và đồ nội thất trong nhà.', b'1', b'0', 'https://images.unsplash.com/photo-1511044568932-338cba0ad803?auto=format&fit=crop&q=80&w=800', 'Trụ cào móng mèo mini', 185000.00, 'PRD-CAT-TOY-002', 165000.00, 'Trụ cào móng nhỏ gọn cho mèo.', 'CAT-TOY-SCRATCHER-MINI', 'cat-toy-scratcher-mini', 28, 'ACTIVE', 70, 'CAT', 12, 1600, @pc_do_choi_meo
WHERE @pc_do_choi_meo IS NOT NULL AND NOT EXISTS (SELECT 1 FROM products WHERE product_code = 'PRD-CAT-TOY-002');

INSERT INTO products (created_at, updated_at, is_active, average_rating, barcode, brand, currency_code, deleted_at, description, is_featured, is_hot, main_image_url, name, price_amount, product_code, sale_price_amount, short_description, sku, slug, sold_quantity, status, stock_quantity, target_species, total_reviews, weight_gram, category_id)
SELECT NOW(6), NOW(6), b'1', 4.40, NULL, 'PetGo Select', 'VND', NULL, 'Dụng cụ nhỏ gọn tạo âm thanh nhất quán khi dạy lệnh cho chó mèo.', b'0', b'0', 'https://images.unsplash.com/photo-1601758125946-6ec2ef64daf8?auto=format&fit=crop&q=80&w=800', 'Clicker huấn luyện chó mèo', 39000.00, 'PRD-ALL-TRAIN-001', 35000.00, 'Clicker dùng cho huấn luyện phản xạ.', 'ALL-TRAIN-CLICKER', 'all-train-clicker', 96, 'ACTIVE', 250, 'ALL', 14, 40, @pc_dung_cu_huan_luyen
WHERE @pc_dung_cu_huan_luyen IS NOT NULL AND NOT EXISTS (SELECT 1 FROM products WHERE product_code = 'PRD-ALL-TRAIN-001');

INSERT INTO products (created_at, updated_at, is_active, average_rating, barcode, brand, currency_code, deleted_at, description, is_featured, is_hot, main_image_url, name, price_amount, product_code, sale_price_amount, short_description, sku, slug, sold_quantity, status, stock_quantity, target_species, total_reviews, weight_gram, category_id)
SELECT NOW(6), NOW(6), b'1', 4.50, NULL, 'PetGo Select', 'VND', NULL, 'Thấm hút nhanh, dùng cho chó con, chó già hoặc khi nuôi trong căn hộ.', b'0', b'0', 'https://images.unsplash.com/photo-1587300003388-59208cc962cb?auto=format&fit=crop&q=80&w=800', 'Tấm lót vệ sinh chó 50 miếng', 145000.00, 'PRD-DOG-PAD-001', 129000.00, 'Tấm lót hỗ trợ dạy đi vệ sinh.', 'DOG-PAD-TRAINING-50PCS', 'dog-pad-training-50pcs', 55, 'ACTIVE', 130, 'DOG', 18, 1200, @pc_dung_cu_huan_luyen
WHERE @pc_dung_cu_huan_luyen IS NOT NULL AND NOT EXISTS (SELECT 1 FROM products WHERE product_code = 'PRD-DOG-PAD-001');

INSERT INTO products (created_at, updated_at, is_active, average_rating, barcode, brand, currency_code, deleted_at, description, is_featured, is_hot, main_image_url, name, price_amount, product_code, sale_price_amount, short_description, sku, slug, sold_quantity, status, stock_quantity, target_species, total_reviews, weight_gram, category_id)
SELECT NOW(6), NOW(6), b'1', 4.60, NULL, 'PetGo Select', 'VND', NULL, 'Dây chắc, tay cầm êm, có chi tiết phản quang hỗ trợ an toàn khi đi dạo.', b'1', b'0', 'https://images.unsplash.com/photo-1530281700549-e82e7bf110d6?auto=format&fit=crop&q=80&w=800', 'Dây dắt phản quang 1.5m', 129000.00, 'PRD-DOG-LEASH-001', 115000.00, 'Dây dắt phản quang đi dạo buổi tối.', 'DOG-LEASH-REFLECTIVE', 'dog-leash-reflective', 36, 'ACTIVE', 95, 'DOG', 13, 180, @pc_vong_co_day_dat_yem
WHERE @pc_vong_co_day_dat_yem IS NOT NULL AND NOT EXISTS (SELECT 1 FROM products WHERE product_code = 'PRD-DOG-LEASH-001');

INSERT INTO products (created_at, updated_at, is_active, average_rating, barcode, brand, currency_code, deleted_at, description, is_featured, is_hot, main_image_url, name, price_amount, product_code, sale_price_amount, short_description, sku, slug, sold_quantity, status, stock_quantity, target_species, total_reviews, weight_gram, category_id)
SELECT NOW(6), NOW(6), b'1', 4.60, NULL, 'PetGo Select', 'VND', NULL, 'Thiết kế ôm thân, phù hợp đi dạo và hạn chế áp lực lên cổ chó.', b'1', b'0', 'https://images.unsplash.com/photo-1558788353-f76d92427f16?auto=format&fit=crop&q=80&w=800', 'Yếm ngực chống giật size M', 189000.00, 'PRD-DOG-HARNESS-001', 169000.00, 'Yếm ngực êm, giảm kéo cổ.', 'DOG-HARNESS-M', 'dog-harness-m', 29, 'ACTIVE', 80, 'DOG', 10, 260, @pc_vong_co_day_dat_yem
WHERE @pc_vong_co_day_dat_yem IS NOT NULL AND NOT EXISTS (SELECT 1 FROM products WHERE product_code = 'PRD-DOG-HARNESS-001');

INSERT INTO products (created_at, updated_at, is_active, average_rating, barcode, brand, currency_code, deleted_at, description, is_featured, is_hot, main_image_url, name, price_amount, product_code, sale_price_amount, short_description, sku, slug, sold_quantity, status, stock_quantity, target_species, total_reviews, weight_gram, category_id)
SELECT NOW(6), NOW(6), b'1', 4.30, NULL, 'PetGo Select', 'VND', NULL, 'Phụ kiện nhỏ gọn, khóa dễ tháo và chuông nhẹ giúp định vị mèo trong nhà.', b'0', b'0', 'https://images.unsplash.com/photo-1536589961747-e239b2abbec2?auto=format&fit=crop&q=80&w=800', 'Vòng cổ chuông mèo dễ tháo', 39000.00, 'PRD-CAT-COLLAR-001', 35000.00, 'Vòng cổ nhẹ có chuông cho mèo.', 'CAT-COLLAR-BELL', 'cat-collar-bell', 88, 'ACTIVE', 190, 'CAT', 11, 30, @pc_vong_co_day_dat_yem
WHERE @pc_vong_co_day_dat_yem IS NOT NULL AND NOT EXISTS (SELECT 1 FROM products WHERE product_code = 'PRD-CAT-COLLAR-001');

INSERT INTO products (created_at, updated_at, is_active, average_rating, barcode, brand, currency_code, deleted_at, description, is_featured, is_hot, main_image_url, name, price_amount, product_code, sale_price_amount, short_description, sku, slug, sold_quantity, status, stock_quantity, target_species, total_reviews, weight_gram, category_id)
SELECT NOW(6), NOW(6), b'1', 4.50, NULL, 'PetGo Select', 'VND', NULL, 'Bát ăn inox bền, ít bám mùi và phù hợp thức ăn hạt hoặc pate.', b'0', b'0', 'https://images.unsplash.com/photo-1576201836106-db1758fd1c97?auto=format&fit=crop&q=80&w=800', 'Bát inox chống trượt cho chó mèo', 75000.00, 'PRD-ALL-BOWL-001', 65000.00, 'Bát inox dễ vệ sinh, đáy chống trượt.', 'ALL-BOWL-STAINLESS', 'all-bowl-stainless', 74, 'ACTIVE', 160, 'ALL', 16, 240, @pc_bat_an_binh_nuoc
WHERE @pc_bat_an_binh_nuoc IS NOT NULL AND NOT EXISTS (SELECT 1 FROM products WHERE product_code = 'PRD-ALL-BOWL-001');

INSERT INTO products (created_at, updated_at, is_active, average_rating, barcode, brand, currency_code, deleted_at, description, is_featured, is_hot, main_image_url, name, price_amount, product_code, sale_price_amount, short_description, sku, slug, sold_quantity, status, stock_quantity, target_species, total_reviews, weight_gram, category_id)
SELECT NOW(6), NOW(6), b'1', 4.50, NULL, 'PetGo Select', 'VND', NULL, 'Thiết kế một tay, có khay uống tích hợp, tiện dùng khi đi dạo hoặc đi chơi.', b'0', b'1', 'https://images.unsplash.com/photo-1591946614720-90a587da4a36?auto=format&fit=crop&q=80&w=800', 'Bình nước du lịch 350ml', 99000.00, 'PRD-ALL-BOTTLE-001', 89000.00, 'Bình nước mang theo khi đi dạo.', 'ALL-BOTTLE-TRAVEL-350ML', 'all-bottle-travel-350ml', 42, 'ACTIVE', 120, 'ALL', 9, 350, @pc_bat_an_binh_nuoc
WHERE @pc_bat_an_binh_nuoc IS NOT NULL AND NOT EXISTS (SELECT 1 FROM products WHERE product_code = 'PRD-ALL-BOTTLE-001');

INSERT INTO products (created_at, updated_at, is_active, average_rating, barcode, brand, currency_code, deleted_at, description, is_featured, is_hot, main_image_url, name, price_amount, product_code, sale_price_amount, short_description, sku, slug, sold_quantity, status, stock_quantity, target_species, total_reviews, weight_gram, category_id)
SELECT NOW(6), NOW(6), b'1', 4.70, NULL, 'PetGo Select', 'VND', NULL, 'Nệm tròn lông mềm, phù hợp thú cưng nhỏ và vừa, dễ đặt trong phòng khách hoặc phòng ngủ.', b'1', b'0', 'https://images.unsplash.com/photo-1596492784531-6e6eb5ea9993?auto=format&fit=crop&q=80&w=800', 'Nệm tròn lông mềm size M', 285000.00, 'PRD-ALL-BED-001', 259000.00, 'Nệm ngủ êm cho chó mèo.', 'ALL-BED-ROUND-M', 'all-bed-round-m', 25, 'ACTIVE', 70, 'ALL', 12, 1100, @pc_chuong_nem_tui_van_chuyen
WHERE @pc_chuong_nem_tui_van_chuyen IS NOT NULL AND NOT EXISTS (SELECT 1 FROM products WHERE product_code = 'PRD-ALL-BED-001');

INSERT INTO products (created_at, updated_at, is_active, average_rating, barcode, brand, currency_code, deleted_at, description, is_featured, is_hot, main_image_url, name, price_amount, product_code, sale_price_amount, short_description, sku, slug, sold_quantity, status, stock_quantity, target_species, total_reviews, weight_gram, category_id)
SELECT NOW(6), NOW(6), b'1', 4.60, NULL, 'PetGo Select', 'VND', NULL, 'Túi có lưới thoáng khí, phù hợp đi khám, đi spa hoặc di chuyển ngắn.', b'1', b'0', 'https://images.unsplash.com/photo-1583512603805-3cc6b41f3edb?auto=format&fit=crop&q=80&w=800', 'Túi vận chuyển thú cưng size M', 489000.00, 'PRD-ALL-CARRIER-001', 449000.00, 'Túi vận chuyển thoáng khí cho chó mèo.', 'ALL-CARRIER-BAG-M', 'all-carrier-bag-m', 17, 'ACTIVE', 45, 'ALL', 8, 1450, @pc_chuong_nem_tui_van_chuyen
WHERE @pc_chuong_nem_tui_van_chuyen IS NOT NULL AND NOT EXISTS (SELECT 1 FROM products WHERE product_code = 'PRD-ALL-CARRIER-001');

INSERT INTO products (created_at, updated_at, is_active, average_rating, barcode, brand, currency_code, deleted_at, description, is_featured, is_hot, main_image_url, name, price_amount, product_code, sale_price_amount, short_description, sku, slug, sold_quantity, status, stock_quantity, target_species, total_reviews, weight_gram, category_id)
SELECT NOW(6), NOW(6), b'1', 4.20, NULL, 'PetGo Select', 'VND', NULL, 'Sản phẩm hỗ trợ bảo vệ chó khi đi dạo, phù hợp dùng cùng lịch chăm sóc định kỳ.', b'0', b'0', 'https://images.unsplash.com/photo-1543466835-00a7907e9de1?auto=format&fit=crop&q=80&w=800', 'Vòng hỗ trợ phòng ve rận cho chó', 110000.00, 'PRD-DOG-FLEA-001', 99000.00, 'Vòng hỗ trợ phòng ve rận khi ra ngoài.', 'DOG-FLEA-COLLAR', 'dog-flea-collar', 37, 'ACTIVE', 100, 'DOG', 10, 80, @pc_phong_ve_ran_bao_ve
WHERE @pc_phong_ve_ran_bao_ve IS NOT NULL AND NOT EXISTS (SELECT 1 FROM products WHERE product_code = 'PRD-DOG-FLEA-001');

INSERT INTO products (created_at, updated_at, is_active, average_rating, barcode, brand, currency_code, deleted_at, description, is_featured, is_hot, main_image_url, name, price_amount, product_code, sale_price_amount, short_description, sku, slug, sold_quantity, status, stock_quantity, target_species, total_reviews, weight_gram, category_id)
SELECT NOW(6), NOW(6), b'1', 4.40, NULL, 'PetGo Select', 'VND', NULL, 'Hỗ trợ làm sạch tai định kỳ, giảm bụi bẩn và mùi khó chịu quanh tai.', b'0', b'0', 'https://images.unsplash.com/photo-1598133894008-61f7fdb8cc3a?auto=format&fit=crop&q=80&w=800', 'Dung dịch vệ sinh tai chó mèo 100ml', 89000.00, 'PRD-ALL-EAR-001', 79000.00, 'Dung dịch vệ sinh tai dịu nhẹ.', 'ALL-EAR-CLEAN-100ML', 'all-ear-clean-100ml', 40, 'ACTIVE', 110, 'ALL', 13, 100, @pc_phong_ve_ran_bao_ve
WHERE @pc_phong_ve_ran_bao_ve IS NOT NULL AND NOT EXISTS (SELECT 1 FROM products WHERE product_code = 'PRD-ALL-EAR-001');

INSERT INTO products (created_at, updated_at, is_active, average_rating, barcode, brand, currency_code, deleted_at, description, is_featured, is_hot, main_image_url, name, price_amount, product_code, sale_price_amount, short_description, sku, slug, sold_quantity, status, stock_quantity, target_species, total_reviews, weight_gram, category_id)
SELECT NOW(6), NOW(6), b'1', 4.40, NULL, 'PetGo Select', 'VND', NULL, 'Dùng cùng bàn chải hoặc gạc, hỗ trợ hơi thở thơm mát và giảm mảng bám nhẹ.', b'0', b'0', 'https://images.unsplash.com/photo-1548199973-03cce0bbc87b?auto=format&fit=crop&q=80&w=800', 'Gel vệ sinh răng miệng thú cưng 70g', 125000.00, 'PRD-ALL-DENTAL-001', 109000.00, 'Gel hỗ trợ làm sạch răng miệng.', 'ALL-DENTAL-GEL-70G', 'all-dental-gel-70g', 26, 'ACTIVE', 95, 'ALL', 7, 70, @pc_rang_mieng_cham_soc
WHERE @pc_rang_mieng_cham_soc IS NOT NULL AND NOT EXISTS (SELECT 1 FROM products WHERE product_code = 'PRD-ALL-DENTAL-001');

INSERT INTO products (created_at, updated_at, is_active, average_rating, barcode, brand, currency_code, deleted_at, description, is_featured, is_hot, main_image_url, name, price_amount, product_code, sale_price_amount, short_description, sku, slug, sold_quantity, status, stock_quantity, target_species, total_reviews, weight_gram, category_id)
SELECT NOW(6), NOW(6), b'1', 4.30, NULL, 'PetGo Select', 'VND', NULL, 'Dùng để làm sạch nhẹ vùng da bên ngoài, nên hỏi bác sĩ thú y nếu vết thương nặng.', b'0', b'0', 'https://images.unsplash.com/photo-1601758123927-196fefb5b65e?auto=format&fit=crop&q=80&w=800', 'Xịt sát khuẩn nhẹ cho thú cưng 100ml', 59000.00, 'PRD-ALL-FIRSTAID-001', 52000.00, 'Xịt vệ sinh vùng da nhỏ.', 'ALL-FIRSTAID-SPRAY', 'all-firstaid-spray', 18, 'ACTIVE', 90, 'ALL', 6, 100, @pc_phong_ve_ran_bao_ve
WHERE @pc_phong_ve_ran_bao_ve IS NOT NULL AND NOT EXISTS (SELECT 1 FROM products WHERE product_code = 'PRD-ALL-FIRSTAID-001');

INSERT INTO products (created_at, updated_at, is_active, average_rating, barcode, brand, currency_code, deleted_at, description, is_featured, is_hot, main_image_url, name, price_amount, product_code, sale_price_amount, short_description, sku, slug, sold_quantity, status, stock_quantity, target_species, total_reviews, weight_gram, category_id)
SELECT NOW(6), NOW(6), b'1', 4.60, NULL, 'PetGo Select', 'VND', NULL, 'Tiện lau nhanh sau khi đi dạo, trước khi lên sofa hoặc vệ sinh vùng lông bẩn.', b'0', b'1', 'https://images.unsplash.com/photo-1601758176175-45914394491c?auto=format&fit=crop&q=80&w=800', 'Khăn ướt thú cưng 80 tờ', 49000.00, 'PRD-ALL-WIPE-001', 43000.00, 'Khăn ướt lau chân, lông và đồ dùng.', 'ALL-WIPE-80PCS', 'all-wipe-80pcs', 112, 'ACTIVE', 220, 'ALL', 20, 350, @pc_khay_cat_ve_sinh_nha
WHERE @pc_khay_cat_ve_sinh_nha IS NOT NULL AND NOT EXISTS (SELECT 1 FROM products WHERE product_code = 'PRD-ALL-WIPE-001');

INSERT INTO products (created_at, updated_at, is_active, average_rating, barcode, brand, currency_code, deleted_at, description, is_featured, is_hot, main_image_url, name, price_amount, product_code, sale_price_amount, short_description, sku, slug, sold_quantity, status, stock_quantity, target_species, total_reviews, weight_gram, category_id)
SELECT NOW(6), NOW(6), b'1', 4.20, NULL, 'PetGo Select', 'VND', NULL, 'Giúp hạn chế ướt lông khi đi dạo trời mưa nhẹ hoặc di chuyển ngắn.', b'0', b'0', 'https://images.unsplash.com/photo-1560807707-8cc77767d783?auto=format&fit=crop&q=80&w=800', 'Áo mưa chó mèo size M', 99000.00, 'PRD-ALL-RAINCOAT-001', 89000.00, 'Áo mưa nhẹ cho thú cưng.', 'ALL-RAINCOAT-M', 'all-raincoat-m', 16, 'ACTIVE', 75, 'ALL', 5, 180, @pc_phong_ve_ran_bao_ve
WHERE @pc_phong_ve_ran_bao_ve IS NOT NULL AND NOT EXISTS (SELECT 1 FROM products WHERE product_code = 'PRD-ALL-RAINCOAT-001');

INSERT INTO products (created_at, updated_at, is_active, average_rating, barcode, brand, currency_code, deleted_at, description, is_featured, is_hot, main_image_url, name, price_amount, product_code, sale_price_amount, short_description, sku, slug, sold_quantity, status, stock_quantity, target_species, total_reviews, weight_gram, category_id)
SELECT NOW(6), NOW(6), b'1', 4.10, NULL, 'PetGo Select', 'VND', NULL, 'Thiết kế thoáng khí, dùng trong các tình huống cần kiểm soát tạm thời và có giám sát.', b'0', b'0', 'https://images.unsplash.com/photo-1561037404-61cd46aa615b?auto=format&fit=crop&q=80&w=800', 'Rọ mõm mềm thoáng khí size M', 89000.00, 'PRD-DOG-MUZZLE-001', 79000.00, 'Rọ mõm mềm dùng khi cần kiểm soát an toàn.', 'DOG-MUZZLE-SOFT-M', 'dog-muzzle-soft-m', 12, 'ACTIVE', 80, 'DOG', 4, 120, @pc_phong_ve_ran_bao_ve
WHERE @pc_phong_ve_ran_bao_ve IS NOT NULL AND NOT EXISTS (SELECT 1 FROM products WHERE product_code = 'PRD-DOG-MUZZLE-001');


-- 3) Extra service catalog entries

SET @sc_1 = (SELECT id FROM service_categories WHERE name = 'Vệ sinh tai, móng, tuyến hôi' ORDER BY id LIMIT 1);

SET @sc_2 = (SELECT id FROM service_categories WHERE name = 'Spa trị liệu' ORDER BY id LIMIT 1);

SET @sc_3 = (SELECT id FROM service_categories WHERE name = 'Tắm sấy chó mèo' ORDER BY id LIMIT 1);

SET @sc_4 = (SELECT id FROM service_categories WHERE name = 'Cắt tỉa theo giống' ORDER BY id LIMIT 1);

SET @sc_5 = (SELECT id FROM service_categories WHERE name = 'Lưu trú mèo' ORDER BY id LIMIT 1);

SET @sc_6 = (SELECT id FROM service_categories WHERE name = 'Daycare nửa ngày' ORDER BY id LIMIT 1);

SET @sc_7 = (SELECT id FROM service_categories WHERE name = 'Trông giữ tại nhà' ORDER BY id LIMIT 1);

SET @sc_8 = (SELECT id FROM service_categories WHERE name = 'Tư vấn dinh dưỡng' ORDER BY id LIMIT 1);

SET @sc_9 = (SELECT id FROM service_categories WHERE name = 'Tiêm vaccine định kỳ' ORDER BY id LIMIT 1);

SET @sc_10 = (SELECT id FROM service_categories WHERE name = 'Chỉnh hành vi' ORDER BY id LIMIT 1);

SET @sc_11 = (SELECT id FROM service_categories WHERE name = 'Đưa đón spa/phòng khám' ORDER BY id LIMIT 1);

SET @sc_12 = (SELECT id FROM service_categories WHERE name = 'Đi vệ sinh đúng chỗ' ORDER BY id LIMIT 1);

SET @sc_13 = (SELECT id FROM service_categories WHERE name = 'Lưu trú chó' ORDER BY id LIMIT 1);

SET @sc_14 = (SELECT id FROM service_categories WHERE name = 'Khám tổng quát' ORDER BY id LIMIT 1);

SET @sc_15 = (SELECT id FROM service_categories WHERE name = 'Dắt chó đi dạo' ORDER BY id LIMIT 1);

INSERT INTO services (created_at, updated_at, is_active, base_price_amount, currency_code, default_duration_minutes, description, name, price_unit, requires_consultation, service_code, short_description, slug, category_id)
SELECT NOW(6), NOW(6), b'1', 90000.00, 'VND', 30, 'Cắt móng, mài móng nhẹ và vệ sinh tai ngoài bằng sản phẩm phù hợp.', 'Cắt móng & vệ sinh tai', 'SESSION', b'0', 'SVC-SEED-NAIL-EAR', 'Vệ sinh tai móng nhanh gọn cho chó mèo.', 'cat-mong-ve-sinh-tai', @sc_1
WHERE @sc_1 IS NOT NULL AND NOT EXISTS (SELECT 1 FROM services WHERE service_code = 'SVC-SEED-NAIL-EAR');

INSERT INTO services (created_at, updated_at, is_active, base_price_amount, currency_code, default_duration_minutes, description, name, price_unit, requires_consultation, service_code, short_description, slug, category_id)
SELECT NOW(6), NOW(6), b'1', 280000.00, 'VND', 75, 'Tắm spa, massage nhẹ, xịt dưỡng và chăm sóc lông cho thú cưng cần thư giãn.', 'Spa khử mùi & dưỡng lông', 'SESSION', b'0', 'SVC-SEED-SPA-COAT', 'Gói spa giúp lông mềm và giảm mùi.', 'spa-khu-mui-duong-long', @sc_2
WHERE @sc_2 IS NOT NULL AND NOT EXISTS (SELECT 1 FROM services WHERE service_code = 'SVC-SEED-SPA-COAT');

INSERT INTO services (created_at, updated_at, is_active, base_price_amount, currency_code, default_duration_minutes, description, name, price_unit, requires_consultation, service_code, short_description, slug, category_id)
SELECT NOW(6), NOW(6), b'1', 220000.00, 'VND', 70, 'Sử dụng sản phẩm tắm phù hợp để hỗ trợ làm sạch, giảm mùi và chăm sóc da lông.', 'Tắm hỗ trợ phòng ve rận', 'SESSION', b'1', 'SVC-SEED-FLEA-BATH', 'Tắm làm sạch sâu cho thú cưng hay đi ngoài trời.', 'tam-ho-tro-phong-ve-ran', @sc_3
WHERE @sc_3 IS NOT NULL AND NOT EXISTS (SELECT 1 FROM services WHERE service_code = 'SVC-SEED-FLEA-BATH');

INSERT INTO services (created_at, updated_at, is_active, base_price_amount, currency_code, default_duration_minutes, description, name, price_unit, requires_consultation, service_code, short_description, slug, category_id)
SELECT NOW(6), NOW(6), b'1', 420000.00, 'VND', 120, 'Dịch vụ grooming cho mèo lông dài, xử lý lông rối nhẹ và tỉa gọn vùng cần thiết.', 'Grooming mèo lông dài', 'SESSION', b'1', 'SVC-SEED-CAT-GROOM', 'Chải gỡ rối và tỉa gọn lông mèo.', 'grooming-meo-long-dai', @sc_4
WHERE @sc_4 IS NOT NULL AND NOT EXISTS (SELECT 1 FROM services WHERE service_code = 'SVC-SEED-CAT-GROOM');

INSERT INTO services (created_at, updated_at, is_active, base_price_amount, currency_code, default_duration_minutes, description, name, price_unit, requires_consultation, service_code, short_description, slug, category_id)
SELECT NOW(6), NOW(6), b'1', 380000.00, 'VND', 1440, 'Cho ăn, vệ sinh khay cát, theo dõi tình trạng và cập nhật ảnh trong thời gian lưu trú.', 'Khách sạn mèo qua đêm', 'DAY', b'1', 'SVC-SEED-CAT-HOTEL', 'Phòng lưu trú yên tĩnh cho mèo.', 'khach-san-meo-qua-dem', @sc_5
WHERE @sc_5 IS NOT NULL AND NOT EXISTS (SELECT 1 FROM services WHERE service_code = 'SVC-SEED-CAT-HOTEL');

INSERT INTO services (created_at, updated_at, is_active, base_price_amount, currency_code, default_duration_minutes, description, name, price_unit, requires_consultation, service_code, short_description, slug, category_id)
SELECT NOW(6), NOW(6), b'1', 220000.00, 'VND', 240, 'Phù hợp khi chủ bận ngắn hạn, bao gồm trông giữ, cho ăn theo dặn dò và vui chơi nhẹ.', 'Daycare nửa ngày', 'SESSION', b'0', 'SVC-SEED-DAYCARE-HALF', 'Trông giữ thú cưng trong nửa ngày.', 'daycare-nua-ngay', @sc_6
WHERE @sc_6 IS NOT NULL AND NOT EXISTS (SELECT 1 FROM services WHERE service_code = 'SVC-SEED-DAYCARE-HALF');

INSERT INTO services (created_at, updated_at, is_active, base_price_amount, currency_code, default_duration_minutes, description, name, price_unit, requires_consultation, service_code, short_description, slug, category_id)
SELECT NOW(6), NOW(6), b'1', 250000.00, 'VND', 120, 'Cho ăn, thay nước, dọn vệ sinh cơ bản và chơi cùng thú cưng tại nhà khách.', 'Pet sitting tại nhà 2 giờ', 'SESSION', b'1', 'SVC-SEED-HOME-SITTING', 'Người chăm sóc đến nhà theo lịch.', 'pet-sitting-tai-nha-2-gio', @sc_7
WHERE @sc_7 IS NOT NULL AND NOT EXISTS (SELECT 1 FROM services WHERE service_code = 'SVC-SEED-HOME-SITTING');

INSERT INTO services (created_at, updated_at, is_active, base_price_amount, currency_code, default_duration_minutes, description, name, price_unit, requires_consultation, service_code, short_description, slug, category_id)
SELECT NOW(6), NOW(6), b'1', 180000.00, 'VND', 45, 'Đánh giá cân nặng, thói quen ăn uống và gợi ý khẩu phần theo độ tuổi, giống và tình trạng.', 'Tư vấn dinh dưỡng thú cưng', 'SESSION', b'1', 'SVC-SEED-NUTRITION', 'Tư vấn khẩu phần và lịch ăn phù hợp.', 'tu-van-dinh-duong-thu-cung', @sc_8
WHERE @sc_8 IS NOT NULL AND NOT EXISTS (SELECT 1 FROM services WHERE service_code = 'SVC-SEED-NUTRITION');

INSERT INTO services (created_at, updated_at, is_active, base_price_amount, currency_code, default_duration_minutes, description, name, price_unit, requires_consultation, service_code, short_description, slug, category_id)
SELECT NOW(6), NOW(6), b'1', 350000.00, 'VND', 30, 'Kiểm tra tình trạng trước tiêm, tư vấn lịch vaccine và theo dõi sau tiêm theo quy trình.', 'Gói tiêm phòng định kỳ', 'SESSION', b'1', 'SVC-SEED-VACCINE', 'Tư vấn và tiêm vaccine theo lịch.', 'goi-tiem-phong-dinh-ky', @sc_9
WHERE @sc_9 IS NOT NULL AND NOT EXISTS (SELECT 1 FROM services WHERE service_code = 'SVC-SEED-VACCINE');

INSERT INTO services (created_at, updated_at, is_active, base_price_amount, currency_code, default_duration_minutes, description, name, price_unit, requires_consultation, service_code, short_description, slug, category_id)
SELECT NOW(6), NOW(6), b'1', 450000.00, 'VND', 90, 'Đánh giá nguyên nhân, hướng dẫn bài tập giảm sủa, cắn phá hoặc lo âu khi xa chủ.', 'Chỉnh hành vi sủa/cắn phá', 'SESSION', b'1', 'SVC-SEED-BEHAVIOR', 'Buổi tư vấn và luyện tập chỉnh hành vi.', 'chinh-hanh-vi-sua-can-pha', @sc_10
WHERE @sc_10 IS NOT NULL AND NOT EXISTS (SELECT 1 FROM services WHERE service_code = 'SVC-SEED-BEHAVIOR');

INSERT INTO services (created_at, updated_at, is_active, base_price_amount, currency_code, default_duration_minutes, description, name, price_unit, requires_consultation, service_code, short_description, slug, category_id)
SELECT NOW(6), NOW(6), b'1', 150000.00, 'VND', 45, 'Đưa đón thú cưng trong khu vực nội thành, có xác nhận bàn giao theo lịch hẹn.', 'Pet taxi nội thành', 'TRIP', b'0', 'SVC-SEED-PET-TAXI', 'Đưa đón thú cưng đi spa/phòng khám.', 'pet-taxi-noi-thanh', @sc_11
WHERE @sc_11 IS NOT NULL AND NOT EXISTS (SELECT 1 FROM services WHERE service_code = 'SVC-SEED-PET-TAXI');

INSERT INTO services (created_at, updated_at, is_active, base_price_amount, currency_code, default_duration_minutes, description, name, price_unit, requires_consultation, service_code, short_description, slug, category_id)
SELECT NOW(6), NOW(6), b'1', 380000.00, 'VND', 90, 'Hướng dẫn chủ và thú cưng tạo thói quen đi vệ sinh đúng nơi, phù hợp chó con hoặc thú cưng mới về nhà.', 'Huấn luyện đi vệ sinh đúng chỗ', 'SESSION', b'1', 'SVC-SEED-POTTY', 'Dạy thói quen đi vệ sinh đúng vị trí.', 'huan-luyen-di-ve-sinh-dung-cho', @sc_12
WHERE @sc_12 IS NOT NULL AND NOT EXISTS (SELECT 1 FROM services WHERE service_code = 'SVC-SEED-POTTY');

INSERT INTO services (created_at, updated_at, is_active, base_price_amount, currency_code, default_duration_minutes, description, name, price_unit, requires_consultation, service_code, short_description, slug, category_id)
SELECT NOW(6), NOW(6), b'1', 550000.00, 'VND', 1440, 'Phòng riêng, cho ăn theo dặn dò, vận động nhẹ và cập nhật hình ảnh trong ngày.', 'Khách sạn chó phòng riêng', 'DAY', b'1', 'SVC-SEED-DOG-HOTEL-PRIVATE', 'Lưu trú chó với không gian riêng.', 'khach-san-cho-phong-rieng', @sc_13
WHERE @sc_13 IS NOT NULL AND NOT EXISTS (SELECT 1 FROM services WHERE service_code = 'SVC-SEED-DOG-HOTEL-PRIVATE');

INSERT INTO services (created_at, updated_at, is_active, base_price_amount, currency_code, default_duration_minutes, description, name, price_unit, requires_consultation, service_code, short_description, slug, category_id)
SELECT NOW(6), NOW(6), b'1', 260000.00, 'VND', 45, 'Đánh giá da lông, tư vấn chăm sóc, vệ sinh và hướng xử lý khi có dấu hiệu bất thường.', 'Khám da lông cơ bản', 'SESSION', b'1', 'SVC-SEED-SKIN-CHECK', 'Kiểm tra tình trạng da lông.', 'kham-da-long-co-ban', @sc_14
WHERE @sc_14 IS NOT NULL AND NOT EXISTS (SELECT 1 FROM services WHERE service_code = 'SVC-SEED-SKIN-CHECK');

INSERT INTO services (created_at, updated_at, is_active, base_price_amount, currency_code, default_duration_minutes, description, name, price_unit, requires_consultation, service_code, short_description, slug, category_id)
SELECT NOW(6), NOW(6), b'1', 650000.00, 'VND', 420, 'Dắt chó đi dạo theo lịch cố định, ghi chú tình trạng sau mỗi buổi và hỗ trợ vận động đều đặn.', 'Gói dắt chó đi dạo theo tuần', 'SESSION', b'0', 'SVC-SEED-WALK-WEEK', 'Gói đi dạo nhiều buổi trong tuần.', 'goi-dat-cho-di-dao-theo-tuan', @sc_15
WHERE @sc_15 IS NOT NULL AND NOT EXISTS (SELECT 1 FROM services WHERE service_code = 'SVC-SEED-WALK-WEEK');


-- 4) Attach extra services to the sample provider if it exists

SET @provider_seed_id = (SELECT id FROM provider_profiles WHERE slug = 'petgo-sample-provider' OR provider_code = 'PRV-SAMPLE-PROVIDER' ORDER BY id LIMIT 1);

SET @svc_id_1 = (SELECT id FROM services WHERE service_code = 'SVC-SEED-NAIL-EAR' ORDER BY id LIMIT 1);

SET @svc_cat_id_1 = (SELECT category_id FROM services WHERE id = @svc_id_1 ORDER BY id LIMIT 1);

INSERT INTO provider_services (created_at, updated_at, is_active, approval_status, booking_buffer_minutes, buffer_after_minutes, capacity_per_slot, category_ids, currency_code, custom_name, description, display_order, duration_minutes, duration_type, is_featured, photo_urls, price_amount, price_unit, short_description, provider_id, service_id)
SELECT NOW(6), NOW(6), b'1', 'APPROVED', 0, 10, 1, CAST(@svc_cat_id_1 AS CHAR), 'VND', 'Cắt móng & vệ sinh tai nhanh', 'Cắt móng, mài móng nhẹ và vệ sinh tai ngoài bằng sản phẩm phù hợp.', 70, 30, 'MINUTES', b'0', 'https://images.unsplash.com/photo-1516734212186-a967f81ad0d7?auto=format&fit=crop&q=80&w=800', 90000.00, 'SESSION', 'Vệ sinh tai móng nhanh gọn cho chó mèo.', @provider_seed_id, @svc_id_1
WHERE @provider_seed_id IS NOT NULL AND @svc_id_1 IS NOT NULL AND NOT EXISTS (SELECT 1 FROM provider_services WHERE provider_id = @provider_seed_id AND service_id = @svc_id_1);

SET @svc_id_2 = (SELECT id FROM services WHERE service_code = 'SVC-SEED-SPA-COAT' ORDER BY id LIMIT 1);

SET @svc_cat_id_2 = (SELECT category_id FROM services WHERE id = @svc_id_2 ORDER BY id LIMIT 1);

INSERT INTO provider_services (created_at, updated_at, is_active, approval_status, booking_buffer_minutes, buffer_after_minutes, capacity_per_slot, category_ids, currency_code, custom_name, description, display_order, duration_minutes, duration_type, is_featured, photo_urls, price_amount, price_unit, short_description, provider_id, service_id)
SELECT NOW(6), NOW(6), b'1', 'APPROVED', 0, 10, 1, CAST(@svc_cat_id_2 AS CHAR), 'VND', 'Spa khử mùi & dưỡng lông', 'Tắm spa, massage nhẹ, xịt dưỡng và chăm sóc lông cho thú cưng cần thư giãn.', 71, 75, 'MINUTES', b'1', 'https://images.unsplash.com/photo-1601758124510-52d02ddb7cbd?auto=format&fit=crop&q=80&w=800', 280000.00, 'SESSION', 'Gói spa giúp lông mềm và giảm mùi.', @provider_seed_id, @svc_id_2
WHERE @provider_seed_id IS NOT NULL AND @svc_id_2 IS NOT NULL AND NOT EXISTS (SELECT 1 FROM provider_services WHERE provider_id = @provider_seed_id AND service_id = @svc_id_2);

SET @svc_id_3 = (SELECT id FROM services WHERE service_code = 'SVC-SEED-FLEA-BATH' ORDER BY id LIMIT 1);

SET @svc_cat_id_3 = (SELECT category_id FROM services WHERE id = @svc_id_3 ORDER BY id LIMIT 1);

INSERT INTO provider_services (created_at, updated_at, is_active, approval_status, booking_buffer_minutes, buffer_after_minutes, capacity_per_slot, category_ids, currency_code, custom_name, description, display_order, duration_minutes, duration_type, is_featured, photo_urls, price_amount, price_unit, short_description, provider_id, service_id)
SELECT NOW(6), NOW(6), b'1', 'APPROVED', 0, 10, 1, CAST(@svc_cat_id_3 AS CHAR), 'VND', 'Tắm hỗ trợ phòng ve rận', 'Sử dụng sản phẩm tắm phù hợp để hỗ trợ làm sạch, giảm mùi và chăm sóc da lông.', 72, 70, 'MINUTES', b'0', 'https://images.unsplash.com/photo-1601758123927-196fefb5b65e?auto=format&fit=crop&q=80&w=800', 220000.00, 'SESSION', 'Tắm làm sạch sâu cho thú cưng hay đi ngoài trời.', @provider_seed_id, @svc_id_3
WHERE @provider_seed_id IS NOT NULL AND @svc_id_3 IS NOT NULL AND NOT EXISTS (SELECT 1 FROM provider_services WHERE provider_id = @provider_seed_id AND service_id = @svc_id_3);

SET @svc_id_4 = (SELECT id FROM services WHERE service_code = 'SVC-SEED-CAT-GROOM' ORDER BY id LIMIT 1);

SET @svc_cat_id_4 = (SELECT category_id FROM services WHERE id = @svc_id_4 ORDER BY id LIMIT 1);

INSERT INTO provider_services (created_at, updated_at, is_active, approval_status, booking_buffer_minutes, buffer_after_minutes, capacity_per_slot, category_ids, currency_code, custom_name, description, display_order, duration_minutes, duration_type, is_featured, photo_urls, price_amount, price_unit, short_description, provider_id, service_id)
SELECT NOW(6), NOW(6), b'1', 'APPROVED', 0, 10, 1, CAST(@svc_cat_id_4 AS CHAR), 'VND', 'Grooming mèo lông dài', 'Dịch vụ grooming cho mèo lông dài, xử lý lông rối nhẹ và tỉa gọn vùng cần thiết.', 73, 120, 'MINUTES', b'1', 'https://images.unsplash.com/photo-1574158622682-e40e69881006?auto=format&fit=crop&q=80&w=800', 420000.00, 'SESSION', 'Chải gỡ rối và tỉa gọn lông mèo.', @provider_seed_id, @svc_id_4
WHERE @provider_seed_id IS NOT NULL AND @svc_id_4 IS NOT NULL AND NOT EXISTS (SELECT 1 FROM provider_services WHERE provider_id = @provider_seed_id AND service_id = @svc_id_4);

SET @svc_id_5 = (SELECT id FROM services WHERE service_code = 'SVC-SEED-CAT-HOTEL' ORDER BY id LIMIT 1);

SET @svc_cat_id_5 = (SELECT category_id FROM services WHERE id = @svc_id_5 ORDER BY id LIMIT 1);

INSERT INTO provider_services (created_at, updated_at, is_active, approval_status, booking_buffer_minutes, buffer_after_minutes, capacity_per_slot, category_ids, currency_code, custom_name, description, display_order, duration_minutes, duration_type, is_featured, photo_urls, price_amount, price_unit, short_description, provider_id, service_id)
SELECT NOW(6), NOW(6), b'1', 'APPROVED', 0, 10, 1, CAST(@svc_cat_id_5 AS CHAR), 'VND', 'Khách sạn mèo qua đêm', 'Cho ăn, vệ sinh khay cát, theo dõi tình trạng và cập nhật ảnh trong thời gian lưu trú.', 74, 480, 'MINUTES', b'1', 'https://images.unsplash.com/photo-1513360371669-4adf3dd7dff8?auto=format&fit=crop&q=80&w=800', 380000.00, 'DAY', 'Phòng lưu trú yên tĩnh cho mèo.', @provider_seed_id, @svc_id_5
WHERE @provider_seed_id IS NOT NULL AND @svc_id_5 IS NOT NULL AND NOT EXISTS (SELECT 1 FROM provider_services WHERE provider_id = @provider_seed_id AND service_id = @svc_id_5);

SET @svc_id_6 = (SELECT id FROM services WHERE service_code = 'SVC-SEED-DAYCARE-HALF' ORDER BY id LIMIT 1);

SET @svc_cat_id_6 = (SELECT category_id FROM services WHERE id = @svc_id_6 ORDER BY id LIMIT 1);

INSERT INTO provider_services (created_at, updated_at, is_active, approval_status, booking_buffer_minutes, buffer_after_minutes, capacity_per_slot, category_ids, currency_code, custom_name, description, display_order, duration_minutes, duration_type, is_featured, photo_urls, price_amount, price_unit, short_description, provider_id, service_id)
SELECT NOW(6), NOW(6), b'1', 'APPROVED', 0, 10, 1, CAST(@svc_cat_id_6 AS CHAR), 'VND', 'Daycare nửa ngày', 'Phù hợp khi chủ bận ngắn hạn, bao gồm trông giữ, cho ăn theo dặn dò và vui chơi nhẹ.', 75, 240, 'MINUTES', b'0', 'https://images.unsplash.com/photo-1587300003388-59208cc962cb?auto=format&fit=crop&q=80&w=800', 220000.00, 'SESSION', 'Trông giữ thú cưng trong nửa ngày.', @provider_seed_id, @svc_id_6
WHERE @provider_seed_id IS NOT NULL AND @svc_id_6 IS NOT NULL AND NOT EXISTS (SELECT 1 FROM provider_services WHERE provider_id = @provider_seed_id AND service_id = @svc_id_6);

SET @svc_id_7 = (SELECT id FROM services WHERE service_code = 'SVC-SEED-HOME-SITTING' ORDER BY id LIMIT 1);

SET @svc_cat_id_7 = (SELECT category_id FROM services WHERE id = @svc_id_7 ORDER BY id LIMIT 1);

INSERT INTO provider_services (created_at, updated_at, is_active, approval_status, booking_buffer_minutes, buffer_after_minutes, capacity_per_slot, category_ids, currency_code, custom_name, description, display_order, duration_minutes, duration_type, is_featured, photo_urls, price_amount, price_unit, short_description, provider_id, service_id)
SELECT NOW(6), NOW(6), b'1', 'APPROVED', 0, 10, 1, CAST(@svc_cat_id_7 AS CHAR), 'VND', 'Pet sitting tại nhà 2 giờ', 'Cho ăn, thay nước, dọn vệ sinh cơ bản và chơi cùng thú cưng tại nhà khách.', 76, 120, 'MINUTES', b'0', 'https://images.unsplash.com/photo-1583512603805-3cc6b41f3edb?auto=format&fit=crop&q=80&w=800', 250000.00, 'SESSION', 'Người chăm sóc đến nhà theo lịch.', @provider_seed_id, @svc_id_7
WHERE @provider_seed_id IS NOT NULL AND @svc_id_7 IS NOT NULL AND NOT EXISTS (SELECT 1 FROM provider_services WHERE provider_id = @provider_seed_id AND service_id = @svc_id_7);

SET @svc_id_8 = (SELECT id FROM services WHERE service_code = 'SVC-SEED-NUTRITION' ORDER BY id LIMIT 1);

SET @svc_cat_id_8 = (SELECT category_id FROM services WHERE id = @svc_id_8 ORDER BY id LIMIT 1);

INSERT INTO provider_services (created_at, updated_at, is_active, approval_status, booking_buffer_minutes, buffer_after_minutes, capacity_per_slot, category_ids, currency_code, custom_name, description, display_order, duration_minutes, duration_type, is_featured, photo_urls, price_amount, price_unit, short_description, provider_id, service_id)
SELECT NOW(6), NOW(6), b'1', 'APPROVED', 0, 10, 1, CAST(@svc_cat_id_8 AS CHAR), 'VND', 'Tư vấn dinh dưỡng thú cưng', 'Đánh giá cân nặng, thói quen ăn uống và gợi ý khẩu phần theo độ tuổi, giống và tình trạng.', 77, 45, 'MINUTES', b'0', 'https://images.unsplash.com/photo-1548199973-03cce0bbc87b?auto=format&fit=crop&q=80&w=800', 180000.00, 'SESSION', 'Tư vấn khẩu phần và lịch ăn phù hợp.', @provider_seed_id, @svc_id_8
WHERE @provider_seed_id IS NOT NULL AND @svc_id_8 IS NOT NULL AND NOT EXISTS (SELECT 1 FROM provider_services WHERE provider_id = @provider_seed_id AND service_id = @svc_id_8);

SET @svc_id_9 = (SELECT id FROM services WHERE service_code = 'SVC-SEED-VACCINE' ORDER BY id LIMIT 1);

SET @svc_cat_id_9 = (SELECT category_id FROM services WHERE id = @svc_id_9 ORDER BY id LIMIT 1);

INSERT INTO provider_services (created_at, updated_at, is_active, approval_status, booking_buffer_minutes, buffer_after_minutes, capacity_per_slot, category_ids, currency_code, custom_name, description, display_order, duration_minutes, duration_type, is_featured, photo_urls, price_amount, price_unit, short_description, provider_id, service_id)
SELECT NOW(6), NOW(6), b'1', 'APPROVED', 0, 10, 1, CAST(@svc_cat_id_9 AS CHAR), 'VND', 'Gói tiêm phòng định kỳ', 'Kiểm tra tình trạng trước tiêm, tư vấn lịch vaccine và theo dõi sau tiêm theo quy trình.', 78, 30, 'MINUTES', b'0', 'https://images.unsplash.com/photo-1628009368231-7bb7cfcb0def?auto=format&fit=crop&q=80&w=800', 350000.00, 'SESSION', 'Tư vấn và tiêm vaccine theo lịch.', @provider_seed_id, @svc_id_9
WHERE @provider_seed_id IS NOT NULL AND @svc_id_9 IS NOT NULL AND NOT EXISTS (SELECT 1 FROM provider_services WHERE provider_id = @provider_seed_id AND service_id = @svc_id_9);

SET @svc_id_10 = (SELECT id FROM services WHERE service_code = 'SVC-SEED-BEHAVIOR' ORDER BY id LIMIT 1);

SET @svc_cat_id_10 = (SELECT category_id FROM services WHERE id = @svc_id_10 ORDER BY id LIMIT 1);

INSERT INTO provider_services (created_at, updated_at, is_active, approval_status, booking_buffer_minutes, buffer_after_minutes, capacity_per_slot, category_ids, currency_code, custom_name, description, display_order, duration_minutes, duration_type, is_featured, photo_urls, price_amount, price_unit, short_description, provider_id, service_id)
SELECT NOW(6), NOW(6), b'1', 'APPROVED', 0, 10, 1, CAST(@svc_cat_id_10 AS CHAR), 'VND', 'Chỉnh hành vi sủa/cắn phá', 'Đánh giá nguyên nhân, hướng dẫn bài tập giảm sủa, cắn phá hoặc lo âu khi xa chủ.', 79, 90, 'MINUTES', b'1', 'https://images.unsplash.com/photo-1601758125946-6ec2ef64daf8?auto=format&fit=crop&q=80&w=800', 450000.00, 'SESSION', 'Buổi tư vấn và luyện tập chỉnh hành vi.', @provider_seed_id, @svc_id_10
WHERE @provider_seed_id IS NOT NULL AND @svc_id_10 IS NOT NULL AND NOT EXISTS (SELECT 1 FROM provider_services WHERE provider_id = @provider_seed_id AND service_id = @svc_id_10);

SET @svc_id_11 = (SELECT id FROM services WHERE service_code = 'SVC-SEED-PET-TAXI' ORDER BY id LIMIT 1);

SET @svc_cat_id_11 = (SELECT category_id FROM services WHERE id = @svc_id_11 ORDER BY id LIMIT 1);

INSERT INTO provider_services (created_at, updated_at, is_active, approval_status, booking_buffer_minutes, buffer_after_minutes, capacity_per_slot, category_ids, currency_code, custom_name, description, display_order, duration_minutes, duration_type, is_featured, photo_urls, price_amount, price_unit, short_description, provider_id, service_id)
SELECT NOW(6), NOW(6), b'1', 'APPROVED', 0, 10, 1, CAST(@svc_cat_id_11 AS CHAR), 'VND', 'Pet taxi Quận 1 - nội thành', 'Đưa đón thú cưng trong khu vực nội thành, có xác nhận bàn giao theo lịch hẹn.', 80, 45, 'MINUTES', b'0', 'https://images.unsplash.com/photo-1530281700549-e82e7bf110d6?auto=format&fit=crop&q=80&w=800', 150000.00, 'TRIP', 'Đưa đón thú cưng đi spa/phòng khám.', @provider_seed_id, @svc_id_11
WHERE @provider_seed_id IS NOT NULL AND @svc_id_11 IS NOT NULL AND NOT EXISTS (SELECT 1 FROM provider_services WHERE provider_id = @provider_seed_id AND service_id = @svc_id_11);

SET @svc_id_12 = (SELECT id FROM services WHERE service_code = 'SVC-SEED-POTTY' ORDER BY id LIMIT 1);

SET @svc_cat_id_12 = (SELECT category_id FROM services WHERE id = @svc_id_12 ORDER BY id LIMIT 1);

INSERT INTO provider_services (created_at, updated_at, is_active, approval_status, booking_buffer_minutes, buffer_after_minutes, capacity_per_slot, category_ids, currency_code, custom_name, description, display_order, duration_minutes, duration_type, is_featured, photo_urls, price_amount, price_unit, short_description, provider_id, service_id)
SELECT NOW(6), NOW(6), b'1', 'APPROVED', 0, 10, 1, CAST(@svc_cat_id_12 AS CHAR), 'VND', 'Huấn luyện đi vệ sinh đúng chỗ', 'Hướng dẫn chủ và thú cưng tạo thói quen đi vệ sinh đúng nơi, phù hợp chó con hoặc thú cưng mới về nhà.', 81, 90, 'MINUTES', b'0', 'https://images.unsplash.com/photo-1548199973-03cce0bbc87b?auto=format&fit=crop&q=80&w=800', 380000.00, 'SESSION', 'Dạy thói quen đi vệ sinh đúng vị trí.', @provider_seed_id, @svc_id_12
WHERE @provider_seed_id IS NOT NULL AND @svc_id_12 IS NOT NULL AND NOT EXISTS (SELECT 1 FROM provider_services WHERE provider_id = @provider_seed_id AND service_id = @svc_id_12);

SET @svc_id_13 = (SELECT id FROM services WHERE service_code = 'SVC-SEED-DOG-HOTEL-PRIVATE' ORDER BY id LIMIT 1);

SET @svc_cat_id_13 = (SELECT category_id FROM services WHERE id = @svc_id_13 ORDER BY id LIMIT 1);

INSERT INTO provider_services (created_at, updated_at, is_active, approval_status, booking_buffer_minutes, buffer_after_minutes, capacity_per_slot, category_ids, currency_code, custom_name, description, display_order, duration_minutes, duration_type, is_featured, photo_urls, price_amount, price_unit, short_description, provider_id, service_id)
SELECT NOW(6), NOW(6), b'1', 'APPROVED', 0, 10, 1, CAST(@svc_cat_id_13 AS CHAR), 'VND', 'Khách sạn chó phòng riêng', 'Phòng riêng, cho ăn theo dặn dò, vận động nhẹ và cập nhật hình ảnh trong ngày.', 82, 480, 'MINUTES', b'1', 'https://images.unsplash.com/photo-1601758174114-e711c0cbaa69?auto=format&fit=crop&q=80&w=800', 550000.00, 'DAY', 'Lưu trú chó với không gian riêng.', @provider_seed_id, @svc_id_13
WHERE @provider_seed_id IS NOT NULL AND @svc_id_13 IS NOT NULL AND NOT EXISTS (SELECT 1 FROM provider_services WHERE provider_id = @provider_seed_id AND service_id = @svc_id_13);

SET @svc_id_14 = (SELECT id FROM services WHERE service_code = 'SVC-SEED-SKIN-CHECK' ORDER BY id LIMIT 1);

SET @svc_cat_id_14 = (SELECT category_id FROM services WHERE id = @svc_id_14 ORDER BY id LIMIT 1);

INSERT INTO provider_services (created_at, updated_at, is_active, approval_status, booking_buffer_minutes, buffer_after_minutes, capacity_per_slot, category_ids, currency_code, custom_name, description, display_order, duration_minutes, duration_type, is_featured, photo_urls, price_amount, price_unit, short_description, provider_id, service_id)
SELECT NOW(6), NOW(6), b'1', 'APPROVED', 0, 10, 1, CAST(@svc_cat_id_14 AS CHAR), 'VND', 'Khám da lông cơ bản', 'Đánh giá da lông, tư vấn chăm sóc, vệ sinh và hướng xử lý khi có dấu hiệu bất thường.', 83, 45, 'MINUTES', b'0', 'https://images.unsplash.com/photo-1628009368231-7bb7cfcb0def?auto=format&fit=crop&q=80&w=800', 260000.00, 'SESSION', 'Kiểm tra tình trạng da lông.', @provider_seed_id, @svc_id_14
WHERE @provider_seed_id IS NOT NULL AND @svc_id_14 IS NOT NULL AND NOT EXISTS (SELECT 1 FROM provider_services WHERE provider_id = @provider_seed_id AND service_id = @svc_id_14);

SET @svc_id_15 = (SELECT id FROM services WHERE service_code = 'SVC-SEED-WALK-WEEK' ORDER BY id LIMIT 1);

SET @svc_cat_id_15 = (SELECT category_id FROM services WHERE id = @svc_id_15 ORDER BY id LIMIT 1);

INSERT INTO provider_services (created_at, updated_at, is_active, approval_status, booking_buffer_minutes, buffer_after_minutes, capacity_per_slot, category_ids, currency_code, custom_name, description, display_order, duration_minutes, duration_type, is_featured, photo_urls, price_amount, price_unit, short_description, provider_id, service_id)
SELECT NOW(6), NOW(6), b'1', 'APPROVED', 0, 10, 1, CAST(@svc_cat_id_15 AS CHAR), 'VND', 'Gói dắt chó đi dạo theo tuần', 'Dắt chó đi dạo theo lịch cố định, ghi chú tình trạng sau mỗi buổi và hỗ trợ vận động đều đặn.', 84, 420, 'MINUTES', b'1', 'https://images.unsplash.com/photo-1548199973-03cce0bbc87b?auto=format&fit=crop&q=80&w=800', 650000.00, 'SESSION', 'Gói đi dạo nhiều buổi trong tuần.', @provider_seed_id, @svc_id_15
WHERE @provider_seed_id IS NOT NULL AND @svc_id_15 IS NOT NULL AND NOT EXISTS (SELECT 1 FROM provider_services WHERE provider_id = @provider_seed_id AND service_id = @svc_id_15);


-- 5) Home sliders for demo homepage

INSERT INTO home_sliders (created_at, updated_at, is_active, cta_label, cta_url, image_url, sort_order, subtitle, title)
SELECT NOW(6), NOW(6), b'1', 'Đặt lịch ngay', '/services', 'https://images.unsplash.com/photo-1601758124510-52d02ddb7cbd?auto=format&fit=crop&q=80&w=1600', 1, 'Đặt lịch spa, grooming, khám sức khỏe và mua đồ dùng thú cưng chỉ trong một nơi.', 'PetGo chăm sóc thú cưng trọn gói'
WHERE NOT EXISTS (SELECT 1 FROM home_sliders WHERE title = 'PetGo chăm sóc thú cưng trọn gói');

INSERT INTO home_sliders (created_at, updated_at, is_active, cta_label, cta_url, image_url, sort_order, subtitle, title)
SELECT NOW(6), NOW(6), b'1', 'Mua sắm ngay', '/shop', 'https://images.unsplash.com/photo-1601758228041-f3b2795255f1?auto=format&fit=crop&q=80&w=1600', 2, 'Thức ăn, phụ kiện, đồ chơi và sản phẩm vệ sinh được chọn lọc cho chó mèo.', 'Shop thú cưng PetGo'
WHERE NOT EXISTS (SELECT 1 FROM home_sliders WHERE title = 'Shop thú cưng PetGo');

INSERT INTO home_sliders (created_at, updated_at, is_active, cta_label, cta_url, image_url, sort_order, subtitle, title)
SELECT NOW(6), NOW(6), b'1', 'Xem provider', '/providers', 'https://images.unsplash.com/photo-1516734212186-a967f81ad0d7?auto=format&fit=crop&q=80&w=1600', 3, 'Tìm provider gần bạn, xem giá rõ ràng và đặt lịch nhanh chóng.', 'Grooming đẹp hơn mỗi ngày'
WHERE NOT EXISTS (SELECT 1 FROM home_sliders WHERE title = 'Grooming đẹp hơn mỗi ngày');


-- 6) Membership plans and features

INSERT INTO membership_plans (created_at, updated_at, is_active, billing_cycle, currency_code, description, discount_percent, monthly_voucher_amount, name, plan_code, is_popular, price_amount, priority_booking, priority_support, slug, sort_order)
SELECT NOW(6), NOW(6), b'1', 'MONTHLY', 'VND', 'Gói tiết kiệm cho người dùng đặt dịch vụ định kỳ.', 5.00, 50000.00, 'PetGo Silver', 'PETGO-SILVER', b'0', 69000.00, b'0', b'0', 'petgo-silver', 1
WHERE NOT EXISTS (SELECT 1 FROM membership_plans WHERE plan_code = 'PETGO-SILVER');

SET @plan_petgo_silver = (SELECT id FROM membership_plans WHERE plan_code = 'PETGO-SILVER' ORDER BY id LIMIT 1);

INSERT INTO membership_plan_features (created_at, updated_at, feature_text, sort_order, membership_plan_id)
SELECT NOW(6), NOW(6), 'Giảm 5% cho dịch vụ đủ điều kiện', 1, @plan_petgo_silver
WHERE @plan_petgo_silver IS NOT NULL AND NOT EXISTS (SELECT 1 FROM membership_plan_features WHERE membership_plan_id = @plan_petgo_silver AND feature_text = 'Giảm 5% cho dịch vụ đủ điều kiện');

INSERT INTO membership_plan_features (created_at, updated_at, feature_text, sort_order, membership_plan_id)
SELECT NOW(6), NOW(6), 'Tặng voucher 50.000đ mỗi tháng', 2, @plan_petgo_silver
WHERE @plan_petgo_silver IS NOT NULL AND NOT EXISTS (SELECT 1 FROM membership_plan_features WHERE membership_plan_id = @plan_petgo_silver AND feature_text = 'Tặng voucher 50.000đ mỗi tháng');

INSERT INTO membership_plan_features (created_at, updated_at, feature_text, sort_order, membership_plan_id)
SELECT NOW(6), NOW(6), 'Theo dõi lịch chăm sóc thú cưng', 3, @plan_petgo_silver
WHERE @plan_petgo_silver IS NOT NULL AND NOT EXISTS (SELECT 1 FROM membership_plan_features WHERE membership_plan_id = @plan_petgo_silver AND feature_text = 'Theo dõi lịch chăm sóc thú cưng');

INSERT INTO membership_plans (created_at, updated_at, is_active, billing_cycle, currency_code, description, discount_percent, monthly_voucher_amount, name, plan_code, is_popular, price_amount, priority_booking, priority_support, slug, sort_order)
SELECT NOW(6), NOW(6), b'1', 'MONTHLY', 'VND', 'Gói phổ biến cho chủ nuôi cần chăm sóc thường xuyên.', 10.00, 100000.00, 'PetGo Gold', 'PETGO-GOLD', b'1', 129000.00, b'1', b'0', 'petgo-gold', 2
WHERE NOT EXISTS (SELECT 1 FROM membership_plans WHERE plan_code = 'PETGO-GOLD');

SET @plan_petgo_gold = (SELECT id FROM membership_plans WHERE plan_code = 'PETGO-GOLD' ORDER BY id LIMIT 1);

INSERT INTO membership_plan_features (created_at, updated_at, feature_text, sort_order, membership_plan_id)
SELECT NOW(6), NOW(6), 'Giảm 10% dịch vụ đủ điều kiện', 1, @plan_petgo_gold
WHERE @plan_petgo_gold IS NOT NULL AND NOT EXISTS (SELECT 1 FROM membership_plan_features WHERE membership_plan_id = @plan_petgo_gold AND feature_text = 'Giảm 10% dịch vụ đủ điều kiện');

INSERT INTO membership_plan_features (created_at, updated_at, feature_text, sort_order, membership_plan_id)
SELECT NOW(6), NOW(6), 'Tặng voucher 100.000đ mỗi tháng', 2, @plan_petgo_gold
WHERE @plan_petgo_gold IS NOT NULL AND NOT EXISTS (SELECT 1 FROM membership_plan_features WHERE membership_plan_id = @plan_petgo_gold AND feature_text = 'Tặng voucher 100.000đ mỗi tháng');

INSERT INTO membership_plan_features (created_at, updated_at, feature_text, sort_order, membership_plan_id)
SELECT NOW(6), NOW(6), 'Ưu tiên đặt lịch cuối tuần', 3, @plan_petgo_gold
WHERE @plan_petgo_gold IS NOT NULL AND NOT EXISTS (SELECT 1 FROM membership_plan_features WHERE membership_plan_id = @plan_petgo_gold AND feature_text = 'Ưu tiên đặt lịch cuối tuần');

INSERT INTO membership_plan_features (created_at, updated_at, feature_text, sort_order, membership_plan_id)
SELECT NOW(6), NOW(6), 'Hỗ trợ nhanh hơn', 4, @plan_petgo_gold
WHERE @plan_petgo_gold IS NOT NULL AND NOT EXISTS (SELECT 1 FROM membership_plan_features WHERE membership_plan_id = @plan_petgo_gold AND feature_text = 'Hỗ trợ nhanh hơn');

INSERT INTO membership_plans (created_at, updated_at, is_active, billing_cycle, currency_code, description, discount_percent, monthly_voucher_amount, name, plan_code, is_popular, price_amount, priority_booking, priority_support, slug, sort_order)
SELECT NOW(6), NOW(6), b'1', 'MONTHLY', 'VND', 'Gói cao cấp cho gia đình nuôi nhiều thú cưng.', 15.00, 220000.00, 'PetGo Platinum', 'PETGO-PLATINUM', b'1', 249000.00, b'1', b'1', 'petgo-platinum', 3
WHERE NOT EXISTS (SELECT 1 FROM membership_plans WHERE plan_code = 'PETGO-PLATINUM');

SET @plan_petgo_platinum = (SELECT id FROM membership_plans WHERE plan_code = 'PETGO-PLATINUM' ORDER BY id LIMIT 1);

INSERT INTO membership_plan_features (created_at, updated_at, feature_text, sort_order, membership_plan_id)
SELECT NOW(6), NOW(6), 'Giảm 15% dịch vụ đủ điều kiện', 1, @plan_petgo_platinum
WHERE @plan_petgo_platinum IS NOT NULL AND NOT EXISTS (SELECT 1 FROM membership_plan_features WHERE membership_plan_id = @plan_petgo_platinum AND feature_text = 'Giảm 15% dịch vụ đủ điều kiện');

INSERT INTO membership_plan_features (created_at, updated_at, feature_text, sort_order, membership_plan_id)
SELECT NOW(6), NOW(6), 'Tặng voucher 220.000đ mỗi tháng', 2, @plan_petgo_platinum
WHERE @plan_petgo_platinum IS NOT NULL AND NOT EXISTS (SELECT 1 FROM membership_plan_features WHERE membership_plan_id = @plan_petgo_platinum AND feature_text = 'Tặng voucher 220.000đ mỗi tháng');

INSERT INTO membership_plan_features (created_at, updated_at, feature_text, sort_order, membership_plan_id)
SELECT NOW(6), NOW(6), 'Ưu tiên đặt lịch và hỗ trợ', 3, @plan_petgo_platinum
WHERE @plan_petgo_platinum IS NOT NULL AND NOT EXISTS (SELECT 1 FROM membership_plan_features WHERE membership_plan_id = @plan_petgo_platinum AND feature_text = 'Ưu tiên đặt lịch và hỗ trợ');

INSERT INTO membership_plan_features (created_at, updated_at, feature_text, sort_order, membership_plan_id)
SELECT NOW(6), NOW(6), 'Quà sinh nhật thú cưng hằng năm', 4, @plan_petgo_platinum
WHERE @plan_petgo_platinum IS NOT NULL AND NOT EXISTS (SELECT 1 FROM membership_plan_features WHERE membership_plan_id = @plan_petgo_platinum AND feature_text = 'Quà sinh nhật thú cưng hằng năm');


-- 7) Promo codes for service/shop demo

INSERT INTO promo_codes (created_at, updated_at, is_active, applicable_days_of_week, is_auto_apply, badge_text, code, description, discount_type, discount_value, ends_at, internal_note, landing_page_url, max_discount_amount, membership_plan_ids, min_completed_bookings, min_order_amount, name, owner_type, priority, promotion_type, provider_ids, provider_service_ids, service_category_ids, is_stackable, starts_at, target_type, terms_and_conditions, usage_count, usage_limit_per_user, usage_limit_total, user_segment, created_by_user_id, provider_id)
SELECT NOW(6), NOW(6), b'1', NULL, b'0', 'Dịch vụ mới', 'PETGO10', 'Giảm 10% cho booking dịch vụ đầu tiên trên PetGo.', 'PERCENT', 10.00, '2027-12-31 23:59:59.000000', 'Seed demo promotion', NULL, 50000.00, NULL, NULL, 100000.00, 'Giảm 10% dịch vụ đầu tiên', 'SYSTEM', 10, 'PROMOTION', NULL, NULL, NULL, b'0', NOW(6), 'BOOKING', 'Áp dụng cho dữ liệu demo, có thể chỉnh trong admin.', 0, 1, 1000, 'NEW_USER', NULL, NULL
WHERE NOT EXISTS (SELECT 1 FROM promo_codes WHERE code = 'PETGO10');

INSERT INTO promo_codes (created_at, updated_at, is_active, applicable_days_of_week, is_auto_apply, badge_text, code, description, discount_type, discount_value, ends_at, internal_note, landing_page_url, max_discount_amount, membership_plan_ids, min_completed_bookings, min_order_amount, name, owner_type, priority, promotion_type, provider_ids, provider_service_ids, service_category_ids, is_stackable, starts_at, target_type, terms_and_conditions, usage_count, usage_limit_per_user, usage_limit_total, user_segment, created_by_user_id, provider_id)
SELECT NOW(6), NOW(6), b'1', NULL, b'0', 'Shop', 'SHOP20K', 'Giảm trực tiếp 20.000đ cho đơn hàng shop từ 199.000đ.', 'FIXED_AMOUNT', 20000.00, '2027-12-31 23:59:59.000000', 'Seed demo promotion', NULL, 20000.00, NULL, NULL, 199000.00, 'Giảm 20.000đ đơn shop', 'SYSTEM', 10, 'PROMOTION', NULL, NULL, NULL, b'0', NOW(6), 'SHOP', 'Áp dụng cho dữ liệu demo, có thể chỉnh trong admin.', 0, 1, 1000, 'ALL', NULL, NULL
WHERE NOT EXISTS (SELECT 1 FROM promo_codes WHERE code = 'SHOP20K');

INSERT INTO promo_codes (created_at, updated_at, is_active, applicable_days_of_week, is_auto_apply, badge_text, code, description, discount_type, discount_value, ends_at, internal_note, landing_page_url, max_discount_amount, membership_plan_ids, min_completed_bookings, min_order_amount, name, owner_type, priority, promotion_type, provider_ids, provider_service_ids, service_category_ids, is_stackable, starts_at, target_type, terms_and_conditions, usage_count, usage_limit_per_user, usage_limit_total, user_segment, created_by_user_id, provider_id)
SELECT NOW(6), NOW(6), b'1', NULL, b'0', 'Grooming', 'GROOMING15', 'Giảm 15% cho các dịch vụ spa và grooming đủ điều kiện.', 'PERCENT', 15.00, '2027-12-31 23:59:59.000000', 'Seed demo promotion', NULL, 80000.00, NULL, NULL, 200000.00, 'Ưu đãi grooming 15%', 'SYSTEM', 10, 'PROMOTION', NULL, NULL, NULL, b'0', NOW(6), 'BOOKING', 'Áp dụng cho dữ liệu demo, có thể chỉnh trong admin.', 0, 1, 1000, 'ALL', NULL, NULL
WHERE NOT EXISTS (SELECT 1 FROM promo_codes WHERE code = 'GROOMING15');



-- ==========================================================
-- UPDATE V2: seed đủ dịch vụ cho toàn bộ danh mục service_categories
-- Mục tiêu: mỗi danh mục dịch vụ trong bảng service_categories đều có ít nhất 1 service và provider_service demo.
-- Chạy lại nhiều lần không bị nhân đôi theo service_code/provider_id/service_id.
-- ==========================================================

DROP TEMPORARY TABLE IF EXISTS petgo_seed_all_category_services;

CREATE TEMPORARY TABLE petgo_seed_all_category_services (
  category_name varchar(120) NOT NULL,
  service_code varchar(32) NOT NULL,
  slug varchar(150) NOT NULL,
  name varchar(150) NOT NULL,
  short_description varchar(255) DEFAULT NULL,
  description text,
  price_amount decimal(12,2) NOT NULL,
  duration_minutes int NOT NULL,
  price_unit varchar(20) NOT NULL,
  requires_consultation tinyint(1) NOT NULL,
  display_order int NOT NULL,
  capacity_per_slot int NOT NULL,
  buffer_after_minutes int NOT NULL,
  duration_type varchar(30) NOT NULL,
  is_featured tinyint(1) NOT NULL,
  photo_url varchar(1000) DEFAULT NULL,
  PRIMARY KEY (service_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO petgo_seed_all_category_services
(category_name, service_code, slug, name, short_description, description, price_amount, duration_minutes, price_unit, requires_consultation, display_order, capacity_per_slot, buffer_after_minutes, duration_type, is_featured, photo_url)
VALUES
    ('Chăm sóc & spa thú cưng','SVC-CAT-001-SPA-COMBO','combo-cham-soc-spa-thu-cung','Combo chăm sóc & spa thú cưng','Gói tổng hợp tắm, vệ sinh, dưỡng lông và chăm sóc ngoại hình.','Dịch vụ tổng hợp dành cho thú cưng cần chăm sóc ngoại hình trọn gói: tắm, sấy, chải lông, vệ sinh tai móng và tư vấn chăm sóc tại nhà.',420000.00,120,'SESSION',0,101,1,15,'MINUTES',1,'https://images.unsplash.com/photo-1516734212186-a967f81ad0d7?auto=format&fit=crop&q=80&w=900'),
    ('Lưu trú & trông giữ','SVC-CAT-002-BOARDING-COMBO','goi-luu-tru-trong-giu-thu-cung','Gói lưu trú & trông giữ thú cưng','Trông giữ, cho ăn, vệ sinh và cập nhật tình trạng trong thời gian chủ vắng mặt.','Gói chăm sóc dành cho chó mèo khi chủ bận hoặc đi xa, bao gồm khu vực nghỉ ngơi, lịch ăn uống, vui chơi và cập nhật hình ảnh định kỳ.',520000.00,480,'DAY',1,102,4,0,'MINUTES',1,'https://images.unsplash.com/photo-1601758174114-e711c0cbaa69?auto=format&fit=crop&q=80&w=900'),
    ('Sức khỏe & thú y','SVC-CAT-003-HEALTH-COMBO','tu-van-suc-khoe-thu-y-tong-hop','Tư vấn sức khỏe & thú y tổng hợp','Kiểm tra cơ bản và tư vấn chăm sóc sức khỏe cho chó mèo.','Dịch vụ tư vấn sức khỏe cơ bản, kiểm tra thể trạng, da lông, cân nặng, răng miệng và hướng dẫn chăm sóc phòng bệnh cho thú cưng.',220000.00,45,'SESSION',1,103,1,10,'MINUTES',0,'https://images.unsplash.com/photo-1628009368231-7bb7cfcb0def?auto=format&fit=crop&q=80&w=900'),
    ('Huấn luyện & hành vi','SVC-CAT-004-TRAINING-COMBO','goi-huan-luyen-hanh-vi-tong-hop','Gói huấn luyện & hành vi tổng hợp','Huấn luyện lệnh cơ bản và tư vấn chỉnh hành vi cho chó.','Gói đánh giá hành vi, hướng dẫn lệnh cơ bản, đi dây dắt, giảm sủa và cải thiện thói quen sinh hoạt cho thú cưng.',350000.00,90,'SESSION',1,104,1,15,'MINUTES',0,'https://images.unsplash.com/photo-1601758123927-196fefb5b65e?auto=format&fit=crop&q=80&w=900'),
    ('Di chuyển & hỗ trợ tại nhà','SVC-CAT-005-HOME-SUPPORT-COMBO','ho-tro-tan-nha-va-di-chuyen-thu-cung','Hỗ trợ tận nhà & di chuyển thú cưng','Đưa đón, dắt đi dạo và hỗ trợ chăm sóc sinh hoạt tại nhà.','Dịch vụ hỗ trợ chủ nuôi trong việc đưa đón thú cưng, dắt đi dạo, cho ăn, kiểm tra nhanh và chăm sóc tận nơi theo lịch hẹn.',180000.00,60,'SESSION',0,105,2,5,'MINUTES',0,'https://images.unsplash.com/photo-1548199973-03cce0bbc87b?auto=format&fit=crop&q=80&w=900'),
    ('Tắm gội & vệ sinh cơ bản','SVC-CAT-006-BATH-HYGIENE','tam-goi-ve-sinh-dinh-ky','Tắm gội & vệ sinh định kỳ','Tắm, sấy, vệ sinh tai móng và chải lông định kỳ.','Gói vệ sinh định kỳ giúp thú cưng sạch sẽ, giảm mùi, kiểm tra tai móng và giữ da lông khỏe mạnh.',190000.00,60,'SESSION',0,106,2,10,'MINUTES',1,'https://images.unsplash.com/photo-1516734212186-a967f81ad0d7?auto=format&fit=crop&q=80&w=900'),
    ('Cắt tỉa & tạo kiểu','SVC-CAT-007-GROOMING-STYLE','grooming-cat-tia-tao-kieu-tong-hop','Grooming cắt tỉa & tạo kiểu','Cắt tỉa lông, tạo dáng và hoàn thiện ngoại hình theo giống.','Gói grooming cho chó mèo cần làm đẹp ngoại hình, bao gồm tư vấn kiểu lông, cắt tỉa, sấy tạo phồng và chỉnh form theo giống.',360000.00,90,'SESSION',0,107,1,15,'MINUTES',1,'https://images.unsplash.com/photo-1560807707-8cc77767d783?auto=format&fit=crop&q=80&w=900'),
    ('Spa trị liệu','SVC-CAT-008-SPA-THERAPY','spa-duong-long-thu-gian','Spa dưỡng lông thư giãn','Dưỡng lông, khử mùi và thư giãn nhẹ cho thú cưng.','Gói spa trị liệu nhẹ với sữa tắm chuyên dụng, dưỡng lông, massage thư giãn, khử mùi và chăm sóc da lông chuyên sâu.',280000.00,75,'SESSION',0,108,1,10,'MINUTES',0,'https://images.unsplash.com/photo-1548199973-03cce0bbc87b?auto=format&fit=crop&q=80&w=900'),
    ('Khách sạn thú cưng','SVC-CAT-009-PET-HOTEL','khach-san-thu-cung-tieu-chuan','Khách sạn thú cưng tiêu chuẩn','Lưu trú an toàn, cho ăn và cập nhật hình ảnh định kỳ.','Phòng lưu trú sạch sẽ cho thú cưng, có lịch ăn uống, khu vực nghỉ ngơi, theo dõi tình trạng và cập nhật cho chủ nuôi.',450000.00,480,'DAY',1,109,4,0,'MINUTES',1,'https://images.unsplash.com/photo-1601758174114-e711c0cbaa69?auto=format&fit=crop&q=80&w=900'),
    ('Daycare ban ngày','SVC-CAT-010-DAYCARE-FULL','daycare-thu-cung-ca-ngay','Daycare thú cưng cả ngày','Trông giữ ban ngày, vui chơi, cho ăn và theo dõi tình trạng.','Dịch vụ chăm sóc trong ngày dành cho chủ bận đi làm, bao gồm vui chơi, nghỉ ngơi, cho ăn theo hướng dẫn và cập nhật tình trạng.',320000.00,480,'DAY',0,110,5,0,'MINUTES',0,'https://images.unsplash.com/photo-1601758125946-6ec2ef64daf8?auto=format&fit=crop&q=80&w=900'),
    ('Trông giữ tại nhà','SVC-CAT-011-HOME-SITTING','trong-giu-tai-nha-theo-buoi','Trông giữ tại nhà theo buổi','Người chăm sóc đến nhà hoặc nhận chăm theo thỏa thuận.','Dịch vụ pet sitter hỗ trợ cho ăn, vệ sinh, chơi cùng thú cưng và cập nhật tình hình tại nhà khách hoặc nhà người chăm sóc.',280000.00,180,'SESSION',1,111,1,15,'MINUTES',0,'https://images.unsplash.com/photo-1601758124096-1fd661873b8f?auto=format&fit=crop&q=80&w=900'),
    ('Khám & tư vấn sức khỏe','SVC-CAT-012-HEALTH-CHECK','kham-tu-van-suc-khoe-co-ban','Khám & tư vấn sức khỏe cơ bản','Kiểm tra thể trạng và tư vấn chăm sóc phòng bệnh.','Dịch vụ kiểm tra tình trạng tổng quan, da lông, cân nặng, răng miệng và tư vấn chăm sóc phù hợp với độ tuổi thú cưng.',250000.00,45,'SESSION',1,112,1,10,'MINUTES',0,'https://images.unsplash.com/photo-1628009368231-7bb7cfcb0def?auto=format&fit=crop&q=80&w=900'),
    ('Tiêm phòng & phòng ký sinh','SVC-CAT-013-VACCINE-PREVENTION','phong-benh-dinh-ky','Phòng bệnh định kỳ','Tiêm phòng, tẩy giun và tư vấn phòng ve rận.','Gói phòng bệnh cơ bản gồm tư vấn lịch vaccine, tẩy giun, phòng ve rận và nhắc lịch chăm sóc định kỳ cho thú cưng.',300000.00,45,'SESSION',1,113,1,10,'MINUTES',0,'https://images.unsplash.com/photo-1583337130417-3346a1be7dee?auto=format&fit=crop&q=80&w=900'),
    ('Chăm sóc sau điều trị','SVC-CAT-014-POST-TREATMENT','cham-soc-sau-dieu-tri-tai-nha','Chăm sóc sau điều trị tại nhà','Hỗ trợ uống thuốc, thay băng và theo dõi phục hồi.','Dịch vụ hỗ trợ sau điều trị theo hướng dẫn chuyên môn: nhắc uống thuốc, thay băng cơ bản, theo dõi ăn uống và cập nhật tình trạng.',350000.00,60,'SESSION',1,114,1,15,'MINUTES',0,'https://images.unsplash.com/photo-1574158622682-e40e69881006?auto=format&fit=crop&q=80&w=900'),
    ('Huấn luyện cơ bản','SVC-CAT-015-BASIC-TRAINING','khoa-huan-luyen-co-ban','Khóa huấn luyện cơ bản','Dạy ngồi, nằm, gọi tên, đi dây dắt và tương tác an toàn.','Buổi huấn luyện nền tảng giúp chó làm quen hiệu lệnh, đi cạnh chủ, phản hồi gọi tên và hình thành thói quen sinh hoạt tốt.',300000.00,75,'SESSION',1,115,1,15,'MINUTES',0,'https://images.unsplash.com/photo-1601758123927-196fefb5b65e?auto=format&fit=crop&q=80&w=900'),
    ('Chỉnh hành vi','SVC-CAT-016-BEHAVIOR-CORRECT','tu-van-chinh-hanh-vi','Tư vấn chỉnh hành vi','Hỗ trợ giảm sủa, cắn phá, lo âu xa chủ và sợ hãi.','Dịch vụ đánh giá hành vi và xây dựng hướng xử lý cho các vấn đề như sủa nhiều, cắn phá, lo âu xa chủ hoặc phản ứng quá mức.',400000.00,90,'SESSION',1,116,1,15,'MINUTES',0,'https://images.unsplash.com/photo-1601758125946-6ec2ef64daf8?auto=format&fit=crop&q=80&w=900'),
    ('Đưa đón thú cưng','SVC-CAT-017-PET-TRANSPORT','dua-don-thu-cung-theo-lich','Đưa đón thú cưng theo lịch','Đưa đón thú cưng đến spa, phòng khám hoặc khách sạn.','Dịch vụ vận chuyển thú cưng an toàn theo lịch hẹn, hỗ trợ bàn giao tại spa, phòng khám, khách sạn hoặc địa điểm đã thống nhất.',150000.00,60,'SESSION',0,117,2,5,'MINUTES',0,'https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?auto=format&fit=crop&q=80&w=900'),
    ('Dắt đi dạo & vận động','SVC-CAT-018-WALK-EXERCISE','dat-di-dao-van-dong','Dắt đi dạo & vận động','Dắt chó đi dạo, vận động nhẹ và cập nhật sau buổi đi.','Dịch vụ đưa chó đi dạo theo tuyến an toàn, hỗ trợ vận động, đi vệ sinh và gửi ghi chú sau buổi cho chủ nuôi.',120000.00,60,'SESSION',0,118,3,5,'MINUTES',0,'https://images.unsplash.com/photo-1548199973-03cce0bbc87b?auto=format&fit=crop&q=80&w=900'),
    ('Tắm sấy chó mèo','SVC-CAT-019-BATH-DRY','tam-say-cho-meo-tieu-chuan','Tắm sấy chó mèo tiêu chuẩn','Tắm bằng sản phẩm phù hợp, sấy khô và chải lông.','Dịch vụ tắm sấy tiêu chuẩn cho chó mèo nhỏ/vừa, giúp làm sạch lông, giảm mùi và kiểm tra nhanh tình trạng da lông.',180000.00,60,'SESSION',0,119,2,10,'MINUTES',1,'https://images.unsplash.com/photo-1516734212186-a967f81ad0d7?auto=format&fit=crop&q=80&w=900'),
    ('Vệ sinh tai, móng, tuyến hôi','SVC-CAT-020-EAR-NAIL','ve-sinh-tai-mong-tuyen-hoi','Vệ sinh tai, móng, tuyến hôi','Cắt móng, vệ sinh tai và hỗ trợ tuyến hôi khi phù hợp.','Dịch vụ vệ sinh nhanh gồm kiểm tra tai, làm sạch tai ngoài, cắt hoặc mài móng và hỗ trợ vệ sinh tuyến hôi theo tình trạng thú cưng.',90000.00,30,'SESSION',0,120,2,5,'MINUTES',0,'https://images.unsplash.com/photo-1583337130417-3346a1be7dee?auto=format&fit=crop&q=80&w=900'),
    ('Cắt tỉa theo giống','SVC-CAT-021-BREED-GROOMING','cat-tia-theo-giong-cao-cap','Cắt tỉa theo giống cao cấp','Tạo kiểu theo giống, độ dài lông và yêu cầu của chủ nuôi.','Gói cắt tỉa chuyên sâu theo giống như Poodle, Corgi, Pomeranian, mèo lông dài; có tư vấn kiểu phù hợp trước khi thực hiện.',350000.00,90,'SESSION',0,121,1,15,'MINUTES',1,'https://images.unsplash.com/photo-1560807707-8cc77767d783?auto=format&fit=crop&q=80&w=900'),
    ('Nhuộm lông & phụ kiện','SVC-CAT-022-DYE-ACCESSORY','nhuom-long-va-phu-kien-an-toan','Nhuộm lông & phụ kiện an toàn','Tạo điểm nhấn thẩm mỹ với màu nhuộm và phụ kiện phù hợp.','Dịch vụ làm đẹp có tư vấn trước, sử dụng sản phẩm phù hợp thú cưng, tạo điểm nhấn màu lông nhẹ và trang trí phụ kiện.',400000.00,120,'SESSION',1,122,1,15,'MINUTES',0,'https://images.unsplash.com/photo-1552053831-71594a27632d?auto=format&fit=crop&q=80&w=900'),
    ('Lưu trú chó','SVC-CAT-023-DOG-HOTEL','luu-tru-cho-tieu-chuan','Lưu trú chó tiêu chuẩn','Phòng lưu trú cho chó, cho ăn và cập nhật tình trạng.','Dịch vụ lưu trú dành cho chó, có khu nghỉ riêng, cho ăn theo hướng dẫn, vệ sinh khu vực và cập nhật ảnh trong ngày.',450000.00,480,'DAY',1,123,4,0,'MINUTES',1,'https://images.unsplash.com/photo-1601758174114-e711c0cbaa69?auto=format&fit=crop&q=80&w=900'),
    ('Lưu trú mèo','SVC-CAT-024-CAT-HOTEL','luu-tru-meo-tieu-chuan','Lưu trú mèo tiêu chuẩn','Không gian yên tĩnh, vệ sinh khay cát và theo dõi mèo.','Dịch vụ lưu trú dành riêng cho mèo, ưu tiên không gian yên tĩnh, vệ sinh khay cát, cho ăn và theo dõi biểu hiện hằng ngày.',420000.00,480,'DAY',1,124,3,0,'MINUTES',0,'https://images.unsplash.com/photo-1574158622682-e40e69881006?auto=format&fit=crop&q=80&w=900'),
    ('Daycare nửa ngày','SVC-CAT-025-DAYCARE-HALF','daycare-nua-ngay','Daycare nửa ngày','Trông giữ trong một buổi, phù hợp lịch làm việc ngắn.','Gói trông giữ thú cưng nửa ngày, bao gồm khu nghỉ, vui chơi nhẹ, cho ăn theo hướng dẫn và cập nhật tình trạng.',220000.00,240,'SESSION',0,125,5,0,'MINUTES',0,'https://images.unsplash.com/photo-1601758125946-6ec2ef64daf8?auto=format&fit=crop&q=80&w=900'),
    ('Khám tổng quát','SVC-CAT-026-GENERAL-CHECKUP','kham-tong-quat','Khám tổng quát','Kiểm tra cân nặng, da lông, răng miệng và thể trạng.','Dịch vụ khám tổng quát cơ bản để đánh giá sức khỏe thú cưng, phát hiện vấn đề thường gặp và tư vấn lịch chăm sóc phù hợp.',250000.00,45,'SESSION',1,126,1,10,'MINUTES',0,'https://images.unsplash.com/photo-1628009368231-7bb7cfcb0def?auto=format&fit=crop&q=80&w=900'),
    ('Tư vấn dinh dưỡng','SVC-CAT-027-NUTRITION','tu-van-dinh-duong-theo-do-tuoi','Tư vấn dinh dưỡng theo độ tuổi','Tư vấn khẩu phần, kiểm soát cân nặng và chế độ ăn.','Dịch vụ tư vấn chế độ ăn theo độ tuổi, cân nặng, giống loài và tình trạng sức khỏe; phù hợp chó mèo cần kiểm soát cân nặng.',180000.00,40,'SESSION',1,127,1,5,'MINUTES',0,'https://images.unsplash.com/photo-1589924691995-400dc9ecc119?auto=format&fit=crop&q=80&w=900'),
    ('Tiêm vaccine định kỳ','SVC-CAT-028-VACCINE','tiem-vaccine-dinh-ky','Tiêm vaccine định kỳ','Tiêm phòng theo lịch và nhắc lịch vaccine.','Dịch vụ tư vấn và thực hiện tiêm phòng định kỳ theo tình trạng thú cưng, kèm nhắc lịch mũi tiếp theo.',320000.00,30,'SESSION',1,128,1,10,'MINUTES',0,'https://images.unsplash.com/photo-1583337130417-3346a1be7dee?auto=format&fit=crop&q=80&w=900'),
    ('Lệnh cơ bản cho chó','SVC-CAT-029-DOG-BASIC-COMMANDS','lenh-co-ban-cho-cho','Lệnh cơ bản cho chó','Dạy ngồi, nằm, đứng, gọi tên và đi cạnh chủ.','Buổi huấn luyện kỹ năng nền tảng cho chó, giúp chó hiểu hiệu lệnh cơ bản và tăng khả năng tương tác với chủ.',300000.00,75,'SESSION',1,129,1,15,'MINUTES',0,'https://images.unsplash.com/photo-1601758123927-196fefb5b65e?auto=format&fit=crop&q=80&w=900'),
    ('Đi vệ sinh đúng chỗ','SVC-CAT-030-POTTY-TRAINING','huan-luyen-di-ve-sinh-dung-cho','Huấn luyện đi vệ sinh đúng chỗ','Xây thói quen đi vệ sinh đúng vị trí và giảm sự cố trong nhà.','Dịch vụ hướng dẫn chủ nuôi thiết lập lịch sinh hoạt, khu vực vệ sinh và phương pháp thưởng phạt phù hợp để giảm đi vệ sinh sai chỗ.',350000.00,90,'SESSION',1,130,1,15,'MINUTES',0,'https://images.unsplash.com/photo-1601758125946-6ec2ef64daf8?auto=format&fit=crop&q=80&w=900'),
    ('Đưa đón spa/phòng khám','SVC-CAT-031-SPA-CLINIC-TAXI','dua-don-spa-phong-kham','Đưa đón spa/phòng khám','Đưa đón thú cưng đến điểm hẹn và bàn giao theo lịch.','Dịch vụ đưa đón thú cưng đến spa, phòng khám hoặc khách sạn; hỗ trợ xác nhận bàn giao và cập nhật khi hoàn thành.',150000.00,60,'SESSION',0,131,2,5,'MINUTES',0,'https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?auto=format&fit=crop&q=80&w=900'),
    ('Dắt chó đi dạo','SVC-CAT-032-DOG-WALKING','dat-cho-di-dao-60-phut-tieu-chuan','Dắt chó đi dạo 60 phút tiêu chuẩn','Dắt chó đi dạo theo lịch, kèm cập nhật tình trạng sau buổi đi.','Dịch vụ dắt chó đi dạo trong khu vực gần provider, hỗ trợ vận động, quan sát hành vi và gửi ghi chú sau buổi đi.',120000.00,60,'SESSION',0,132,3,5,'MINUTES',0,'https://images.unsplash.com/photo-1548199973-03cce0bbc87b?auto=format&fit=crop&q=80&w=900');

-- Thêm service vào catalog theo đúng category hiện có trong service_categories
INSERT INTO services (created_at, updated_at, is_active, base_price_amount, currency_code, default_duration_minutes, description, name, price_unit, requires_consultation, service_code, short_description, slug, category_id)
SELECT
  NOW(6), NOW(6), b'1', t.price_amount, 'VND', t.duration_minutes,
  t.description, t.name, t.price_unit, IF(t.requires_consultation = 1, b'1', b'0'),
  t.service_code, t.short_description, t.slug, sc.id
FROM petgo_seed_all_category_services t
JOIN service_categories sc ON sc.name = t.category_name
LEFT JOIN services existed ON existed.service_code = t.service_code
WHERE existed.id IS NULL;

-- Gắn toàn bộ dịch vụ vừa seed vào provider demo để trang tìm kiếm/booking có dữ liệu hiển thị
SET @provider_seed_id = (
  SELECT id FROM provider_profiles
  WHERE slug = 'petgo-sample-provider' OR provider_code = 'PRV-SAMPLE-PROVIDER'
  ORDER BY id LIMIT 1
);

INSERT INTO provider_services (
  created_at, updated_at, is_active, approval_status, booking_buffer_minutes, buffer_after_minutes,
  capacity_per_slot, category_ids, currency_code, custom_name, description, display_order, duration_minutes,
  duration_type, is_featured, photo_urls, price_amount, price_unit, short_description, provider_id, service_id
)
SELECT
  NOW(6), NOW(6), b'1', 'APPROVED', 0, t.buffer_after_minutes,
  t.capacity_per_slot, CAST(sc.id AS CHAR), 'VND', t.name, t.description, t.display_order, t.duration_minutes,
  t.duration_type, IF(t.is_featured = 1, b'1', b'0'), t.photo_url, t.price_amount, t.price_unit,
  t.short_description, @provider_seed_id, s.id
FROM petgo_seed_all_category_services t
JOIN service_categories sc ON sc.name = t.category_name
JOIN services s ON s.service_code = t.service_code
LEFT JOIN provider_services ps ON ps.provider_id = @provider_seed_id AND ps.service_id = s.id
WHERE @provider_seed_id IS NOT NULL AND ps.id IS NULL;

-- Cập nhật lại giá thấp nhất của provider demo sau khi có đủ dịch vụ
UPDATE provider_profiles pp
SET pp.price_from_amount = (
  SELECT MIN(ps.price_amount)
  FROM provider_services ps
  WHERE ps.provider_id = pp.id AND ps.is_active = b'1' AND ps.approval_status = 'APPROVED'
)
WHERE pp.id = @provider_seed_id;

DROP TEMPORARY TABLE IF EXISTS petgo_seed_all_category_services;

COMMIT;

-- Quick check after running:
-- SELECT COUNT(*) AS product_categories FROM product_categories;
-- SELECT COUNT(*) AS products FROM products;
-- SELECT COUNT(*) AS services FROM services;
-- SELECT COUNT(*) AS provider_services FROM provider_services;


-- ==========================================================
-- PetGo update seed V3: thêm nhiều provider, mỗi provider chỉ cung cấp 2-3 dịch vụ
-- Chạy sau dump gốc + file seed sản phẩm/dịch vụ V2.
-- Safe to run multiple times trong cùng một ngày: không nhân đôi provider, provider_services, provider_photos, availability slots.
-- Lưu ý: file này KHÔNG xóa provider demo cũ; nó chỉ thêm provider mới có 2-3 dịch vụ/provider.
-- ==========================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 1;
START TRANSACTION;

DROP TEMPORARY TABLE IF EXISTS petgo_seed_many_providers;
CREATE TEMPORARY TABLE petgo_seed_many_providers (
  provider_code varchar(32) NOT NULL PRIMARY KEY,
  slug varchar(190) NOT NULL,
  business_name varchar(180) NOT NULL,
  provider_type varchar(30) NOT NULL,
  province varchar(120) DEFAULT NULL,
  city varchar(120) DEFAULT NULL,
  district varchar(120) DEFAULT NULL,
  ward varchar(120) DEFAULT NULL,
  phone varchar(30) DEFAULT NULL,
  latitude decimal(10,7) DEFAULT NULL,
  longitude decimal(10,7) DEFAULT NULL,
  average_rating decimal(3,2) NOT NULL,
  total_completed_bookings int NOT NULL,
  total_reviews int NOT NULL,
  years_experience int DEFAULT NULL,
  service_radius_km decimal(6,2) DEFAULT NULL,
  is_featured tinyint(1) NOT NULL,
  is_hot tinyint(1) NOT NULL,
  address_line1 varchar(255) DEFAULT NULL,
  headline varchar(255) DEFAULT NULL,
  description text,
  main_image_url varchar(500) DEFAULT NULL,
  cover_image_url varchar(500) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO petgo_seed_many_providers
(provider_code, slug, business_name, provider_type, province, city, district, ward, phone, latitude, longitude, average_rating, total_completed_bookings, total_reviews, years_experience, service_radius_km, is_featured, is_hot, address_line1, headline, description, main_image_url, cover_image_url)
VALUES
('PRV-SEED-PAWSOME-SPA-Q1','pawsome-spa-quan-1','Pawsome Spa Quận 1','BUSINESS','Hồ Chí Minh','Hồ Chí Minh','Quận 1','Phường Bến Nghé','0921000101',10.7769000,106.7009000,4.92,312,86,5,6.00,1,1,'18 Nguyễn Huệ','Spa, tắm sấy và grooming cao cấp cho chó mèo','Không gian spa sạch sẽ, chuyên các gói tắm sấy, dưỡng lông và tạo kiểu cho chó mèo nhỏ/vừa.','https://images.unsplash.com/photo-1516734212186-a967f81ad0d7?auto=format&fit=crop&q=80&w=900','https://images.unsplash.com/photo-1601758124510-52d02ddb7cbd?auto=format&fit=crop&q=80&w=1400'),
('PRV-SEED-MEOMEO-CAT-HOTEL','meo-meo-cat-hotel-quan-3','Meo Meo Cat Hotel Quận 3','BUSINESS','Hồ Chí Minh','Hồ Chí Minh','Quận 3','Phường Võ Thị Sáu','0921000102',10.7821000,106.6864000,4.88,205,61,4,5.00,1,0,'42 Võ Văn Tần','Khách sạn mèo yên tĩnh, sạch sẽ, cập nhật ảnh mỗi ngày','Chuyên lưu trú mèo, vệ sinh khay cát, chăm ăn uống và tư vấn dinh dưỡng cho mèo nhạy cảm.','https://images.unsplash.com/photo-1574158622682-e40e69881006?auto=format&fit=crop&q=80&w=900','https://images.unsplash.com/photo-1592194996308-7b43878e84a6?auto=format&fit=crop&q=80&w=1400'),
('PRV-SEED-HAPPYDOG-HOTEL-TD','happy-dog-hotel-thu-duc','Happy Dog Hotel Thủ Đức','BUSINESS','Hồ Chí Minh','Hồ Chí Minh','Thủ Đức','Phường Linh Trung','0921000103',10.8492000,106.7717000,4.80,428,94,6,9.00,0,1,'76 Kha Vạn Cân','Lưu trú chó, daycare và vận động trong khuôn viên rộng','Nhận lưu trú chó qua ngày, daycare ban ngày và dắt đi dạo có cập nhật tình trạng cho chủ nuôi.','https://images.unsplash.com/photo-1601758174114-e711c0cbaa69?auto=format&fit=crop&q=80&w=900','https://images.unsplash.com/photo-1548199973-03cce0bbc87b?auto=format&fit=crop&q=80&w=1400'),
('PRV-SEED-VETCARE-Q7','vetcare-pet-clinic-quan-7','VetCare Pet Clinic Quận 7','CLINIC','Hồ Chí Minh','Hồ Chí Minh','Quận 7','Phường Tân Phú','0921000104',10.7295000,106.7219000,4.90,536,137,8,7.00,1,0,'12 Nguyễn Lương Bằng','Khám tổng quát, vaccine và tư vấn dinh dưỡng','Phòng khám thú y cơ bản, hỗ trợ kiểm tra sức khỏe định kỳ, tiêm vaccine và tư vấn khẩu phần ăn.','https://images.unsplash.com/photo-1628009368231-7bb7cfcb0def?auto=format&fit=crop&q=80&w=900','https://images.unsplash.com/photo-1583337130417-3346a1be7dee?auto=format&fit=crop&q=80&w=1400'),
('PRV-SEED-PETWALKER-BADINH','pet-walker-ba-dinh','Pet Walker Ba Đình','INDIVIDUAL','Hà Nội','Hà Nội','Ba Đình','Phường Ngọc Khánh','0921000105',21.0320000,105.8143000,4.76,168,42,3,4.50,0,0,'25 Kim Mã','Dắt chó đi dạo và hỗ trợ chăm sóc tận nhà','Phù hợp chó cần vận động hằng ngày, nhận đưa đón và ghé nhà cho ăn/chăm sóc ngắn hạn.','https://images.unsplash.com/photo-1548199973-03cce0bbc87b?auto=format&fit=crop&q=80&w=900','https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?auto=format&fit=crop&q=80&w=1400'),
('PRV-SEED-GROOMHOUSE-CAUGIAY','groom-house-cau-giay','Groom House Cầu Giấy','BUSINESS','Hà Nội','Hà Nội','Cầu Giấy','Phường Dịch Vọng','0921000106',21.0362000,105.7906000,4.84,284,73,5,6.50,1,1,'88 Trần Thái Tông','Grooming theo giống, nhuộm lông an toàn và tắm sấy','Studio grooming chuyên Poodle, Corgi, mèo lông dài, có tư vấn kiểu lông trước khi thực hiện.','https://images.unsplash.com/photo-1560807707-8cc77767d783?auto=format&fit=crop&q=80&w=900','https://images.unsplash.com/photo-1552053831-71594a27632d?auto=format&fit=crop&q=80&w=1400'),
('PRV-SEED-PAWTRAINING-HCM','paw-training-center-hcm','Paw Training Center HCM','BUSINESS','Hồ Chí Minh','Hồ Chí Minh','Bình Thạnh','Phường 25','0921000107',10.8021000,106.7146000,4.79,191,49,4,8.00,0,1,'31 D1','Huấn luyện lệnh cơ bản và chỉnh hành vi cho chó','Tập trung vào các kỹ năng nền tảng: ngồi, nằm, gọi tên, đi vệ sinh đúng chỗ và giảm hành vi xấu.','https://images.unsplash.com/photo-1601758123927-196fefb5b65e?auto=format&fit=crop&q=80&w=900','https://images.unsplash.com/photo-1601758125946-6ec2ef64daf8?auto=format&fit=crop&q=80&w=1400'),
('PRV-SEED-DANANG-PETTAXI','da-nang-pet-taxi-care','Đà Nẵng Pet Taxi & Care','BUSINESS','Đà Nẵng','Đà Nẵng','Hải Châu','Phường Hải Châu 1','0921000108',16.0678000,108.2208000,4.72,142,35,3,10.00,0,0,'9 Bạch Đằng','Đưa đón thú cưng và chăm sóc tận nhà tại Đà Nẵng','Hỗ trợ đưa đón spa/phòng khám, dắt chó đi dạo và trông giữ tại nhà theo lịch hẹn.','https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?auto=format&fit=crop&q=80&w=900','https://images.unsplash.com/photo-1548199973-03cce0bbc87b?auto=format&fit=crop&q=80&w=1400'),
('PRV-SEED-CATLOVER-HANOI','cat-lover-home-stay-hanoi','Cat Lover Home Stay Hà Nội','INDIVIDUAL','Hà Nội','Hà Nội','Đống Đa','Phường Láng Hạ','0921000109',21.0169000,105.8129000,4.81,96,28,2,4.00,0,0,'16 Láng Hạ','Home stay cho mèo, daycare nửa ngày và trông giữ tại nhà','Không gian nhỏ, yên tĩnh, phù hợp mèo cần chăm riêng và không thích nơi đông thú cưng.','https://images.unsplash.com/photo-1592194996308-7b43878e84a6?auto=format&fit=crop&q=80&w=900','https://images.unsplash.com/photo-1574158622682-e40e69881006?auto=format&fit=crop&q=80&w=1400'),
('PRV-SEED-SAIGON-DAYCARE','saigon-pet-daycare','Saigon Pet Daycare','BUSINESS','Hồ Chí Minh','Hồ Chí Minh','Phú Nhuận','Phường 9','0921000110',10.7990000,106.6789000,4.75,226,58,4,6.00,1,0,'54 Nguyễn Văn Trỗi','Daycare ban ngày, daycare nửa ngày và tắm sấy nhanh','Nhận chăm thú cưng theo ngày làm việc, có khu nghỉ, vui chơi nhẹ và tắm sấy trước khi đón về.','https://images.unsplash.com/photo-1601758125946-6ec2ef64daf8?auto=format&fit=crop&q=80&w=900','https://images.unsplash.com/photo-1601758124096-1fd661873b8f?auto=format&fit=crop&q=80&w=1400'),
('PRV-SEED-POODLE-BEAUTY','poodle-beauty-studio','Poodle Beauty Studio','BUSINESS','Hồ Chí Minh','Hồ Chí Minh','Tân Bình','Phường 2','0921000111',10.8132000,106.6629000,4.89,342,91,7,7.00,1,1,'22 Trường Sơn','Chuyên tạo kiểu Poodle, vệ sinh tai móng và spa dưỡng lông','Studio làm đẹp cho chó lông xoăn và chó nhỏ, ưu tiên kiểu teddy, puppy cut và vệ sinh định kỳ.','https://images.unsplash.com/photo-1516734212186-a967f81ad0d7?auto=format&fit=crop&q=80&w=900','https://images.unsplash.com/photo-1560807707-8cc77767d783?auto=format&fit=crop&q=80&w=1400'),
('PRV-SEED-SENIOR-PETCARE','senior-pet-care','Senior Pet Care','CLINIC','Hà Nội','Hà Nội','Hai Bà Trưng','Phường Bạch Mai','0921000112',21.0007000,105.8496000,4.83,188,44,6,5.50,0,0,'101 Bạch Mai','Chăm sóc thú cưng lớn tuổi và sau điều trị','Hỗ trợ theo dõi sức khỏe, tư vấn dinh dưỡng và chăm sóc phục hồi cơ bản tại nhà hoặc phòng khám.','https://images.unsplash.com/photo-1628009368231-7bb7cfcb0def?auto=format&fit=crop&q=80&w=900','https://images.unsplash.com/photo-1574158622682-e40e69881006?auto=format&fit=crop&q=80&w=1400'),
('PRV-SEED-PUPPY-SCHOOL','puppy-basic-school','Puppy Basic School','INDIVIDUAL','Hồ Chí Minh','Hồ Chí Minh','Gò Vấp','Phường 5','0921000113',10.8380000,106.6659000,4.70,122,31,3,5.00,0,0,'39 Quang Trung','Huấn luyện chó con lệnh cơ bản và đi vệ sinh đúng chỗ','Dành cho chó con mới về nhà, giúp xây thói quen tốt và phản hồi hiệu lệnh cơ bản.','https://images.unsplash.com/photo-1601758123927-196fefb5b65e?auto=format&fit=crop&q=80&w=900','https://images.unsplash.com/photo-1601758125946-6ec2ef64daf8?auto=format&fit=crop&q=80&w=1400'),
('PRV-SEED-NHATRANG-HOTEL','pet-hotel-nha-trang','Pet Hotel Nha Trang','BUSINESS','Khánh Hòa','Nha Trang','Nha Trang','Phường Lộc Thọ','0921000114',12.2388000,109.1967000,4.74,156,38,4,8.00,0,0,'7 Trần Phú','Lưu trú chó mèo và daycare khi chủ đi du lịch','Cơ sở lưu trú phù hợp khách du lịch cần gửi thú cưng trong ngày hoặc qua đêm tại Nha Trang.','https://images.unsplash.com/photo-1601758174114-e711c0cbaa69?auto=format&fit=crop&q=80&w=900','https://images.unsplash.com/photo-1574158622682-e40e69881006?auto=format&fit=crop&q=80&w=1400'),
('PRV-SEED-HOMEVET-MOBILE','homevet-mobile-care','HomeVet Mobile Care','CLINIC','Hồ Chí Minh','Hồ Chí Minh','Quận 10','Phường 12','0921000115',10.7705000,106.6679000,4.86,248,69,5,8.50,1,0,'65 Sư Vạn Hạnh','Khám, vaccine và chăm sóc sau điều trị tại nhà','Đội ngũ hỗ trợ thăm khám cơ bản, tiêm phòng và theo dõi phục hồi tại nhà theo lịch hẹn.','https://images.unsplash.com/photo-1583337130417-3346a1be7dee?auto=format&fit=crop&q=80&w=900','https://images.unsplash.com/photo-1628009368231-7bb7cfcb0def?auto=format&fit=crop&q=80&w=1400'),
('PRV-SEED-COZY-SITTER-Q10','cozy-pet-sitter-quan-10','Cozy Pet Sitter Quận 10','INDIVIDUAL','Hồ Chí Minh','Hồ Chí Minh','Quận 10','Phường 14','0921000116',10.7750000,106.6660000,4.68,88,24,2,3.50,0,0,'19 Thành Thái','Trông giữ tại nhà, daycare nửa ngày và dắt đi dạo','Phù hợp thú cưng quen chăm riêng, nhận lịch ngắn trong ngày và cập nhật đều cho chủ.','https://images.unsplash.com/photo-1601758124096-1fd661873b8f?auto=format&fit=crop&q=80&w=900','https://images.unsplash.com/photo-1548199973-03cce0bbc87b?auto=format&fit=crop&q=80&w=1400'),
('PRV-SEED-FURSPA-PREMIUM','fur-spa-premium','Fur Spa Premium','BUSINESS','Hà Nội','Hà Nội','Hoàn Kiếm','Phường Hàng Bài','0921000117',21.0247000,105.8533000,4.91,298,80,6,6.00,1,1,'11 Hai Bà Trưng','Spa trị liệu, tắm sấy và phụ kiện làm đẹp an toàn','Gói làm đẹp cao cấp cho thú cưng trước sự kiện, chụp ảnh hoặc chăm sóc lông định kỳ.','https://images.unsplash.com/photo-1516734212186-a967f81ad0d7?auto=format&fit=crop&q=80&w=900','https://images.unsplash.com/photo-1552053831-71594a27632d?auto=format&fit=crop&q=80&w=1400'),
('PRV-SEED-PETGO-EXPRESS','petgo-express-pickup','PetGo Express Pickup','BUSINESS','Hồ Chí Minh','Hồ Chí Minh','Quận 5','Phường 6','0921000118',10.7540000,106.6636000,4.66,132,27,3,12.00,0,0,'33 Nguyễn Trãi','Đưa đón thú cưng đến spa, phòng khám và khách sạn','Dịch vụ vận chuyển thú cưng theo lịch hẹn, phù hợp khi chủ bận hoặc cần bàn giao an toàn.','https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?auto=format&fit=crop&q=80&w=900','https://images.unsplash.com/photo-1548199973-03cce0bbc87b?auto=format&fit=crop&q=80&w=1400');


-- Thêm provider profile. user_id để NULL để không vướng unique user_id của provider_profiles.
INSERT INTO provider_profiles (
  created_at, updated_at, accepts_instant_booking, accepts_membership, average_rating, business_name,
  cancellation_free_hours, city, country_code, cover_image_url, currency_code, deleted_at, description,
  district, emergency_phone, is_featured, headline, is_hot, latitude, longitude, main_image_url,
  price_from_amount, primary_address_line1, primary_address_line2, provider_code, provider_type, province,
  service_radius_km, slug, status, total_completed_bookings, total_reviews, verification_status, ward,
  years_experience, user_id
)
SELECT
  NOW(6), NOW(6), b'1', b'1', p.average_rating, p.business_name,
  24, p.city, 'VN', p.cover_image_url, 'VND', NULL, p.description,
  p.district, p.phone, IF(p.is_featured = 1, b'1', b'0'), p.headline, IF(p.is_hot = 1, b'1', b'0'),
  p.latitude, p.longitude, p.main_image_url,
  NULL, p.address_line1, NULL, p.provider_code, p.provider_type, p.province,
  p.service_radius_km, p.slug, 'ACTIVE', p.total_completed_bookings, p.total_reviews, 'VERIFIED', p.ward,
  p.years_experience, NULL
FROM petgo_seed_many_providers p
LEFT JOIN provider_profiles existed
  ON existed.provider_code = p.provider_code OR existed.slug = p.slug
WHERE existed.id IS NULL;

DROP TEMPORARY TABLE IF EXISTS petgo_seed_provider_service_map;
CREATE TEMPORARY TABLE petgo_seed_provider_service_map (
  provider_code varchar(32) NOT NULL,
  service_code varchar(32) NOT NULL,
  custom_name varchar(150) NOT NULL,
  price_amount decimal(12,2) NOT NULL,
  capacity_per_slot int NOT NULL,
  buffer_after_minutes int NOT NULL,
  is_featured tinyint(1) NOT NULL,
  display_order int NOT NULL,
  PRIMARY KEY (provider_code, service_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO petgo_seed_provider_service_map
(provider_code, service_code, custom_name, price_amount, capacity_per_slot, buffer_after_minutes, is_featured, display_order)
VALUES
('PRV-SEED-PAWSOME-SPA-Q1','SVC-CAT-019-BATH-DRY','Tắm sấy chó mèo tiêu chuẩn tại Pawsome',190000.00,2,10,1,1),
('PRV-SEED-PAWSOME-SPA-Q1','SVC-CAT-021-BREED-GROOMING','Cắt tỉa theo giống tại Pawsome',380000.00,1,15,1,2),
('PRV-SEED-PAWSOME-SPA-Q1','SVC-CAT-008-SPA-THERAPY','Spa dưỡng lông thư giãn Pawsome',300000.00,1,10,0,3),
('PRV-SEED-MEOMEO-CAT-HOTEL','SVC-CAT-024-CAT-HOTEL','Lưu trú mèo phòng yên tĩnh',430000.00,3,0,1,1),
('PRV-SEED-MEOMEO-CAT-HOTEL','SVC-CAT-027-NUTRITION','Tư vấn dinh dưỡng cho mèo',190000.00,1,5,0,2),
('PRV-SEED-MEOMEO-CAT-HOTEL','SVC-CAT-020-EAR-NAIL','Vệ sinh tai móng mèo nhẹ nhàng',95000.00,2,5,0,3),
('PRV-SEED-HAPPYDOG-HOTEL-TD','SVC-CAT-023-DOG-HOTEL','Lưu trú chó tiêu chuẩn Happy Dog',460000.00,4,0,1,1),
('PRV-SEED-HAPPYDOG-HOTEL-TD','SVC-CAT-010-DAYCARE-FULL','Daycare chó cả ngày Happy Dog',330000.00,5,0,0,2),
('PRV-SEED-HAPPYDOG-HOTEL-TD','SVC-CAT-018-WALK-EXERCISE','Dắt đi dạo vận động trong khuôn viên',130000.00,3,5,0,3),
('PRV-SEED-VETCARE-Q7','SVC-CAT-026-GENERAL-CHECKUP','Khám tổng quát tại VetCare Q7',260000.00,1,10,1,1),
('PRV-SEED-VETCARE-Q7','SVC-CAT-028-VACCINE','Tiêm vaccine định kỳ VetCare',330000.00,1,10,0,2),
('PRV-SEED-VETCARE-Q7','SVC-CAT-027-NUTRITION','Tư vấn dinh dưỡng thú cưng VetCare',200000.00,1,5,0,3),
('PRV-SEED-PETWALKER-BADINH','SVC-CAT-032-DOG-WALKING','Dắt chó đi dạo 60 phút Ba Đình',120000.00,3,5,1,1),
('PRV-SEED-PETWALKER-BADINH','SVC-CAT-017-PET-TRANSPORT','Đưa đón thú cưng nội quận Ba Đình',150000.00,2,5,0,2),
('PRV-SEED-PETWALKER-BADINH','SVC-CAT-011-HOME-SITTING','Ghé nhà chăm thú cưng theo buổi',290000.00,1,15,0,3),
('PRV-SEED-GROOMHOUSE-CAUGIAY','SVC-CAT-021-BREED-GROOMING','Grooming theo giống tại Cầu Giấy',370000.00,1,15,1,1),
('PRV-SEED-GROOMHOUSE-CAUGIAY','SVC-CAT-022-DYE-ACCESSORY','Nhuộm lông và phụ kiện an toàn',420000.00,1,15,0,2),
('PRV-SEED-GROOMHOUSE-CAUGIAY','SVC-CAT-019-BATH-DRY','Tắm sấy trước grooming',185000.00,2,10,0,3),
('PRV-SEED-PAWTRAINING-HCM','SVC-CAT-029-DOG-BASIC-COMMANDS','Lệnh cơ bản cho chó tại Paw Training',320000.00,1,15,1,1),
('PRV-SEED-PAWTRAINING-HCM','SVC-CAT-030-POTTY-TRAINING','Huấn luyện đi vệ sinh đúng chỗ',360000.00,1,15,0,2),
('PRV-SEED-PAWTRAINING-HCM','SVC-CAT-016-BEHAVIOR-CORRECT','Tư vấn chỉnh hành vi chó',420000.00,1,15,0,3),
('PRV-SEED-DANANG-PETTAXI','SVC-CAT-031-SPA-CLINIC-TAXI','Đưa đón spa/phòng khám Đà Nẵng',150000.00,2,5,1,1),
('PRV-SEED-DANANG-PETTAXI','SVC-CAT-018-WALK-EXERCISE','Dắt đi dạo ven sông Hàn',125000.00,3,5,0,2),
('PRV-SEED-DANANG-PETTAXI','SVC-CAT-011-HOME-SITTING','Trông giữ tại nhà Đà Nẵng',280000.00,1,15,0,3),
('PRV-SEED-CATLOVER-HANOI','SVC-CAT-024-CAT-HOTEL','Lưu trú mèo home stay Hà Nội',420000.00,3,0,1,1),
('PRV-SEED-CATLOVER-HANOI','SVC-CAT-011-HOME-SITTING','Trông mèo tại nhà theo buổi',270000.00,1,15,0,2),
('PRV-SEED-CATLOVER-HANOI','SVC-CAT-025-DAYCARE-HALF','Daycare mèo nửa ngày',220000.00,3,0,0,3),
('PRV-SEED-SAIGON-DAYCARE','SVC-CAT-010-DAYCARE-FULL','Daycare thú cưng cả ngày Saigon',330000.00,5,0,1,1),
('PRV-SEED-SAIGON-DAYCARE','SVC-CAT-025-DAYCARE-HALF','Daycare nửa ngày Saigon',230000.00,5,0,0,2),
('PRV-SEED-SAIGON-DAYCARE','SVC-CAT-019-BATH-DRY','Tắm sấy nhanh sau daycare',180000.00,2,10,0,3),
('PRV-SEED-POODLE-BEAUTY','SVC-CAT-021-BREED-GROOMING','Tạo kiểu Poodle teddy cut',390000.00,1,15,1,1),
('PRV-SEED-POODLE-BEAUTY','SVC-CAT-020-EAR-NAIL','Vệ sinh tai móng Poodle',90000.00,2,5,0,2),
('PRV-SEED-POODLE-BEAUTY','SVC-CAT-008-SPA-THERAPY','Spa dưỡng lông chó nhỏ',290000.00,1,10,0,3),
('PRV-SEED-SENIOR-PETCARE','SVC-CAT-014-POST-TREATMENT','Chăm sóc sau điều trị cho thú cưng lớn tuổi',360000.00,1,15,1,1),
('PRV-SEED-SENIOR-PETCARE','SVC-CAT-026-GENERAL-CHECKUP','Kiểm tra sức khỏe thú cưng lớn tuổi',270000.00,1,10,0,2),
('PRV-SEED-SENIOR-PETCARE','SVC-CAT-027-NUTRITION','Tư vấn dinh dưỡng thú cưng lớn tuổi',200000.00,1,5,0,3),
('PRV-SEED-PUPPY-SCHOOL','SVC-CAT-029-DOG-BASIC-COMMANDS','Lệnh cơ bản cho chó con',300000.00,1,15,1,1),
('PRV-SEED-PUPPY-SCHOOL','SVC-CAT-030-POTTY-TRAINING','Đi vệ sinh đúng chỗ cho chó con',350000.00,1,15,0,2),
('PRV-SEED-NHATRANG-HOTEL','SVC-CAT-023-DOG-HOTEL','Lưu trú chó tại Nha Trang',440000.00,4,0,1,1),
('PRV-SEED-NHATRANG-HOTEL','SVC-CAT-024-CAT-HOTEL','Lưu trú mèo tại Nha Trang',410000.00,3,0,0,2),
('PRV-SEED-NHATRANG-HOTEL','SVC-CAT-010-DAYCARE-FULL','Daycare thú cưng khi chủ đi biển',310000.00,5,0,0,3),
('PRV-SEED-HOMEVET-MOBILE','SVC-CAT-028-VACCINE','Tiêm vaccine tại nhà HomeVet',340000.00,1,10,1,1),
('PRV-SEED-HOMEVET-MOBILE','SVC-CAT-026-GENERAL-CHECKUP','Khám tổng quát tại nhà HomeVet',280000.00,1,10,0,2),
('PRV-SEED-HOMEVET-MOBILE','SVC-CAT-014-POST-TREATMENT','Theo dõi sau điều trị tại nhà',370000.00,1,15,0,3),
('PRV-SEED-COZY-SITTER-Q10','SVC-CAT-011-HOME-SITTING','Trông giữ tại nhà Quận 10',260000.00,1,15,1,1),
('PRV-SEED-COZY-SITTER-Q10','SVC-CAT-025-DAYCARE-HALF','Daycare nửa ngày tại Cozy Sitter',210000.00,3,0,0,2),
('PRV-SEED-COZY-SITTER-Q10','SVC-CAT-032-DOG-WALKING','Dắt chó đi dạo gần nhà Quận 10',115000.00,3,5,0,3),
('PRV-SEED-FURSPA-PREMIUM','SVC-CAT-008-SPA-THERAPY','Spa trị liệu Fur Spa Premium',310000.00,1,10,1,1),
('PRV-SEED-FURSPA-PREMIUM','SVC-CAT-019-BATH-DRY','Tắm sấy cao cấp Fur Spa',200000.00,2,10,0,2),
('PRV-SEED-FURSPA-PREMIUM','SVC-CAT-022-DYE-ACCESSORY','Nhuộm lông và phụ kiện sự kiện',430000.00,1,15,0,3),
('PRV-SEED-PETGO-EXPRESS','SVC-CAT-017-PET-TRANSPORT','Đưa đón thú cưng nội thành',150000.00,2,5,1,1),
('PRV-SEED-PETGO-EXPRESS','SVC-CAT-031-SPA-CLINIC-TAXI','Đưa đón spa/phòng khám theo lịch',160000.00,2,5,0,2);


-- Map dịch vụ cho từng provider: mỗi provider mới chỉ có 2-3 dịch vụ.
-- Nếu service_code chưa tồn tại vì anh chưa chạy seed V2, dòng đó sẽ được bỏ qua thay vì lỗi FK.
INSERT INTO provider_services (
  created_at, updated_at, is_active, approval_status, booking_buffer_minutes, buffer_after_minutes,
  capacity_per_slot, category_ids, currency_code, custom_name, description, display_order, duration_minutes,
  duration_type, is_featured, photo_urls, price_amount, price_unit, short_description, provider_id, service_id
)
SELECT
  NOW(6), NOW(6), b'1', 'APPROVED', 0, m.buffer_after_minutes,
  m.capacity_per_slot, CAST(s.category_id AS CHAR), 'VND', m.custom_name,
  CONCAT(s.description, '

Provider: ', pp.business_name),
  m.display_order, s.default_duration_minutes, 'MINUTES', IF(m.is_featured = 1, b'1', b'0'),
  pp.main_image_url, m.price_amount, s.price_unit, s.short_description, pp.id, s.id
FROM petgo_seed_provider_service_map m
JOIN provider_profiles pp ON pp.provider_code = m.provider_code
JOIN services s ON s.service_code = m.service_code
LEFT JOIN provider_services existed
  ON existed.provider_id = pp.id AND existed.service_id = s.id
WHERE existed.id IS NULL;

-- Thêm ảnh provider, tránh trùng theo provider_id + photo_url.
INSERT INTO provider_photos (created_at, updated_at, media_type, photo_url, is_primary, sort_order, provider_id)
SELECT NOW(6), NOW(6), 'IMAGE', p.main_image_url, b'1', 0, pp.id
FROM petgo_seed_many_providers p
JOIN provider_profiles pp ON pp.provider_code = p.provider_code
LEFT JOIN provider_photos existed ON existed.provider_id = pp.id AND existed.photo_url = p.main_image_url
WHERE p.main_image_url IS NOT NULL AND existed.id IS NULL;

INSERT INTO provider_photos (created_at, updated_at, media_type, photo_url, is_primary, sort_order, provider_id)
SELECT NOW(6), NOW(6), 'IMAGE', p.cover_image_url, b'0', 1, pp.id
FROM petgo_seed_many_providers p
JOIN provider_profiles pp ON pp.provider_code = p.provider_code
LEFT JOIN provider_photos existed ON existed.provider_id = pp.id AND existed.photo_url = p.cover_image_url
WHERE p.cover_image_url IS NOT NULL AND existed.id IS NULL;

-- Tạo availability slots tương lai để test booking/tìm kiếm lịch trống.
DROP TEMPORARY TABLE IF EXISTS petgo_seed_slot_patterns;
CREATE TEMPORARY TABLE petgo_seed_slot_patterns (
  day_offset int NOT NULL,
  start_time time NOT NULL,
  PRIMARY KEY (day_offset, start_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO petgo_seed_slot_patterns (day_offset, start_time) VALUES
(1, '09:00:00'), (1, '14:00:00'),
(3, '09:00:00'), (3, '14:00:00'),
(5, '09:00:00');

INSERT INTO provider_availability_slots (
  created_at, updated_at, capacity_booked, capacity_total, end_time, note, slot_date, slot_status,
  start_time, provider_id, provider_service_id
)
SELECT
  NOW(6), NOW(6), 0, ps.capacity_per_slot,
  CASE
    WHEN ps.duration_minutes >= 240 OR ps.price_unit = 'DAY' THEN '17:00:00'
    ELSE ADDTIME(sp.start_time, SEC_TO_TIME(LEAST(ps.duration_minutes, 180) * 60))
  END AS end_time,
  'Seed V3: slot khả dụng cho provider mới',
  DATE_ADD(CURDATE(), INTERVAL sp.day_offset DAY),
  'AVAILABLE',
  sp.start_time,
  pp.id,
  ps.id
FROM petgo_seed_many_providers p
JOIN provider_profiles pp ON pp.provider_code = p.provider_code
JOIN provider_services ps ON ps.provider_id = pp.id AND ps.is_active = b'1' AND ps.approval_status = 'APPROVED'
JOIN services s ON s.id = ps.service_id
JOIN petgo_seed_provider_service_map m ON m.provider_code = p.provider_code AND m.service_code = s.service_code
CROSS JOIN petgo_seed_slot_patterns sp
LEFT JOIN provider_availability_slots existed
  ON existed.provider_id = pp.id
 AND existed.provider_service_id = ps.id
 AND existed.slot_date = DATE_ADD(CURDATE(), INTERVAL sp.day_offset DAY)
 AND existed.start_time = sp.start_time
WHERE existed.id IS NULL;

-- Cập nhật giá thấp nhất cho provider mới.
-- Tắt SQL_SAFE_UPDATES tạm thời để chạy được trên MySQL Workbench đang bật Safe Updates.
SET @OLD_SQL_SAFE_UPDATES = @@SQL_SAFE_UPDATES;
SET SQL_SAFE_UPDATES = 0;

UPDATE provider_profiles pp
JOIN petgo_seed_many_providers p ON p.provider_code = pp.provider_code
SET pp.price_from_amount = (
  SELECT MIN(ps.price_amount)
  FROM provider_services ps
  WHERE ps.provider_id = pp.id AND ps.is_active = b'1' AND ps.approval_status = 'APPROVED'
)
WHERE pp.id > 0;

SET SQL_SAFE_UPDATES = @OLD_SQL_SAFE_UPDATES;

DROP TEMPORARY TABLE IF EXISTS petgo_seed_slot_patterns;
DROP TEMPORARY TABLE IF EXISTS petgo_seed_provider_service_map;
DROP TEMPORARY TABLE IF EXISTS petgo_seed_many_providers;

COMMIT;

-- Quick check sau khi chạy:
-- SELECT pp.provider_code, pp.business_name, COUNT(ps.id) AS total_services, MIN(ps.price_amount) AS price_from
-- FROM provider_profiles pp
-- LEFT JOIN provider_services ps ON ps.provider_id = pp.id AND ps.is_active = b'1' AND ps.approval_status = 'APPROVED'
-- WHERE pp.provider_code LIKE 'PRV-SEED-%'
-- GROUP BY pp.provider_code, pp.business_name
-- ORDER BY pp.provider_code;
--
-- SELECT COUNT(*) AS new_providers FROM provider_profiles WHERE provider_code LIKE 'PRV-SEED-%';
-- SELECT COUNT(*) AS new_provider_services
-- FROM provider_services ps JOIN provider_profiles pp ON pp.id = ps.provider_id
-- WHERE pp.provider_code LIKE 'PRV-SEED-%';
