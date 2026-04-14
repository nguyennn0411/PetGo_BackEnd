CREATE DATABASE IF NOT EXISTS petgo_db
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE petgo_db;

-- =========================
-- 1) AUTH / USERS
-- =========================

CREATE TABLE roles (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(255) NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE users (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(150) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    phone VARCHAR(30) NOT NULL UNIQUE,
    avatar_url VARCHAR(500) NULL,
    date_of_birth DATE NULL,
    gender ENUM('male','female','other','unknown') NOT NULL DEFAULT 'unknown',

    street VARCHAR(255) NULL,
    ward VARCHAR(120) NULL,
    district VARCHAR(120) NULL,
    city VARCHAR(120) NULL,
    country VARCHAR(120) NULL DEFAULT 'Vietnam',
    latitude DECIMAL(10,7) NULL,
    longitude DECIMAL(10,7) NULL,

    is_active TINYINT(1) NOT NULL DEFAULT 1,
    email_verified_at DATETIME NULL,
    phone_verified_at DATETIME NULL,
    last_login_at DATETIME NULL,

    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at DATETIME NULL,

    INDEX idx_users_city (city),
    INDEX idx_users_active (is_active)
) ENGINE=InnoDB;

CREATE TABLE user_roles (
    user_id BIGINT UNSIGNED NOT NULL,
    role_id BIGINT UNSIGNED NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_roles_user
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_user_roles_role
        FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- Seed role gợi ý:
-- customer, shop_owner, admin

-- =========================
-- 2) MEMBERSHIP
-- =========================

CREATE TABLE membership_plans (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(120) NOT NULL,
    description TEXT NULL,
    billing_cycle ENUM('monthly','yearly') NOT NULL,
    price DECIMAL(12,2) NOT NULL,
    discount_percent DECIMAL(5,2) NOT NULL DEFAULT 0.00,
    monthly_voucher_amount DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    priority_booking TINYINT(1) NOT NULL DEFAULT 0,
    reminder_enabled TINYINT(1) NOT NULL DEFAULT 0,
    support_level ENUM('standard','priority','vip') NOT NULL DEFAULT 'standard',
    is_popular TINYINT(1) NOT NULL DEFAULT 0,
    is_active TINYINT(1) NOT NULL DEFAULT 1,
    sort_order INT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE membership_plan_features (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    plan_id BIGINT UNSIGNED NOT NULL,
    feature_key VARCHAR(100) NULL,
    feature_text VARCHAR(255) NOT NULL,
    sort_order INT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_membership_features_plan
        FOREIGN KEY (plan_id) REFERENCES membership_plans(id) ON DELETE CASCADE,
    INDEX idx_membership_features_plan_sort (plan_id, sort_order)
) ENGINE=InnoDB;

CREATE TABLE user_memberships (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT UNSIGNED NOT NULL,
    plan_id BIGINT UNSIGNED NOT NULL,
    status ENUM('pending','active','expired','cancelled') NOT NULL DEFAULT 'pending',
    auto_renew TINYINT(1) NOT NULL DEFAULT 1,
    started_at DATETIME NULL,
    ended_at DATETIME NULL,
    next_billing_at DATETIME NULL,
    cancelled_at DATETIME NULL,
    price_at_purchase DECIMAL(12,2) NOT NULL,
    discount_amount DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    total_amount DECIMAL(12,2) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_user_memberships_user
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_user_memberships_plan
        FOREIGN KEY (plan_id) REFERENCES membership_plans(id) ON DELETE RESTRICT,

    INDEX idx_user_memberships_user_status (user_id, status),
    INDEX idx_user_memberships_next_billing (next_billing_at)
) ENGINE=InnoDB;

-- =========================
-- 3) PETS
-- =========================

CREATE TABLE pets (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    owner_user_id BIGINT UNSIGNED NOT NULL,
    name VARCHAR(120) NOT NULL,
    species ENUM('dog','cat','bird','rabbit','hamster','other') NOT NULL,
    breed VARCHAR(120) NULL,
    gender ENUM('male','female','unknown') NOT NULL DEFAULT 'unknown',
    birth_date DATE NULL,
    weight_kg DECIMAL(5,2) NULL,
    color VARCHAR(100) NULL,
    avatar_url VARCHAR(500) NULL,

    vaccination_notes TEXT NULL,
    allergy_notes TEXT NULL,
    medical_notes TEXT NULL,
    general_notes TEXT NULL,

    is_active TINYINT(1) NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at DATETIME NULL,

    CONSTRAINT fk_pets_owner
        FOREIGN KEY (owner_user_id) REFERENCES users(id) ON DELETE CASCADE,

    INDEX idx_pets_owner (owner_user_id),
    INDEX idx_pets_species (species)
) ENGINE=InnoDB;

CREATE TABLE pet_photos (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    pet_id BIGINT UNSIGNED NOT NULL,
    image_url VARCHAR(500) NOT NULL,
    is_primary TINYINT(1) NOT NULL DEFAULT 0,
    sort_order INT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_pet_photos_pet
        FOREIGN KEY (pet_id) REFERENCES pets(id) ON DELETE CASCADE,

    INDEX idx_pet_photos_pet_sort (pet_id, sort_order)
) ENGINE=InnoDB;

-- =========================
-- 4) PROVIDERS
-- =========================

