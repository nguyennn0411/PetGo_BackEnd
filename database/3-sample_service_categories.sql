-- Query tạo danh mục mẫu nhiều cấp cho dịch vụ PetGo.
-- Chạy thủ công khi cần seed danh mục dịch vụ demo; file này chỉ tạo query, KHÔNG tự chạy.
-- Phù hợp sau khi reset database: chạy reset_database.sql, khởi động backend để seed role nếu cần,
-- sau đó có thể chạy file này để tạo cây service_categories.

USE petgo_db;

SET @old_sql_safe_updates = @@SQL_SAFE_UPDATES;
SET SQL_SAFE_UPDATES = 0;

-- =========================
-- Cấp 1: Nhóm dịch vụ chính
-- =========================
INSERT INTO service_categories (parent_id, name, description, is_active)
SELECT NULL, 'Chăm sóc & spa thú cưng', 'Các dịch vụ vệ sinh, tắm gội, cắt tỉa và chăm sóc ngoại hình cho thú cưng.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM service_categories WHERE parent_id IS NULL AND name = 'Chăm sóc & spa thú cưng');

INSERT INTO service_categories (parent_id, name, description, is_active)
SELECT NULL, 'Lưu trú & trông giữ', 'Dịch vụ khách sạn, daycare, trông giữ tại nhà và chăm sóc trong thời gian chủ vắng mặt.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM service_categories WHERE parent_id IS NULL AND name = 'Lưu trú & trông giữ');

INSERT INTO service_categories (parent_id, name, description, is_active)
SELECT NULL, 'Sức khỏe & thú y', 'Dịch vụ kiểm tra sức khỏe, tiêm phòng, tư vấn và chăm sóc y tế cơ bản cho thú cưng.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM service_categories WHERE parent_id IS NULL AND name = 'Sức khỏe & thú y');

INSERT INTO service_categories (parent_id, name, description, is_active)
SELECT NULL, 'Huấn luyện & hành vi', 'Dịch vụ huấn luyện vâng lời, chỉnh hành vi và phát triển kỹ năng xã hội cho thú cưng.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM service_categories WHERE parent_id IS NULL AND name = 'Huấn luyện & hành vi');

INSERT INTO service_categories (parent_id, name, description, is_active)
SELECT NULL, 'Di chuyển & hỗ trợ tại nhà', 'Dịch vụ đưa đón, dắt đi dạo, chăm sóc tận nơi và hỗ trợ sinh hoạt hằng ngày.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM service_categories WHERE parent_id IS NULL AND name = 'Di chuyển & hỗ trợ tại nhà');

-- =========================
-- Cấp 2: Danh mục con
-- =========================
INSERT INTO service_categories (parent_id, name, description, is_active)
SELECT p.id, 'Tắm gội & vệ sinh cơ bản', 'Tắm, sấy, vệ sinh tai, cắt móng và chăm sóc vệ sinh định kỳ.', TRUE
FROM service_categories p
WHERE p.parent_id IS NULL AND p.name = 'Chăm sóc & spa thú cưng'
  AND NOT EXISTS (SELECT 1 FROM service_categories c WHERE c.parent_id = p.id AND c.name = 'Tắm gội & vệ sinh cơ bản');

INSERT INTO service_categories (parent_id, name, description, is_active)
SELECT p.id, 'Cắt tỉa & tạo kiểu', 'Cắt lông, tạo kiểu, tỉa lông theo giống và nhu cầu thẩm mỹ.', TRUE
FROM service_categories p
WHERE p.parent_id IS NULL AND p.name = 'Chăm sóc & spa thú cưng'
  AND NOT EXISTS (SELECT 1 FROM service_categories c WHERE c.parent_id = p.id AND c.name = 'Cắt tỉa & tạo kiểu');

INSERT INTO service_categories (parent_id, name, description, is_active)
SELECT p.id, 'Spa trị liệu', 'Chăm sóc da lông, khử mùi, dưỡng lông và các gói thư giãn chuyên sâu.', TRUE
FROM service_categories p
WHERE p.parent_id IS NULL AND p.name = 'Chăm sóc & spa thú cưng'
  AND NOT EXISTS (SELECT 1 FROM service_categories c WHERE c.parent_id = p.id AND c.name = 'Spa trị liệu');

