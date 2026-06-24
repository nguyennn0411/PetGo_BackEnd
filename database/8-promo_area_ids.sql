-- Chuyển promo_codes từ provider scope sang area scope.
-- Chạy thủ công một lần trên DB hiện có.

USE petgo_db;

SET @db := DATABASE();

SET @has_area_ids := (
    SELECT COUNT(*)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = @db AND TABLE_NAME = 'promo_codes' AND COLUMN_NAME = 'area_ids'
);

SET @sql_add_area := IF(
    @has_area_ids = 0,
    'ALTER TABLE promo_codes ADD COLUMN area_ids VARCHAR(1000) NULL AFTER applicable_days_of_week',
    'SELECT ''area_ids already exists'' AS info'
);
PREPARE stmt FROM @sql_add_area;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @has_provider_ids := (
    SELECT COUNT(*)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = @db AND TABLE_NAME = 'promo_codes' AND COLUMN_NAME = 'provider_ids'
);

SET @sql_drop_provider := IF(
    @has_provider_ids > 0,
    'ALTER TABLE promo_codes DROP COLUMN provider_ids',
    'SELECT ''provider_ids already dropped'' AS info'
);
PREPARE stmt FROM @sql_drop_provider;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @has_provider_service_ids := (
    SELECT COUNT(*)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = @db AND TABLE_NAME = 'promo_codes' AND COLUMN_NAME = 'provider_service_ids'
);

SET @sql_drop_provider_service := IF(
    @has_provider_service_ids > 0,
    'ALTER TABLE promo_codes DROP COLUMN provider_service_ids',
    'SELECT ''provider_service_ids already dropped'' AS info'
);
PREPARE stmt FROM @sql_drop_provider_service;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