CREATE TABLE providers (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    owner_user_id BIGINT UNSIGNED NULL,
    code VARCHAR(50) NOT NULL UNIQUE,
    slug VARCHAR(180) NOT NULL UNIQUE,
    name VARCHAR(180) NOT NULL,
    provider_type ENUM('spa','clinic','hotel','boarding','training','other') NOT NULL,
    description TEXT NULL,

    email VARCHAR(150) NULL,
    phone VARCHAR(30) NULL,
    website_url VARCHAR(255) NULL,

    logo_url VARCHAR(500) NULL,
    banner_url VARCHAR(500) NULL,

    street VARCHAR(255) NULL,
    ward VARCHAR(120) NULL,
    district VARCHAR(120) NULL,
    city VARCHAR(120) NOT NULL,
    country VARCHAR(120) NOT NULL DEFAULT 'Vietnam',
    latitude DECIMAL(10,7) NULL,
    longitude DECIMAL(10,7) NULL,

    avg_rating DECIMAL(3,2) NOT NULL DEFAULT 0.00,
    review_count INT NOT NULL DEFAULT 0,
    price_from DECIMAL(12,2) NOT NULL DEFAULT 0.00,

    is_verified TINYINT(1) NOT NULL DEFAULT 0,
    is_featured TINYINT(1) NOT NULL DEFAULT 0,
    status ENUM('draft','pending_approval','active','suspended','closed') NOT NULL DEFAULT 'active',

    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at DATETIME NULL,

    CONSTRAINT fk_providers_owner
        FOREIGN KEY (owner_user_id) REFERENCES users(id) ON DELETE SET NULL,

    INDEX idx_providers_city (city),
    INDEX idx_providers_type (provider_type),
    INDEX idx_providers_status (status),
    INDEX idx_providers_featured (is_featured),
    INDEX idx_providers_location (latitude, longitude)
) ENGINE=InnoDB;

CREATE TABLE provider_gallery (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    provider_id BIGINT UNSIGNED NOT NULL,
    media_url VARCHAR(500) NOT NULL,
    media_type ENUM('image','video') NOT NULL DEFAULT 'image',
    sort_order INT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_provider_gallery_provider
        FOREIGN KEY (provider_id) REFERENCES providers(id) ON DELETE CASCADE,

    INDEX idx_provider_gallery_provider_sort (provider_id, sort_order)
) ENGINE=InnoDB;

CREATE TABLE provider_business_hours (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    provider_id BIGINT UNSIGNED NOT NULL,
    day_of_week TINYINT NOT NULL COMMENT '1=Mon ... 7=Sun',
    open_time TIME NULL,
    close_time TIME NULL,
    is_closed TINYINT(1) NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_provider_hours_provider
        FOREIGN KEY (provider_id) REFERENCES providers(id) ON DELETE CASCADE,

    UNIQUE KEY uk_provider_day (provider_id, day_of_week)
) ENGINE=InnoDB;

CREATE TABLE provider_unavailable_periods (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    provider_id BIGINT UNSIGNED NOT NULL,
    start_at DATETIME NOT NULL,
    end_at DATETIME NOT NULL,
    reason VARCHAR(255) NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_provider_unavailable_provider
        FOREIGN KEY (provider_id) REFERENCES providers(id) ON DELETE CASCADE,

    INDEX idx_provider_unavailable_range (provider_id, start_at, end_at)
) ENGINE=InnoDB;

-- =========================
-- 5) SERVICES
-- =========================