INSERT INTO service_categories (parent_id, name, description, is_active)
SELECT p.id, 'Khách sạn thú cưng', 'Lưu trú qua đêm hoặc nhiều ngày tại cơ sở chăm sóc thú cưng.', TRUE
FROM service_categories p
WHERE p.parent_id IS NULL AND p.name = 'Lưu trú & trông giữ'
  AND NOT EXISTS (SELECT 1 FROM service_categories c WHERE c.parent_id = p.id AND c.name = 'Khách sạn thú cưng');

INSERT INTO service_categories (parent_id, name, description, is_active)
SELECT p.id, 'Daycare ban ngày', 'Trông giữ, cho ăn, vui chơi và theo dõi thú cưng trong ngày.', TRUE
FROM service_categories p
WHERE p.parent_id IS NULL AND p.name = 'Lưu trú & trông giữ'
  AND NOT EXISTS (SELECT 1 FROM service_categories c WHERE c.parent_id = p.id AND c.name = 'Daycare ban ngày');

INSERT INTO service_categories (parent_id, name, description, is_active)
SELECT p.id, 'Trông giữ tại nhà', 'Người chăm sóc đến nhà khách hoặc nhận chăm thú cưng tại nhà riêng theo thỏa thuận.', TRUE
FROM service_categories p
WHERE p.parent_id IS NULL AND p.name = 'Lưu trú & trông giữ'
  AND NOT EXISTS (SELECT 1 FROM service_categories c WHERE c.parent_id = p.id AND c.name = 'Trông giữ tại nhà');

INSERT INTO service_categories (parent_id, name, description, is_active)
SELECT p.id, 'Khám & tư vấn sức khỏe', 'Khám tổng quát, tư vấn dinh dưỡng, chăm sóc phòng bệnh và theo dõi sức khỏe.', TRUE
FROM service_categories p
WHERE p.parent_id IS NULL AND p.name = 'Sức khỏe & thú y'
  AND NOT EXISTS (SELECT 1 FROM service_categories c WHERE c.parent_id = p.id AND c.name = 'Khám & tư vấn sức khỏe');

INSERT INTO service_categories (parent_id, name, description, is_active)
SELECT p.id, 'Tiêm phòng & phòng ký sinh', 'Tiêm vaccine, tẩy giun, phòng ve rận và các gói phòng bệnh định kỳ.', TRUE
FROM service_categories p
WHERE p.parent_id IS NULL AND p.name = 'Sức khỏe & thú y'
  AND NOT EXISTS (SELECT 1 FROM service_categories c WHERE c.parent_id = p.id AND c.name = 'Tiêm phòng & phòng ký sinh');

INSERT INTO service_categories (parent_id, name, description, is_active)
SELECT p.id, 'Chăm sóc sau điều trị', 'Hỗ trợ uống thuốc, thay băng, theo dõi phục hồi theo hướng dẫn chuyên môn.', TRUE
FROM service_categories p
WHERE p.parent_id IS NULL AND p.name = 'Sức khỏe & thú y'
  AND NOT EXISTS (SELECT 1 FROM service_categories c WHERE c.parent_id = p.id AND c.name = 'Chăm sóc sau điều trị');

INSERT INTO service_categories (parent_id, name, description, is_active)
SELECT p.id, 'Huấn luyện cơ bản', 'Dạy lệnh cơ bản, đi vệ sinh đúng chỗ, đi dây dắt và kỹ năng sinh hoạt.', TRUE
FROM service_categories p
WHERE p.parent_id IS NULL AND p.name = 'Huấn luyện & hành vi'
  AND NOT EXISTS (SELECT 1 FROM service_categories c WHERE c.parent_id = p.id AND c.name = 'Huấn luyện cơ bản');

