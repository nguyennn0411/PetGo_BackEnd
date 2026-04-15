-- PetGo MySQL schema v1
-- Target: MySQL 8+, Spring Boot + JPA + Flyway friendly
-- Purpose: marketplace platform connecting pet owners and providers/caregivers

CREATE DATABASE IF NOT EXISTS petgo_db
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE petgo_db;

-- =========================
-- 1) AUTH / USER
-- =========================

CREATE TABLE roles (
                       id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                       code VARCHAR(50) NOT NULL,
                       name VARCHAR(100) NOT NULL,
                       description VARCHAR(255) NULL,
                       created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                       PRIMARY KEY (id),
                       UNIQUE KEY uk_roles_code (code)
) ENGINE=InnoDB;

CREATE TABLE users (
                       id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                       user_code VARCHAR(32) NOT NULL,
                       email VARCHAR(190) NOT NULL,
                       password_hash VARCHAR(255) NOT NULL,
                       full_name VARCHAR(150) NOT NULL,
                       phone_number VARCHAR(30) NULL,
                       avatar_url VARCHAR(500) NULL,
                       cover_url VARCHAR(500) NULL,
                       gender ENUM('MALE','FEMALE','OTHER','PREFER_NOT_TO_SAY') NULL,
                       date_of_birth DATE NULL,
                       address_line1 VARCHAR(255) NULL,
                       address_line2 VARCHAR(255) NULL,
                       ward VARCHAR(120) NULL,
                       district VARCHAR(120) NULL,
                       city VARCHAR(120) NULL,
                       province VARCHAR(120) NULL,
                       country_code CHAR(2) NOT NULL DEFAULT 'VN',
                       latitude DECIMAL(10,7) NULL,
                       longitude DECIMAL(10,7) NULL,
                       email_verified_at DATETIME NULL,
                       phone_verified_at DATETIME NULL,
                       status ENUM('ACTIVE','INACTIVE','SUSPENDED','PENDING_VERIFICATION') NOT NULL DEFAULT 'ACTIVE',
                       last_login_at DATETIME NULL,
                       deleted_at DATETIME NULL,
                       created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                       PRIMARY KEY (id),
                       UNIQUE KEY uk_users_user_code (user_code),
                       UNIQUE KEY uk_users_email (email),
                       UNIQUE KEY uk_users_phone (phone_number),
                       KEY idx_users_status (status),
                       KEY idx_users_city (city),
                       KEY idx_users_deleted_at (deleted_at)
) ENGINE=InnoDB;

CREATE TABLE user_roles (
                            user_id BIGINT UNSIGNED NOT NULL,
                            role_id BIGINT UNSIGNED NOT NULL,
                            assigned_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            PRIMARY KEY (user_id, role_id),
                            CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users(id),
                            CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles(id)
) ENGINE=InnoDB;

CREATE TABLE refresh_tokens (
                                id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                                user_id BIGINT UNSIGNED NOT NULL,
                                token_hash VARCHAR(255) NOT NULL,
                                expires_at DATETIME NOT NULL,
                                revoked_at DATETIME NULL,
                                created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                PRIMARY KEY (id),
                                UNIQUE KEY uk_refresh_tokens_token_hash (token_hash),
                                KEY idx_refresh_tokens_user (user_id),
                                KEY idx_refresh_tokens_expires (expires_at),
                                CONSTRAINT fk_refresh_tokens_user FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB;

-- =========================
-- 2) PETS
-- =========================

CREATE TABLE pets (
                      id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                      pet_code VARCHAR(32) NOT NULL,
                      owner_user_id BIGINT UNSIGNED NOT NULL,
                      name VARCHAR(120) NOT NULL,
                      species ENUM('DOG','CAT','BIRD','RABBIT','HAMSTER','REPTILE','OTHER') NOT NULL,
                      breed VARCHAR(120) NULL,
                      gender ENUM('MALE','FEMALE','UNKNOWN') NULL,
                      date_of_birth DATE NULL,
                      age_label VARCHAR(50) NULL,
                      weight_kg DECIMAL(6,2) NULL,
                      color VARCHAR(100) NULL,
                      size ENUM('XS','S','M','L','XL','UNKNOWN') NOT NULL DEFAULT 'UNKNOWN',
                      avatar_url VARCHAR(500) NULL,
                      health_notes TEXT NULL,
                      allergy_notes TEXT NULL,
                      behavior_notes TEXT NULL,
                      vaccination_notes TEXT NULL,
                      status ENUM('ACTIVE','INACTIVE') NOT NULL DEFAULT 'ACTIVE',
                      deleted_at DATETIME NULL,
                      created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                      updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                      PRIMARY KEY (id),
                      UNIQUE KEY uk_pets_pet_code (pet_code),
                      KEY idx_pets_owner (owner_user_id),
                      KEY idx_pets_species (species),
                      KEY idx_pets_status (status),
                      CONSTRAINT fk_pets_owner FOREIGN KEY (owner_user_id) REFERENCES users(id)
) ENGINE=InnoDB;