CREATE TABLE service_categories (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    parent_id BIGINT UNSIGNED NULL,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(120) NOT NULL,
    slug VARCHAR(160) NOT NULL UNIQUE,
    description VARCHAR(255) NULL,
    sort_order INT NOT NULL DEFAULT 0,
    is_active TINYINT(1) NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_service_categories_parent
        FOREIGN KEY (parent_id) REFERENCES service_categories(id) ON DELETE SET NULL
) ENGINE=InnoDB;

CREATE TABLE service_catalogs (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    category_id BIGINT UNSIGNED NOT NULL,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(150) NOT NULL,
    slug VARCHAR(180) NOT NULL UNIQUE,
    description TEXT NULL,
    pet_species ENUM('dog','cat','bird','rabbit','hamster','all','other') NOT NULL DEFAULT 'all',
    is_active TINYINT(1) NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_service_catalogs_category
        FOREIGN KEY (category_id) REFERENCES service_categories(id) ON DELETE RESTRICT,

    INDEX idx_service_catalogs_category (category_id)
) ENGINE=InnoDB;

CREATE TABLE provider_services (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    provider_id BIGINT UNSIGNED NOT NULL,
    service_catalog_id BIGINT UNSIGNED NULL,
    name VARCHAR(150) NOT NULL,
    description TEXT NULL,
    price DECIMAL(12,2) NOT NULL,
    duration_minutes INT NOT NULL,
    image_url VARCHAR(500) NULL,
    is_active TINYINT(1) NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at DATETIME NULL,

    CONSTRAINT fk_provider_services_provider
        FOREIGN KEY (provider_id) REFERENCES providers(id) ON DELETE CASCADE,
    CONSTRAINT fk_provider_services_catalog
        FOREIGN KEY (service_catalog_id) REFERENCES service_catalogs(id) ON DELETE SET NULL,

    INDEX idx_provider_services_provider (provider_id),
    INDEX idx_provider_services_catalog (service_catalog_id),
    INDEX idx_provider_services_active (is_active),
    INDEX idx_provider_services_price (price)
) ENGINE=InnoDB;

-- =========================
-- 6) FAVORITES / PROMO
-- =========================

CREATE TABLE favorites (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT UNSIGNED NOT NULL,
    provider_id BIGINT UNSIGNED NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_favorites_user
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_favorites_provider
        FOREIGN KEY (provider_id) REFERENCES providers(id) ON DELETE CASCADE,

    UNIQUE KEY uk_favorite_user_provider (user_id, provider_id),
    INDEX idx_favorites_provider (provider_id)
) ENGINE=InnoDB;

CREATE TABLE promo_codes (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(150) NOT NULL,
    description VARCHAR(255) NULL,
    discount_type ENUM('percent','fixed') NOT NULL,
    discount_value DECIMAL(12,2) NOT NULL,
    max_discount_amount DECIMAL(12,2) NULL,
    min_order_amount DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    applies_to ENUM('booking','membership','all') NOT NULL DEFAULT 'all',
    start_at DATETIME NOT NULL,
    end_at DATETIME NOT NULL,
    usage_limit INT NULL,
    usage_count INT NOT NULL DEFAULT 0,
    max_uses_per_user INT NULL,
    is_active TINYINT(1) NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_promo_codes_active_range (is_active, start_at, end_at)
) ENGINE=InnoDB;

-- =========================
-- 7) BOOKINGS
-- =========================

