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
                        otp_code VARCHAR(10) NULL,
                        otp_expiry_time DATETIME NULL,
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
                                    description VARCHAR(255) NULL,
                                    is_active BOOLEAN NOT NULL DEFAULT TRUE,
                                    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                    PRIMARY KEY (id),
                                    KEY idx_service_categories_parent (parent_id),
                                    KEY idx_service_categories_active (is_active),
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
-- 11) REGISTRATION APPLICATIONS
-- =========================

CREATE TABLE registration_applications (
                                           id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                                           user_id BIGINT UNSIGNED NOT NULL,
                                           type ENUM('PARTNER','AFFILIATE') NOT NULL,
                                           status ENUM('DRAFT','AWAITING_APPROVAL','NEEDS_MORE_INFO','APPROVED','REJECTED') NOT NULL DEFAULT 'DRAFT',
                                           business_name VARCHAR(255) NOT NULL,
                                           business_phone VARCHAR(50) NOT NULL,
                                           business_email VARCHAR(255) NOT NULL,
                                           business_address VARCHAR(500) NOT NULL,
                                           tax_code VARCHAR(100) NULL,
                                           representative_name VARCHAR(255) NOT NULL,
                                           representative_phone VARCHAR(50) NOT NULL,
                                           representative_email VARCHAR(255) NOT NULL,
                                           service_category_ids VARCHAR(500) NULL,
                                           location_image_urls TEXT NULL,
                                           description TEXT NULL,
                                           additional_information TEXT NULL,
                                           admin_message TEXT NULL,
                                           rejection_reason TEXT NULL,
                                           submitted_at DATETIME NULL,
                                           reviewed_at DATETIME NULL,
                                           reviewer_id BIGINT UNSIGNED NULL,
                                           created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                           updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                           PRIMARY KEY (id),
                                           UNIQUE KEY uk_registration_user_type (user_id, type),
                                           KEY idx_registration_type_status (type, status),
                                           KEY idx_registration_submitted_at (submitted_at),
                                           KEY idx_registration_reviewer (reviewer_id),
                                           CONSTRAINT fk_registration_user FOREIGN KEY (user_id) REFERENCES users(id),
                                           CONSTRAINT fk_registration_reviewer FOREIGN KEY (reviewer_id) REFERENCES users(id)
) ENGINE=InnoDB;

-- =========================
-- 12) SEED DATA (minimal)
-- =========================

INSERT INTO roles (code, name, description) VALUES
                                                ('USER', 'User', 'Người dùng hệ thống'),
                                                ('SHOP', 'Shop', 'Shop/đối tác cung cấp dịch vụ'),
                                                ('ADMIN', 'Administrator', 'Quản trị hệ thống');

INSERT INTO service_categories (name, description) VALUES
                                                       ('Làm đẹp & vệ sinh', 'Nhóm dịch vụ spa, tắm và grooming cho thú cưng'),
                                                       ('Sức khỏe thú cưng', 'Nhóm dịch vụ khám, tư vấn và chăm sóc sức khỏe'),
                                                       ('Chăm sóc hằng ngày', 'Nhóm dịch vụ lưu trú, dắt đi dạo và huấn luyện');

INSERT INTO service_categories (parent_id, name, description)
SELECT id, 'Pet Spa', 'Dịch vụ spa cho thú cưng' FROM service_categories WHERE name = 'Làm đẹp & vệ sinh';

INSERT INTO service_categories (parent_id, name, description)
SELECT id, 'Grooming', 'Tắm, cắt tỉa, vệ sinh' FROM service_categories WHERE name = 'Làm đẹp & vệ sinh';

INSERT INTO service_categories (parent_id, name, description)
SELECT id, 'Veterinary', 'Khám, tư vấn, tiêm phòng' FROM service_categories WHERE name = 'Sức khỏe thú cưng';

INSERT INTO service_categories (parent_id, name, description)
SELECT id, 'Pet Boarding', 'Lưu trú và chăm sóc theo ngày' FROM service_categories WHERE name = 'Chăm sóc hằng ngày';

INSERT INTO service_categories (parent_id, name, description)
SELECT id, 'Pet Training', 'Huấn luyện cơ bản và nâng cao' FROM service_categories WHERE name = 'Chăm sóc hằng ngày';

INSERT INTO service_categories (parent_id, name, description)
SELECT id, 'Pet Walking', 'Dắt đi dạo và vận động' FROM service_categories WHERE name = 'Chăm sóc hằng ngày';

INSERT INTO services (service_code, category_id, name, slug, short_description, description, default_duration_minutes, base_price_amount, currency_code, price_unit)
SELECT 'SVC-SPA-BATH', id, 'Gói Tắm Thư Giãn', 'relax-bath', 'Tắm bằng nước ấm, sấy và chải lông', 'Gói spa cơ bản cho thú cưng', 45, 200000, 'VND', 'PER_SESSION' FROM service_categories WHERE name='Pet Spa';

INSERT INTO services (service_code, category_id, name, slug, short_description, description, default_duration_minutes, base_price_amount, currency_code, price_unit)
SELECT 'SVC-GROOM-STYLE', id, 'Cắt Tỉa Tạo Kiểu', 'groom-style', 'Cắt tỉa và tạo kiểu lông', 'Dịch vụ grooming nâng cao', 90, 350000, 'VND', 'PER_SESSION' FROM service_categories WHERE name='Grooming';

INSERT INTO services (service_code, category_id, name, slug, short_description, description, default_duration_minutes, base_price_amount, currency_code, price_unit)
SELECT 'SVC-VET-CHECKUP', id, 'Khám Tổng Quát', 'general-checkup', 'Khám cơ bản cho thú cưng', 'Khám sức khỏe tổng quát', 30, 150000, 'VND', 'PER_VISIT' FROM service_categories WHERE name='Veterinary';

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