CREATE TABLE pet_photos (
                            id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                            pet_id BIGINT UNSIGNED NOT NULL,
                            photo_url VARCHAR(500) NOT NULL,
                            is_primary BOOLEAN NOT NULL DEFAULT FALSE,
                            sort_order INT NOT NULL DEFAULT 0,
                            created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                            PRIMARY KEY (id),
                            KEY idx_pet_photos_pet (pet_id),
                            KEY idx_pet_photos_primary (pet_id, is_primary),
                            CONSTRAINT fk_pet_photos_pet FOREIGN KEY (pet_id) REFERENCES pets(id)
) ENGINE=InnoDB;

-- =========================
-- 3) PROVIDERS / CAREGIVERS
-- =========================

CREATE TABLE provider_profiles (
                                   id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                                   provider_code VARCHAR(32) NOT NULL,
                                   user_id BIGINT UNSIGNED NOT NULL,
                                   business_name VARCHAR(180) NOT NULL,
                                   slug VARCHAR(190) NOT NULL,
                                   provider_type ENUM('INDIVIDUAL','BUSINESS','CLINIC','SPA','BOARDING','TRAINING_CENTER','WALKER','OTHER') NOT NULL DEFAULT 'BUSINESS',
                                   headline VARCHAR(255) NULL,
                                   description TEXT NULL,
                                   years_experience INT NULL,
                                   verification_status ENUM('PENDING','VERIFIED','REJECTED') NOT NULL DEFAULT 'PENDING',
                                   is_featured BOOLEAN NOT NULL DEFAULT FALSE,
                                   is_hot BOOLEAN NOT NULL DEFAULT FALSE,
                                   accepts_instant_booking BOOLEAN NOT NULL DEFAULT TRUE,
                                   accepts_membership BOOLEAN NOT NULL DEFAULT TRUE,
                                   average_rating DECIMAL(3,2) NOT NULL DEFAULT 0.00,
                                   total_reviews INT NOT NULL DEFAULT 0,
                                   total_completed_bookings INT NOT NULL DEFAULT 0,
                                   service_radius_km DECIMAL(6,2) NULL,
                                   cancellation_free_hours INT NOT NULL DEFAULT 24,
                                   emergency_phone VARCHAR(30) NULL,
                                   primary_address_line1 VARCHAR(255) NULL,
                                   primary_address_line2 VARCHAR(255) NULL,
                                   ward VARCHAR(120) NULL,
                                   district VARCHAR(120) NULL,
                                   city VARCHAR(120) NULL,
                                   province VARCHAR(120) NULL,
                                   country_code CHAR(2) NOT NULL DEFAULT 'VN',
                                   latitude DECIMAL(10,7) NULL,
                                   longitude DECIMAL(10,7) NULL,
                                   main_image_url VARCHAR(500) NULL,
                                   cover_image_url VARCHAR(500) NULL,
                                   price_from_amount DECIMAL(12,2) NULL,
                                   currency_code CHAR(3) NOT NULL DEFAULT 'VND',
                                   status ENUM('DRAFT','ACTIVE','INACTIVE','SUSPENDED') NOT NULL DEFAULT 'ACTIVE',
                                   deleted_at DATETIME NULL,
                                   created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                   updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                   PRIMARY KEY (id),
                                   UNIQUE KEY uk_provider_profiles_provider_code (provider_code),
                                   UNIQUE KEY uk_provider_profiles_user (user_id),
                                   UNIQUE KEY uk_provider_profiles_slug (slug),
                                   KEY idx_provider_profiles_status (status),
                                   KEY idx_provider_profiles_city (city),
                                   KEY idx_provider_profiles_featured (is_featured),
                                   KEY idx_provider_profiles_rating (average_rating),
                                   CONSTRAINT fk_provider_profiles_user FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB;

CREATE TABLE provider_photos (
                                 id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                                 provider_id BIGINT UNSIGNED NOT NULL,
                                 photo_url VARCHAR(500) NOT NULL,
                                 media_type ENUM('IMAGE','VIDEO') NOT NULL DEFAULT 'IMAGE',
                                 is_primary BOOLEAN NOT NULL DEFAULT FALSE,
                                 sort_order INT NOT NULL DEFAULT 0,
                                 created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                 updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                 PRIMARY KEY (id),
                                 KEY idx_provider_photos_provider (provider_id),
                                 KEY idx_provider_photos_primary (provider_id, is_primary),
                                 CONSTRAINT fk_provider_photos_provider FOREIGN KEY (provider_id) REFERENCES provider_profiles(id)
) ENGINE=InnoDB;

CREATE TABLE provider_business_hours (
                                         id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                                         provider_id BIGINT UNSIGNED NOT NULL,
                                         weekday TINYINT NOT NULL COMMENT '1=Mon ... 7=Sun',
                                         opens_at TIME NULL,
                                         closes_at TIME NULL,
                                         break_starts_at TIME NULL,
                                         break_ends_at TIME NULL,
                                         is_closed BOOLEAN NOT NULL DEFAULT FALSE,
                                         created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                         updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                         PRIMARY KEY (id),
                                         UNIQUE KEY uk_provider_business_hours (provider_id, weekday),
                                         CONSTRAINT fk_provider_business_hours_provider FOREIGN KEY (provider_id) REFERENCES provider_profiles(id)
) ENGINE=InnoDB;

CREATE TABLE provider_unavailable_dates (
                                            id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                                            provider_id BIGINT UNSIGNED NOT NULL,
                                            unavailable_date DATE NOT NULL,
                                            reason VARCHAR(255) NULL,
                                            created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                            updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                            PRIMARY KEY (id),
                                            UNIQUE KEY uk_provider_unavailable_dates (provider_id, unavailable_date),
                                            CONSTRAINT fk_provider_unavailable_dates_provider FOREIGN KEY (provider_id) REFERENCES provider_profiles(id)
) ENGINE=InnoDB;

-- =========================
-- 4) CATALOG
-- =========================

