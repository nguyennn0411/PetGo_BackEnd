-- MySQL dump 10.13  Distrib 8.0.45, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: petgo_db
-- ------------------------------------------------------
-- Server version	8.0.45

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `booking_cancellations`
--

DROP TABLE IF EXISTS `booking_cancellations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `booking_cancellations` (
                                         `id` bigint unsigned NOT NULL AUTO_INCREMENT,
                                         `booking_id` bigint unsigned NOT NULL,
                                         `cancelled_by_user_id` bigint unsigned NOT NULL,
                                         `reason_code` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
                                         `reason_text` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                         `refund_status` enum('NOT_REQUIRED','PENDING','PARTIAL','FULL','REJECTED') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'NOT_REQUIRED',
                                         `refund_amount` decimal(12,2) NOT NULL DEFAULT '0.00',
                                         `cancelled_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                         PRIMARY KEY (`id`),
                                         UNIQUE KEY `uk_booking_cancellations_booking` (`booking_id`),
                                         KEY `idx_booking_cancellations_user` (`cancelled_by_user_id`),
                                         CONSTRAINT `fk_booking_cancellations_booking` FOREIGN KEY (`booking_id`) REFERENCES `bookings` (`id`),
                                         CONSTRAINT `fk_booking_cancellations_user` FOREIGN KEY (`cancelled_by_user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `booking_cancellations`
--

LOCK TABLES `booking_cancellations` WRITE;
/*!40000 ALTER TABLE `booking_cancellations` DISABLE KEYS */;
INSERT INTO `booking_cancellations` VALUES (1,1,3,'SICK_OR_EMERGENCY',NULL,'NOT_REQUIRED',0.00,'2026-05-20 01:39:54'),(2,2,3,'CHANGE_OF_PLANS',NULL,'NOT_REQUIRED',0.00,'2026-05-20 01:44:02');
/*!40000 ALTER TABLE `booking_cancellations` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `booking_reschedules`
--

DROP TABLE IF EXISTS `booking_reschedules`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `booking_reschedules` (
                                       `id` bigint unsigned NOT NULL AUTO_INCREMENT,
                                       `booking_id` bigint unsigned NOT NULL,
                                       `requested_by_user_id` bigint unsigned NOT NULL,
                                       `old_appointment_date` date NOT NULL,
                                       `old_start_time` time NOT NULL,
                                       `old_end_time` time NOT NULL,
                                       `new_appointment_date` date NOT NULL,
                                       `new_start_time` time NOT NULL,
                                       `new_end_time` time NOT NULL,
                                       `fee_amount` decimal(12,2) NOT NULL DEFAULT '0.00',
                                       `status` enum('REQUESTED','APPROVED','REJECTED','APPLIED') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'APPLIED',
                                       `note` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                       `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                       `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                       PRIMARY KEY (`id`),
                                       KEY `idx_booking_reschedules_booking` (`booking_id`),
                                       KEY `idx_booking_reschedules_requester` (`requested_by_user_id`),
                                       CONSTRAINT `fk_booking_reschedules_booking` FOREIGN KEY (`booking_id`) REFERENCES `bookings` (`id`),
                                       CONSTRAINT `fk_booking_reschedules_requester` FOREIGN KEY (`requested_by_user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `booking_reschedules`
--

LOCK TABLES `booking_reschedules` WRITE;
/*!40000 ALTER TABLE `booking_reschedules` DISABLE KEYS */;
/*!40000 ALTER TABLE `booking_reschedules` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `booking_status_history`
--

DROP TABLE IF EXISTS `booking_status_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `booking_status_history` (
                                          `id` bigint unsigned NOT NULL AUTO_INCREMENT,
                                          `booking_id` bigint unsigned NOT NULL,
                                          `from_status` enum('PENDING_PAYMENT','PENDING_CONFIRMATION','CONFIRMED','IN_PROGRESS','COMPLETED','CANCELLED','NO_SHOW') COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                          `to_status` enum('PENDING_PAYMENT','PENDING_CONFIRMATION','CONFIRMED','IN_PROGRESS','COMPLETED','CANCELLED','NO_SHOW') COLLATE utf8mb4_unicode_ci NOT NULL,
                                          `changed_by_user_id` bigint unsigned DEFAULT NULL,
                                          `note` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                          `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                          `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                          PRIMARY KEY (`id`),
                                          KEY `idx_booking_status_history_booking` (`booking_id`,`created_at`),
                                          KEY `idx_booking_status_history_user` (`changed_by_user_id`),
                                          CONSTRAINT `fk_booking_status_history_booking` FOREIGN KEY (`booking_id`) REFERENCES `bookings` (`id`),
                                          CONSTRAINT `fk_booking_status_history_user` FOREIGN KEY (`changed_by_user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `booking_status_history`
--

LOCK TABLES `booking_status_history` WRITE;
/*!40000 ALTER TABLE `booking_status_history` DISABLE KEYS */;
INSERT INTO `booking_status_history` VALUES (1,1,NULL,'PENDING_PAYMENT',3,'Tạo booking mới từ BookingPage','2026-05-20 01:38:52','2026-05-20 01:38:52'),(2,1,'PENDING_PAYMENT','PENDING_CONFIRMATION',3,'Thanh toán checkout bằng COD','2026-05-20 01:38:54','2026-05-20 01:38:54'),(3,1,'PENDING_CONFIRMATION','CANCELLED',3,'Hủy booking. Lý do: SICK_OR_EMERGENCY','2026-05-20 01:39:54','2026-05-20 01:39:54'),(4,2,NULL,'PENDING_PAYMENT',3,'Tạo booking mới từ BookingPage','2026-05-20 01:43:15','2026-05-20 01:43:15'),(5,2,'PENDING_PAYMENT','PENDING_CONFIRMATION',3,'Thanh toán checkout bằng COD','2026-05-20 01:43:21','2026-05-20 01:43:21'),(6,2,'PENDING_CONFIRMATION','CANCELLED',3,'Hủy booking. Lý do: CHANGE_OF_PLANS','2026-05-20 01:44:02','2026-05-20 01:44:02');
/*!40000 ALTER TABLE `booking_status_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `bookings`
--

DROP TABLE IF EXISTS `bookings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `bookings` (
                            `id` bigint unsigned NOT NULL AUTO_INCREMENT,
                            `booking_code` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL,
                            `customer_user_id` bigint unsigned NOT NULL,
                            `provider_id` bigint unsigned NOT NULL,
                            `pet_id` bigint unsigned NOT NULL,
                            `provider_service_id` bigint unsigned NOT NULL,
                            `availability_slot_id` bigint unsigned DEFAULT NULL,
                            `appointment_date` date NOT NULL,
                            `start_time` time NOT NULL,
                            `end_time` time NOT NULL,
                            `timezone` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'Asia/Ho_Chi_Minh',
                            `status` enum('PENDING_PAYMENT','PENDING_CONFIRMATION','CONFIRMED','IN_PROGRESS','COMPLETED','CANCELLED','NO_SHOW') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'PENDING_PAYMENT',
                            `cancellation_reason_code` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                            `customer_note` text COLLATE utf8mb4_unicode_ci,
                            `internal_note` text COLLATE utf8mb4_unicode_ci,
                            `reschedule_count` int NOT NULL DEFAULT '0',
                            `provider_name_snapshot` varchar(180) COLLATE utf8mb4_unicode_ci NOT NULL,
                            `provider_phone_snapshot` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                            `provider_address_snapshot` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                            `service_name_snapshot` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
                            `service_description_snapshot` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                            `service_duration_minutes_snapshot` int NOT NULL,
                            `pet_name_snapshot` varchar(120) COLLATE utf8mb4_unicode_ci NOT NULL,
                            `pet_breed_snapshot` varchar(120) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                            `subtotal_amount` decimal(12,2) NOT NULL,
                            `membership_discount_amount` decimal(12,2) NOT NULL DEFAULT '0.00',
                            `promo_discount_amount` decimal(12,2) NOT NULL DEFAULT '0.00',
                            `tax_amount` decimal(12,2) NOT NULL DEFAULT '0.00',
                            `total_amount` decimal(12,2) NOT NULL,
                            `currency_code` char(3) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'VND',
                            `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                            PRIMARY KEY (`id`),
                            UNIQUE KEY `uk_bookings_booking_code` (`booking_code`),
                            KEY `idx_bookings_customer` (`customer_user_id`,`status`),
                            KEY `idx_bookings_provider` (`provider_id`,`status`),
                            KEY `idx_bookings_pet` (`pet_id`),
                            KEY `idx_bookings_schedule` (`appointment_date`,`start_time`),
                            KEY `idx_bookings_service` (`provider_service_id`),
                            KEY `fk_bookings_slot` (`availability_slot_id`),
                            CONSTRAINT `fk_bookings_customer` FOREIGN KEY (`customer_user_id`) REFERENCES `users` (`id`),
                            CONSTRAINT `fk_bookings_pet` FOREIGN KEY (`pet_id`) REFERENCES `pets` (`id`),
                            CONSTRAINT `fk_bookings_provider` FOREIGN KEY (`provider_id`) REFERENCES `provider_profiles` (`id`),
                            CONSTRAINT `fk_bookings_provider_service` FOREIGN KEY (`provider_service_id`) REFERENCES `provider_services` (`id`),
                            CONSTRAINT `fk_bookings_slot` FOREIGN KEY (`availability_slot_id`) REFERENCES `provider_availability_slots` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bookings`
--

LOCK TABLES `bookings` WRITE;
/*!40000 ALTER TABLE `bookings` DISABLE KEYS */;
INSERT INTO `bookings` VALUES (1,'BKP0010003F34A7D',3,1,3,1,1,'2026-05-20','09:00:00','10:00:00','Asia/Ho_Chi_Minh','CANCELLED','SICK_OR_EMERGENCY',NULL,NULL,0,'Happy Pets Spa',NULL,'','Gói Tắm Thư Giãn','Tắm bằng nước ấm, sấy và chải lông',60,'sâdsas','adsđ',200000.00,0.00,0.00,0.00,200000.00,'VND','2026-05-20 01:38:52','2026-05-20 01:39:54'),(2,'BKP001000394F401',3,1,4,1,1,'2026-05-20','09:00:00','10:00:00','Asia/Ho_Chi_Minh','CANCELLED','CHANGE_OF_PLANS','jbhj',NULL,0,'Happy Pets Spa',NULL,'','Gói Tắm Thư Giãn','Tắm bằng nước ấm, sấy và chải lông',60,'ygyuguy','iuguyg',200000.00,0.00,0.00,0.00,200000.00,'VND','2026-05-20 01:43:15','2026-05-20 01:44:02');
/*!40000 ALTER TABLE `bookings` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cart_items`
--

DROP TABLE IF EXISTS `cart_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cart_items` (
                              `id` bigint unsigned NOT NULL AUTO_INCREMENT,
                              `user_id` bigint unsigned NOT NULL,
                              `product_id` bigint unsigned NOT NULL,
                              `quantity` int NOT NULL DEFAULT '1',
                              `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                              `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                              PRIMARY KEY (`id`),
                              UNIQUE KEY `uk_cart_user_product` (`user_id`,`product_id`),
                              KEY `idx_cart_items_user` (`user_id`),
                              KEY `idx_cart_items_product` (`product_id`),
                              CONSTRAINT `fk_cart_items_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`),
                              CONSTRAINT `fk_cart_items_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cart_items`
--

LOCK TABLES `cart_items` WRITE;
/*!40000 ALTER TABLE `cart_items` DISABLE KEYS */;
INSERT INTO `cart_items` VALUES (4,1,4,1,'2026-05-25 11:19:08','2026-05-25 11:19:08'),(5,4,4,7,'2026-05-25 11:20:01','2026-05-25 11:21:53'),(6,3,4,3,'2026-05-25 18:54:06','2026-05-25 19:28:44'),(7,3,1,1,'2026-05-25 19:44:34','2026-05-25 19:44:34');
/*!40000 ALTER TABLE `cart_items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `favorites`
--

DROP TABLE IF EXISTS `favorites`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `favorites` (
                             `id` bigint unsigned NOT NULL AUTO_INCREMENT,
                             `user_id` bigint unsigned NOT NULL,
                             `provider_id` bigint unsigned NOT NULL,
                             `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                             PRIMARY KEY (`id`),
                             UNIQUE KEY `uk_favorites_user_provider` (`user_id`,`provider_id`),
                             KEY `idx_favorites_provider` (`provider_id`),
                             CONSTRAINT `fk_favorites_provider` FOREIGN KEY (`provider_id`) REFERENCES `provider_profiles` (`id`),
                             CONSTRAINT `fk_favorites_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4405 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `favorites`
--

LOCK TABLES `favorites` WRITE;
/*!40000 ALTER TABLE `favorites` DISABLE KEYS */;
INSERT INTO `favorites` VALUES (4401,1001,2001,'2026-04-17 13:47:58'),(4402,1001,2002,'2026-04-17 13:47:58'),(4403,1001,2004,'2026-04-17 13:47:58'),(4404,1002,2002,'2026-04-17 13:47:58');
/*!40000 ALTER TABLE `favorites` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `flyway_schema_history`
--

DROP TABLE IF EXISTS `flyway_schema_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `flyway_schema_history` (
                                         `installed_rank` int NOT NULL,
                                         `version` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                         `description` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
                                         `type` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
                                         `script` varchar(1000) COLLATE utf8mb4_unicode_ci NOT NULL,
                                         `checksum` int DEFAULT NULL,
                                         `installed_by` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
                                         `installed_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                         `execution_time` int NOT NULL,
                                         `success` tinyint(1) NOT NULL,
                                         PRIMARY KEY (`installed_rank`),
                                         KEY `flyway_schema_history_s_idx` (`success`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `flyway_schema_history`
--

LOCK TABLES `flyway_schema_history` WRITE;
/*!40000 ALTER TABLE `flyway_schema_history` DISABLE KEYS */;
INSERT INTO `flyway_schema_history` VALUES (1,'1','<< Flyway Baseline >>','BASELINE','<< Flyway Baseline >>',NULL,'root','2026-05-24 15:50:38',0,1),(2,'2','add petgo shop module','SQL','V2__add_petgo_shop_module.sql',-216326823,'root','2026-05-24 15:50:38',613,1);
/*!40000 ALTER TABLE `flyway_schema_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `invoice_items`
--

DROP TABLE IF EXISTS `invoice_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `invoice_items` (
                                 `id` bigint unsigned NOT NULL AUTO_INCREMENT,
                                 `invoice_id` bigint unsigned NOT NULL,
                                 `item_type` enum('BOOKING_SERVICE','MEMBERSHIP_PLAN','SHOP_PRODUCT','SHIPPING_FEE','DISCOUNT','FEE','TAX') COLLATE utf8mb4_unicode_ci NOT NULL,
                                 `item_name` varchar(180) COLLATE utf8mb4_unicode_ci NOT NULL,
                                 `description` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                 `quantity` int NOT NULL DEFAULT '1',
                                 `unit_price` decimal(12,2) NOT NULL,
                                 `line_total` decimal(12,2) NOT NULL,
                                 `sort_order` int NOT NULL DEFAULT '0',
                                 `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                 `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                 PRIMARY KEY (`id`),
                                 KEY `idx_invoice_items_invoice` (`invoice_id`),
                                 CONSTRAINT `fk_invoice_items_invoice` FOREIGN KEY (`invoice_id`) REFERENCES `invoices` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5228 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `invoice_items`
--

LOCK TABLES `invoice_items` WRITE;
/*!40000 ALTER TABLE `invoice_items` DISABLE KEYS */;
INSERT INTO `invoice_items` VALUES (5201,5001,'BOOKING_SERVICE','Grooming Chuẩn Spa','Dịch vụ grooming cho Mochi',1,350000.00,350000.00,1,'2026-04-17 13:47:58','2026-04-17 13:47:58'),(5202,5001,'DISCOUNT','Membership Discount','Ưu đãi hội viên PRO',1,-35000.00,-35000.00,2,'2026-04-17 13:47:58','2026-04-17 13:47:58'),(5203,5002,'BOOKING_SERVICE','Khám Tổng Quát Tại Phòng Khám','Dịch vụ khám cho Bơ',1,150000.00,150000.00,1,'2026-04-17 13:47:58','2026-04-17 13:47:58'),(5204,5002,'DISCOUNT','Membership Discount','Ưu đãi hội viên PRO',1,-15000.00,-15000.00,2,'2026-04-17 13:47:58','2026-04-17 13:47:58'),(5205,5002,'DISCOUNT','Promo WELCOME10','Giảm giá mã WELCOME10',1,-15000.00,-15000.00,3,'2026-04-17 13:47:58','2026-04-17 13:47:58'),(5206,5003,'BOOKING_SERVICE','Khám Tổng Quát Tại Phòng Khám','Dịch vụ khám cho Cà Phê',1,150000.00,150000.00,1,'2026-04-17 13:47:58','2026-04-17 13:47:58'),(5207,5004,'BOOKING_SERVICE','Walk 30 Phút Quanh Trung Tâm','Dịch vụ walk cho Mochi',1,90000.00,90000.00,1,'2026-04-17 13:47:58','2026-04-17 13:47:58'),(5208,5004,'DISCOUNT','Membership Discount','Ưu đãi hội viên PRO',1,-9000.00,-9000.00,2,'2026-04-17 13:47:58','2026-04-17 13:47:58'),(5209,5005,'BOOKING_SERVICE','Khám Tổng Quát Tại Phòng Khám','Dịch vụ khám cho Cà Phê',1,150000.00,150000.00,1,'2026-04-17 13:47:58','2026-04-17 13:47:58'),(5210,5006,'MEMBERSHIP_PLAN','Gói hội viên PRO','Thanh toán gói PRO tháng 04/2026',1,99000.00,99000.00,1,'2026-04-17 13:47:58','2026-04-17 13:47:58'),(5211,5006,'DISCOUNT','Promo MEMBER15','Giảm giá mã MEMBER15',1,-14850.00,-14850.00,2,'2026-04-17 13:47:58','2026-04-17 13:47:58'),(5212,5007,'BOOKING_SERVICE','Grooming Chuẩn Spa','20/04/2026 • 09:00 - 10:30',1,350000.00,350000.00,1,'2026-04-17 13:52:17','2026-04-17 13:52:17'),(5213,5008,'MEMBERSHIP_PLAN','Pro Membership','Gói phù hợp người dùng thường xuyên',1,99000.00,99000.00,1,'2026-04-17 13:53:54','2026-04-17 13:53:54'),(5214,5009,'MEMBERSHIP_PLAN','Basic Membership','Gói cơ bản cho nhu cầu nhẹ',1,49000.00,49000.00,1,'2026-04-17 13:54:07','2026-04-17 13:54:07'),(5215,5010,'MEMBERSHIP_PLAN','Premium Membership','Gói cao cấp với nhiều ưu đãi',1,199000.00,199000.00,1,'2026-04-17 13:54:22','2026-04-17 13:54:22'),(5216,5011,'BOOKING_SERVICE','Grooming Chuẩn Spa','22/04/2026 • 10:00 - 11:30',1,350000.00,350000.00,1,'2026-04-17 14:21:31','2026-04-17 14:21:31'),(5217,5012,'BOOKING_SERVICE','Grooming Chuẩn Spa','22/04/2026 • 10:00 - 11:30',1,350000.00,350000.00,1,'2026-04-17 19:39:58','2026-04-17 19:39:58'),(5218,5013,'MEMBERSHIP_PLAN','Pro Membership','Gói phù hợp người dùng thường xuyên',1,99000.00,99000.00,1,'2026-05-11 15:48:49','2026-05-11 15:48:49'),(5219,5014,'MEMBERSHIP_PLAN','Pro Membership','Gói phù hợp người dùng thường xuyên',1,99000.00,99000.00,1,'2026-05-11 15:49:00','2026-05-11 15:49:00'),(5220,5015,'MEMBERSHIP_PLAN','Pro Membership','Gói phù hợp người dùng thường xuyên',1,99000.00,99000.00,1,'2026-05-20 00:40:18','2026-05-20 00:40:18'),(5221,5016,'BOOKING_SERVICE','Gói Tắm Thư Giãn','20/05/2026 • 09:00 - 10:00',1,200000.00,200000.00,1,'2026-05-20 01:38:54','2026-05-20 01:38:54'),(5222,5017,'BOOKING_SERVICE','Gói Tắm Thư Giãn','20/05/2026 • 09:00 - 10:00',1,200000.00,200000.00,1,'2026-05-20 01:43:21','2026-05-20 01:43:21'),(5223,5018,'SHOP_PRODUCT','Hạt Royal Canin Mini Puppy Cho Chó Con',NULL,2,235000.00,470000.00,0,'2026-05-25 10:43:26','2026-05-25 10:43:26'),(5224,5019,'SHOP_PRODUCT','Pate Cho Mèo Trưởng Thành Whiskas Vị Cá Thu',NULL,1,45000.00,45000.00,0,'2026-05-25 10:50:16','2026-05-25 10:50:16'),(5225,5019,'SHIPPING_FEE','Phí giao hàng',NULL,1,30000.00,30000.00,1,'2026-05-25 10:50:16','2026-05-25 10:50:16'),(5226,5020,'SHOP_PRODUCT','Balo Phi Hành Gia Vận Chuyển Thú Cưng',NULL,1,290000.00,290000.00,0,'2026-05-25 10:53:55','2026-05-25 10:53:55'),(5227,5020,'SHIPPING_FEE','Phí giao hàng',NULL,1,30000.00,30000.00,1,'2026-05-25 10:53:55','2026-05-25 10:53:55');
/*!40000 ALTER TABLE `invoice_items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `invoices`
--

DROP TABLE IF EXISTS `invoices`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `invoices` (
                            `id` bigint unsigned NOT NULL AUTO_INCREMENT,
                            `invoice_number` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL,
                            `user_id` bigint unsigned NOT NULL,
                            `booking_id` bigint unsigned DEFAULT NULL,
                            `membership_subscription_id` bigint unsigned DEFAULT NULL,
                            `shop_order_id` bigint unsigned DEFAULT NULL,
                            `invoice_type` enum('BOOKING','MEMBERSHIP','SHOP_ORDER') COLLATE utf8mb4_unicode_ci NOT NULL,
                            `status` enum('DRAFT','ISSUED','PAID','VOID') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'ISSUED',
                            `billing_name` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
                            `billing_email` varchar(190) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                            `billing_phone` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                            `billing_address` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                            `subtotal_amount` decimal(12,2) NOT NULL,
                            `discount_amount` decimal(12,2) NOT NULL DEFAULT '0.00',
                            `tax_amount` decimal(12,2) NOT NULL DEFAULT '0.00',
                            `total_amount` decimal(12,2) NOT NULL,
                            `currency_code` char(3) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'VND',
                            `issued_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            `due_at` datetime DEFAULT NULL,
                            `paid_at` datetime DEFAULT NULL,
                            `note` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                            `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                            PRIMARY KEY (`id`),
                            UNIQUE KEY `uk_invoices_invoice_number` (`invoice_number`),
                            UNIQUE KEY `uk_invoices_booking` (`booking_id`),
                            UNIQUE KEY `uk_invoices_shop_order` (`shop_order_id`),
                            KEY `idx_invoices_user` (`user_id`,`status`),
                            KEY `idx_invoices_membership` (`membership_subscription_id`),
                            CONSTRAINT `fk_invoices_booking` FOREIGN KEY (`booking_id`) REFERENCES `bookings` (`id`),
                            CONSTRAINT `fk_invoices_membership` FOREIGN KEY (`membership_subscription_id`) REFERENCES `membership_subscriptions` (`id`),
                            CONSTRAINT `fk_invoices_shop_order` FOREIGN KEY (`shop_order_id`) REFERENCES `shop_orders` (`id`),
                            CONSTRAINT `fk_invoices_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5021 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `invoices`
--

LOCK TABLES `invoices` WRITE;
/*!40000 ALTER TABLE `invoices` DISABLE KEYS */;
INSERT INTO `invoices` VALUES (5001,'INV-DEMO-001',1001,4001,NULL,NULL,'BOOKING','PAID','Nguyễn Thu Lan','customer1@demo.petgo.local','0901000001','12 Nguyễn Huệ, Quận 1, Hồ Chí Minh',350000.00,35000.00,0.00,315000.00,'VND','2026-03-24 20:05:00','2026-03-25 20:05:00','2026-03-24 20:05:00','Thanh toán booking grooming','2026-04-17 13:47:58','2026-04-17 13:47:58'),(5002,'INV-DEMO-002',1001,4002,NULL,NULL,'BOOKING','PAID','Nguyễn Thu Lan','customer1@demo.petgo.local','0901000001','12 Nguyễn Huệ, Quận 1, Hồ Chí Minh',150000.00,30000.00,0.00,120000.00,'VND','2026-04-17 09:03:00','2026-04-22 09:03:00','2026-04-17 09:03:00','Thanh toán booking khám','2026-04-17 13:47:58','2026-04-17 13:47:58'),(5003,'INV-DEMO-003',1002,4003,NULL,NULL,'BOOKING','PAID','Trần Minh Khôi','customer2@demo.petgo.local','0901000002','28 Pasteur, Quận 1, Hồ Chí Minh',150000.00,0.00,0.00,150000.00,'VND','2026-04-15 10:05:00','2026-04-26 10:05:00','2026-04-15 10:05:00','Thanh toán booking khám','2026-04-17 13:47:58','2026-04-17 13:47:58'),(5004,'INV-DEMO-004',1001,4004,NULL,NULL,'BOOKING','VOID','Nguyễn Thu Lan','customer1@demo.petgo.local','0901000001','12 Nguyễn Huệ, Quận 1, Hồ Chí Minh',90000.00,9000.00,0.00,81000.00,'VND','2026-04-16 14:02:00','2026-04-19 14:02:00','2026-04-16 14:02:00','Booking đã hủy và hoàn tiền','2026-04-17 13:47:58','2026-04-17 13:47:58'),(5005,'INV-DEMO-005',1002,4005,NULL,NULL,'BOOKING','PAID','Trần Minh Khôi','customer2@demo.petgo.local','0901000002','28 Pasteur, Quận 1, Hồ Chí Minh',150000.00,0.00,0.00,150000.00,'VND','2026-03-27 09:05:00','2026-03-28 09:05:00','2026-03-27 09:05:00','Thanh toán booking khám','2026-04-17 13:47:58','2026-04-17 13:47:58'),(5006,'INV-DEMO-006',1001,NULL,6001,NULL,'MEMBERSHIP','PAID','Nguyễn Thu Lan','customer1@demo.petgo.local','0901000001','12 Nguyễn Huệ, Quận 1, Hồ Chí Minh',99000.00,14850.00,0.00,84150.00,'VND','2026-04-01 00:00:00','2026-04-01 00:00:00','2026-04-01 00:00:00','Thanh toán gói hội viên PRO','2026-04-17 13:47:58','2026-04-17 13:47:58'),(5007,'INV7BB44C4C1756',1007,4006,NULL,NULL,'BOOKING','ISSUED','PetGo Admin','admin@demo.petgo.local','0909000001','1 Admin Street, Bến Nghé, Quận 1, Hồ Chí Minh, Hồ Chí Minh',350000.00,0.00,0.00,350000.00,'VND','2026-04-17 13:52:18','2026-04-18 13:52:18',NULL,'Tạo từ checkout booking BK-0011007E51F79','2026-04-17 13:52:17','2026-04-17 13:52:17'),(5008,'INV-8728778874',1007,NULL,6003,NULL,'MEMBERSHIP','PAID','PetGo Admin','admin@demo.petgo.local','0909000001','1 Admin Street, Bến Nghé, Quận 1, Hồ Chí Minh, Hồ Chí Minh',99000.00,0.00,0.00,99000.00,'VND','2026-04-17 13:53:54','2026-04-17 13:53:54','2026-04-17 13:53:54','Thanh toán membership plan PRO','2026-04-17 13:53:54','2026-04-17 13:53:54'),(5009,'INV-F911A7C7A9',1007,NULL,6004,NULL,'MEMBERSHIP','PAID','PetGo Admin','admin@demo.petgo.local','0909000001','1 Admin Street, Bến Nghé, Quận 1, Hồ Chí Minh, Hồ Chí Minh',49000.00,0.00,0.00,49000.00,'VND','2026-04-17 13:54:08','2026-04-17 13:54:08','2026-04-17 13:54:08','Thanh toán membership plan BASIC','2026-04-17 13:54:07','2026-04-17 13:54:07'),(5010,'INV-5D26869B4E',1007,NULL,6005,NULL,'MEMBERSHIP','PAID','PetGo Admin','admin@demo.petgo.local','0909000001','1 Admin Street, Bến Nghé, Quận 1, Hồ Chí Minh, Hồ Chí Minh',199000.00,0.00,0.00,199000.00,'VND','2026-04-17 13:54:22','2026-04-17 13:54:22','2026-04-17 13:54:22','Thanh toán membership plan PREMIUM','2026-04-17 13:54:22','2026-04-17 13:54:22'),(5011,'INV84362BC558C8',1008,4007,NULL,NULL,'BOOKING','ISSUED','Nguyên Cao Phúc','phucnguyenxt2004@gmail.com','123456789',NULL,350000.00,0.00,0.00,350000.00,'VND','2026-04-17 14:21:31','2026-04-18 14:21:31',NULL,'Tạo từ checkout booking BK-00110083EEA82','2026-04-17 14:21:31','2026-04-17 14:21:31'),(5012,'INV8F236B818CBA',1008,4008,NULL,NULL,'BOOKING','PAID','Nguyên Cao Phúc','phucnguyenxt2004@gmail.com','123456789',NULL,350000.00,0.00,0.00,350000.00,'VND','2026-04-17 19:39:59','2026-04-18 19:39:59','2026-04-17 19:39:59','Tạo từ checkout booking BK-00110086359EB','2026-04-17 19:39:58','2026-04-17 19:39:58'),(5013,'INV-125319AF62',1008,NULL,6006,NULL,'MEMBERSHIP','PAID','Nguyên Cao Phúc','phucnguyenxt2004@gmail.com','12345','chó nghĩa, 36, thanh hoá',99000.00,0.00,0.00,99000.00,'VND','2026-05-11 15:48:49','2026-05-11 15:48:49','2026-05-11 15:48:49','Thanh toán membership plan PRO','2026-05-11 15:48:49','2026-05-11 15:48:49'),(5014,'INV-E1945E75B6',1008,NULL,6006,NULL,'MEMBERSHIP','PAID','Nguyên Cao Phúc','phucnguyenxt2004@gmail.com','12345','chó nghĩa, 36, thanh hoá',99000.00,0.00,0.00,99000.00,'VND','2026-05-11 15:49:01','2026-05-11 15:49:01','2026-05-11 15:49:01','Thanh toán membership plan PRO','2026-05-11 15:49:00','2026-05-11 15:49:00'),(5015,'INV-EFC4AB5686',1010,NULL,6007,NULL,'MEMBERSHIP','PAID','Nguyên Cao Phúc','nguyencphe181659@fpt.edu.vn','12345678',NULL,99000.00,0.00,0.00,99000.00,'VND','2026-05-20 00:40:18','2026-05-20 00:40:18','2026-05-20 00:40:18','Thanh toán membership plan PRO','2026-05-20 00:40:18','2026-05-20 00:40:18'),(5016,'INVB23437489752',3,1,NULL,NULL,'BOOKING','ISSUED','Nguyên Cao Phúc','phucnguyenxt2004@gmail.com','12345678',NULL,200000.00,0.00,0.00,200000.00,'VND','2026-05-20 01:38:54','2026-05-21 01:38:54',NULL,'Tạo từ checkout booking BKP0010003F34A7D','2026-05-20 01:38:54','2026-05-20 01:38:54'),(5017,'INV8A35EE91923F',3,2,NULL,NULL,'BOOKING','ISSUED','Nguyên Cao Phúc','phucnguyenxt2004@gmail.com','12345678',NULL,200000.00,0.00,0.00,200000.00,'VND','2026-05-20 01:43:21','2026-05-21 01:43:21',NULL,'Tạo từ checkout booking BKP001000394F401','2026-05-20 01:43:21','2026-05-20 01:43:21'),(5018,'INV-26052510432635',1,NULL,NULL,1,'SHOP_ORDER','PAID','Nguyễn Văn Anh','customer1@example.com','0912345678','Số 123 Đường Nguyễn Trãi',470000.00,0.00,0.00,470000.00,'VND','2026-05-25 10:43:26',NULL,'2026-05-25 10:43:26',NULL,'2026-05-25 10:43:26','2026-05-25 10:43:26'),(5019,'INV-26052510501668',1,NULL,NULL,2,'SHOP_ORDER','PAID','Nguyễn Văn Anh','customer1@example.com','0912345678','Số 123 Đường Nguyễn Trãi',45000.00,0.00,0.00,75000.00,'VND','2026-05-25 10:50:16',NULL,'2026-05-25 10:50:16',NULL,'2026-05-25 10:50:16','2026-05-25 10:50:16'),(5020,'INV-26052510535496',1,NULL,NULL,3,'SHOP_ORDER','PAID','Nguyễn Văn Anh','customer1@example.com','0912345678','Số 123 Đường Nguyễn Trãi',290000.00,0.00,0.00,320000.00,'VND','2026-05-25 10:53:55',NULL,'2026-05-25 10:53:55',NULL,'2026-05-25 10:53:55','2026-05-25 10:53:55');
/*!40000 ALTER TABLE `invoices` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `membership_plan_features`
--

DROP TABLE IF EXISTS `membership_plan_features`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `membership_plan_features` (
                                            `id` bigint unsigned NOT NULL AUTO_INCREMENT,
                                            `membership_plan_id` bigint unsigned NOT NULL,
                                            `feature_text` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
                                            `sort_order` int NOT NULL DEFAULT '0',
                                            `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                            `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                            PRIMARY KEY (`id`),
                                            KEY `idx_membership_plan_features_plan` (`membership_plan_id`),
                                            CONSTRAINT `fk_membership_plan_features_plan` FOREIGN KEY (`membership_plan_id`) REFERENCES `membership_plans` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `membership_plan_features`
--

LOCK TABLES `membership_plan_features` WRITE;
/*!40000 ALTER TABLE `membership_plan_features` DISABLE KEYS */;
INSERT INTO `membership_plan_features` VALUES (1,2,'Giảm 10% cho mọi dịch vụ thú cưng',1,'2026-04-15 23:28:24','2026-04-15 23:28:24'),(2,2,'Bộ voucher 200k mỗi tháng',2,'2026-04-15 23:28:24','2026-04-15 23:28:24'),(3,2,'Ưu tiên slot đặt lịch cao điểm',3,'2026-04-15 23:28:24','2026-04-15 23:28:24'),(4,2,'Nhắc lịch grooming và tiêm phòng',4,'2026-04-15 23:28:24','2026-04-15 23:28:24'),(5,2,'Hỗ trợ ưu tiên 24/7',5,'2026-04-15 23:28:24','2026-04-15 23:28:24'),(6,1,'Giảm 5% cho mọi dịch vụ thú cưng',1,'2026-04-17 13:47:58','2026-04-17 13:47:58'),(7,2,'Voucher 200k mỗi tháng',2,'2026-04-17 13:47:58','2026-04-17 13:47:58'),(8,3,'Giảm 15% cho mọi dịch vụ thú cưng',1,'2026-04-17 13:47:58','2026-04-17 13:47:58'),(9,3,'Voucher 500k mỗi tháng',2,'2026-04-17 13:47:58','2026-04-17 13:47:58');
/*!40000 ALTER TABLE `membership_plan_features` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `membership_plans`
--

DROP TABLE IF EXISTS `membership_plans`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `membership_plans` (
                                    `id` bigint unsigned NOT NULL AUTO_INCREMENT,
                                    `plan_code` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
                                    `name` varchar(120) COLLATE utf8mb4_unicode_ci NOT NULL,
                                    `slug` varchar(120) COLLATE utf8mb4_unicode_ci NOT NULL,
                                    `description` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                    `billing_cycle` enum('MONTHLY','QUARTERLY','YEARLY') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'MONTHLY',
                                    `price_amount` decimal(12,2) NOT NULL,
                                    `currency_code` char(3) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'VND',
                                    `discount_percent` decimal(5,2) NOT NULL DEFAULT '0.00',
                                    `monthly_voucher_amount` decimal(12,2) NOT NULL DEFAULT '0.00',
                                    `priority_booking` tinyint(1) NOT NULL DEFAULT '0',
                                    `priority_support` tinyint(1) NOT NULL DEFAULT '0',
                                    `is_popular` tinyint(1) NOT NULL DEFAULT '0',
                                    `sort_order` int NOT NULL DEFAULT '0',
                                    `is_active` tinyint(1) NOT NULL DEFAULT '1',
                                    `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                    `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                    PRIMARY KEY (`id`),
                                    UNIQUE KEY `uk_membership_plans_code` (`plan_code`),
                                    UNIQUE KEY `uk_membership_plans_slug` (`slug`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `membership_plans`
--

LOCK TABLES `membership_plans` WRITE;
/*!40000 ALTER TABLE `membership_plans` DISABLE KEYS */;
INSERT INTO `membership_plans` VALUES (1,'BASIC','Basic Membership','basic','Gói cơ bản cho nhu cầu nhẹ','MONTHLY',49000.00,'VND',5.00,50000.00,0,0,0,1,1,'2026-04-15 23:28:24','2026-04-15 23:28:24'),(2,'PRO','Pro Membership','pro','Gói phù hợp người dùng thường xuyên','MONTHLY',99000.00,'VND',10.00,200000.00,1,1,1,2,1,'2026-04-15 23:28:24','2026-04-15 23:28:24'),(3,'PREMIUM','Premium Membership','premium','Gói cao cấp với nhiều ưu đãi','MONTHLY',199000.00,'VND',15.00,500000.00,1,1,0,3,1,'2026-04-15 23:28:24','2026-04-15 23:28:24');
/*!40000 ALTER TABLE `membership_plans` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `membership_subscriptions`
--

DROP TABLE IF EXISTS `membership_subscriptions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `membership_subscriptions` (
                                            `id` bigint unsigned NOT NULL AUTO_INCREMENT,
                                            `subscription_code` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL,
                                            `user_id` bigint unsigned NOT NULL,
                                            `membership_plan_id` bigint unsigned NOT NULL,
                                            `status` enum('PENDING_PAYMENT','ACTIVE','PAST_DUE','CANCELLED','EXPIRED') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'PENDING_PAYMENT',
                                            `auto_renew` tinyint(1) NOT NULL DEFAULT '1',
                                            `started_at` datetime DEFAULT NULL,
                                            `expires_at` datetime DEFAULT NULL,
                                            `next_billing_at` datetime DEFAULT NULL,
                                            `cancelled_at` datetime DEFAULT NULL,
                                            `cancel_reason` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                            `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                            `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                            PRIMARY KEY (`id`),
                                            UNIQUE KEY `uk_membership_subscriptions_code` (`subscription_code`),
                                            KEY `idx_membership_subscriptions_user` (`user_id`),
                                            KEY `idx_membership_subscriptions_status` (`status`),
                                            KEY `fk_membership_subscriptions_plan` (`membership_plan_id`),
                                            CONSTRAINT `fk_membership_subscriptions_plan` FOREIGN KEY (`membership_plan_id`) REFERENCES `membership_plans` (`id`),
                                            CONSTRAINT `fk_membership_subscriptions_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6008 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `membership_subscriptions`
--

LOCK TABLES `membership_subscriptions` WRITE;
/*!40000 ALTER TABLE `membership_subscriptions` DISABLE KEYS */;
INSERT INTO `membership_subscriptions` VALUES (6001,'SUB-DEMO-001',1001,2,'ACTIVE',1,'2026-04-01 00:00:00','2026-04-30 23:59:59','2026-05-01 00:00:00',NULL,NULL,'2026-04-17 13:47:58','2026-04-17 13:47:58'),(6002,'SUB-DEMO-002',1002,1,'EXPIRED',0,'2026-02-01 00:00:00','2026-02-28 23:59:59',NULL,'2026-02-28 23:59:59','Không tiếp tục','2026-04-17 13:47:58','2026-04-17 13:47:58'),(6003,'SUB-BCBC19184E',1007,2,'CANCELLED',0,'2026-04-17 13:53:54','2026-05-17 13:53:54','2026-05-17 13:53:54','2026-04-17 13:54:08','Chuyển sang gói Basic Membership','2026-04-17 13:53:54','2026-04-17 13:54:07'),(6004,'SUB-BEB45FFA46',1007,1,'CANCELLED',0,'2026-04-17 13:54:08','2026-05-17 13:54:08','2026-05-17 13:54:08','2026-04-17 13:54:22','Chuyển sang gói Premium Membership','2026-04-17 13:54:07','2026-04-17 13:54:22'),(6005,'SUB-EF75AE3DFC',1007,3,'ACTIVE',1,'2026-04-17 13:54:22','2026-05-17 13:54:22','2026-05-17 13:54:22',NULL,NULL,'2026-04-17 13:54:22','2026-04-17 13:54:22'),(6006,'SUB-527DF284DA',1008,2,'ACTIVE',1,'2026-05-11 15:48:49','2026-07-11 15:48:49','2026-07-11 15:48:49',NULL,NULL,'2026-05-11 15:48:49','2026-05-11 15:49:00'),(6007,'SUB-7A14B0AD2E',1010,2,'ACTIVE',1,'2026-05-20 00:40:18','2026-06-20 00:40:18','2026-06-20 00:40:18',NULL,NULL,'2026-05-20 00:40:18','2026-05-20 00:40:18');
/*!40000 ALTER TABLE `membership_subscriptions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notifications`
--

DROP TABLE IF EXISTS `notifications`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `notifications` (
                                 `id` bigint unsigned NOT NULL AUTO_INCREMENT,
                                 `user_id` bigint unsigned NOT NULL,
                                 `notification_type` enum('BOOKING_CREATED','BOOKING_CONFIRMED','BOOKING_REMINDER','BOOKING_CANCELLED','BOOKING_RESCHEDULED','PAYMENT_SUCCESS','PAYMENT_FAILED','REVIEW_RECEIVED','MEMBERSHIP_ACTIVATED','MEMBERSHIP_EXPIRING','SYSTEM') COLLATE utf8mb4_unicode_ci NOT NULL,
                                 `title` varchar(180) COLLATE utf8mb4_unicode_ci NOT NULL,
                                 `message` varchar(500) COLLATE utf8mb4_unicode_ci NOT NULL,
                                 `reference_type` enum('BOOKING','INVOICE','PAYMENT','MEMBERSHIP','PROVIDER','SYSTEM') COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                 `reference_id` bigint unsigned DEFAULT NULL,
                                 `is_read` tinyint(1) NOT NULL DEFAULT '0',
                                 `sent_at` datetime DEFAULT NULL,
                                 `read_at` datetime DEFAULT NULL,
                                 `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                 `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                 PRIMARY KEY (`id`),
                                 KEY `idx_notifications_user_read` (`user_id`,`is_read`,`created_at`),
                                 CONSTRAINT `fk_notifications_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5406 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notifications`
--

LOCK TABLES `notifications` WRITE;
/*!40000 ALTER TABLE `notifications` DISABLE KEYS */;
INSERT INTO `notifications` VALUES (5401,1001,'PAYMENT_SUCCESS','Thanh toán thành công','Bạn đã thanh toán thành công booking BKG-DEMO-002.','PAYMENT',5102,1,'2026-04-17 09:03:00','2026-04-17 09:05:00','2026-04-17 13:47:58','2026-04-17 13:47:58'),(5402,1001,'BOOKING_CANCELLED','Booking đã hủy','Booking BKG-DEMO-004 đã được hủy và hoàn tiền.','BOOKING',4004,0,'2026-04-17 08:00:00',NULL,'2026-04-17 13:47:58','2026-04-17 13:47:58'),(5403,1002,'BOOKING_RESCHEDULED','Lịch hẹn đã được đổi','Booking BKG-DEMO-003 đã được đổi sang 26/04/2026 11:00.','BOOKING',4003,0,'2026-04-15 10:10:00',NULL,'2026-04-17 13:47:58','2026-04-17 13:47:58'),(5404,1003,'REVIEW_RECEIVED','Bạn có đánh giá mới','Khách hàng vừa để lại đánh giá 5 sao cho dịch vụ của bạn.','PROVIDER',2001,0,'2026-03-25 12:00:00',NULL,'2026-04-17 13:47:58','2026-04-17 13:47:58'),(5405,1001,'MEMBERSHIP_ACTIVATED','Hội viên đã kích hoạt','Gói PRO của bạn đã được kích hoạt thành công.','MEMBERSHIP',6001,1,'2026-04-01 00:00:00','2026-04-01 00:10:00','2026-04-17 13:47:58','2026-04-17 13:47:58');
/*!40000 ALTER TABLE `notifications` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `payments`
--

DROP TABLE IF EXISTS `payments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `payments` (
                            `id` bigint unsigned NOT NULL AUTO_INCREMENT,
                            `payment_code` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL,
                            `invoice_id` bigint unsigned NOT NULL,
                            `payer_user_id` bigint unsigned NOT NULL,
                            `amount` decimal(12,2) NOT NULL,
                            `currency_code` char(3) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'VND',
                            `payment_method` enum('COD','CASH','CARD','BANK_TRANSFER','MOMO','VNPAY','ZALOPAY','WALLET') COLLATE utf8mb4_unicode_ci NOT NULL,
                            `gateway_name` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                            `gateway_transaction_id` varchar(120) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                            `status` enum('PENDING','AUTHORIZED','SUCCEEDED','FAILED','REFUNDED','PARTIALLY_REFUNDED','CANCELLED') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'PENDING',
                            `paid_at` datetime DEFAULT NULL,
                            `failure_reason` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                            `metadata_json` json DEFAULT NULL,
                            `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                            PRIMARY KEY (`id`),
                            UNIQUE KEY `uk_payments_payment_code` (`payment_code`),
                            UNIQUE KEY `uk_payments_gateway_txn` (`gateway_transaction_id`),
                            KEY `idx_payments_invoice` (`invoice_id`),
                            KEY `idx_payments_user` (`payer_user_id`),
                            KEY `idx_payments_status` (`status`),
                            CONSTRAINT `fk_payments_invoice` FOREIGN KEY (`invoice_id`) REFERENCES `invoices` (`id`),
                            CONSTRAINT `fk_payments_user` FOREIGN KEY (`payer_user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5121 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `payments`
--

LOCK TABLES `payments` WRITE;
/*!40000 ALTER TABLE `payments` DISABLE KEYS */;
INSERT INTO `payments` VALUES (5101,'PAY-DEMO-001',5001,1001,315000.00,'VND','MOMO','MOMO','MM-20260324-0001','SUCCEEDED','2026-03-24 20:05:00',NULL,'{\"source\": \"demo-seed\"}','2026-04-17 13:47:58','2026-04-17 13:47:58'),(5102,'PAY-DEMO-002',5002,1001,120000.00,'VND','CARD','STRIPE','ST-20260417-0002','SUCCEEDED','2026-04-17 09:03:00',NULL,'{\"source\": \"demo-seed\"}','2026-04-17 13:47:58','2026-04-17 13:47:58'),(5103,'PAY-DEMO-003',5003,1002,150000.00,'VND','BANK_TRANSFER','BANK','BK-20260415-0003','SUCCEEDED','2026-04-15 10:05:00',NULL,'{\"source\": \"demo-seed\"}','2026-04-17 13:47:58','2026-04-17 13:47:58'),(5104,'PAY-DEMO-004',5004,1001,81000.00,'VND','MOMO','MOMO','MM-20260416-0004','REFUNDED','2026-04-16 14:02:00',NULL,'{\"source\": \"demo-seed\", \"refundAmount\": 81000}','2026-04-17 13:47:58','2026-04-17 13:47:58'),(5105,'PAY-DEMO-005',5005,1002,150000.00,'VND','COD',NULL,NULL,'SUCCEEDED','2026-03-27 09:05:00',NULL,'{\"source\": \"demo-seed\"}','2026-04-17 13:47:58','2026-04-17 13:47:58'),(5106,'PAY-DEMO-006',5006,1001,84150.00,'VND','VNPAY','VNPAY','VN-20260401-0006','SUCCEEDED','2026-04-01 00:00:00',NULL,'{\"source\": \"demo-seed\"}','2026-04-17 13:47:58','2026-04-17 13:47:58'),(5107,'PAY9EF45A2A4CD8',5007,1007,350000.00,'VND','COD','COD','TXN6E7F4137F9A2','PENDING',NULL,NULL,'{\"source\": \"petgo-checkout\"}','2026-04-17 13:52:17','2026-04-17 13:52:17'),(5108,'PAY-A52AE35F9D',5008,1007,99000.00,'VND','CARD','Card','TXN-CBC03C50C0','SUCCEEDED','2026-04-17 13:53:54',NULL,'{\"source\": \"petgo-membership-checkout\"}','2026-04-17 13:53:54','2026-04-17 13:53:54'),(5109,'PAY-34AD02E058',5009,1007,49000.00,'VND','CARD','Card','TXN-720B2818EB','SUCCEEDED','2026-04-17 13:54:08',NULL,'{\"source\": \"petgo-membership-checkout\"}','2026-04-17 13:54:07','2026-04-17 13:54:07'),(5110,'PAY-FD1683A1FC',5010,1007,199000.00,'VND','CARD','Card','TXN-9FA6105FAC','SUCCEEDED','2026-04-17 13:54:22',NULL,'{\"source\": \"petgo-membership-checkout\"}','2026-04-17 13:54:22','2026-04-17 13:54:22'),(5111,'PAY242390D7102A',5011,1008,350000.00,'VND','COD','COD','TXN318E093EDCD3','PENDING',NULL,NULL,'{\"source\": \"petgo-checkout\"}','2026-04-17 14:21:31','2026-04-17 14:21:31'),(5112,'PAYB09B095F3D84',5012,1008,350000.00,'VND','VNPAY','VNPay','TXNC49D1F7A6F2C','SUCCEEDED','2026-04-17 19:39:59',NULL,'{\"source\": \"petgo-checkout\"}','2026-04-17 19:39:58','2026-04-17 19:39:58'),(5113,'PAY-2F0C7188F1',5013,1008,99000.00,'VND','CARD','Card','TXN-DA8D26170A','SUCCEEDED','2026-05-11 15:48:49',NULL,'{\"source\": \"petgo-membership-checkout\"}','2026-05-11 15:48:49','2026-05-11 15:48:49'),(5114,'PAY-0C3E8A3C6E',5014,1008,99000.00,'VND','CARD','Card','TXN-D5922199D8','SUCCEEDED','2026-05-11 15:49:01',NULL,'{\"source\": \"petgo-membership-checkout\"}','2026-05-11 15:49:00','2026-05-11 15:49:00'),(5115,'PAY-C630CF414D',5015,1010,99000.00,'VND','CARD','Card','TXN-95940F883D','SUCCEEDED','2026-05-20 00:40:18',NULL,'{\"source\": \"petgo-membership-checkout\"}','2026-05-20 00:40:18','2026-05-20 00:40:18'),(5116,'PAYC0E834EC2072',5016,3,200000.00,'VND','COD','COD','TXNCE63735C9511','PENDING',NULL,NULL,'{\"source\": \"petgo-checkout\"}','2026-05-20 01:38:54','2026-05-20 01:38:54'),(5117,'PAY3ED3A02DFB17',5017,3,200000.00,'VND','COD','COD','TXNDD2CB1E1FB5E','PENDING',NULL,NULL,'{\"source\": \"petgo-checkout\"}','2026-05-20 01:43:21','2026-05-20 01:43:21'),(5118,'PAY-26052510432620',5018,1,470000.00,'VND','COD',NULL,NULL,'SUCCEEDED','2026-05-25 10:43:26',NULL,NULL,'2026-05-25 10:43:26','2026-05-25 10:43:26'),(5119,'PAY-26052510501625',5019,1,75000.00,'VND','COD',NULL,NULL,'SUCCEEDED','2026-05-25 10:50:16',NULL,NULL,'2026-05-25 10:50:16','2026-05-25 10:50:16'),(5120,'PAY-26052510535420',5020,1,320000.00,'VND','COD',NULL,NULL,'SUCCEEDED','2026-05-25 10:53:55',NULL,NULL,'2026-05-25 10:53:55','2026-05-25 10:53:55');
/*!40000 ALTER TABLE `payments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pet_photos`
--

DROP TABLE IF EXISTS `pet_photos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pet_photos` (
                              `id` bigint unsigned NOT NULL AUTO_INCREMENT,
                              `pet_id` bigint unsigned NOT NULL,
                              `photo_url` varchar(500) COLLATE utf8mb4_unicode_ci NOT NULL,
                              `is_primary` tinyint(1) NOT NULL DEFAULT '0',
                              `sort_order` int NOT NULL DEFAULT '0',
                              `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                              `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                              PRIMARY KEY (`id`),
                              KEY `idx_pet_photos_pet` (`pet_id`),
                              KEY `idx_pet_photos_primary` (`pet_id`,`is_primary`),
                              CONSTRAINT `fk_pet_photos_pet` FOREIGN KEY (`pet_id`) REFERENCES `pets` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3124 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pet_photos`
--

LOCK TABLES `pet_photos` WRITE;
/*!40000 ALTER TABLE `pet_photos` DISABLE KEYS */;
INSERT INTO `pet_photos` VALUES (3101,3001,'https://placehold.co/800x600?text=Mochi+1',1,1,'2026-04-17 13:47:58','2026-04-17 13:47:58'),(3102,3002,'https://placehold.co/800x600?text=Bo+1',1,1,'2026-04-17 13:47:58','2026-04-17 13:47:58'),(3103,3003,'https://placehold.co/800x600?text=CaPhe+1',1,1,'2026-04-17 13:47:58','2026-04-17 13:47:58'),(3104,3004,'https://img.tripi.vn/cdn-cgi/image/width=700,height=700/https://gcs.tripi.vn/public-tripi/tripi-feed/img/482759win/anh-mo-ta.png',1,0,'2026-04-17 13:51:28','2026-04-17 13:51:28'),(3105,3005,'https://img.tripi.vn/cdn-cgi/image/width=700,height=700/https://gcs.tripi.vn/public-tripi/tripi-feed/img/482759win/anh-mo-ta.png',1,0,'2026-04-17 14:21:02','2026-04-17 14:21:02'),(3116,3006,'https://res.cloudinary.com/dxaok6qzg/image/upload/v1778603686/petgo/pets/avatar/a817e7f2b1624d07b94d749b011d948c.png',1,0,'2026-05-12 23:36:50','2026-05-12 23:36:50'),(3117,3006,'/uploads/pets/01216360f0414be3b9bec77777850056.png',0,1,'2026-05-12 23:36:50','2026-05-12 23:36:50'),(3118,3006,'01216360f0414be3b9bec77777850056.png',0,2,'2026-05-12 23:36:50','2026-05-12 23:36:50'),(3119,3006,'9f3069a0f66c4b0cbeadf53093105b62.png',0,3,'2026-05-12 23:36:50','2026-05-12 23:36:50'),(3120,3007,'https://res.cloudinary.com/dxaok6qzg/image/upload/v1778653969/petgo/pets/avatar/aa605dd07a4f4044b788a8746331f19f.png',1,0,'2026-05-13 13:34:52','2026-05-13 13:34:52'),(3121,3008,'https://res.cloudinary.com/dxaok6qzg/image/upload/v1779209732/petgo/pets/avatar/d79cc790316846c6b1d8cd69c4809a33.jpg',1,0,'2026-05-19 23:57:39','2026-05-19 23:57:39'),(3122,3,'https://res.cloudinary.com/dxaok6qzg/image/upload/v1779215783/petgo/pets/avatar/9ec4ce50b2c44a44966a1971226f284c.jpg',1,0,'2026-05-20 01:38:30','2026-05-20 01:38:30'),(3123,4,'https://res.cloudinary.com/dxaok6qzg/image/upload/v1779216020/petgo/pets/avatar/9dcc4f9b930e40418771a3f7e4989a1a.jpg',1,0,'2026-05-20 01:42:27','2026-05-20 01:42:27');
/*!40000 ALTER TABLE `pet_photos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pets`
--

DROP TABLE IF EXISTS `pets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pets` (
                        `id` bigint unsigned NOT NULL AUTO_INCREMENT,
                        `pet_code` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL,
                        `owner_user_id` bigint unsigned NOT NULL,
                        `name` varchar(120) COLLATE utf8mb4_unicode_ci NOT NULL,
                        `species` enum('DOG','CAT','BIRD','RABBIT','HAMSTER','REPTILE','OTHER') COLLATE utf8mb4_unicode_ci NOT NULL,
                        `breed` varchar(120) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                        `gender` enum('MALE','FEMALE','UNKNOWN') COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                        `date_of_birth` date DEFAULT NULL,
                        `age_label` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                        `weight_kg` decimal(6,2) DEFAULT NULL,
                        `color` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                        `size` enum('XS','S','M','L','XL','UNKNOWN') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'UNKNOWN',
                        `avatar_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                        `health_notes` text COLLATE utf8mb4_unicode_ci,
                        `allergy_notes` text COLLATE utf8mb4_unicode_ci,
                        `behavior_notes` text COLLATE utf8mb4_unicode_ci,
                        `vaccination_notes` text COLLATE utf8mb4_unicode_ci,
                        `status` enum('ACTIVE','INACTIVE') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'ACTIVE',
                        `deleted_at` datetime DEFAULT NULL,
                        `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                        PRIMARY KEY (`id`),
                        UNIQUE KEY `uk_pets_pet_code` (`pet_code`),
                        KEY `idx_pets_owner` (`owner_user_id`),
                        KEY `idx_pets_species` (`species`),
                        KEY `idx_pets_status` (`status`),
                        CONSTRAINT `fk_pets_owner` FOREIGN KEY (`owner_user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pets`
--

LOCK TABLES `pets` WRITE;
/*!40000 ALTER TABLE `pets` DISABLE KEYS */;
INSERT INTO `pets` VALUES (1,'PET001',1,'Bobby','DOG',NULL,NULL,NULL,NULL,NULL,NULL,'M',NULL,NULL,NULL,NULL,NULL,'ACTIVE',NULL,'2026-05-20 01:35:05','2026-05-20 01:35:05'),(2,'PET002',1,'Kitty','CAT',NULL,NULL,NULL,NULL,NULL,NULL,'S',NULL,NULL,NULL,NULL,NULL,'ACTIVE',NULL,'2026-05-20 01:35:05','2026-05-20 01:35:05'),(3,'PET-4474B1F922',3,'sâdsas','DOG','adsđ','UNKNOWN',NULL,'sdsds',12.00,'ssdsad','UNKNOWN','https://res.cloudinary.com/dxaok6qzg/image/upload/v1779215783/petgo/pets/avatar/9ec4ce50b2c44a44966a1971226f284c.jpg','ád','ádas','dsdas','asdas','ACTIVE',NULL,'2026-05-20 01:38:30','2026-05-20 01:38:30'),(4,'PET-1BE75CDD71',3,'ygyuguy','CAT','iuguyg','FEMALE','2026-05-06','4e54',4656.00,'hvghv','M','https://res.cloudinary.com/dxaok6qzg/image/upload/v1779216020/petgo/pets/avatar/9dcc4f9b930e40418771a3f7e4989a1a.jpg','hvjhv','hgv h','hjvjh','hjbjh','ACTIVE',NULL,'2026-05-20 01:42:27','2026-05-20 01:42:27');
/*!40000 ALTER TABLE `pets` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product_categories`
--

DROP TABLE IF EXISTS `product_categories`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product_categories` (
                                      `id` bigint unsigned NOT NULL AUTO_INCREMENT,
                                      `parent_id` bigint unsigned DEFAULT NULL,
                                      `name` varchar(120) COLLATE utf8mb4_unicode_ci NOT NULL,
                                      `slug` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
                                      `icon_key` varchar(80) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                      `description` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                      `sort_order` int NOT NULL DEFAULT '0',
                                      `is_active` tinyint(1) NOT NULL DEFAULT '1',
                                      `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                      `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                      PRIMARY KEY (`id`),
                                      UNIQUE KEY `uk_product_categories_slug` (`slug`),
                                      KEY `idx_product_categories_parent` (`parent_id`),
                                      CONSTRAINT `fk_product_categories_parent` FOREIGN KEY (`parent_id`) REFERENCES `product_categories` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product_categories`
--

LOCK TABLES `product_categories` WRITE;
/*!40000 ALTER TABLE `product_categories` DISABLE KEYS */;
INSERT INTO `product_categories` VALUES (1,NULL,'Thức ăn','thuc-an','bone','Hạt, pate, bánh thưởng',1,1,'2026-05-25 10:42:40','2026-05-25 10:42:40'),(2,NULL,'Phụ kiện','phu-kien','shield-dog','Dây dắt, vòng cổ, balo',2,1,'2026-05-25 10:42:40','2026-05-25 10:42:40'),(3,NULL,'Đồ chơi','do-choi','ball','Bóng ném, cần câu mèo',3,1,'2026-05-25 10:42:40','2026-05-25 10:42:40'),(4,NULL,'Chăm sóc','cham-soc','soap','Sữa tắm, lược chải, bỉm',4,1,'2026-05-25 10:42:40','2026-05-25 10:42:40');
/*!40000 ALTER TABLE `product_categories` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product_favorites`
--

DROP TABLE IF EXISTS `product_favorites`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product_favorites` (
                                     `id` bigint unsigned NOT NULL AUTO_INCREMENT,
                                     `user_id` bigint unsigned NOT NULL,
                                     `product_id` bigint unsigned NOT NULL,
                                     `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                     PRIMARY KEY (`id`),
                                     UNIQUE KEY `uk_product_favorites_user_product` (`user_id`,`product_id`),
                                     KEY `idx_product_favorites_product` (`product_id`),
                                     CONSTRAINT `fk_product_favorites_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`),
                                     CONSTRAINT `fk_product_favorites_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product_favorites`
--

LOCK TABLES `product_favorites` WRITE;
/*!40000 ALTER TABLE `product_favorites` DISABLE KEYS */;
/*!40000 ALTER TABLE `product_favorites` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product_images`
--

DROP TABLE IF EXISTS `product_images`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product_images` (
                                  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
                                  `product_id` bigint unsigned NOT NULL,
                                  `image_url` varchar(500) COLLATE utf8mb4_unicode_ci NOT NULL,
                                  `is_primary` tinyint(1) NOT NULL DEFAULT '0',
                                  `sort_order` int NOT NULL DEFAULT '0',
                                  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                  PRIMARY KEY (`id`),
                                  KEY `idx_product_images_product` (`product_id`),
                                  KEY `idx_product_images_primary` (`product_id`,`is_primary`),
                                  CONSTRAINT `fk_product_images_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product_images`
--

LOCK TABLES `product_images` WRITE;
/*!40000 ALTER TABLE `product_images` DISABLE KEYS */;
/*!40000 ALTER TABLE `product_images` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product_reviews`
--

DROP TABLE IF EXISTS `product_reviews`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product_reviews` (
                                   `id` bigint unsigned NOT NULL AUTO_INCREMENT,
                                   `product_id` bigint unsigned NOT NULL,
                                   `shop_order_id` bigint unsigned DEFAULT NULL,
                                   `customer_user_id` bigint unsigned NOT NULL,
                                   `rating` tinyint NOT NULL,
                                   `comment` text COLLATE utf8mb4_unicode_ci,
                                   `status` enum('VISIBLE','HIDDEN','REPORTED') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'VISIBLE',
                                   `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                   `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                   `deleted_at` datetime DEFAULT NULL,
                                   PRIMARY KEY (`id`),
                                   KEY `idx_product_reviews_product` (`product_id`,`status`),
                                   KEY `idx_product_reviews_customer` (`customer_user_id`),
                                   KEY `idx_product_reviews_order` (`shop_order_id`),
                                   CONSTRAINT `fk_product_reviews_customer` FOREIGN KEY (`customer_user_id`) REFERENCES `users` (`id`),
                                   CONSTRAINT `fk_product_reviews_order` FOREIGN KEY (`shop_order_id`) REFERENCES `shop_orders` (`id`),
                                   CONSTRAINT `fk_product_reviews_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product_reviews`
--

LOCK TABLES `product_reviews` WRITE;
/*!40000 ALTER TABLE `product_reviews` DISABLE KEYS */;
/*!40000 ALTER TABLE `product_reviews` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `products`
--

DROP TABLE IF EXISTS `products`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `products` (
                            `id` bigint unsigned NOT NULL AUTO_INCREMENT,
                            `product_code` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL,
                            `category_id` bigint unsigned NOT NULL,
                            `name` varchar(180) COLLATE utf8mb4_unicode_ci NOT NULL,
                            `slug` varchar(190) COLLATE utf8mb4_unicode_ci NOT NULL,
                            `brand` varchar(120) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                            `short_description` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                            `description` text COLLATE utf8mb4_unicode_ci,
                            `target_species` enum('DOG','CAT','BIRD','RABBIT','HAMSTER','REPTILE','ALL','OTHER') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'ALL',
                            `price_amount` decimal(12,2) NOT NULL,
                            `sale_price_amount` decimal(12,2) DEFAULT NULL,
                            `currency_code` char(3) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'VND',
                            `stock_quantity` int NOT NULL DEFAULT '0',
                            `sold_quantity` int NOT NULL DEFAULT '0',
                            `sku` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                            `barcode` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                            `weight_gram` int DEFAULT NULL,
                            `main_image_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                            `average_rating` decimal(3,2) NOT NULL DEFAULT '0.00',
                            `total_reviews` int NOT NULL DEFAULT '0',
                            `is_featured` tinyint(1) NOT NULL DEFAULT '0',
                            `is_hot` tinyint(1) NOT NULL DEFAULT '0',
                            `is_active` tinyint(1) NOT NULL DEFAULT '1',
                            `status` enum('DRAFT','ACTIVE','OUT_OF_STOCK','INACTIVE') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'ACTIVE',
                            `deleted_at` datetime DEFAULT NULL,
                            `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                            PRIMARY KEY (`id`),
                            UNIQUE KEY `uk_products_product_code` (`product_code`),
                            UNIQUE KEY `uk_products_slug` (`slug`),
                            UNIQUE KEY `uk_products_sku` (`sku`),
                            KEY `idx_products_category` (`category_id`),
                            KEY `idx_products_status` (`status`),
                            KEY `idx_products_species` (`target_species`),
                            KEY `idx_products_featured` (`is_featured`),
                            KEY `idx_products_hot` (`is_hot`),
                            CONSTRAINT `fk_products_category` FOREIGN KEY (`category_id`) REFERENCES `product_categories` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `products`
--

LOCK TABLES `products` WRITE;
/*!40000 ALTER TABLE `products` DISABLE KEYS */;
INSERT INTO `products` VALUES (1,'PRD-1779680560145',1,'Hạt Royal Canin Mini Puppy Cho Chó Con','hat-royal-canin-mini-puppy-cho-cho-con','Royal Canin','Cung cấp đầy đủ đạm, khoáng chất giúp cún phát triển xương vững chắc.','Cung cấp đầy đủ đạm, khoáng chất giúp cún phát triển xương vững chắc.','DOG',265000.00,235000.00,'VND',38,185,NULL,NULL,NULL,'https://images.unsplash.com/photo-1589924691995-400dc9ecc119?w=600&auto=format&fit=crop&q=80',4.90,37,1,1,1,'ACTIVE',NULL,'2026-05-25 10:42:40','2026-05-25 10:43:26'),(2,'PRD-1779680560159',1,'Pate Cho Mèo Trưởng Thành Whiskas Vị Cá Thu','pate-cho-meo-truong-thanh-whiskas-vi-ca-thu','Whiskas','Pate vị cá thu tươi ngon, bổ sung omega 3 và omega 6.','Pate vị cá thu tươi ngon, bổ sung omega 3 và omega 6.','CAT',60000.00,45000.00,'VND',98,54,NULL,NULL,NULL,'https://images.unsplash.com/photo-1591871937573-74dbba515c4c?w=600&auto=format&fit=crop&q=80',4.90,77,1,1,1,'ACTIVE',NULL,'2026-05-25 10:42:40','2026-05-25 10:50:16'),(3,'PRD-1779680560164',3,'Cần Câu Đồ Chơi Cho Mèo Đầu Lông Vũ','can-cau-do-choi-cho-meo-dau-long-vu','PetGo','Đồ chơi tương tác giúp mèo giải tỏa stress.','Đồ chơi tương tác giúp mèo giải tỏa stress.','CAT',35000.00,NULL,'VND',30,98,NULL,NULL,NULL,'https://images.unsplash.com/photo-1545249390-6bdfa286032f?w=600&auto=format&fit=crop&q=80',4.90,20,1,0,1,'ACTIVE',NULL,'2026-05-25 10:42:40','2026-05-25 10:42:40'),(4,'PRD-1779680560168',2,'Balo Phi Hành Gia Vận Chuyển Thú Cưng','balo-phi-hanh-gia-van-chuyen-thu-cung','PetGo','Balo trong suốt thiết kế phi hành gia cao cấp, rộng rãi thoáng khí.','Balo trong suốt thiết kế phi hành gia cao cấp, rộng rãi thoáng khí.','ALL',380000.00,290000.00,'VND',11,51,NULL,NULL,NULL,'https://images.unsplash.com/photo-1517849845537-4d257902454a?w=600&auto=format&fit=crop&q=80',4.90,27,1,1,1,'ACTIVE',NULL,'2026-05-25 10:42:40','2026-05-25 10:53:55'),(5,'PRD-1779680560171',4,'Sữa Tắm Cho Chó Mèo SOS Màu Xanh Dương','sua-tam-cho-cho-meo-sos-mau-xanh-duong','SOS','Sữa tắm chuyên dụng khử mùi hôi, giữ màu lông sáng bóng.','Sữa tắm chuyên dụng khử mùi hôi, giữ màu lông sáng bóng.','ALL',135000.00,NULL,'VND',50,112,NULL,NULL,NULL,'https://images.unsplash.com/photo-1516733725897-1aa73b87c8e8?w=600&auto=format&fit=crop&q=80',4.80,26,0,0,1,'ACTIVE',NULL,'2026-05-25 10:42:40','2026-05-25 10:42:40'),(6,'PRD-1779680560175',3,'Xương Gặm Cao Su Đồ Chơi Sạch Răng','xuong-gam-cao-su-do-choi-sach-rang','PetGo','Xương gặm cao su tự nhiên giúp cún sạch răng.','Xương gặm cao su tự nhiên giúp cún sạch răng.','DOG',65000.00,NULL,'VND',25,22,NULL,NULL,NULL,'https://images.unsplash.com/photo-1576201836106-db1758fd1c97?w=600&auto=format&fit=crop&q=80',5.00,64,0,0,1,'ACTIVE',NULL,'2026-05-25 10:42:40','2026-05-25 10:42:40');
/*!40000 ALTER TABLE `products` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `promo_code_redemptions`
--

DROP TABLE IF EXISTS `promo_code_redemptions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `promo_code_redemptions` (
                                          `id` bigint unsigned NOT NULL AUTO_INCREMENT,
                                          `promo_code_id` bigint unsigned NOT NULL,
                                          `user_id` bigint unsigned NOT NULL,
                                          `booking_id` bigint unsigned DEFAULT NULL,
                                          `membership_subscription_id` bigint unsigned DEFAULT NULL,
                                          `discount_amount` decimal(12,2) NOT NULL DEFAULT '0.00',
                                          `redeemed_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                          PRIMARY KEY (`id`),
                                          KEY `idx_promo_code_redemptions_promo` (`promo_code_id`),
                                          KEY `idx_promo_code_redemptions_user` (`user_id`),
                                          KEY `fk_promo_code_redemptions_membership` (`membership_subscription_id`),
                                          KEY `fk_promo_code_redemptions_booking` (`booking_id`),
                                          CONSTRAINT `fk_promo_code_redemptions_booking` FOREIGN KEY (`booking_id`) REFERENCES `bookings` (`id`),
                                          CONSTRAINT `fk_promo_code_redemptions_membership` FOREIGN KEY (`membership_subscription_id`) REFERENCES `membership_subscriptions` (`id`),
                                          CONSTRAINT `fk_promo_code_redemptions_promo` FOREIGN KEY (`promo_code_id`) REFERENCES `promo_codes` (`id`),
                                          CONSTRAINT `fk_promo_code_redemptions_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5303 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `promo_code_redemptions`
--

LOCK TABLES `promo_code_redemptions` WRITE;
/*!40000 ALTER TABLE `promo_code_redemptions` DISABLE KEYS */;
INSERT INTO `promo_code_redemptions` VALUES (5301,2,1001,4002,NULL,15000.00,'2026-04-17 09:03:00'),(5302,3,1001,NULL,6001,14850.00,'2026-04-01 00:00:00');
/*!40000 ALTER TABLE `promo_code_redemptions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `promo_codes`
--

DROP TABLE IF EXISTS `promo_codes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `promo_codes` (
                               `id` bigint unsigned NOT NULL AUTO_INCREMENT,
                               `code` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
                               `target_type` enum('BOOKING','MEMBERSHIP','BOTH') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'BOTH',
                               `discount_type` enum('FIXED_AMOUNT','PERCENTAGE') COLLATE utf8mb4_unicode_ci NOT NULL,
                               `discount_value` decimal(12,2) NOT NULL,
                               `max_discount_amount` decimal(12,2) DEFAULT NULL,
                               `min_order_amount` decimal(12,2) DEFAULT NULL,
                               `usage_limit_total` int DEFAULT NULL,
                               `usage_limit_per_user` int DEFAULT NULL,
                               `starts_at` datetime DEFAULT NULL,
                               `ends_at` datetime DEFAULT NULL,
                               `is_active` tinyint(1) NOT NULL DEFAULT '1',
                               `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                               `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                               PRIMARY KEY (`id`),
                               UNIQUE KEY `uk_promo_codes_code` (`code`),
                               KEY `idx_promo_codes_active_period` (`is_active`,`starts_at`,`ends_at`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `promo_codes`
--

LOCK TABLES `promo_codes` WRITE;
/*!40000 ALTER TABLE `promo_codes` DISABLE KEYS */;
INSERT INTO `promo_codes` VALUES (1,'PETGO20','BOTH','FIXED_AMOUNT',20000.00,NULL,0.00,10000,3,'2026-01-01 00:00:00','2027-12-31 23:59:59',1,'2026-04-15 23:28:24','2026-04-17 13:47:58'),(2,'WELCOME10','BOOKING','PERCENTAGE',10.00,50000.00,100000.00,10000,1,'2026-01-01 00:00:00','2027-12-31 23:59:59',1,'2026-04-17 13:47:58','2026-04-17 13:47:58'),(3,'MEMBER15','MEMBERSHIP','PERCENTAGE',15.00,50000.00,49000.00,10000,1,'2026-01-01 00:00:00','2027-12-31 23:59:59',1,'2026-04-17 13:47:58','2026-04-17 13:47:58');
/*!40000 ALTER TABLE `promo_codes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `provider_availability_slots`
--

DROP TABLE IF EXISTS `provider_availability_slots`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `provider_availability_slots` (
                                               `id` bigint unsigned NOT NULL AUTO_INCREMENT,
                                               `provider_id` bigint unsigned NOT NULL,
                                               `provider_service_id` bigint unsigned DEFAULT NULL,
                                               `slot_date` date NOT NULL,
                                               `start_time` time NOT NULL,
                                               `end_time` time NOT NULL,
                                               `slot_status` enum('AVAILABLE','BLOCKED','BOOKED','UNAVAILABLE') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'AVAILABLE',
                                               `capacity_total` int NOT NULL DEFAULT '1',
                                               `capacity_booked` int NOT NULL DEFAULT '0',
                                               `note` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                               `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                               `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                               PRIMARY KEY (`id`),
                                               UNIQUE KEY `uk_provider_availability_slot` (`provider_id`,`provider_service_id`,`slot_date`,`start_time`),
                                               KEY `idx_provider_availability_slot_lookup` (`provider_id`,`slot_date`,`slot_status`),
                                               KEY `idx_provider_availability_service` (`provider_service_id`),
                                               CONSTRAINT `fk_provider_availability_provider` FOREIGN KEY (`provider_id`) REFERENCES `provider_profiles` (`id`),
                                               CONSTRAINT `fk_provider_availability_provider_service` FOREIGN KEY (`provider_service_id`) REFERENCES `provider_services` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `provider_availability_slots`
--

LOCK TABLES `provider_availability_slots` WRITE;
/*!40000 ALTER TABLE `provider_availability_slots` DISABLE KEYS */;
INSERT INTO `provider_availability_slots` VALUES (1,1,1,'2026-05-20','09:00:00','10:00:00','AVAILABLE',1,0,NULL,'2026-05-20 01:35:05','2026-05-20 01:44:02');
/*!40000 ALTER TABLE `provider_availability_slots` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `provider_business_hours`
--

DROP TABLE IF EXISTS `provider_business_hours`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `provider_business_hours` (
                                           `id` bigint unsigned NOT NULL AUTO_INCREMENT,
                                           `provider_id` bigint unsigned NOT NULL,
                                           `weekday` tinyint NOT NULL COMMENT '1=Mon ... 7=Sun',
                                           `opens_at` time DEFAULT NULL,
                                           `closes_at` time DEFAULT NULL,
                                           `break_starts_at` time DEFAULT NULL,
                                           `break_ends_at` time DEFAULT NULL,
                                           `is_closed` tinyint(1) NOT NULL DEFAULT '0',
                                           `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                           `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                           PRIMARY KEY (`id`),
                                           UNIQUE KEY `uk_provider_business_hours` (`provider_id`,`weekday`),
                                           CONSTRAINT `fk_provider_business_hours_provider` FOREIGN KEY (`provider_id`) REFERENCES `provider_profiles` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `provider_business_hours`
--

LOCK TABLES `provider_business_hours` WRITE;
/*!40000 ALTER TABLE `provider_business_hours` DISABLE KEYS */;
/*!40000 ALTER TABLE `provider_business_hours` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `provider_photos`
--

DROP TABLE IF EXISTS `provider_photos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `provider_photos` (
                                   `id` bigint unsigned NOT NULL AUTO_INCREMENT,
                                   `provider_id` bigint unsigned NOT NULL,
                                   `photo_url` varchar(500) COLLATE utf8mb4_unicode_ci NOT NULL,
                                   `media_type` enum('IMAGE','VIDEO') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'IMAGE',
                                   `is_primary` tinyint(1) NOT NULL DEFAULT '0',
                                   `sort_order` int NOT NULL DEFAULT '0',
                                   `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                   `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                   PRIMARY KEY (`id`),
                                   KEY `idx_provider_photos_provider` (`provider_id`),
                                   KEY `idx_provider_photos_primary` (`provider_id`,`is_primary`),
                                   CONSTRAINT `fk_provider_photos_provider` FOREIGN KEY (`provider_id`) REFERENCES `provider_profiles` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `provider_photos`
--

LOCK TABLES `provider_photos` WRITE;
/*!40000 ALTER TABLE `provider_photos` DISABLE KEYS */;
/*!40000 ALTER TABLE `provider_photos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `provider_profiles`
--

DROP TABLE IF EXISTS `provider_profiles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `provider_profiles` (
                                     `id` bigint unsigned NOT NULL AUTO_INCREMENT,
                                     `provider_code` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL,
                                     `user_id` bigint unsigned NOT NULL,
                                     `business_name` varchar(180) COLLATE utf8mb4_unicode_ci NOT NULL,
                                     `slug` varchar(190) COLLATE utf8mb4_unicode_ci NOT NULL,
                                     `provider_type` enum('INDIVIDUAL','BUSINESS','CLINIC','SPA','BOARDING','TRAINING_CENTER','WALKER','OTHER') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'BUSINESS',
                                     `headline` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                     `description` text COLLATE utf8mb4_unicode_ci,
                                     `years_experience` int DEFAULT NULL,
                                     `verification_status` enum('PENDING','VERIFIED','REJECTED') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'PENDING',
                                     `is_featured` tinyint(1) NOT NULL DEFAULT '0',
                                     `is_hot` tinyint(1) NOT NULL DEFAULT '0',
                                     `accepts_instant_booking` tinyint(1) NOT NULL DEFAULT '1',
                                     `accepts_membership` tinyint(1) NOT NULL DEFAULT '1',
                                     `average_rating` decimal(3,2) NOT NULL DEFAULT '0.00',
                                     `total_reviews` int NOT NULL DEFAULT '0',
                                     `total_completed_bookings` int NOT NULL DEFAULT '0',
                                     `service_radius_km` decimal(6,2) DEFAULT NULL,
                                     `cancellation_free_hours` int NOT NULL DEFAULT '24',
                                     `emergency_phone` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                     `primary_address_line1` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                     `primary_address_line2` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                     `ward` varchar(120) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                     `district` varchar(120) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                     `city` varchar(120) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                     `province` varchar(120) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                     `country_code` char(2) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'VN',
                                     `latitude` decimal(10,7) DEFAULT NULL,
                                     `longitude` decimal(10,7) DEFAULT NULL,
                                     `main_image_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                     `cover_image_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                     `price_from_amount` decimal(12,2) DEFAULT NULL,
                                     `currency_code` char(3) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'VND',
                                     `status` enum('DRAFT','ACTIVE','INACTIVE','SUSPENDED') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'ACTIVE',
                                     `deleted_at` datetime DEFAULT NULL,
                                     `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                     `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                     PRIMARY KEY (`id`),
                                     UNIQUE KEY `uk_provider_profiles_provider_code` (`provider_code`),
                                     UNIQUE KEY `uk_provider_profiles_user` (`user_id`),
                                     UNIQUE KEY `uk_provider_profiles_slug` (`slug`),
                                     KEY `idx_provider_profiles_status` (`status`),
                                     KEY `idx_provider_profiles_city` (`city`),
                                     KEY `idx_provider_profiles_featured` (`is_featured`),
                                     KEY `idx_provider_profiles_rating` (`average_rating`),
                                     CONSTRAINT `fk_provider_profiles_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `provider_profiles`
--

LOCK TABLES `provider_profiles` WRITE;
/*!40000 ALTER TABLE `provider_profiles` DISABLE KEYS */;
INSERT INTO `provider_profiles` VALUES (1,'P001',2,'Happy Pets Spa','happy-pets-spa','SPA',NULL,NULL,NULL,'PENDING',0,0,1,1,0.00,0,0,NULL,24,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'VN',NULL,NULL,NULL,NULL,NULL,'VND','ACTIVE',NULL,'2026-05-20 01:35:05','2026-05-20 01:35:05');
/*!40000 ALTER TABLE `provider_profiles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `provider_services`
--

DROP TABLE IF EXISTS `provider_services`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `provider_services` (
                                     `id` bigint unsigned NOT NULL AUTO_INCREMENT,
                                     `provider_id` bigint unsigned NOT NULL,
                                     `service_id` bigint unsigned NOT NULL,
                                     `custom_name` varchar(150) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                     `short_description` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                     `description` text COLLATE utf8mb4_unicode_ci,
                                     `duration_minutes` int NOT NULL,
                                     `price_amount` decimal(12,2) NOT NULL,
                                     `currency_code` char(3) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'VND',
                                     `price_unit` enum('PER_SESSION','PER_HOUR','PER_DAY','PER_VISIT') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'PER_SESSION',
                                     `is_featured` tinyint(1) NOT NULL DEFAULT '0',
                                     `is_active` tinyint(1) NOT NULL DEFAULT '1',
                                     `capacity_per_slot` int NOT NULL DEFAULT '1',
                                     `booking_buffer_minutes` int NOT NULL DEFAULT '0',
                                     `display_order` int NOT NULL DEFAULT '0',
                                     `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                     `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                     PRIMARY KEY (`id`),
                                     UNIQUE KEY `uk_provider_services_provider_service_name` (`provider_id`,`service_id`,`custom_name`),
                                     KEY `idx_provider_services_provider` (`provider_id`),
                                     KEY `idx_provider_services_service` (`service_id`),
                                     KEY `idx_provider_services_featured` (`provider_id`,`is_featured`),
                                     CONSTRAINT `fk_provider_services_provider` FOREIGN KEY (`provider_id`) REFERENCES `provider_profiles` (`id`),
                                     CONSTRAINT `fk_provider_services_service` FOREIGN KEY (`service_id`) REFERENCES `services` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `provider_services`
--

LOCK TABLES `provider_services` WRITE;
/*!40000 ALTER TABLE `provider_services` DISABLE KEYS */;
INSERT INTO `provider_services` VALUES (1,1,1,NULL,NULL,NULL,60,200000.00,'VND','PER_SESSION',0,1,1,0,0,'2026-05-20 01:35:05','2026-05-20 01:35:05');
/*!40000 ALTER TABLE `provider_services` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `provider_unavailable_dates`
--

DROP TABLE IF EXISTS `provider_unavailable_dates`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `provider_unavailable_dates` (
                                              `id` bigint unsigned NOT NULL AUTO_INCREMENT,
                                              `provider_id` bigint unsigned NOT NULL,
                                              `unavailable_date` date NOT NULL,
                                              `reason` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                              `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                              `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                              PRIMARY KEY (`id`),
                                              UNIQUE KEY `uk_provider_unavailable_dates` (`provider_id`,`unavailable_date`),
                                              CONSTRAINT `fk_provider_unavailable_dates_provider` FOREIGN KEY (`provider_id`) REFERENCES `provider_profiles` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `provider_unavailable_dates`
--

LOCK TABLES `provider_unavailable_dates` WRITE;
/*!40000 ALTER TABLE `provider_unavailable_dates` DISABLE KEYS */;
/*!40000 ALTER TABLE `provider_unavailable_dates` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `refresh_tokens`
--

DROP TABLE IF EXISTS `refresh_tokens`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `refresh_tokens` (
                                  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
                                  `user_id` bigint unsigned NOT NULL,
                                  `token_hash` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
                                  `expires_at` datetime NOT NULL,
                                  `revoked_at` datetime DEFAULT NULL,
                                  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                  PRIMARY KEY (`id`),
                                  UNIQUE KEY `uk_refresh_tokens_token_hash` (`token_hash`),
                                  KEY `idx_refresh_tokens_user` (`user_id`),
                                  KEY `idx_refresh_tokens_expires` (`expires_at`),
                                  CONSTRAINT `fk_refresh_tokens_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `refresh_tokens`
--

LOCK TABLES `refresh_tokens` WRITE;
/*!40000 ALTER TABLE `refresh_tokens` DISABLE KEYS */;
INSERT INTO `refresh_tokens` VALUES (1,1007,'6c25b8c6efef5c7bcb4a1fb9e964bce48d7de231daa04bcc1135f8057ef309db','2026-05-17 13:49:51',NULL,'2026-04-17 13:49:51'),(2,1008,'9e2551372a477c7fc6038207ce2b6c8d8b4375b72e5c23b4b875efb4bcf43ed8','2026-05-17 14:20:09','2026-04-17 19:35:47','2026-04-17 14:20:09'),(3,1008,'1ef49e16a4b9dae18d1946c0b3976a18113d785bf9dff51fcd378a26f352264d','2026-05-17 19:35:47',NULL,'2026-04-17 19:35:47'),(7,1008,'d13fbe24e24bbc8adec5ae4bfe25d92258ff753edb94780d7c380fc32cadcbda','2026-06-10 15:17:12',NULL,'2026-05-11 15:17:12'),(8,1008,'9001079a616af2da19ed5535ff8105bb527c432be035024c4d8454c3271b3bd8','2026-06-10 15:29:01','2026-05-12 22:42:23','2026-05-11 15:29:01'),(9,1008,'ff6df4c645de660a092e05981570d3045340e1fdf00efd08b5aa1899b9fd8127','2026-06-11 22:42:22',NULL,'2026-05-12 22:42:22'),(11,1008,'b0e169ab167aba832bdcdce39a1b63020edb66c9f66d5f84d1f38b2b9c21aa35','2026-06-11 22:42:26',NULL,'2026-05-12 22:42:26'),(12,1008,'08ea23fd26e81e607ba687729933fa013f66f8f2264eb87f8b06f091c04514c5','2026-06-12 00:01:07',NULL,'2026-05-13 00:01:07'),(13,1010,'416c1c9ce292d70d4edc190d696cc84818ceaf22011fad5a8ae02e1505304a49','2026-06-12 13:34:25','2026-05-19 23:49:38','2026-05-13 13:34:25'),(15,1010,'a729adc360ca49e21ddbbe03cb745ad73975e7ec5b06cc23ef9ad6eb49e6706c','2026-06-18 23:49:37',NULL,'2026-05-19 23:49:37'),(16,1010,'ef796aa10a73345b763fb64bd67bda7d0e7a3203fdb2f9d2e54cf82bb3a21375','2026-06-18 23:49:41',NULL,'2026-05-19 23:49:41'),(17,3,'576abf2bb6294adf95a3160b34bea5cc13a07c7833b9179af1ba9e1346bc61d2','2026-06-19 01:38:11','2026-05-25 11:54:14','2026-05-20 01:38:11'),(18,4,'1cc694c7346a32fd19e7f7e5156acaace008b2cfb7eb666367a0e9b0ea098f03','2026-06-24 11:19:54',NULL,'2026-05-25 11:19:54'),(19,3,'32adf42a2f35a7d6084e965add65480ea461130f0f7cac06af3cb9b842eb3013','2026-06-24 11:54:13',NULL,'2026-05-25 11:54:13'),(21,3,'62a7726440e4f487293e5f47494c3cf6fa6ade5e969dfe1b63af22b80481d5b0','2026-06-24 11:54:20','2026-05-25 18:47:18','2026-05-25 11:54:20'),(22,3,'a4d07a1e2c519cea6925af49be3c0b3e4576dcaf3f141a856c8e70b9cd37c462','2026-06-24 18:47:17',NULL,'2026-05-25 18:47:17'),(24,3,'39a3c15f7222cb7cb80bdc3893437ca7a517a52e91e218fea4635aa0a6d49f8b','2026-06-24 18:47:25',NULL,'2026-05-25 18:47:25');
/*!40000 ALTER TABLE `refresh_tokens` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `review_photos`
--

DROP TABLE IF EXISTS `review_photos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `review_photos` (
                                 `id` bigint unsigned NOT NULL AUTO_INCREMENT,
                                 `review_id` bigint unsigned NOT NULL,
                                 `photo_url` varchar(500) COLLATE utf8mb4_unicode_ci NOT NULL,
                                 `sort_order` int NOT NULL DEFAULT '0',
                                 `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                 PRIMARY KEY (`id`),
                                 KEY `idx_review_photos_review` (`review_id`),
                                 CONSTRAINT `fk_review_photos_review` FOREIGN KEY (`review_id`) REFERENCES `reviews` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4602 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `review_photos`
--

LOCK TABLES `review_photos` WRITE;
/*!40000 ALTER TABLE `review_photos` DISABLE KEYS */;
INSERT INTO `review_photos` VALUES (4601,4501,'https://placehold.co/1000x700?text=Review+Mochi',1,'2026-04-17 13:47:58');
/*!40000 ALTER TABLE `review_photos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reviews`
--

DROP TABLE IF EXISTS `reviews`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reviews` (
                           `id` bigint unsigned NOT NULL AUTO_INCREMENT,
                           `booking_id` bigint unsigned NOT NULL,
                           `customer_user_id` bigint unsigned NOT NULL,
                           `provider_id` bigint unsigned NOT NULL,
                           `rating` tinyint NOT NULL,
                           `comment` text COLLATE utf8mb4_unicode_ci,
                           `status` enum('VISIBLE','HIDDEN','REPORTED') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'VISIBLE',
                           `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                           `deleted_at` datetime DEFAULT NULL,
                           PRIMARY KEY (`id`),
                           UNIQUE KEY `uk_reviews_booking` (`booking_id`),
                           KEY `idx_reviews_provider` (`provider_id`,`status`),
                           KEY `idx_reviews_customer` (`customer_user_id`),
                           CONSTRAINT `fk_reviews_booking` FOREIGN KEY (`booking_id`) REFERENCES `bookings` (`id`),
                           CONSTRAINT `fk_reviews_customer` FOREIGN KEY (`customer_user_id`) REFERENCES `users` (`id`),
                           CONSTRAINT `fk_reviews_provider` FOREIGN KEY (`provider_id`) REFERENCES `provider_profiles` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4503 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reviews`
--

LOCK TABLES `reviews` WRITE;
/*!40000 ALTER TABLE `reviews` DISABLE KEYS */;
INSERT INTO `reviews` VALUES (4501,4001,1001,2001,5,'Nhân viên rất nhẹ nhàng, cắt tỉa đẹp và giao thú cưng đúng giờ.','VISIBLE','2026-04-17 13:47:58','2026-04-17 13:47:58',NULL),(4502,4005,1002,2002,4,'Bác sĩ tư vấn kỹ, phòng khám sạch sẽ, sẽ quay lại.','VISIBLE','2026-04-17 13:47:58','2026-04-17 13:47:58',NULL);
/*!40000 ALTER TABLE `reviews` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `roles`
--

DROP TABLE IF EXISTS `roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `roles` (
                         `id` bigint unsigned NOT NULL AUTO_INCREMENT,
                         `code` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
                         `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
                         `description` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                         `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                         PRIMARY KEY (`id`),
                         UNIQUE KEY `uk_roles_code` (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `roles`
--

LOCK TABLES `roles` WRITE;
/*!40000 ALTER TABLE `roles` DISABLE KEYS */;
INSERT INTO `roles` VALUES (1,'CUSTOMER','Customer / Pet Owner','Người dùng đặt dịch vụ cho thú cưng','2026-04-15 23:28:24','2026-04-15 23:28:24'),(2,'PROVIDER','Caregiver / Provider','Người cung cấp dịch vụ chăm sóc thú cưng','2026-04-15 23:28:24','2026-04-15 23:28:24'),(3,'ADMIN','Administrator','Quản trị hệ thống','2026-04-15 23:28:24','2026-04-15 23:28:24');
/*!40000 ALTER TABLE `roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `service_categories`
--

DROP TABLE IF EXISTS `service_categories`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `service_categories` (
                                      `id` bigint unsigned NOT NULL AUTO_INCREMENT,
                                      `parent_id` bigint unsigned DEFAULT NULL,
                                      `name` varchar(120) COLLATE utf8mb4_unicode_ci NOT NULL,
                                      `slug` varchar(120) COLLATE utf8mb4_unicode_ci NOT NULL,
                                      `icon_key` varchar(80) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                      `description` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                      `sort_order` int NOT NULL DEFAULT '0',
                                      `is_active` tinyint(1) NOT NULL DEFAULT '1',
                                      `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                      `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                      PRIMARY KEY (`id`),
                                      UNIQUE KEY `uk_service_categories_slug` (`slug`),
                                      KEY `idx_service_categories_parent` (`parent_id`),
                                      CONSTRAINT `fk_service_categories_parent` FOREIGN KEY (`parent_id`) REFERENCES `service_categories` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `service_categories`
--

LOCK TABLES `service_categories` WRITE;
/*!40000 ALTER TABLE `service_categories` DISABLE KEYS */;
INSERT INTO `service_categories` VALUES (1,NULL,'Pet Spa','spa','paw-print','Dịch vụ spa cho thú cưng',1,1,'2026-04-15 23:28:24','2026-04-15 23:28:24'),(2,NULL,'Grooming','grooming','scissors','Tắm, cắt tỉa, vệ sinh',2,1,'2026-04-15 23:28:24','2026-04-15 23:28:24'),(3,NULL,'Veterinary','clinic','stethoscope','Khám, tư vấn, tiêm phòng',3,1,'2026-04-15 23:28:24','2026-04-15 23:28:24'),(4,NULL,'Pet Boarding','boarding','hotel','Lưu trú và chăm sóc theo ngày',4,1,'2026-04-15 23:28:24','2026-04-15 23:28:24'),(5,NULL,'Pet Training','training','award','Huấn luyện cơ bản và nâng cao',5,1,'2026-04-15 23:28:24','2026-04-15 23:28:24'),(6,NULL,'Pet Walking','walking','navigation','Dắt đi dạo và vận động',6,1,'2026-04-15 23:28:24','2026-04-15 23:28:24'),(8,NULL,'Spa & Massage','spa-massage','icon-spa','Các dịch vụ spa và massage',1,1,'2026-05-20 01:08:27','2026-05-20 01:08:27'),(9,NULL,'Hair & Beauty','hair-beauty','icon-hair','Dịch vụ làm tóc và làm đẹp',2,1,'2026-05-20 01:08:27','2026-05-20 01:08:27');
/*!40000 ALTER TABLE `service_categories` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `services`
--

DROP TABLE IF EXISTS `services`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `services` (
                            `id` bigint unsigned NOT NULL AUTO_INCREMENT,
                            `service_code` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL,
                            `category_id` bigint unsigned NOT NULL,
                            `name` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
                            `slug` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
                            `short_description` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                            `description` text COLLATE utf8mb4_unicode_ci,
                            `default_duration_minutes` int NOT NULL,
                            `base_price_amount` decimal(12,2) DEFAULT NULL,
                            `currency_code` char(3) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'VND',
                            `price_unit` enum('PER_SESSION','PER_HOUR','PER_DAY','PER_VISIT') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'PER_SESSION',
                            `requires_consultation` tinyint(1) NOT NULL DEFAULT '0',
                            `is_active` tinyint(1) NOT NULL DEFAULT '1',
                            `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                            PRIMARY KEY (`id`),
                            UNIQUE KEY `uk_services_service_code` (`service_code`),
                            UNIQUE KEY `uk_services_slug` (`slug`),
                            KEY `idx_services_category` (`category_id`),
                            KEY `idx_services_active` (`is_active`),
                            CONSTRAINT `fk_services_category` FOREIGN KEY (`category_id`) REFERENCES `service_categories` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `services`
--

LOCK TABLES `services` WRITE;
/*!40000 ALTER TABLE `services` DISABLE KEYS */;
INSERT INTO `services` VALUES (1,'SVC-SPA-BATH',1,'Gói Tắm Thư Giãn','relax-bath','Tắm bằng nước ấm, sấy và chải lông','Gói spa cơ bản cho thú cưng',45,200000.00,'VND','PER_SESSION',0,1,'2026-04-15 23:28:24','2026-04-15 23:28:24'),(2,'SVC-GROOM-STYLE',2,'Cắt Tỉa Tạo Kiểu','groom-style','Cắt tỉa và tạo kiểu lông','Dịch vụ grooming nâng cao',90,350000.00,'VND','PER_SESSION',0,1,'2026-04-15 23:28:24','2026-04-15 23:28:24'),(3,'SVC-VET-CHECKUP',3,'Khám Tổng Quát','general-checkup','Khám cơ bản cho thú cưng','Khám sức khỏe tổng quát',30,150000.00,'VND','PER_VISIT',1,1,'2026-04-15 23:28:24','2026-04-17 13:47:58'),(7,'SVC-BOARD-DAYCARE',4,'Gửi Thú Cưng Ban Ngày','daycare-boarding','Chăm sóc và lưu trú trong ngày','Phù hợp khi chủ bận công việc',480,250000.00,'VND','PER_DAY',0,1,'2026-04-17 13:47:58','2026-04-17 13:47:58'),(8,'SVC-WALK-30',6,'Dắt Đi Dạo 30 Phút','walk-30','Đi dạo 30 phút quanh khu vực','Dành cho thú cưng cần vận động nhẹ mỗi ngày',30,90000.00,'VND','PER_VISIT',0,1,'2026-04-17 13:47:58','2026-04-17 13:47:58'),(9,'SVC-TRAIN-BASIC',5,'Huấn Luyện Cơ Bản','basic-training','Lệnh cơ bản và xử lý hành vi','Khóa huấn luyện cơ bản cho chó mèo',60,300000.00,'VND','PER_SESSION',1,1,'2026-04-17 13:47:58','2026-04-17 13:47:58'),(10,'SPA001',1,'Full Body Massage','full-body-massage','Massage toàn thân 60 phút','Massage thư giãn toàn thân, sử dụng tinh dầu thiên nhiên',60,500000.00,'VND','PER_SESSION',0,1,'2026-05-20 01:08:27','2026-05-20 01:08:27'),(11,'HAIR001',2,'Haircut Men','haircut-men','Cắt tóc nam','Dịch vụ cắt tóc nam chuyên nghiệp',30,150000.00,'VND','PER_SESSION',0,1,'2026-05-20 01:08:27','2026-05-20 01:08:27');
/*!40000 ALTER TABLE `services` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `shop_order_items`
--

DROP TABLE IF EXISTS `shop_order_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `shop_order_items` (
                                    `id` bigint unsigned NOT NULL AUTO_INCREMENT,
                                    `shop_order_id` bigint unsigned NOT NULL,
                                    `product_id` bigint unsigned NOT NULL,
                                    `product_name_snapshot` varchar(180) COLLATE utf8mb4_unicode_ci NOT NULL,
                                    `product_sku_snapshot` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                    `product_image_snapshot` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                    `quantity` int NOT NULL,
                                    `unit_price` decimal(12,2) NOT NULL,
                                    `line_total` decimal(12,2) NOT NULL,
                                    `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                    PRIMARY KEY (`id`),
                                    KEY `idx_shop_order_items_order` (`shop_order_id`),
                                    KEY `idx_shop_order_items_product` (`product_id`),
                                    CONSTRAINT `fk_shop_order_items_order` FOREIGN KEY (`shop_order_id`) REFERENCES `shop_orders` (`id`),
                                    CONSTRAINT `fk_shop_order_items_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `shop_order_items`
--

LOCK TABLES `shop_order_items` WRITE;
/*!40000 ALTER TABLE `shop_order_items` DISABLE KEYS */;
INSERT INTO `shop_order_items` VALUES (1,1,1,'Hạt Royal Canin Mini Puppy Cho Chó Con',NULL,'https://images.unsplash.com/photo-1589924691995-400dc9ecc119?w=600&auto=format&fit=crop&q=80',2,235000.00,470000.00,'2026-05-25 10:43:26'),(2,2,2,'Pate Cho Mèo Trưởng Thành Whiskas Vị Cá Thu',NULL,'https://images.unsplash.com/photo-1591871937573-74dbba515c4c?w=600&auto=format&fit=crop&q=80',1,45000.00,45000.00,'2026-05-25 10:50:16'),(3,3,4,'Balo Phi Hành Gia Vận Chuyển Thú Cưng',NULL,'https://images.unsplash.com/photo-1517849845537-4d257902454a?w=600&auto=format&fit=crop&q=80',1,290000.00,290000.00,'2026-05-25 10:53:55');
/*!40000 ALTER TABLE `shop_order_items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `shop_order_status_history`
--

DROP TABLE IF EXISTS `shop_order_status_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `shop_order_status_history` (
                                             `id` bigint unsigned NOT NULL AUTO_INCREMENT,
                                             `shop_order_id` bigint unsigned NOT NULL,
                                             `from_status` enum('PENDING_PAYMENT','PAID','PACKING','SHIPPING','COMPLETED','CANCELLED','REFUNDED') COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                             `to_status` enum('PENDING_PAYMENT','PAID','PACKING','SHIPPING','COMPLETED','CANCELLED','REFUNDED') COLLATE utf8mb4_unicode_ci NOT NULL,
                                             `changed_by_user_id` bigint unsigned DEFAULT NULL,
                                             `note` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                             `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                             PRIMARY KEY (`id`),
                                             KEY `idx_shop_order_status_history_order` (`shop_order_id`,`created_at`),
                                             KEY `idx_shop_order_status_history_user` (`changed_by_user_id`),
                                             CONSTRAINT `fk_shop_order_status_history_order` FOREIGN KEY (`shop_order_id`) REFERENCES `shop_orders` (`id`),
                                             CONSTRAINT `fk_shop_order_status_history_user` FOREIGN KEY (`changed_by_user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `shop_order_status_history`
--

LOCK TABLES `shop_order_status_history` WRITE;
/*!40000 ALTER TABLE `shop_order_status_history` DISABLE KEYS */;
/*!40000 ALTER TABLE `shop_order_status_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `shop_orders`
--

DROP TABLE IF EXISTS `shop_orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `shop_orders` (
                               `id` bigint unsigned NOT NULL AUTO_INCREMENT,
                               `order_code` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL,
                               `customer_user_id` bigint unsigned NOT NULL,
                               `status` enum('PENDING_PAYMENT','PAID','PACKING','SHIPPING','COMPLETED','CANCELLED','REFUNDED') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'PENDING_PAYMENT',
                               `receiver_name` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
                               `receiver_phone` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL,
                               `shipping_address` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
                               `ward` varchar(120) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                               `district` varchar(120) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                               `city` varchar(120) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                               `province` varchar(120) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                               `country_code` char(2) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'VN',
                               `customer_note` text COLLATE utf8mb4_unicode_ci,
                               `internal_note` text COLLATE utf8mb4_unicode_ci,
                               `subtotal_amount` decimal(12,2) NOT NULL,
                               `shipping_fee_amount` decimal(12,2) NOT NULL DEFAULT '0.00',
                               `discount_amount` decimal(12,2) NOT NULL DEFAULT '0.00',
                               `tax_amount` decimal(12,2) NOT NULL DEFAULT '0.00',
                               `total_amount` decimal(12,2) NOT NULL,
                               `currency_code` char(3) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'VND',
                               `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                               `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                               PRIMARY KEY (`id`),
                               UNIQUE KEY `uk_shop_orders_order_code` (`order_code`),
                               KEY `idx_shop_orders_customer` (`customer_user_id`,`status`),
                               KEY `idx_shop_orders_status` (`status`),
                               CONSTRAINT `fk_shop_orders_customer` FOREIGN KEY (`customer_user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `shop_orders`
--

LOCK TABLES `shop_orders` WRITE;
/*!40000 ALTER TABLE `shop_orders` DISABLE KEYS */;
INSERT INTO `shop_orders` VALUES (1,'PGO-26052510432684',1,'PACKING','Nguyễn Văn Anh','0912345678','Số 123 Đường Nguyễn Trãi','Phường Thanh Xuân Trung','Thanh Xuân','Hà Nội',NULL,'VN','',NULL,470000.00,0.00,0.00,0.00,470000.00,'VND','2026-05-25 10:43:26','2026-05-25 10:43:26'),(2,'PGO-26052510501610',1,'PACKING','Nguyễn Văn Anh','0912345678','Số 123 Đường Nguyễn Trãi','Phường Thanh Xuân Trung','Thanh Xuân','Hà Nội',NULL,'VN','',NULL,45000.00,30000.00,0.00,0.00,75000.00,'VND','2026-05-25 10:50:16','2026-05-25 10:50:16'),(3,'PGO-26052510535477',1,'PACKING','Nguyễn Văn Anh','0912345678','Số 123 Đường Nguyễn Trãi','Phường Thanh Xuân Trung','Thanh Xuân','Hà Nội',NULL,'VN','',NULL,290000.00,30000.00,0.00,0.00,320000.00,'VND','2026-05-25 10:53:55','2026-05-25 10:53:55');
/*!40000 ALTER TABLE `shop_orders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_notification_settings`
--

DROP TABLE IF EXISTS `user_notification_settings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_notification_settings` (
                                              `user_id` bigint unsigned NOT NULL,
                                              `email_booking_updates` tinyint(1) NOT NULL DEFAULT '1',
                                              `email_promotions` tinyint(1) NOT NULL DEFAULT '1',
                                              `push_booking_updates` tinyint(1) NOT NULL DEFAULT '1',
                                              `push_reminders` tinyint(1) NOT NULL DEFAULT '1',
                                              `sms_booking_updates` tinyint(1) NOT NULL DEFAULT '0',
                                              `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                              `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                              PRIMARY KEY (`user_id`),
                                              CONSTRAINT `fk_user_notification_settings_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_notification_settings`
--

LOCK TABLES `user_notification_settings` WRITE;
/*!40000 ALTER TABLE `user_notification_settings` DISABLE KEYS */;
INSERT INTO `user_notification_settings` VALUES (1001,1,1,1,1,0,'2026-04-17 13:47:58','2026-04-17 13:47:58'),(1002,1,0,1,1,0,'2026-04-17 13:47:58','2026-04-17 13:47:58'),(1003,1,0,1,1,0,'2026-04-17 13:47:58','2026-04-17 13:47:58'),(1004,1,0,1,1,0,'2026-04-17 13:47:58','2026-04-17 13:47:58'),(1005,1,0,1,1,0,'2026-04-17 13:47:58','2026-04-17 13:47:58'),(1006,1,0,1,1,0,'2026-04-17 13:47:58','2026-04-17 13:47:58'),(1007,1,0,1,1,0,'2026-04-17 13:47:58','2026-04-17 13:47:58');
/*!40000 ALTER TABLE `user_notification_settings` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_roles`
--

DROP TABLE IF EXISTS `user_roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_roles` (
                              `user_id` bigint unsigned NOT NULL,
                              `role_id` bigint unsigned NOT NULL,
                              `assigned_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                              PRIMARY KEY (`user_id`,`role_id`),
                              KEY `fk_user_roles_role` (`role_id`),
                              CONSTRAINT `fk_user_roles_role` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`),
                              CONSTRAINT `fk_user_roles_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_roles`
--

LOCK TABLES `user_roles` WRITE;
/*!40000 ALTER TABLE `user_roles` DISABLE KEYS */;
INSERT INTO `user_roles` VALUES (3,1,'2026-05-20 01:37:49'),(4,1,'2026-05-25 11:19:37'),(1001,1,'2026-04-17 13:47:58'),(1002,1,'2026-04-17 13:47:58'),(1003,2,'2026-04-17 13:47:58'),(1004,2,'2026-04-17 13:47:58'),(1005,2,'2026-04-17 13:47:58'),(1006,2,'2026-04-17 13:47:58'),(1007,3,'2026-04-17 13:47:58'),(1008,1,'2026-04-17 14:20:03'),(1009,1,'2026-05-13 13:32:42'),(1010,1,'2026-05-13 13:33:31');
/*!40000 ALTER TABLE `user_roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
                         `id` bigint unsigned NOT NULL AUTO_INCREMENT,
                         `user_code` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL,
                         `email` varchar(190) COLLATE utf8mb4_unicode_ci NOT NULL,
                         `password_hash` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
                         `full_name` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
                         `phone_number` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                         `avatar_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                         `cover_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                         `gender` enum('MALE','FEMALE','OTHER','PREFER_NOT_TO_SAY') COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                         `date_of_birth` date DEFAULT NULL,
                         `address_line1` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                         `address_line2` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                         `ward` varchar(120) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                         `district` varchar(120) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                         `city` varchar(120) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                         `province` varchar(120) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                         `country_code` char(2) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'VN',
                         `latitude` decimal(10,7) DEFAULT NULL,
                         `longitude` decimal(10,7) DEFAULT NULL,
                         `email_verified_at` datetime DEFAULT NULL,
                         `phone_verified_at` datetime DEFAULT NULL,
                         `status` enum('ACTIVE','INACTIVE','SUSPENDED','PENDING_VERIFICATION') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'ACTIVE',
                         `last_login_at` datetime DEFAULT NULL,
                         `deleted_at` datetime DEFAULT NULL,
                         `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                         `otp_code` varchar(6) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                         `otp_expiry_time` datetime DEFAULT NULL,
                         PRIMARY KEY (`id`),
                         UNIQUE KEY `uk_users_user_code` (`user_code`),
                         UNIQUE KEY `uk_users_email` (`email`),
                         UNIQUE KEY `uk_users_phone` (`phone_number`),
                         KEY `idx_users_status` (`status`),
                         KEY `idx_users_city` (`city`),
                         KEY `idx_users_deleted_at` (`deleted_at`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'U001','customer1@example.com','hashedpassword','Nguyen Van A',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'VN',NULL,NULL,NULL,NULL,'ACTIVE',NULL,NULL,'2026-05-20 01:35:05','2026-05-20 01:35:05',NULL,NULL),(2,'U002','provider1@example.com','hashedpassword','Tran Thi B',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'VN',NULL,NULL,NULL,NULL,'ACTIVE',NULL,NULL,'2026-05-20 01:35:05','2026-05-20 01:35:05',NULL,NULL),(3,'USR-34E530EDFB','phucnguyenxt2004@gmail.com','$2a$10$EFADIZSODtzHjA.11FLeLuZQSgBpOEDitrKtQFOudsTqU0PQBxLB2','Nguyên Cao Phúc','12345678','https://images.unsplash.com/photo-1517841905240-472988babdf9?auto=format&fit=crop&q=80&w=300','https://images.unsplash.com/photo-1548199973-03cce0bbc87b?auto=format&fit=crop&q=80&w=1600',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'VN',NULL,NULL,'2026-05-20 01:38:09',NULL,'ACTIVE','2026-05-25 18:47:25',NULL,'2026-05-20 01:37:45','2026-05-25 18:47:25',NULL,NULL),(4,'USR-FB15892FEF','nguyencphe181659@fpt.edu.vn','$2a$10$4if516mX8A7s3iBoldFjueUmo6X/NouuH/TvGY8KdaUtoMjGfiCYK','QP0212 Cao Phuc Nguyen','123456789','https://images.unsplash.com/photo-1517841905240-472988babdf9?auto=format&fit=crop&q=80&w=300','https://images.unsplash.com/photo-1548199973-03cce0bbc87b?auto=format&fit=crop&q=80&w=1600',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'VN',NULL,NULL,'2026-05-25 11:19:52',NULL,'ACTIVE','2026-05-25 11:19:55',NULL,'2026-05-25 11:19:32','2026-05-25 11:19:54',NULL,NULL);
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-05-29 22:54:12