INSERT INTO service_categories (parent_id, name, description, is_active)
SELECT p.id, 'Chỉnh hành vi', 'Hỗ trợ giảm sủa, cắn phá, lo âu xa chủ, hung hăng hoặc sợ hãi quá mức.', TRUE
FROM service_categories p
WHERE p.parent_id IS NULL AND p.name = 'Huấn luyện & hành vi'
  AND NOT EXISTS (SELECT 1 FROM service_categories c WHERE c.parent_id = p.id AND c.name = 'Chỉnh hành vi');

INSERT INTO service_categories (parent_id, name, description, is_active)
SELECT p.id, 'Đưa đón thú cưng', 'Đưa đón thú cưng đến spa, phòng khám, khách sạn hoặc địa điểm theo lịch hẹn.', TRUE
FROM service_categories p
WHERE p.parent_id IS NULL AND p.name = 'Di chuyển & hỗ trợ tại nhà'
  AND NOT EXISTS (SELECT 1 FROM service_categories c WHERE c.parent_id = p.id AND c.name = 'Đưa đón thú cưng');

INSERT INTO service_categories (parent_id, name, description, is_active)
SELECT p.id, 'Dắt đi dạo & vận động', 'Dắt chó đi dạo, vận động, vui chơi và theo dõi tình trạng trong buổi đi dạo.', TRUE
FROM service_categories p
WHERE p.parent_id IS NULL AND p.name = 'Di chuyển & hỗ trợ tại nhà'
  AND NOT EXISTS (SELECT 1 FROM service_categories c WHERE c.parent_id = p.id AND c.name = 'Dắt đi dạo & vận động');

-- =========================
-- Cấp 3: Gói/nhánh chi tiết
-- =========================
INSERT INTO service_categories (parent_id, name, description, is_active)
SELECT p.id, 'Tắm sấy chó mèo', 'Tắm bằng sản phẩm phù hợp, sấy khô và chải lông cơ bản.', TRUE
FROM service_categories p
JOIN service_categories root ON root.id = p.parent_id
WHERE root.name = 'Chăm sóc & spa thú cưng' AND p.name = 'Tắm gội & vệ sinh cơ bản'
  AND NOT EXISTS (SELECT 1 FROM service_categories c WHERE c.parent_id = p.id AND c.name = 'Tắm sấy chó mèo');

INSERT INTO service_categories (parent_id, name, description, is_active)
SELECT p.id, 'Vệ sinh tai, móng, tuyến hôi', 'Vệ sinh tai, cắt móng, mài móng và hỗ trợ vệ sinh tuyến hôi khi phù hợp.', TRUE
FROM service_categories p
JOIN service_categories root ON root.id = p.parent_id
WHERE root.name = 'Chăm sóc & spa thú cưng' AND p.name = 'Tắm gội & vệ sinh cơ bản'
  AND NOT EXISTS (SELECT 1 FROM service_categories c WHERE c.parent_id = p.id AND c.name = 'Vệ sinh tai, móng, tuyến hôi');

INSERT INTO service_categories (parent_id, name, description, is_active)
SELECT p.id, 'Cắt tỉa theo giống', 'Cắt tỉa theo đặc điểm giống, độ dài lông và yêu cầu của chủ nuôi.', TRUE
FROM service_categories p
JOIN service_categories root ON root.id = p.parent_id
WHERE root.name = 'Chăm sóc & spa thú cưng' AND p.name = 'Cắt tỉa & tạo kiểu'
  AND NOT EXISTS (SELECT 1 FROM service_categories c WHERE c.parent_id = p.id AND c.name = 'Cắt tỉa theo giống');

INSERT INTO service_categories (parent_id, name, description, is_active)
SELECT p.id, 'Nhuộm lông & phụ kiện', 'Tạo điểm nhấn thẩm mỹ, nhuộm lông an toàn và trang trí phụ kiện.', TRUE
FROM service_categories p
JOIN service_categories root ON root.id = p.parent_id
WHERE root.name = 'Chăm sóc & spa thú cưng' AND p.name = 'Cắt tỉa & tạo kiểu'
  AND NOT EXISTS (SELECT 1 FROM service_categories c WHERE c.parent_id = p.id AND c.name = 'Nhuộm lông & phụ kiện');