CREATE TABLE service_categories (
                                    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                                    parent_id BIGINT UNSIGNED NULL,
                                    name VARCHAR(120) NOT NULL,
                                    slug VARCHAR(120) NOT NULL,
                                    icon_key VARCHAR(80) NULL,
                                    description VARCHAR(255) NULL,
                                    sort_order INT NOT NULL DEFAULT 0,
                                    is_active BOOLEAN NOT NULL DEFAULT TRUE,
                                    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                    PRIMARY KEY (id),
                                    UNIQUE KEY uk_service_categories_slug (slug),
                                    KEY idx_service_categories_parent (parent_id),
                                    CONSTRAINT fk_service_categories_parent FOREIGN KEY (parent_id) REFERENCES service_categories(id)
) ENGINE=InnoDB;

CREATE TABLE services (
                          id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                          service_code VARCHAR(32) NOT NULL,
                          category_id BIGINT UNSIGNED NOT NULL,
                          name VARCHAR(150) NOT NULL,
                          slug VARCHAR(150) NOT NULL,
                          short_description VARCHAR(255) NULL,
                          description TEXT NULL,
                          default_duration_minutes INT NOT NULL,
                          base_price_amount DECIMAL(12,2) NULL,
                          currency_code CHAR(3) NOT NULL DEFAULT 'VND',
                          price_unit ENUM('PER_SESSION','PER_HOUR','PER_DAY','PER_VISIT') NOT NULL DEFAULT 'PER_SESSION',
                          requires_consultation BOOLEAN NOT NULL DEFAULT FALSE,
                          is_active BOOLEAN NOT NULL DEFAULT TRUE,
                          created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                          PRIMARY KEY (id),
                          UNIQUE KEY uk_services_service_code (service_code),
                          UNIQUE KEY uk_services_slug (slug),
                          KEY idx_services_category (category_id),
                          KEY idx_services_active (is_active),
                          CONSTRAINT fk_services_category FOREIGN KEY (category_id) REFERENCES service_categories(id)
) ENGINE=InnoDB;

CREATE TABLE provider_services (
                                   id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                                   provider_id BIGINT UNSIGNED NOT NULL,
                                   service_id BIGINT UNSIGNED NOT NULL,
                                   custom_name VARCHAR(150) NULL,
                                   short_description VARCHAR(255) NULL,
                                   description TEXT NULL,
                                   duration_minutes INT NOT NULL,
                                   price_amount DECIMAL(12,2) NOT NULL,
                                   currency_code CHAR(3) NOT NULL DEFAULT 'VND',
                                   price_unit ENUM('PER_SESSION','PER_HOUR','PER_DAY','PER_VISIT') NOT NULL DEFAULT 'PER_SESSION',
                                   is_featured BOOLEAN NOT NULL DEFAULT FALSE,
                                   is_active BOOLEAN NOT NULL DEFAULT TRUE,
                                   capacity_per_slot INT NOT NULL DEFAULT 1,
                                   booking_buffer_minutes INT NOT NULL DEFAULT 0,
                                   display_order INT NOT NULL DEFAULT 0,
                                   created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                   updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                   PRIMARY KEY (id),
                                   UNIQUE KEY uk_provider_services_unique (provider_id, service_id, custom_name),
                                   KEY idx_provider_services_provider (provider_id),
                                   KEY idx_provider_services_service (service_id),
                                   KEY idx_provider_services_featured (provider_id, is_featured),
                                   CONSTRAINT fk_provider_services_provider FOREIGN KEY (provider_id) REFERENCES provider_profiles(id),
                                   CONSTRAINT fk_provider_services_service FOREIGN KEY (service_id) REFERENCES services(id)
) ENGINE=InnoDB;

CREATE TABLE provider_availability_slots (
                                             id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                                             provider_id BIGINT UNSIGNED NOT NULL,
                                             provider_service_id BIGINT UNSIGNED NULL,
                                             slot_date DATE NOT NULL,
                                             start_time TIME NOT NULL,
                                             end_time TIME NOT NULL,
                                             slot_status ENUM('AVAILABLE','BLOCKED','BOOKED','UNAVAILABLE') NOT NULL DEFAULT 'AVAILABLE',
                                             capacity_total INT NOT NULL DEFAULT 1,
                                             capacity_booked INT NOT NULL DEFAULT 0,
                                             note VARCHAR(255) NULL,
                                             created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                             updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                             PRIMARY KEY (id),
                                             UNIQUE KEY uk_provider_availability_slot (provider_id, provider_service_id, slot_date, start_time),
                                             KEY idx_provider_availability_slot_lookup (provider_id, slot_date, slot_status),
                                             KEY idx_provider_availability_service (provider_service_id),
                                             CONSTRAINT fk_provider_availability_provider FOREIGN KEY (provider_id) REFERENCES provider_profiles(id),
                                             CONSTRAINT fk_provider_availability_provider_service FOREIGN KEY (provider_service_id) REFERENCES provider_services(id)
) ENGINE=InnoDB;