CREATE TABLE bookings (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    booking_code VARCHAR(30) NOT NULL UNIQUE,

    user_id BIGINT UNSIGNED NOT NULL,
    pet_id BIGINT UNSIGNED NOT NULL,
    provider_id BIGINT UNSIGNED NOT NULL,
    provider_service_id BIGINT UNSIGNED NOT NULL,

    booking_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,

    status ENUM('pending','confirmed','completed','cancelled','rescheduled','no_show')
        NOT NULL DEFAULT 'pending',

    payment_method ENUM('cash','transfer','card','momo','vnpay','bank')
        NOT NULL DEFAULT 'cash',

    payment_status ENUM('pending','paid','failed','refunded','partially_refunded')
        NOT NULL DEFAULT 'pending',

    pet_name_snapshot VARCHAR(120) NOT NULL,
    provider_name_snapshot VARCHAR(180) NOT NULL,
    service_name_snapshot VARCHAR(150) NOT NULL,
    unit_price DECIMAL(12,2) NOT NULL,

    subtotal DECIMAL(12,2) NOT NULL,
    discount_amount DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    total_amount DECIMAL(12,2) NOT NULL,

    notes TEXT NULL,
    cancel_reason VARCHAR(255) NULL,
    cancelled_at DATETIME NULL,
    cancelled_by_user_id BIGINT UNSIGNED NULL,
    confirmed_at DATETIME NULL,
    completed_at DATETIME NULL,

    rescheduled_from_booking_id BIGINT UNSIGNED NULL,

    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_bookings_user
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_bookings_pet
        FOREIGN KEY (pet_id) REFERENCES pets(id) ON DELETE RESTRICT,
    CONSTRAINT fk_bookings_provider
        FOREIGN KEY (provider_id) REFERENCES providers(id) ON DELETE RESTRICT,
    CONSTRAINT fk_bookings_provider_service
        FOREIGN KEY (provider_service_id) REFERENCES provider_services(id) ON DELETE RESTRICT,
    CONSTRAINT fk_bookings_cancelled_by
        FOREIGN KEY (cancelled_by_user_id) REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT fk_bookings_rescheduled_from
        FOREIGN KEY (rescheduled_from_booking_id) REFERENCES bookings(id) ON DELETE SET NULL,

    INDEX idx_bookings_user (user_id),
    INDEX idx_bookings_provider (provider_id),
    INDEX idx_bookings_pet (pet_id),
    INDEX idx_bookings_date_status (booking_date, status),
    INDEX idx_bookings_provider_datetime (provider_id, booking_date, start_time, end_time)
) ENGINE=InnoDB;

CREATE TABLE booking_status_history (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    booking_id BIGINT UNSIGNED NOT NULL,
    old_status ENUM('pending','confirmed','completed','cancelled','rescheduled','no_show') NULL,
    new_status ENUM('pending','confirmed','completed','cancelled','rescheduled','no_show') NOT NULL,
    changed_by_user_id BIGINT UNSIGNED NULL,
    note VARCHAR(255) NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_booking_history_booking
        FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE CASCADE,
    CONSTRAINT fk_booking_history_changed_by
        FOREIGN KEY (changed_by_user_id) REFERENCES users(id) ON DELETE SET NULL,

    INDEX idx_booking_history_booking (booking_id, created_at)
) ENGINE=InnoDB;

CREATE TABLE booking_promotions (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    booking_id BIGINT UNSIGNED NOT NULL,
    promo_code_id BIGINT UNSIGNED NOT NULL,
    discount_amount DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_booking_promotions_booking
        FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE CASCADE,
    CONSTRAINT fk_booking_promotions_promo
        FOREIGN KEY (promo_code_id) REFERENCES promo_codes(id) ON DELETE RESTRICT,

    UNIQUE KEY uk_booking_promo (booking_id, promo_code_id)
) ENGINE=InnoDB;

-- =========================
-- 8) INVOICES / PAYMENTS
-- =========================

CREATE TABLE invoices (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    invoice_number VARCHAR(40) NOT NULL UNIQUE,
    user_id BIGINT UNSIGNED NOT NULL,
    booking_id BIGINT UNSIGNED NULL,
    user_membership_id BIGINT UNSIGNED NULL,

    invoice_type ENUM('booking','membership') NOT NULL,
    currency_code CHAR(3) NOT NULL DEFAULT 'VND',

    subtotal DECIMAL(12,2) NOT NULL,
    discount_amount DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    tax_amount DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    total_amount DECIMAL(12,2) NOT NULL,

    status ENUM('issued','paid','void') NOT NULL DEFAULT 'issued',
    issued_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    due_at DATETIME NULL,
    paid_at DATETIME NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_invoices_user
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_invoices_booking
        FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE SET NULL,
    CONSTRAINT fk_invoices_user_membership
        FOREIGN KEY (user_membership_id) REFERENCES user_memberships(id) ON DELETE SET NULL,

    INDEX idx_invoices_user (user_id),
    INDEX idx_invoices_status (status)
) ENGINE=InnoDB;