INSERT INTO service_categories (parent_id, name, description, is_active)
SELECT p.id, 'Lưu trú chó', 'Phòng lưu trú, cho ăn, vệ sinh và theo dõi dành cho chó.', TRUE
FROM service_categories p
JOIN service_categories root ON root.id = p.parent_id
WHERE root.name = 'Lưu trú & trông giữ' AND p.name = 'Khách sạn thú cưng'
  AND NOT EXISTS (SELECT 1 FROM service_categories c WHERE c.parent_id = p.id AND c.name = 'Lưu trú chó');

INSERT INTO service_categories (parent_id, name, description, is_active)
SELECT p.id, 'Lưu trú mèo', 'Phòng lưu trú yên tĩnh, vệ sinh khay cát, cho ăn và theo dõi dành cho mèo.', TRUE
FROM service_categories p
JOIN service_categories root ON root.id = p.parent_id
WHERE root.name = 'Lưu trú & trông giữ' AND p.name = 'Khách sạn thú cưng'
  AND NOT EXISTS (SELECT 1 FROM service_categories c WHERE c.parent_id = p.id AND c.name = 'Lưu trú mèo');

INSERT INTO service_categories (parent_id, name, description, is_active)
SELECT p.id, 'Daycare nửa ngày', 'Trông giữ và chăm sóc trong một buổi, phù hợp lịch làm việc ngắn.', TRUE
FROM service_categories p
JOIN service_categories root ON root.id = p.parent_id
WHERE root.name = 'Lưu trú & trông giữ' AND p.name = 'Daycare ban ngày'
  AND NOT EXISTS (SELECT 1 FROM service_categories c WHERE c.parent_id = p.id AND c.name = 'Daycare nửa ngày');

INSERT INTO service_categories (parent_id, name, description, is_active)
SELECT p.id, 'Khám tổng quát', 'Kiểm tra thể trạng, cân nặng, da lông, răng miệng và tư vấn chăm sóc.', TRUE
FROM service_categories p
JOIN service_categories root ON root.id = p.parent_id
WHERE root.name = 'Sức khỏe & thú y' AND p.name = 'Khám & tư vấn sức khỏe'
  AND NOT EXISTS (SELECT 1 FROM service_categories c WHERE c.parent_id = p.id AND c.name = 'Khám tổng quát');

INSERT INTO service_categories (parent_id, name, description, is_active)
SELECT p.id, 'Tư vấn dinh dưỡng', 'Tư vấn khẩu phần, chế độ ăn, kiểm soát cân nặng và chăm sóc theo độ tuổi.', TRUE
FROM service_categories p
JOIN service_categories root ON root.id = p.parent_id
WHERE root.name = 'Sức khỏe & thú y' AND p.name = 'Khám & tư vấn sức khỏe'
  AND NOT EXISTS (SELECT 1 FROM service_categories c WHERE c.parent_id = p.id AND c.name = 'Tư vấn dinh dưỡng');

INSERT INTO service_categories (parent_id, name, description, is_active)
SELECT p.id, 'Tiêm vaccine định kỳ', 'Tiêm phòng theo lịch và tư vấn nhắc lịch vaccine cho thú cưng.', TRUE
FROM service_categories p
JOIN service_categories root ON root.id = p.parent_id
WHERE root.name = 'Sức khỏe & thú y' AND p.name = 'Tiêm phòng & phòng ký sinh'
  AND NOT EXISTS (SELECT 1 FROM service_categories c WHERE c.parent_id = p.id AND c.name = 'Tiêm vaccine định kỳ');

INSERT INTO service_categories (parent_id, name, description, is_active)
SELECT p.id, 'Lệnh cơ bản cho chó', 'Dạy ngồi, nằm, đứng, gọi tên, đi cạnh chủ và phản hồi hiệu lệnh.', TRUE
FROM service_categories p
JOIN service_categories root ON root.id = p.parent_id
WHERE root.name = 'Huấn luyện & hành vi' AND p.name = 'Huấn luyện cơ bản'
  AND NOT EXISTS (SELECT 1 FROM service_categories c WHERE c.parent_id = p.id AND c.name = 'Lệnh cơ bản cho chó');