-- =========================
-- 5) MEMBERSHIP / PROMO
-- =========================

CREATE TABLE membership_plans (
                                  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                                  plan_code VARCHAR(50) NOT NULL,
                                  name VARCHAR(120) NOT NULL,
                                  slug VARCHAR(120) NOT NULL,
                                  description VARCHAR(255) NULL,
                                  billing_cycle ENUM('MONTHLY','QUARTERLY','YEARLY') NOT NULL DEFAULT 'MONTHLY',
                                  price_amount DECIMAL(12,2) NOT NULL,
                                  currency_code CHAR(3) NOT NULL DEFAULT 'VND',
                                  discount_percent DECIMAL(5,2) NOT NULL DEFAULT 0.00,
                                  monthly_voucher_amount DECIMAL(12,2) NOT NULL DEFAULT 0.00,
                                  priority_booking BOOLEAN NOT NULL DEFAULT FALSE,
                                  priority_support BOOLEAN NOT NULL DEFAULT FALSE,
                                  is_popular BOOLEAN NOT NULL DEFAULT FALSE,
                                  sort_order INT NOT NULL DEFAULT 0,
                                  is_active BOOLEAN NOT NULL DEFAULT TRUE,
                                  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                  PRIMARY KEY (id),
                                  UNIQUE KEY uk_membership_plans_code (plan_code),
                                  UNIQUE KEY uk_membership_plans_slug (slug)
) ENGINE=InnoDB;

CREATE TABLE membership_plan_features (
                                          id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                                          membership_plan_id BIGINT UNSIGNED NOT NULL,
                                          feature_text VARCHAR(255) NOT NULL,
                                          sort_order INT NOT NULL DEFAULT 0,
                                          created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                          updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                          PRIMARY KEY (id),
                                          KEY idx_membership_plan_features_plan (membership_plan_id),
                                          CONSTRAINT fk_membership_plan_features_plan FOREIGN KEY (membership_plan_id) REFERENCES membership_plans(id)
) ENGINE=InnoDB;

CREATE TABLE membership_subscriptions (
                                          id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                                          subscription_code VARCHAR(32) NOT NULL,
                                          user_id BIGINT UNSIGNED NOT NULL,
                                          membership_plan_id BIGINT UNSIGNED NOT NULL,
                                          status ENUM('PENDING_PAYMENT','ACTIVE','PAST_DUE','CANCELLED','EXPIRED') NOT NULL DEFAULT 'PENDING_PAYMENT',
                                          auto_renew BOOLEAN NOT NULL DEFAULT TRUE,
                                          started_at DATETIME NULL,
                                          expires_at DATETIME NULL,
                                          next_billing_at DATETIME NULL,
                                          cancelled_at DATETIME NULL,
                                          cancel_reason VARCHAR(255) NULL,
                                          created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                          updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                          PRIMARY KEY (id),
                                          UNIQUE KEY uk_membership_subscriptions_code (subscription_code),
                                          KEY idx_membership_subscriptions_user (user_id),
                                          KEY idx_membership_subscriptions_status (status),
                                          CONSTRAINT fk_membership_subscriptions_user FOREIGN KEY (user_id) REFERENCES users(id),
                                          CONSTRAINT fk_membership_subscriptions_plan FOREIGN KEY (membership_plan_id) REFERENCES membership_plans(id)
) ENGINE=InnoDB;

CREATE TABLE promo_codes (
                             id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                             code VARCHAR(50) NOT NULL,
                             target_type ENUM('BOOKING','MEMBERSHIP','BOTH') NOT NULL DEFAULT 'BOTH',
                             discount_type ENUM('FIXED_AMOUNT','PERCENTAGE') NOT NULL,
                             discount_value DECIMAL(12,2) NOT NULL,
                             max_discount_amount DECIMAL(12,2) NULL,
                             min_order_amount DECIMAL(12,2) NULL,
                             usage_limit_total INT NULL,
                             usage_limit_per_user INT NULL,
                             starts_at DATETIME NULL,
                             ends_at DATETIME NULL,
                             is_active BOOLEAN NOT NULL DEFAULT TRUE,
                             created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                             updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                             PRIMARY KEY (id),
                             UNIQUE KEY uk_promo_codes_code (code),
                             KEY idx_promo_codes_active_period (is_active, starts_at, ends_at)
) ENGINE=InnoDB;

CREATE TABLE promo_code_redemptions (
                                        id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                                        promo_code_id BIGINT UNSIGNED NOT NULL,
                                        user_id BIGINT UNSIGNED NOT NULL,
                                        booking_id BIGINT UNSIGNED NULL,
                                        membership_subscription_id BIGINT UNSIGNED NULL,
                                        discount_amount DECIMAL(12,2) NOT NULL DEFAULT 0.00,
                                        redeemed_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                        PRIMARY KEY (id),
                                        KEY idx_promo_code_redemptions_promo (promo_code_id),
                                        KEY idx_promo_code_redemptions_user (user_id),
                                        CONSTRAINT fk_promo_code_redemptions_promo FOREIGN KEY (promo_code_id) REFERENCES promo_codes(id),
                                        CONSTRAINT fk_promo_code_redemptions_user FOREIGN KEY (user_id) REFERENCES users(id),
                                        CONSTRAINT fk_promo_code_redemptions_membership FOREIGN KEY (membership_subscription_id) REFERENCES membership_subscriptions(id)
) ENGINE=InnoDB;