CREATE TABLE payments (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    payment_code VARCHAR(40) NOT NULL UNIQUE,
    invoice_id BIGINT UNSIGNED NULL,
    booking_id BIGINT UNSIGNED NULL,
    user_membership_id BIGINT UNSIGNED NULL,
    user_id BIGINT UNSIGNED NOT NULL,

    payment_type ENUM('booking','membership') NOT NULL,
    method ENUM('cash','transfer','card','momo','vnpay','bank') NOT NULL,
    gateway_provider VARCHAR(50) NULL,
    gateway_transaction_id VARCHAR(120) NULL UNIQUE,

    amount DECIMAL(12,2) NOT NULL,
    currency_code CHAR(3) NOT NULL DEFAULT 'VND',

    status ENUM('pending','completed','failed','cancelled','refunded')
        NOT NULL DEFAULT 'pending',

    paid_at DATETIME NULL,
    failed_reason VARCHAR(255) NULL,
    refunded_at DATETIME NULL,
    raw_response_json JSON NULL,

    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_payments_invoice
        FOREIGN KEY (invoice_id) REFERENCES invoices(id) ON DELETE SET NULL,
    CONSTRAINT fk_payments_booking
        FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE SET NULL,
    CONSTRAINT fk_payments_user_membership
        FOREIGN KEY (user_membership_id) REFERENCES user_memberships(id) ON DELETE SET NULL,
    CONSTRAINT fk_payments_user
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,

    INDEX idx_payments_user (user_id),
    INDEX idx_payments_booking (booking_id),
    INDEX idx_payments_membership (user_membership_id),
    INDEX idx_payments_status (status)
) ENGINE=InnoDB;

-- =========================
-- 9) REVIEWS
-- =========================

CREATE TABLE reviews (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT UNSIGNED NOT NULL,
    booking_id BIGINT UNSIGNED NOT NULL,
    provider_id BIGINT UNSIGNED NOT NULL,
    provider_service_id BIGINT UNSIGNED NULL,

    rating TINYINT NOT NULL,
    title VARCHAR(150) NULL,
    comment TEXT NULL,

    provider_reply TEXT NULL,
    provider_replied_at DATETIME NULL,

    is_visible TINYINT(1) NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_reviews_user
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_reviews_booking
        FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE CASCADE,
    CONSTRAINT fk_reviews_provider
        FOREIGN KEY (provider_id) REFERENCES providers(id) ON DELETE CASCADE,
    CONSTRAINT fk_reviews_provider_service
        FOREIGN KEY (provider_service_id) REFERENCES provider_services(id) ON DELETE SET NULL,

    CONSTRAINT chk_reviews_rating CHECK (rating BETWEEN 1 AND 5),
    UNIQUE KEY uk_reviews_booking (booking_id),
    INDEX idx_reviews_provider (provider_id),
    INDEX idx_reviews_user (user_id)
) ENGINE=InnoDB;

CREATE TABLE review_media (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    review_id BIGINT UNSIGNED NOT NULL,
    media_url VARCHAR(500) NOT NULL,
    media_type ENUM('image','video') NOT NULL DEFAULT 'image',
    sort_order INT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_review_media_review
        FOREIGN KEY (review_id) REFERENCES reviews(id) ON DELETE CASCADE,

    INDEX idx_review_media_review_sort (review_id, sort_order)
) ENGINE=InnoDB;

-- =========================
-- 10) HELP CENTER / FAQ
-- =========================

CREATE TABLE faq_categories (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(120) NOT NULL,
    sort_order INT NOT NULL DEFAULT 0,
    is_active TINYINT(1) NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE faqs (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    category_id BIGINT UNSIGNED NULL,
    question VARCHAR(255) NOT NULL,
    answer TEXT NOT NULL,
    sort_order INT NOT NULL DEFAULT 0,
    is_active TINYINT(1) NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_faqs_category
        FOREIGN KEY (category_id) REFERENCES faq_categories(id) ON DELETE SET NULL,

    INDEX idx_faqs_category_sort (category_id, sort_order)
) ENGINE=InnoDB;

-- =========================
-- 11) NOTIFICATIONS
-- =========================

CREATE TABLE notifications (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT UNSIGNED NOT NULL,
    notification_type ENUM('booking','payment','membership','review','system') NOT NULL,
    title VARCHAR(180) NOT NULL,
    body TEXT NOT NULL,
    related_entity_type VARCHAR(50) NULL,
    related_entity_id BIGINT UNSIGNED NULL,
    is_read TINYINT(1) NOT NULL DEFAULT 0,
    read_at DATETIME NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_notifications_user
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,

    INDEX idx_notifications_user_read (user_id, is_read, created_at)
) ENGINE=InnoDB;