INSERT INTO service_categories (parent_id, name, description, is_active)
SELECT p.id, 'Đi vệ sinh đúng chỗ', 'Huấn luyện thói quen đi vệ sinh đúng vị trí và giảm sự cố trong nhà.', TRUE
FROM service_categories p
JOIN service_categories root ON root.id = p.parent_id
WHERE root.name = 'Huấn luyện & hành vi' AND p.name = 'Huấn luyện cơ bản'
  AND NOT EXISTS (SELECT 1 FROM service_categories c WHERE c.parent_id = p.id AND c.name = 'Đi vệ sinh đúng chỗ');

INSERT INTO service_categories (parent_id, name, description, is_active)
SELECT p.id, 'Đưa đón spa/phòng khám', 'Đưa đón thú cưng đến điểm hẹn và bàn giao theo lịch đặt trước.', TRUE
FROM service_categories p
JOIN service_categories root ON root.id = p.parent_id
WHERE root.name = 'Di chuyển & hỗ trợ tại nhà' AND p.name = 'Đưa đón thú cưng'
  AND NOT EXISTS (SELECT 1 FROM service_categories c WHERE c.parent_id = p.id AND c.name = 'Đưa đón spa/phòng khám');

INSERT INTO service_categories (parent_id, name, description, is_active)
SELECT p.id, 'Dắt chó đi dạo', 'Dắt chó đi dạo theo lịch, kèm cập nhật tình trạng sau buổi đi.', TRUE
FROM service_categories p
JOIN service_categories root ON root.id = p.parent_id
WHERE root.name = 'Di chuyển & hỗ trợ tại nhà' AND p.name = 'Dắt đi dạo & vận động'
  AND NOT EXISTS (SELECT 1 FROM service_categories c WHERE c.parent_id = p.id AND c.name = 'Dắt chó đi dạo');

UPDATE service_categories
SET is_active = TRUE
WHERE name IN (
    'Chăm sóc & spa thú cưng', 'Lưu trú & trông giữ', 'Sức khỏe & thú y', 'Huấn luyện & hành vi', 'Di chuyển & hỗ trợ tại nhà',
    'Tắm gội & vệ sinh cơ bản', 'Cắt tỉa & tạo kiểu', 'Spa trị liệu', 'Khách sạn thú cưng', 'Daycare ban ngày', 'Trông giữ tại nhà',
    'Khám & tư vấn sức khỏe', 'Tiêm phòng & phòng ký sinh', 'Chăm sóc sau điều trị', 'Huấn luyện cơ bản', 'Chỉnh hành vi',
    'Đưa đón thú cưng', 'Dắt đi dạo & vận động', 'Tắm sấy chó mèo', 'Vệ sinh tai, móng, tuyến hôi', 'Cắt tỉa theo giống',
    'Nhuộm lông & phụ kiện', 'Lưu trú chó', 'Lưu trú mèo', 'Daycare nửa ngày', 'Khám tổng quát', 'Tư vấn dinh dưỡng',
    'Tiêm vaccine định kỳ', 'Lệnh cơ bản cho chó', 'Đi vệ sinh đúng chỗ', 'Đưa đón spa/phòng khám', 'Dắt chó đi dạo'
);

SET SQL_SAFE_UPDATES = @old_sql_safe_updates;

SELECT
    root.name AS root_category,
    child.name AS child_category,
    leaf.name AS leaf_category,
    COALESCE(leaf.is_active, child.is_active, root.is_active) AS is_active
FROM service_categories root
LEFT JOIN service_categories child ON child.parent_id = root.id
LEFT JOIN service_categories leaf ON leaf.parent_id = child.id
WHERE root.parent_id IS NULL
  AND root.name IN ('Chăm sóc & spa thú cưng', 'Lưu trú & trông giữ', 'Sức khỏe & thú y', 'Huấn luyện & hành vi', 'Di chuyển & hỗ trợ tại nhà')
ORDER BY root.name, child.name, leaf.name;