-- =========================
-- 6) BOOKINGS
-- =========================

CREATE TABLE bookings (
                          id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                          booking_code VARCHAR(32) NOT NULL,
                          customer_user_id BIGINT UNSIGNED NOT NULL,
                          provider_id BIGINT UNSIGNED NOT NULL,
                          pet_id BIGINT UNSIGNED NOT NULL,
                          provider_service_id BIGINT UNSIGNED NOT NULL,
                          availability_slot_id BIGINT UNSIGNED NULL,
                          appointment_date DATE NOT NULL,
                          start_time TIME NOT NULL,
                          end_time TIME NOT NULL,
                          timezone VARCHAR(50) NOT NULL DEFAULT 'Asia/Ho_Chi_Minh',
                          status ENUM('PENDING_PAYMENT','PENDING_CONFIRMATION','CONFIRMED','IN_PROGRESS','COMPLETED','CANCELLED','NO_SHOW') NOT NULL DEFAULT 'PENDING_PAYMENT',
                          cancellation_reason_code VARCHAR(50) NULL,
                          customer_note TEXT NULL,
                          internal_note TEXT NULL,
                          reschedule_count INT NOT NULL DEFAULT 0,

    -- Snapshots for immutable booking history / invoice display
                          provider_name_snapshot VARCHAR(180) NOT NULL,
                          provider_phone_snapshot VARCHAR(30) NULL,
                          provider_address_snapshot VARCHAR(255) NULL,
                          service_name_snapshot VARCHAR(150) NOT NULL,
                          service_description_snapshot VARCHAR(255) NULL,
                          service_duration_minutes_snapshot INT NOT NULL,
                          pet_name_snapshot VARCHAR(120) NOT NULL,
                          pet_breed_snapshot VARCHAR(120) NULL,

                          subtotal_amount DECIMAL(12,2) NOT NULL,
                          membership_discount_amount DECIMAL(12,2) NOT NULL DEFAULT 0.00,
                          promo_discount_amount DECIMAL(12,2) NOT NULL DEFAULT 0.00,
                          tax_amount DECIMAL(12,2) NOT NULL DEFAULT 0.00,
                          total_amount DECIMAL(12,2) NOT NULL,
                          currency_code CHAR(3) NOT NULL DEFAULT 'VND',

                          created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                          PRIMARY KEY (id),
                          UNIQUE KEY uk_bookings_booking_code (booking_code),
                          KEY idx_bookings_customer (customer_user_id, status),
                          KEY idx_bookings_provider (provider_id, status),
                          KEY idx_bookings_pet (pet_id),
                          KEY idx_bookings_schedule (appointment_date, start_time),
                          KEY idx_bookings_service (provider_service_id),
                          CONSTRAINT fk_bookings_customer FOREIGN KEY (customer_user_id) REFERENCES users(id),
                          CONSTRAINT fk_bookings_provider FOREIGN KEY (provider_id) REFERENCES provider_profiles(id),
                          CONSTRAINT fk_bookings_pet FOREIGN KEY (pet_id) REFERENCES pets(id),
                          CONSTRAINT fk_bookings_provider_service FOREIGN KEY (provider_service_id) REFERENCES provider_services(id),
                          CONSTRAINT fk_bookings_slot FOREIGN KEY (availability_slot_id) REFERENCES provider_availability_slots(id)
) ENGINE=InnoDB;

ALTER TABLE promo_code_redemptions
    ADD CONSTRAINT fk_promo_code_redemptions_booking FOREIGN KEY (booking_id) REFERENCES bookings(id);

CREATE TABLE booking_status_history (
                                        id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                                        booking_id BIGINT UNSIGNED NOT NULL,
                                        from_status ENUM('PENDING_PAYMENT','PENDING_CONFIRMATION','CONFIRMED','IN_PROGRESS','COMPLETED','CANCELLED','NO_SHOW') NULL,
                                        to_status ENUM('PENDING_PAYMENT','PENDING_CONFIRMATION','CONFIRMED','IN_PROGRESS','COMPLETED','CANCELLED','NO_SHOW') NOT NULL,
                                        changed_by_user_id BIGINT UNSIGNED NULL,
                                        note VARCHAR(255) NULL,
                                        created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                        PRIMARY KEY (id),
                                        KEY idx_booking_status_history_booking (booking_id, created_at),
                                        KEY idx_booking_status_history_user (changed_by_user_id),
                                        CONSTRAINT fk_booking_status_history_booking FOREIGN KEY (booking_id) REFERENCES bookings(id),
                                        CONSTRAINT fk_booking_status_history_user FOREIGN KEY (changed_by_user_id) REFERENCES users(id)
) ENGINE=InnoDB;

CREATE TABLE booking_cancellations (
                                       id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                                       booking_id BIGINT UNSIGNED NOT NULL,
                                       cancelled_by_user_id BIGINT UNSIGNED NOT NULL,
                                       reason_code VARCHAR(50) NOT NULL,
                                       reason_text VARCHAR(255) NULL,
                                       refund_status ENUM('NOT_REQUIRED','PENDING','PARTIAL','FULL','REJECTED') NOT NULL DEFAULT 'NOT_REQUIRED',
                                       refund_amount DECIMAL(12,2) NOT NULL DEFAULT 0.00,
                                       cancelled_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                       PRIMARY KEY (id),
                                       UNIQUE KEY uk_booking_cancellations_booking (booking_id),
                                       KEY idx_booking_cancellations_user (cancelled_by_user_id),
                                       CONSTRAINT fk_booking_cancellations_booking FOREIGN KEY (booking_id) REFERENCES bookings(id),
                                       CONSTRAINT fk_booking_cancellations_user FOREIGN KEY (cancelled_by_user_id) REFERENCES users(id)
) ENGINE=InnoDB;

CREATE TABLE booking_reschedules (
                                     id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                                     booking_id BIGINT UNSIGNED NOT NULL,
                                     requested_by_user_id BIGINT UNSIGNED NOT NULL,
                                     old_appointment_date DATE NOT NULL,
                                     old_start_time TIME NOT NULL,
                                     old_end_time TIME NOT NULL,
                                     new_appointment_date DATE NOT NULL,
                                     new_start_time TIME NOT NULL,
                                     new_end_time TIME NOT NULL,
                                     fee_amount DECIMAL(12,2) NOT NULL DEFAULT 0.00,
                                     status ENUM('REQUESTED','APPROVED','REJECTED','APPLIED') NOT NULL DEFAULT 'APPLIED',
                                     note VARCHAR(255) NULL,
                                     created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                     updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                     PRIMARY KEY (id),
                                     KEY idx_booking_reschedules_booking (booking_id),
                                     KEY idx_booking_reschedules_requester (requested_by_user_id),
                                     CONSTRAINT fk_booking_reschedules_booking FOREIGN KEY (booking_id) REFERENCES bookings(id),
                                     CONSTRAINT fk_booking_reschedules_requester FOREIGN KEY (requested_by_user_id) REFERENCES users(id)
) ENGINE=InnoDB;

-- =========================
-- 7) REVIEW / FAVORITE
-- =========================

CREATE TABLE favorites (
                           id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                           user_id BIGINT UNSIGNED NOT NULL,
                           provider_id BIGINT UNSIGNED NOT NULL,
                           created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           PRIMARY KEY (id),
                           UNIQUE KEY uk_favorites_user_provider (user_id, provider_id),
                           KEY idx_favorites_provider (provider_id),
                           CONSTRAINT fk_favorites_user FOREIGN KEY (user_id) REFERENCES users(id),
                           CONSTRAINT fk_favorites_provider FOREIGN KEY (provider_id) REFERENCES provider_profiles(id)
) ENGINE=InnoDB;

CREATE TABLE reviews (
                         id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                         booking_id BIGINT UNSIGNED NOT NULL,
                         customer_user_id BIGINT UNSIGNED NOT NULL,
                         provider_id BIGINT UNSIGNED NOT NULL,
                         rating TINYINT NOT NULL,
                         comment TEXT NULL,
                         status ENUM('VISIBLE','HIDDEN','REPORTED') NOT NULL DEFAULT 'VISIBLE',
                         created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                         deleted_at DATETIME NULL,
                         PRIMARY KEY (id),
                         UNIQUE KEY uk_reviews_booking (booking_id),
                         KEY idx_reviews_provider (provider_id, status),
                         KEY idx_reviews_customer (customer_user_id),
                         CONSTRAINT fk_reviews_booking FOREIGN KEY (booking_id) REFERENCES bookings(id),
                         CONSTRAINT fk_reviews_customer FOREIGN KEY (customer_user_id) REFERENCES users(id),
                         CONSTRAINT fk_reviews_provider FOREIGN KEY (provider_id) REFERENCES provider_profiles(id)
) ENGINE=InnoDB;

CREATE TABLE review_photos (
                               id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                               review_id BIGINT UNSIGNED NOT NULL,
                               photo_url VARCHAR(500) NOT NULL,
                               sort_order INT NOT NULL DEFAULT 0,
                               created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                               PRIMARY KEY (id),
                               KEY idx_review_photos_review (review_id),
                               CONSTRAINT fk_review_photos_review FOREIGN KEY (review_id) REFERENCES reviews(id)
) ENGINE=InnoDB;

-- =========================
-- 8) BILLING / PAYMENT
-- =========================

CREATE TABLE invoices (
                          id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                          invoice_number VARCHAR(32) NOT NULL,
                          user_id BIGINT UNSIGNED NOT NULL,
                          booking_id BIGINT UNSIGNED NULL,
                          membership_subscription_id BIGINT UNSIGNED NULL,
                          invoice_type ENUM('BOOKING','MEMBERSHIP') NOT NULL,
                          status ENUM('DRAFT','ISSUED','PAID','VOID') NOT NULL DEFAULT 'ISSUED',
                          billing_name VARCHAR(150) NOT NULL,
                          billing_email VARCHAR(190) NULL,
                          billing_phone VARCHAR(30) NULL,
                          billing_address VARCHAR(255) NULL,
                          subtotal_amount DECIMAL(12,2) NOT NULL,
                          discount_amount DECIMAL(12,2) NOT NULL DEFAULT 0.00,
                          tax_amount DECIMAL(12,2) NOT NULL DEFAULT 0.00,
                          total_amount DECIMAL(12,2) NOT NULL,
                          currency_code CHAR(3) NOT NULL DEFAULT 'VND',
                          issued_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          due_at DATETIME NULL,
                          paid_at DATETIME NULL,
                          note VARCHAR(255) NULL,
                          created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                          PRIMARY KEY (id),
                          UNIQUE KEY uk_invoices_invoice_number (invoice_number),
                          UNIQUE KEY uk_invoices_booking (booking_id),
                          KEY idx_invoices_user (user_id, status),
                          KEY idx_invoices_membership (membership_subscription_id),
                          CONSTRAINT fk_invoices_user FOREIGN KEY (user_id) REFERENCES users(id),
                          CONSTRAINT fk_invoices_booking FOREIGN KEY (booking_id) REFERENCES bookings(id),
                          CONSTRAINT fk_invoices_membership FOREIGN KEY (membership_subscription_id) REFERENCES membership_subscriptions(id)
) ENGINE=InnoDB;

CREATE TABLE invoice_items (
                               id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                               invoice_id BIGINT UNSIGNED NOT NULL,
                               item_type ENUM('BOOKING_SERVICE','MEMBERSHIP_PLAN','DISCOUNT','FEE','TAX') NOT NULL,
                               item_name VARCHAR(180) NOT NULL,
                               description VARCHAR(255) NULL,
                               quantity INT NOT NULL DEFAULT 1,
                               unit_price DECIMAL(12,2) NOT NULL,
                               line_total DECIMAL(12,2) NOT NULL,
                               sort_order INT NOT NULL DEFAULT 0,
                               created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                               updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                               PRIMARY KEY (id),
                               KEY idx_invoice_items_invoice (invoice_id),
                               CONSTRAINT fk_invoice_items_invoice FOREIGN KEY (invoice_id) REFERENCES invoices(id)
) ENGINE=InnoDB;

CREATE TABLE payments (
                          id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                          payment_code VARCHAR(32) NOT NULL,
                          invoice_id BIGINT UNSIGNED NOT NULL,
                          payer_user_id BIGINT UNSIGNED NOT NULL,
                          amount DECIMAL(12,2) NOT NULL,
                          currency_code CHAR(3) NOT NULL DEFAULT 'VND',
                          payment_method ENUM('COD','CASH','CARD','BANK_TRANSFER','MOMO','VNPAY','ZALOPAY','WALLET') NOT NULL,
                          gateway_name VARCHAR(50) NULL,
                          gateway_transaction_id VARCHAR(120) NULL,
                          status ENUM('PENDING','AUTHORIZED','SUCCEEDED','FAILED','REFUNDED','PARTIALLY_REFUNDED','CANCELLED') NOT NULL DEFAULT 'PENDING',
                          paid_at DATETIME NULL,
                          failure_reason VARCHAR(255) NULL,
                          metadata_json JSON NULL,
                          created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                          PRIMARY KEY (id),
                          UNIQUE KEY uk_payments_payment_code (payment_code),
                          UNIQUE KEY uk_payments_gateway_txn (gateway_transaction_id),
                          KEY idx_payments_invoice (invoice_id),
                          KEY idx_payments_user (payer_user_id),
                          KEY idx_payments_status (status),
                          CONSTRAINT fk_payments_invoice FOREIGN KEY (invoice_id) REFERENCES invoices(id),
                          CONSTRAINT fk_payments_user FOREIGN KEY (payer_user_id) REFERENCES users(id)
) ENGINE=InnoDB;

-- =========================
-- 9) NOTIFICATION
-- =========================

CREATE TABLE notifications (
                               id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                               user_id BIGINT UNSIGNED NOT NULL,
                               notification_type ENUM(
        'BOOKING_CREATED',
        'BOOKING_CONFIRMED',
        'BOOKING_REMINDER',
        'BOOKING_CANCELLED',
        'BOOKING_RESCHEDULED',
        'PAYMENT_SUCCESS',
        'PAYMENT_FAILED',
        'REVIEW_RECEIVED',
        'MEMBERSHIP_ACTIVATED',
        'MEMBERSHIP_EXPIRING',
        'SYSTEM'
    ) NOT NULL,
                               title VARCHAR(180) NOT NULL,
                               message VARCHAR(500) NOT NULL,
                               reference_type ENUM('BOOKING','INVOICE','PAYMENT','MEMBERSHIP','PROVIDER','SYSTEM') NULL,
                               reference_id BIGINT UNSIGNED NULL,
                               is_read BOOLEAN NOT NULL DEFAULT FALSE,
                               sent_at DATETIME NULL,
                               read_at DATETIME NULL,
                               created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                               updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                               PRIMARY KEY (id),
                               KEY idx_notifications_user_read (user_id, is_read, created_at),
                               CONSTRAINT fk_notifications_user FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB;

-- =========================
-- 10) OPTIONAL SETTINGS TABLES
-- =========================

CREATE TABLE user_notification_settings (
                                            user_id BIGINT UNSIGNED NOT NULL,
                                            email_booking_updates BOOLEAN NOT NULL DEFAULT TRUE,
                                            email_promotions BOOLEAN NOT NULL DEFAULT TRUE,
                                            push_booking_updates BOOLEAN NOT NULL DEFAULT TRUE,
                                            push_reminders BOOLEAN NOT NULL DEFAULT TRUE,
                                            sms_booking_updates BOOLEAN NOT NULL DEFAULT FALSE,
                                            created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                            updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                            PRIMARY KEY (user_id),
                                            CONSTRAINT fk_user_notification_settings_user FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB;

-- =========================
-- 11) SEED DATA (minimal)
-- =========================

INSERT INTO roles (code, name, description) VALUES
                                                ('CUSTOMER', 'Customer / Pet Owner', 'Người dùng đặt dịch vụ cho thú cưng'),
                                                ('PROVIDER', 'Caregiver / Provider', 'Người cung cấp dịch vụ chăm sóc thú cưng'),
                                                ('ADMIN', 'Administrator', 'Quản trị hệ thống');

INSERT INTO service_categories (name, slug, icon_key, description, sort_order) VALUES
                                                                                   ('Pet Spa', 'spa', 'paw-print', 'Dịch vụ spa cho thú cưng', 1),
                                                                                   ('Grooming', 'grooming', 'scissors', 'Tắm, cắt tỉa, vệ sinh', 2),
                                                                                   ('Veterinary', 'clinic', 'stethoscope', 'Khám, tư vấn, tiêm phòng', 3),
                                                                                   ('Pet Boarding', 'boarding', 'hotel', 'Lưu trú và chăm sóc theo ngày', 4),
                                                                                   ('Pet Training', 'training', 'award', 'Huấn luyện cơ bản và nâng cao', 5),
                                                                                   ('Pet Walking', 'walking', 'navigation', 'Dắt đi dạo và vận động', 6);

INSERT INTO services (service_code, category_id, name, slug, short_description, description, default_duration_minutes, base_price_amount, currency_code, price_unit)
SELECT 'SVC-SPA-BATH', id, 'Gói Tắm Thư Giãn', 'relax-bath', 'Tắm bằng nước ấm, sấy và chải lông', 'Gói spa cơ bản cho thú cưng', 45, 200000, 'VND', 'PER_SESSION' FROM service_categories WHERE slug='spa';

INSERT INTO services (service_code, category_id, name, slug, short_description, description, default_duration_minutes, base_price_amount, currency_code, price_unit)
SELECT 'SVC-GROOM-STYLE', id, 'Cắt Tỉa Tạo Kiểu', 'groom-style', 'Cắt tỉa và tạo kiểu lông', 'Dịch vụ grooming nâng cao', 90, 350000, 'VND', 'PER_SESSION' FROM service_categories WHERE slug='grooming';

INSERT INTO services (service_code, category_id, name, slug, short_description, description, default_duration_minutes, base_price_amount, currency_code, price_unit)
SELECT 'SVC-VET-CHECKUP', id, 'Khám Tổng Quát', 'general-checkup', 'Khám cơ bản cho thú cưng', 'Khám sức khỏe tổng quát', 30, 150000, 'VND', 'PER_VISIT' FROM service_categories WHERE slug='clinic';

INSERT INTO membership_plans (plan_code, name, slug, description, billing_cycle, price_amount, currency_code, discount_percent, monthly_voucher_amount, priority_booking, priority_support, is_popular, sort_order)
VALUES
    ('BASIC', 'Basic Membership', 'basic', 'Gói cơ bản cho nhu cầu nhẹ', 'MONTHLY', 49000, 'VND', 5.00, 50000, FALSE, FALSE, FALSE, 1),
    ('PRO', 'Pro Membership', 'pro', 'Gói phù hợp người dùng thường xuyên', 'MONTHLY', 99000, 'VND', 10.00, 200000, TRUE, TRUE, TRUE, 2),
    ('PREMIUM', 'Premium Membership', 'premium', 'Gói cao cấp với nhiều ưu đãi', 'MONTHLY', 199000, 'VND', 15.00, 500000, TRUE, TRUE, FALSE, 3);

INSERT INTO membership_plan_features (membership_plan_id, feature_text, sort_order)
SELECT id, 'Giảm 10% cho mọi dịch vụ thú cưng', 1 FROM membership_plans WHERE plan_code='PRO';
INSERT INTO membership_plan_features (membership_plan_id, feature_text, sort_order)
SELECT id, 'Bộ voucher 200k mỗi tháng', 2 FROM membership_plans WHERE plan_code='PRO';
INSERT INTO membership_plan_features (membership_plan_id, feature_text, sort_order)
SELECT id, 'Ưu tiên slot đặt lịch cao điểm', 3 FROM membership_plans WHERE plan_code='PRO';
INSERT INTO membership_plan_features (membership_plan_id, feature_text, sort_order)
SELECT id, 'Nhắc lịch grooming và tiêm phòng', 4 FROM membership_plans WHERE plan_code='PRO';
INSERT INTO membership_plan_features (membership_plan_id, feature_text, sort_order)
SELECT id, 'Hỗ trợ ưu tiên 24/7', 5 FROM membership_plans WHERE plan_code='PRO';

INSERT INTO promo_codes (code, target_type, discount_type, discount_value, max_discount_amount, min_order_amount, usage_limit_total, usage_limit_per_user, starts_at, ends_at, is_active)
VALUES ('PETGO20', 'BOTH', 'FIXED_AMOUNT', 20000, NULL, 0, 10000, 1, NOW(), DATE_ADD(NOW(), INTERVAL 365 DAY), TRUE);
