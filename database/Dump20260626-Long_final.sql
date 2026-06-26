CREATE DATABASE  IF NOT EXISTS `petgo_db` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `petgo_db`;
-- MySQL dump 10.13  Distrib 8.0.44, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: petgo_db
-- ------------------------------------------------------
-- Server version	9.5.0

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
SET @MYSQLDUMP_TEMP_LOG_BIN = @@SESSION.SQL_LOG_BIN;
SET @@SESSION.SQL_LOG_BIN= 0;

--
-- GTID state at the beginning of the backup 
--

SET @@GLOBAL.GTID_PURGED=/*!80000 '+'*/ 'b80684da-b284-11f0-b076-e65896217b68:1-5209';

--
-- Table structure for table `area_schedule_overrides`
--

DROP TABLE IF EXISTS `area_schedule_overrides`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `area_schedule_overrides` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `close_time` time DEFAULT NULL,
  `is_closed` bit(1) NOT NULL,
  `open_time` time DEFAULT NULL,
  `override_date` date NOT NULL,
  `reason` varchar(255) DEFAULT NULL,
  `area_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK6j1i288av7muc5us8ulr0pgh4` (`area_id`),
  CONSTRAINT `FK6j1i288av7muc5us8ulr0pgh4` FOREIGN KEY (`area_id`) REFERENCES `areas` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `area_schedule_overrides`
--

LOCK TABLES `area_schedule_overrides` WRITE;
/*!40000 ALTER TABLE `area_schedule_overrides` DISABLE KEYS */;
/*!40000 ALTER TABLE `area_schedule_overrides` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `area_schedules`
--

DROP TABLE IF EXISTS `area_schedules`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `area_schedules` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `is_active` bit(1) NOT NULL,
  `close_time` time NOT NULL,
  `day_of_week` int NOT NULL,
  `open_time` time NOT NULL,
  `area_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK1ngpa6jgwmiqhlw0jpq740tlo` (`area_id`),
  CONSTRAINT `FK1ngpa6jgwmiqhlw0jpq740tlo` FOREIGN KEY (`area_id`) REFERENCES `areas` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `area_schedules`
--

LOCK TABLES `area_schedules` WRITE;
/*!40000 ALTER TABLE `area_schedules` DISABLE KEYS */;
/*!40000 ALTER TABLE `area_schedules` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `area_service_configs`
--

DROP TABLE IF EXISTS `area_service_configs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `area_service_configs` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `is_active` bit(1) NOT NULL,
  `area_id` bigint NOT NULL,
  `service_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKpumqv7w0fhpp67u1aqm02lure` (`area_id`),
  KEY `FKd4mwk76wnbmf86myayswgi3xf` (`service_id`),
  CONSTRAINT `FKd4mwk76wnbmf86myayswgi3xf` FOREIGN KEY (`service_id`) REFERENCES `services` (`id`),
  CONSTRAINT `FKpumqv7w0fhpp67u1aqm02lure` FOREIGN KEY (`area_id`) REFERENCES `areas` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `area_service_configs`
--

LOCK TABLES `area_service_configs` WRITE;
/*!40000 ALTER TABLE `area_service_configs` DISABLE KEYS */;
INSERT INTO `area_service_configs` VALUES (1,NULL,NULL,_binary '',1,1),(2,NULL,NULL,_binary '',1,2),(3,NULL,NULL,_binary '',1,3),(4,NULL,NULL,_binary '',1,4),(5,NULL,NULL,_binary '',1,5),(6,NULL,NULL,_binary '',1,6),(7,NULL,NULL,_binary '',1,7),(8,NULL,NULL,_binary '',1,8),(9,NULL,NULL,_binary '',1,9),(10,NULL,NULL,_binary '',1,10);
/*!40000 ALTER TABLE `area_service_configs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `areas`
--

DROP TABLE IF EXISTS `areas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `areas` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `district_code` varchar(50) DEFAULT NULL,
  `long_slots` int NOT NULL,
  `name` varchar(255) NOT NULL,
  `pickup_address` varchar(500) DEFAULT NULL,
  `pickup_instructions` text,
  `pickup_latitude` decimal(10,7) DEFAULT NULL,
  `pickup_longitude` decimal(10,7) DEFAULT NULL,
  `pickup_phone` varchar(20) DEFAULT NULL,
  `province_code` varchar(50) DEFAULT NULL,
  `short_slots` int NOT NULL,
  `ward_code` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `areas`
--

LOCK TABLES `areas` WRITE;
/*!40000 ALTER TABLE `areas` DISABLE KEYS */;
INSERT INTO `areas` VALUES (1,NULL,NULL,NULL,10,'Hòa Lạc',NULL,NULL,21.0135277,105.5252615,'0123123123',NULL,10,NULL);
/*!40000 ALTER TABLE `areas` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `booking_disputes`
--

DROP TABLE IF EXISTS `booking_disputes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `booking_disputes` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `note` text,
  `reason` text,
  `refund_to_user_amount` decimal(12,2) DEFAULT NULL,
  `release_to_provider_amount` decimal(12,2) DEFAULT NULL,
  `resolved_at` datetime(6) DEFAULT NULL,
  `status` varchar(30) NOT NULL,
  `booking_id` bigint NOT NULL,
  `resolved_by` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKlnjih0l1yc2wk97mhusod4ve7` (`booking_id`),
  KEY `FKru14tltqlwwf30x5qrr6r34pq` (`resolved_by`),
  CONSTRAINT `FKlnjih0l1yc2wk97mhusod4ve7` FOREIGN KEY (`booking_id`) REFERENCES `shipping_bookings` (`id`),
  CONSTRAINT `FKru14tltqlwwf30x5qrr6r34pq` FOREIGN KEY (`resolved_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `booking_disputes`
--

LOCK TABLES `booking_disputes` WRITE;
/*!40000 ALTER TABLE `booking_disputes` DISABLE KEYS */;
/*!40000 ALTER TABLE `booking_disputes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `booking_status_histories`
--

DROP TABLE IF EXISTS `booking_status_histories`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `booking_status_histories` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `changed_by_id` bigint DEFAULT NULL,
  `changed_by_name` varchar(150) DEFAULT NULL,
  `changed_by_type` varchar(20) DEFAULT NULL,
  `created_at` datetime(6) NOT NULL,
  `from_status` varchar(50) DEFAULT NULL,
  `note` text,
  `to_status` varchar(50) NOT NULL,
  `booking_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK1dhjtw59ane8kc0igpk7souh8` (`booking_id`),
  CONSTRAINT `FK1dhjtw59ane8kc0igpk7souh8` FOREIGN KEY (`booking_id`) REFERENCES `shipping_bookings` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `booking_status_histories`
--

LOCK TABLES `booking_status_histories` WRITE;
/*!40000 ALTER TABLE `booking_status_histories` DISABLE KEYS */;
/*!40000 ALTER TABLE `booking_status_histories` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cart_items`
--

DROP TABLE IF EXISTS `cart_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cart_items` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `quantity` int NOT NULL,
  `product_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK1re40cjegsfvw58xrkdp6bac6` (`product_id`),
  KEY `FK709eickf3kc0dujx3ub9i7btf` (`user_id`),
  CONSTRAINT `FK1re40cjegsfvw58xrkdp6bac6` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`),
  CONSTRAINT `FK709eickf3kc0dujx3ub9i7btf` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cart_items`
--

LOCK TABLES `cart_items` WRITE;
/*!40000 ALTER TABLE `cart_items` DISABLE KEYS */;
/*!40000 ALTER TABLE `cart_items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `conversations`
--

DROP TABLE IF EXISTS `conversations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `conversations` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `deleted_at` datetime(6) DEFAULT NULL,
  `status` varchar(30) NOT NULL,
  `title` varchar(255) DEFAULT NULL,
  `type` enum('QA','REPORT') NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKpltqvfcbkql9svdqwh0hw4g1d` (`user_id`),
  CONSTRAINT `FKpltqvfcbkql9svdqwh0hw4g1d` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `conversations`
--

LOCK TABLES `conversations` WRITE;
/*!40000 ALTER TABLE `conversations` DISABLE KEYS */;
/*!40000 ALTER TABLE `conversations` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `home_sliders`
--

DROP TABLE IF EXISTS `home_sliders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `home_sliders` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `is_active` bit(1) NOT NULL,
  `cta_label` varchar(80) DEFAULT NULL,
  `cta_url` varchar(500) DEFAULT NULL,
  `image_url` varchar(1000) NOT NULL,
  `sort_order` int NOT NULL,
  `subtitle` varchar(500) DEFAULT NULL,
  `title` varchar(160) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `home_sliders`
--

LOCK TABLES `home_sliders` WRITE;
/*!40000 ALTER TABLE `home_sliders` DISABLE KEYS */;
/*!40000 ALTER TABLE `home_sliders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `invoice_items`
--

DROP TABLE IF EXISTS `invoice_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `invoice_items` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `item_name` varchar(180) NOT NULL,
  `item_type` varchar(30) NOT NULL,
  `line_total` decimal(12,2) NOT NULL,
  `quantity` int NOT NULL,
  `sort_order` int NOT NULL,
  `unit_price` decimal(12,2) NOT NULL,
  `invoice_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK46ae0lhu1oqs7cv91fn6y9n7w` (`invoice_id`),
  CONSTRAINT `FK46ae0lhu1oqs7cv91fn6y9n7w` FOREIGN KEY (`invoice_id`) REFERENCES `invoices` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `invoice_items`
--

LOCK TABLES `invoice_items` WRITE;
/*!40000 ALTER TABLE `invoice_items` DISABLE KEYS */;
/*!40000 ALTER TABLE `invoice_items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `invoices`
--

DROP TABLE IF EXISTS `invoices`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `invoices` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `billing_address` varchar(255) DEFAULT NULL,
  `billing_email` varchar(190) DEFAULT NULL,
  `billing_name` varchar(150) NOT NULL,
  `billing_phone` varchar(30) DEFAULT NULL,
  `currency_code` varchar(3) NOT NULL,
  `discount_amount` decimal(12,2) NOT NULL,
  `due_at` datetime(6) DEFAULT NULL,
  `invoice_number` varchar(32) NOT NULL,
  `invoice_type` varchar(20) NOT NULL,
  `issued_at` datetime(6) DEFAULT NULL,
  `note` varchar(255) DEFAULT NULL,
  `paid_at` datetime(6) DEFAULT NULL,
  `status` varchar(20) NOT NULL,
  `subtotal_amount` decimal(12,2) NOT NULL,
  `tax_amount` decimal(12,2) NOT NULL,
  `total_amount` decimal(12,2) NOT NULL,
  `membership_subscription_id` bigint DEFAULT NULL,
  `shop_order_id` bigint DEFAULT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKlsc8a4g4j6fpuaiaeuv8aqovo` (`membership_subscription_id`),
  KEY `FKnf9gye2fx8fwi9cx5i9hjnvlj` (`shop_order_id`),
  KEY `FKbwr4d4vyqf2bkoetxtt8j9dx7` (`user_id`),
  CONSTRAINT `FKbwr4d4vyqf2bkoetxtt8j9dx7` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKlsc8a4g4j6fpuaiaeuv8aqovo` FOREIGN KEY (`membership_subscription_id`) REFERENCES `membership_subscriptions` (`id`),
  CONSTRAINT `FKnf9gye2fx8fwi9cx5i9hjnvlj` FOREIGN KEY (`shop_order_id`) REFERENCES `shop_orders` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `invoices`
--

LOCK TABLES `invoices` WRITE;
/*!40000 ALTER TABLE `invoices` DISABLE KEYS */;
/*!40000 ALTER TABLE `invoices` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `membership_plan_features`
--

DROP TABLE IF EXISTS `membership_plan_features`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `membership_plan_features` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `feature_text` varchar(255) NOT NULL,
  `sort_order` int NOT NULL,
  `membership_plan_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKoplsbm3iimrgqjjunydvdesyj` (`membership_plan_id`),
  CONSTRAINT `FKoplsbm3iimrgqjjunydvdesyj` FOREIGN KEY (`membership_plan_id`) REFERENCES `membership_plans` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `membership_plan_features`
--

LOCK TABLES `membership_plan_features` WRITE;
/*!40000 ALTER TABLE `membership_plan_features` DISABLE KEYS */;
/*!40000 ALTER TABLE `membership_plan_features` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `membership_plans`
--

DROP TABLE IF EXISTS `membership_plans`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `membership_plans` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `is_active` bit(1) NOT NULL,
  `billing_cycle` varchar(20) NOT NULL,
  `currency_code` varchar(3) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `discount_percent` decimal(5,2) NOT NULL,
  `monthly_voucher_amount` decimal(12,2) NOT NULL,
  `name` varchar(120) NOT NULL,
  `plan_code` varchar(50) NOT NULL,
  `is_popular` bit(1) NOT NULL,
  `price_amount` decimal(12,2) NOT NULL,
  `priority_booking` bit(1) NOT NULL,
  `priority_support` bit(1) NOT NULL,
  `slug` varchar(120) NOT NULL,
  `sort_order` int NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `membership_plans`
--

LOCK TABLES `membership_plans` WRITE;
/*!40000 ALTER TABLE `membership_plans` DISABLE KEYS */;
/*!40000 ALTER TABLE `membership_plans` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `membership_subscriptions`
--

DROP TABLE IF EXISTS `membership_subscriptions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `membership_subscriptions` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `auto_renew` bit(1) NOT NULL,
  `cancel_reason` varchar(255) DEFAULT NULL,
  `cancelled_at` datetime(6) DEFAULT NULL,
  `expires_at` datetime(6) DEFAULT NULL,
  `next_billing_at` datetime(6) DEFAULT NULL,
  `started_at` datetime(6) DEFAULT NULL,
  `status` varchar(20) NOT NULL,
  `subscription_code` varchar(32) NOT NULL,
  `membership_plan_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKfto5rll6q1lx2fd09w7bmev1d` (`membership_plan_id`),
  KEY `FK24kdpuid705u179fc30d4vrop` (`user_id`),
  CONSTRAINT `FK24kdpuid705u179fc30d4vrop` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKfto5rll6q1lx2fd09w7bmev1d` FOREIGN KEY (`membership_plan_id`) REFERENCES `membership_plans` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `membership_subscriptions`
--

LOCK TABLES `membership_subscriptions` WRITE;
/*!40000 ALTER TABLE `membership_subscriptions` DISABLE KEYS */;
/*!40000 ALTER TABLE `membership_subscriptions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `messages`
--

DROP TABLE IF EXISTS `messages`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `messages` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `content` text,
  `error_code` varchar(100) DEFAULT NULL,
  `image_url` varchar(500) DEFAULT NULL,
  `is_system_message` bit(1) NOT NULL,
  `conversation_id` bigint NOT NULL,
  `sender_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKt492th6wsovh1nush5yl5jj8e` (`conversation_id`),
  KEY `FK4ui4nnwntodh6wjvck53dbk9m` (`sender_id`),
  CONSTRAINT `FK4ui4nnwntodh6wjvck53dbk9m` FOREIGN KEY (`sender_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKt492th6wsovh1nush5yl5jj8e` FOREIGN KEY (`conversation_id`) REFERENCES `conversations` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `messages`
--

LOCK TABLES `messages` WRITE;
/*!40000 ALTER TABLE `messages` DISABLE KEYS */;
/*!40000 ALTER TABLE `messages` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notification_recipients`
--

DROP TABLE IF EXISTS `notification_recipients`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `notification_recipients` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `delivered_at` datetime(6) NOT NULL,
  `read_at` datetime(6) DEFAULT NULL,
  `notification_id` bigint NOT NULL,
  `recipient_user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_notification_recipient_user` (`notification_id`,`recipient_user_id`),
  KEY `idx_notification_recipients_user_read` (`recipient_user_id`,`read_at`),
  KEY `idx_notification_recipients_notification` (`notification_id`),
  CONSTRAINT `FK4ju76iqqracsb8gllihpk2ujd` FOREIGN KEY (`recipient_user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKiuf5qgbttjq6ry57u1dni7qn4` FOREIGN KEY (`notification_id`) REFERENCES `notifications` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notification_recipients`
--

LOCK TABLES `notification_recipients` WRITE;
/*!40000 ALTER TABLE `notification_recipients` DISABLE KEYS */;
/*!40000 ALTER TABLE `notification_recipients` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notifications`
--

DROP TABLE IF EXISTS `notifications`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `notifications` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `action_url` varchar(500) DEFAULT NULL,
  `audience_type` enum('ALL','INDIVIDUAL','ROLE') NOT NULL,
  `category` enum('ACCOUNT','BOOKING','MEMBERSHIP','PAYMENT','PROMOTION','SYSTEM') NOT NULL,
  `content` text NOT NULL,
  `expires_at` datetime(6) DEFAULT NULL,
  `priority` enum('HIGH','LOW','NORMAL','URGENT') NOT NULL,
  `sent_at` datetime(6) NOT NULL,
  `target_roles` varchar(255) DEFAULT NULL,
  `title` varchar(180) NOT NULL,
  `created_by_admin_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_notifications_sent_at` (`sent_at`),
  KEY `idx_notifications_audience_type` (`audience_type`),
  KEY `idx_notifications_expires_at` (`expires_at`),
  KEY `FKj3isy3gatyxolmh0eyhoefwl6` (`created_by_admin_id`),
  CONSTRAINT `FKj3isy3gatyxolmh0eyhoefwl6` FOREIGN KEY (`created_by_admin_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notifications`
--

LOCK TABLES `notifications` WRITE;
/*!40000 ALTER TABLE `notifications` DISABLE KEYS */;
/*!40000 ALTER TABLE `notifications` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `payments`
--

DROP TABLE IF EXISTS `payments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `payments` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `amount` decimal(12,2) NOT NULL,
  `currency_code` varchar(3) NOT NULL,
  `failure_reason` varchar(255) DEFAULT NULL,
  `gateway_name` varchar(50) DEFAULT NULL,
  `gateway_transaction_id` varchar(120) DEFAULT NULL,
  `metadata_json` json DEFAULT NULL,
  `paid_at` datetime(6) DEFAULT NULL,
  `payment_code` varchar(32) NOT NULL,
  `payment_method` varchar(20) NOT NULL,
  `status` varchar(30) NOT NULL,
  `invoice_id` bigint NOT NULL,
  `payer_user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKrbqec6be74wab8iifh8g3i50i` (`invoice_id`),
  KEY `FKny36alosvbrn6r83arnwp0u3f` (`payer_user_id`),
  CONSTRAINT `FKny36alosvbrn6r83arnwp0u3f` FOREIGN KEY (`payer_user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKrbqec6be74wab8iifh8g3i50i` FOREIGN KEY (`invoice_id`) REFERENCES `invoices` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `payments`
--

LOCK TABLES `payments` WRITE;
/*!40000 ALTER TABLE `payments` DISABLE KEYS */;
/*!40000 ALTER TABLE `payments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pet_photos`
--

DROP TABLE IF EXISTS `pet_photos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pet_photos` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `photo_url` varchar(500) NOT NULL,
  `is_primary` bit(1) NOT NULL,
  `sort_order` int NOT NULL,
  `pet_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKfdp39phgvx1umgkmyevib6te8` (`pet_id`),
  CONSTRAINT `FKfdp39phgvx1umgkmyevib6te8` FOREIGN KEY (`pet_id`) REFERENCES `pets` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pet_photos`
--

LOCK TABLES `pet_photos` WRITE;
/*!40000 ALTER TABLE `pet_photos` DISABLE KEYS */;
/*!40000 ALTER TABLE `pet_photos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pets`
--

DROP TABLE IF EXISTS `pets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pets` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `age_label` varchar(50) DEFAULT NULL,
  `allergy_notes` text,
  `avatar_url` varchar(500) DEFAULT NULL,
  `behavior_notes` text,
  `breed` varchar(120) DEFAULT NULL,
  `color` varchar(100) DEFAULT NULL,
  `date_of_birth` date DEFAULT NULL,
  `deleted_at` datetime(6) DEFAULT NULL,
  `gender` varchar(20) DEFAULT NULL,
  `health_notes` text,
  `name` varchar(120) NOT NULL,
  `pet_code` varchar(32) NOT NULL,
  `size` varchar(20) DEFAULT NULL,
  `species` varchar(20) NOT NULL,
  `status` varchar(20) NOT NULL,
  `vaccination_notes` text,
  `weight_kg` decimal(6,2) DEFAULT NULL,
  `owner_user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKdk25k5uhad19gjwwe86577tr8` (`owner_user_id`),
  CONSTRAINT `FKdk25k5uhad19gjwwe86577tr8` FOREIGN KEY (`owner_user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pets`
--

LOCK TABLES `pets` WRITE;
/*!40000 ALTER TABLE `pets` DISABLE KEYS */;
/*!40000 ALTER TABLE `pets` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product_categories`
--

DROP TABLE IF EXISTS `product_categories`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product_categories` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `is_active` bit(1) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `icon_key` varchar(80) DEFAULT NULL,
  `name` varchar(120) NOT NULL,
  `slug` varchar(150) NOT NULL,
  `sort_order` int NOT NULL,
  `parent_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKnhstaep8s818kydkq4teq8v4e` (`parent_id`),
  CONSTRAINT `FKnhstaep8s818kydkq4teq8v4e` FOREIGN KEY (`parent_id`) REFERENCES `product_categories` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product_categories`
--

LOCK TABLES `product_categories` WRITE;
/*!40000 ALTER TABLE `product_categories` DISABLE KEYS */;
INSERT INTO `product_categories` VALUES (1,NULL,NULL,_binary '','Thức ăn khô, thức ăn ướt, pate và súp thưởng cho chó mèo.','restaurant','Đồ ăn','do-an',1,NULL),(2,NULL,NULL,_binary '','Cát vệ sinh, sản phẩm chăm sóc, khay, bát ăn và chuồng cho thú cưng.','inventory_2','Đồ dùng','do-dung',2,NULL),(3,NULL,NULL,_binary '','Đồ chơi, bàn cào móng, dây dắt, vòng cổ và thời trang thú cưng.','pets','Phụ kiện','phu-kien',3,NULL);
/*!40000 ALTER TABLE `product_categories` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `products`
--

DROP TABLE IF EXISTS `products`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `products` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `is_active` bit(1) NOT NULL,
  `average_rating` decimal(3,2) NOT NULL,
  `barcode` varchar(100) DEFAULT NULL,
  `brand` varchar(120) DEFAULT NULL,
  `currency_code` char(3) NOT NULL,
  `deleted_at` datetime(6) DEFAULT NULL,
  `description` text,
  `is_featured` bit(1) NOT NULL,
  `is_hot` bit(1) NOT NULL,
  `main_image_url` varchar(500) DEFAULT NULL,
  `name` varchar(180) NOT NULL,
  `price_amount` decimal(12,2) NOT NULL,
  `product_code` varchar(32) NOT NULL,
  `sale_price_amount` decimal(12,2) DEFAULT NULL,
  `short_description` varchar(255) DEFAULT NULL,
  `sku` varchar(100) DEFAULT NULL,
  `slug` varchar(190) NOT NULL,
  `sold_quantity` int NOT NULL,
  `status` varchar(30) NOT NULL,
  `stock_quantity` int NOT NULL,
  `target_species` varchar(20) NOT NULL,
  `total_reviews` int NOT NULL,
  `weight_gram` int DEFAULT NULL,
  `category_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK6t5dtw6tyo83ywljwohuc6g7k` (`category_id`),
  CONSTRAINT `FK6t5dtw6tyo83ywljwohuc6g7k` FOREIGN KEY (`category_id`) REFERENCES `product_categories` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=32 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `products`
--

LOCK TABLES `products` WRITE;
/*!40000 ALTER TABLE `products` DISABLE KEYS */;
INSERT INTO `products` VALUES (1,'2026-06-26 15:35:15.799528','2026-06-26 15:35:15.799528',_binary '',0.00,'8938500000011','Catsrang','VND',NULL,'Hạt khô giá hợp lý cho mèo, phù hợp dùng hằng ngày. Giá nhập: 60.000đ.',_binary '',_binary '\0','https://down-vn.img.susercontent.com/file/vn-11134207-7qukw-ligq1vr4s3kc4b','Thức ăn hạt Catsrang cho mèo 1kg',90000.00,'PETSHOP-P001',NULL,'Hạt khô giá hợp lý cho mèo, phù hợp dùng hằng ngày.','CATSRANG-1KG','thuc-an-hat-catsrang-cho-meo-1kg',0,'ACTIVE',80,'CAT',0,1000,1),(2,'2026-06-26 15:35:15.799528','2026-06-26 15:35:15.799528',_binary '',0.00,'3182550737556','Royal Canin','VND',NULL,'Hạt dành cho mèo trưởng thành đã triệt sản. Giá nhập: 115.000đ.',_binary '',_binary '','https://down-vn.img.susercontent.com/file/vn-11134207-7r98o-lol9xxtw8fcn34','Thức ăn hạt Royal Canin Sterilised 400g',165000.00,'PETSHOP-P002',NULL,'Hạt dành cho mèo trưởng thành đã triệt sản.','RC-STERILISED-400G','thuc-an-hat-royal-canin-sterilised-400g',0,'ACTIVE',55,'CAT',0,400,1),(3,'2026-06-26 15:35:15.799528','2026-06-26 15:35:15.799528',_binary '',0.00,'064992280185','Orijen','VND',NULL,'Dòng hạt cao cấp giàu đạm cho mèo. Giá nhập: 285.000đ.',_binary '',_binary '','https://down-vn.img.susercontent.com/file/vn-11134207-7ra0g-m8vpg0soux6qfb','Thức ăn hạt Orijen Original Cat 1.8kg',400000.00,'PETSHOP-P003',NULL,'Dòng hạt cao cấp giàu đạm cho mèo.','ORIJEN-CAT-18KG','thuc-an-hat-orijen-original-cat-1-8kg',0,'ACTIVE',30,'CAT',0,1800,1),(4,'2026-06-26 15:35:15.799528','2026-06-26 15:35:15.799528',_binary '',0.00,'8853301551088','Whiskas','VND',NULL,'Thức ăn ướt mềm, dễ ăn cho mèo trưởng thành. Giá nhập: 16.500đ.',_binary '\0',_binary '\0','https://down-vn.img.susercontent.com/file/daaf7f5e60d632c5a62ea8f675541f65','Pate Whiskas cho mèo gói 80g',25000.00,'PETSHOP-P004',NULL,'Thức ăn ướt mềm, dễ ăn cho mèo trưởng thành.','WHISKAS-PATE-80G','pate-whiskas-cho-meo-goi-80g',0,'ACTIVE',140,'CAT',0,80,1),(5,'2026-06-26 15:35:15.799528','2026-06-26 15:35:15.799528',_binary '',0.00,'4901133718431','Ciao Churu','VND',NULL,'Súp thưởng dạng thanh dùng làm món ăn vặt cho mèo. Giá nhập: 9.000đ.',_binary '\0',_binary '','https://down-vn.img.susercontent.com/file/vn-11134201-7r98o-lqvivrkahip569','Súp thưởng Ciao Churu cho mèo gói 4 thanh',16000.00,'PETSHOP-P005',NULL,'Súp thưởng dạng thanh dùng làm món ăn vặt cho mèo.','CIAO-CHURU-4','sup-thuong-ciao-churu-goi-4-thanh',0,'ACTIVE',180,'CAT',0,56,1),(6,'2026-06-26 15:35:15.799528','2026-06-26 15:35:15.799528',_binary '',0.00,'8938500000066','Moon Cat','VND',NULL,'Cát đất sét vón cục nhanh, hỗ trợ khử mùi. Giá nhập: 36.000đ.',_binary '\0',_binary '\0','https://down-vn.img.susercontent.com/file/vn-11134207-81ztc-mnjt2oewjj7n71','Cát vệ sinh Moon Cat Bentonite 9L',60000.00,'PETSHOP-P006',NULL,'Cát đất sét vón cục nhanh, hỗ trợ khử mùi.','MOONCAT-BENTONITE-9L','cat-ve-sinh-moon-cat-bentonite-9l',0,'ACTIVE',65,'CAT',0,4000,2),(7,'2026-06-26 15:35:15.799528','2026-06-26 15:35:15.799528',_binary '',0.00,'6970000000067','Cature','VND',NULL,'Cát đậu nành hữu cơ, ít bụi và thấm hút nhanh. Giá nhập: 60.000đ.',_binary '\0',_binary '','https://down-vn.img.susercontent.com/file/vn-11134207-81ztc-mprrwr1putxe00','Cát đậu nành Cature Tofu 6L',100000.00,'PETSHOP-P007',NULL,'Cát đậu nành hữu cơ, ít bụi và thấm hút nhanh.','CATURE-TOFU-6L','cat-dau-nanh-cature-tofu-6l',0,'ACTIVE',50,'CAT',0,2500,2),(8,'2026-06-26 15:35:15.799528','2026-06-26 15:35:15.799528',_binary '',0.00,'8938500000080','SOS','VND',NULL,'Sữa tắm làm sạch và chăm sóc lông cho chó mèo. Giá nhập: 60.000đ.',_binary '',_binary '\0','https://down-vn.img.susercontent.com/file/sg-11134201-7rdxu-lzxjiixl1gh67d','Sữa tắm SOS cho chó mèo 500ml',105000.00,'PETSHOP-P008',NULL,'Sữa tắm làm sạch và chăm sóc lông cho chó mèo.','SOS-SHAMPOO-500ML','sua-tam-sos-cho-cho-meo-500ml',0,'ACTIVE',45,'ALL',0,NULL,2),(9,'2026-06-26 15:35:15.799528','2026-06-26 15:35:15.799528',_binary '',0.00,'8938500000097','Generic','VND',NULL,'Khay nhựa thành cao, có xẻng và viền chống văng cát. Giá nhập: 90.000đ.',_binary '\0',_binary '\0','https://down-vn.img.susercontent.com/file/vn-11134207-7r98o-ll5v2rxkieuu80','Khay vệ sinh cho mèo size lớn',165000.00,'PETSHOP-P009',NULL,'Khay nhựa thành cao, có xẻng và viền chống văng cát.','CAT-LITTER-TRAY-L','khay-ve-sinh-cho-meo-size-lon',0,'ACTIVE',35,'CAT',0,NULL,2),(10,'2026-06-26 15:35:15.799528','2026-06-26 15:35:15.799528',_binary '',0.00,'8938500000103','Generic','VND',NULL,'Bộ bát ăn chống lật kèm bình cấp nước tự động. Giá nhập: 52.500đ.',_binary '',_binary '\0','https://down-vn.img.susercontent.com/file/f9bd15dcbe9b357e230701c024499c56','Bát ăn inox kèm bình nước tự động',100000.00,'PETSHOP-P010',NULL,'Bộ bát ăn chống lật kèm bình cấp nước tự động.','PET-BOWL-WATER-AUTO','bat-an-inox-kem-binh-nuoc-tu-dong',0,'ACTIVE',48,'ALL',0,NULL,2),(11,'2026-06-26 15:35:15.799528','2026-06-26 15:35:15.799528',_binary '',0.00,'8938500000110','Hanpet','VND',NULL,'Chuồng gấp gọn bằng sắt sơn tĩnh điện cho chó mèo nhỏ. Giá nhập: 150.000đ.',_binary '\0',_binary '\0','https://down-vn.img.susercontent.com/file/sg-11134201-7rdy0-lzrgzc2liabbab','Chuồng sắt sơn tĩnh điện 60cm',260000.00,'PETSHOP-P011',NULL,'Chuồng gấp gọn bằng sắt sơn tĩnh điện cho chó mèo nhỏ.','PET-CAGE-60CM','chuong-sat-son-tinh-dien-60cm',0,'ACTIVE',22,'ALL',0,NULL,2),(12,'2026-06-26 15:35:15.799528','2026-06-26 15:35:15.799528',_binary '',0.00,'8938500000127','Generic','VND',NULL,'Đồ chơi tương tác giúp mèo vận động và giảm căng thẳng. Giá nhập: 8.500đ.',_binary '\0',_binary '','https://down-vn.img.susercontent.com/file/28e7bc8597b2a44061cb6cb1ff33360f','Cần câu mèo kèm chuột bông và chuông',22000.00,'PETSHOP-P012',NULL,'Đồ chơi tương tác giúp mèo vận động và giảm căng thẳng.','CAT-WAND-MOUSE','can-cau-meo-kem-chuot-bong-va-chuong',0,'ACTIVE',160,'CAT',0,NULL,3),(13,'2026-06-26 15:35:15.799528','2026-06-26 15:35:15.799528',_binary '',0.00,'8938500000134','Generic','VND',NULL,'Đồ chơi cao su cho chó gặm, hỗ trợ vận động và làm sạch răng. Giá nhập: 17.500đ.',_binary '\0',_binary '\0','https://down-vn.img.susercontent.com/file/vn-11134207-81ztc-mp1zy9ae12bwd8','Bóng gai và xương cao su gặm cho chó',42000.00,'PETSHOP-P013',NULL,'Đồ chơi cao su cho chó gặm, hỗ trợ vận động và làm sạch răng.','DOG-RUBBER-CHEW','bong-gai-xuong-cao-su-gam-cho-cho',0,'ACTIVE',100,'DOG',0,NULL,3),(14,'2026-06-26 15:35:15.799528','2026-06-26 15:35:15.799528',_binary '',0.00,'8938500000141','Generic','VND',NULL,'Bàn cào móng bằng carton giúp hạn chế mèo cào nội thất. Giá nhập: 32.500đ.',_binary '',_binary '\0','https://down-vn.img.susercontent.com/file/vn-11134207-7r98o-lv6cpjrh998962','Bàn cào móng carton cho mèo',67000.00,'PETSHOP-P014',NULL,'Bàn cào móng bằng carton giúp hạn chế mèo cào nội thất.','CAT-SCRATCHER-CARD','ban-cao-mong-carton-cho-meo',0,'ACTIVE',60,'CAT',0,500,3),(15,'2026-06-26 15:35:15.799528','2026-06-26 15:35:15.799528',_binary '',0.00,'8938500000158','Farm Pet','VND',NULL,'Bộ vòng cổ và dây dắt nhiều màu cho thú cưng nhỏ. Giá nhập: 14.000đ.',_binary '\0',_binary '','https://down-vn.img.susercontent.com/file/vn-11134207-7qukw-lgivh5sjsvhm40','Dây dắt kèm vòng cổ cho chó mèo',35000.00,'PETSHOP-P015',NULL,'Bộ vòng cổ và dây dắt nhiều màu cho thú cưng nhỏ.','PET-COLLAR-LEASH','day-dat-kem-vong-co-cho-cho-meo',0,'ACTIVE',120,'ALL',0,NULL,3),(16,'2026-06-26 15:35:15.799528','2026-06-26 15:35:15.799528',_binary '',0.00,'8938500000165','LaLi Petfashion','VND',NULL,'Áo sơ mi họa tiết dành cho chó mèo, nhiều kích cỡ. Giá nhập: 45.000đ.',_binary '',_binary '\0','https://down-vn.img.susercontent.com/file/db67ea766549252890243db693e0807f','Áo sơ mi thời trang cho chó mèo',95000.00,'PETSHOP-P016',NULL,'Áo sơ mi họa tiết dành cho chó mèo, nhiều kích cỡ.','PET-FASHION-SHIRT','ao-so-mi-thoi-trang-cho-cho-meo',0,'ACTIVE',75,'ALL',0,NULL,3);
/*!40000 ALTER TABLE `products` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `promo_code_redemptions`
--

DROP TABLE IF EXISTS `promo_code_redemptions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `promo_code_redemptions` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `discount_amount` decimal(12,2) NOT NULL,
  `discount_type` varchar(20) NOT NULL,
  `owner_type` varchar(20) NOT NULL,
  `promo_code_snapshot` varchar(50) NOT NULL,
  `redeemed_at` datetime(6) NOT NULL,
  `subtotal_amount` decimal(12,2) NOT NULL,
  `target_type` varchar(20) NOT NULL,
  `invoice_id` bigint DEFAULT NULL,
  `membership_subscription_id` bigint DEFAULT NULL,
  `promo_code_id` bigint NOT NULL,
  `shipping_booking_id` bigint DEFAULT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK84sdgdf5kkdf6px3xuf517ulw` (`invoice_id`),
  KEY `FKseeh8jp7q4xkmb9a24k9bxdwo` (`membership_subscription_id`),
  KEY `FKr52safg3hage6cqkojclv6yn1` (`promo_code_id`),
  KEY `FKj1j78b34xn8mtxsi7c5u5wbxc` (`shipping_booking_id`),
  KEY `FK8mln1obonekngrm1skqh2m91p` (`user_id`),
  CONSTRAINT `FK84sdgdf5kkdf6px3xuf517ulw` FOREIGN KEY (`invoice_id`) REFERENCES `invoices` (`id`),
  CONSTRAINT `FK8mln1obonekngrm1skqh2m91p` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKj1j78b34xn8mtxsi7c5u5wbxc` FOREIGN KEY (`shipping_booking_id`) REFERENCES `shipping_bookings` (`id`),
  CONSTRAINT `FKr52safg3hage6cqkojclv6yn1` FOREIGN KEY (`promo_code_id`) REFERENCES `promo_codes` (`id`),
  CONSTRAINT `FKseeh8jp7q4xkmb9a24k9bxdwo` FOREIGN KEY (`membership_subscription_id`) REFERENCES `membership_subscriptions` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `promo_code_redemptions`
--

LOCK TABLES `promo_code_redemptions` WRITE;
/*!40000 ALTER TABLE `promo_code_redemptions` DISABLE KEYS */;
/*!40000 ALTER TABLE `promo_code_redemptions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `promo_codes`
--

DROP TABLE IF EXISTS `promo_codes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `promo_codes` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `is_active` bit(1) NOT NULL,
  `applicable_days_of_week` varchar(100) DEFAULT NULL,
  `area_ids` varchar(1000) DEFAULT NULL,
  `is_auto_apply` bit(1) DEFAULT NULL,
  `badge_text` varchar(80) DEFAULT NULL,
  `code` varchar(50) NOT NULL,
  `description` text,
  `discount_type` varchar(20) NOT NULL,
  `discount_value` decimal(12,2) NOT NULL,
  `ends_at` datetime(6) DEFAULT NULL,
  `internal_note` text,
  `landing_page_url` varchar(500) DEFAULT NULL,
  `max_discount_amount` decimal(12,2) DEFAULT NULL,
  `membership_plan_ids` varchar(1000) DEFAULT NULL,
  `min_completed_bookings` int DEFAULT NULL,
  `min_order_amount` decimal(12,2) DEFAULT NULL,
  `name` varchar(120) DEFAULT NULL,
  `owner_type` varchar(20) DEFAULT NULL,
  `priority` int DEFAULT NULL,
  `promotion_type` varchar(30) DEFAULT NULL,
  `service_category_ids` varchar(1000) DEFAULT NULL,
  `is_stackable` bit(1) DEFAULT NULL,
  `starts_at` datetime(6) DEFAULT NULL,
  `target_type` varchar(20) NOT NULL,
  `terms_and_conditions` text,
  `usage_count` int DEFAULT NULL,
  `usage_limit_per_user` int DEFAULT NULL,
  `usage_limit_total` int DEFAULT NULL,
  `user_segment` varchar(30) DEFAULT NULL,
  `created_by_user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKoclkwb6aq06kietanmwnt1rco` (`created_by_user_id`),
  CONSTRAINT `FKoclkwb6aq06kietanmwnt1rco` FOREIGN KEY (`created_by_user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `promo_codes`
--

LOCK TABLES `promo_codes` WRITE;
/*!40000 ALTER TABLE `promo_codes` DISABLE KEYS */;
/*!40000 ALTER TABLE `promo_codes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `refresh_tokens`
--

DROP TABLE IF EXISTS `refresh_tokens`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `refresh_tokens` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `expires_at` datetime(6) NOT NULL,
  `revoked_at` datetime(6) DEFAULT NULL,
  `token_hash` varchar(255) NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK1lih5y2npsf8u5o3vhdb9y0os` (`user_id`),
  CONSTRAINT `FK1lih5y2npsf8u5o3vhdb9y0os` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `refresh_tokens`
--

LOCK TABLES `refresh_tokens` WRITE;
/*!40000 ALTER TABLE `refresh_tokens` DISABLE KEYS */;
INSERT INTO `refresh_tokens` VALUES (1,NULL,'2026-07-26 22:35:42.000000',NULL,'dc8e2cb3336ebed2edae98df423874e650a35c9f1ac65d1a77cb9eb155c18a45',2);
/*!40000 ALTER TABLE `refresh_tokens` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `roles`
--

DROP TABLE IF EXISTS `roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `roles` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `code` enum('ADMIN','USER') NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `name` varchar(100) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `roles`
--

LOCK TABLES `roles` WRITE;
/*!40000 ALTER TABLE `roles` DISABLE KEYS */;
INSERT INTO `roles` VALUES (1,NULL,NULL,'USER','Người dùng hệ thống','User'),(2,NULL,NULL,'ADMIN','Quản trị hệ thống','Administrator');
/*!40000 ALTER TABLE `roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `service_categories`
--

DROP TABLE IF EXISTS `service_categories`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `service_categories` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `is_active` bit(1) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `name` varchar(120) NOT NULL,
  `parent_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK1lggon6dtecbuwkkf706i1a15` (`parent_id`),
  CONSTRAINT `FK1lggon6dtecbuwkkf706i1a15` FOREIGN KEY (`parent_id`) REFERENCES `service_categories` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `service_categories`
--

LOCK TABLES `service_categories` WRITE;
/*!40000 ALTER TABLE `service_categories` DISABLE KEYS */;
INSERT INTO `service_categories` VALUES (1,_binary '','Tắm, cắt tỉa, vệ sinh thú cưng','Spa & Grooming',NULL),(2,_binary '','Trông giữ và lưu trú qua đêm','Khách sạn thú cưng',NULL),(3,_binary '','Khám, tiêm vaccine, xét nghiệm','Y tế & Phòng bệnh',NULL);
/*!40000 ALTER TABLE `service_categories` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `service_category_mapping`
--

DROP TABLE IF EXISTS `service_category_mapping`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `service_category_mapping` (
  `service_id` bigint NOT NULL,
  `category_id` bigint NOT NULL,
  KEY `FKpbd4i0jt290wynr0jycqm8fkv` (`category_id`),
  KEY `FKctp5w7klssuqqsaocqvj19daa` (`service_id`),
  CONSTRAINT `FKctp5w7klssuqqsaocqvj19daa` FOREIGN KEY (`service_id`) REFERENCES `services` (`id`),
  CONSTRAINT `FKpbd4i0jt290wynr0jycqm8fkv` FOREIGN KEY (`category_id`) REFERENCES `service_categories` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `service_category_mapping`
--

LOCK TABLES `service_category_mapping` WRITE;
/*!40000 ALTER TABLE `service_category_mapping` DISABLE KEYS */;
INSERT INTO `service_category_mapping` VALUES (3,1),(2,1),(5,2),(6,2),(1,1),(9,3),(7,3),(8,3),(10,3),(4,1);
/*!40000 ALTER TABLE `service_category_mapping` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `service_price_tiers`
--

DROP TABLE IF EXISTS `service_price_tiers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `service_price_tiers` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `price_amount` decimal(12,2) NOT NULL,
  `species` varchar(10) NOT NULL,
  `weight_from` decimal(8,2) NOT NULL,
  `weight_to` decimal(8,2) NOT NULL,
  `service_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKbk896fox006lpkspqhl06dfmw` (`service_id`),
  CONSTRAINT `FKbk896fox006lpkspqhl06dfmw` FOREIGN KEY (`service_id`) REFERENCES `services` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=65 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `service_price_tiers`
--

LOCK TABLES `service_price_tiers` WRITE;
/*!40000 ALTER TABLE `service_price_tiers` DISABLE KEYS */;
INSERT INTO `service_price_tiers` VALUES (32,40000.00,'ALL',0.00,200.00,3),(33,400000.00,'CAT',0.00,5.00,2),(34,480000.00,'CAT',5.00,10.00,2),(35,550000.00,'CAT',10.00,200.00,2),(36,350000.00,'DOG',0.00,5.00,2),(37,480000.00,'DOG',5.00,10.00,2),(38,680000.00,'DOG',10.00,20.00,2),(39,900000.00,'DOG',20.00,200.00,2),(40,110000.00,'DOG',0.00,5.00,5),(41,140000.00,'DOG',5.00,10.00,5),(42,200000.00,'DOG',10.00,20.00,5),(43,400000.00,'DOG',20.00,40.00,5),(44,100000.00,'CAT',0.00,5.00,6),(45,150000.00,'CAT',5.00,200.00,6),(47,180000.00,'CAT',0.00,5.00,1),(48,250000.00,'CAT',5.00,10.00,1),(49,300000.00,'CAT',10.00,200.00,1),(50,120000.00,'DOG',0.00,5.00,1),(51,200000.00,'DOG',5.00,10.00,1),(52,320000.00,'DOG',10.00,20.00,1),(53,550000.00,'DOG',20.00,200.00,1),(54,90000.00,'ALL',0.00,200.00,9),(56,150000.00,'ALL',0.00,200.00,7),(57,220000.00,'CAT',0.00,200.00,8),(58,150000.00,'DOG',0.00,200.00,8),(59,300000.00,'CAT',0.00,5.00,10),(60,600000.00,'CAT',5.00,200.00,10),(61,800000.00,'DOG',0.00,5.00,10),(62,1200000.00,'DOG',5.00,10.00,10),(63,1800000.00,'DOG',10.00,200.00,10),(64,40000.00,'ALL',0.00,200.00,4);
/*!40000 ALTER TABLE `service_price_tiers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `service_reviews`
--

DROP TABLE IF EXISTS `service_reviews`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `service_reviews` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `content` text,
  `hidden` bit(1) NOT NULL,
  `rating` int NOT NULL,
  `reply` text,
  `booking_id` bigint NOT NULL,
  `service_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKjuyj523uw9j972jurbc2qrrkl` (`booking_id`),
  KEY `FKswvvdd1fiadm0niifdauvqi3d` (`service_id`),
  KEY `FKow2v5yr1c9fcbprwh07q9dp1l` (`user_id`),
  CONSTRAINT `FKow2v5yr1c9fcbprwh07q9dp1l` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKpaji3qbj3yam69qs3wfx0ksfx` FOREIGN KEY (`booking_id`) REFERENCES `shipping_bookings` (`id`),
  CONSTRAINT `FKswvvdd1fiadm0niifdauvqi3d` FOREIGN KEY (`service_id`) REFERENCES `services` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `service_reviews`
--

LOCK TABLES `service_reviews` WRITE;
/*!40000 ALTER TABLE `service_reviews` DISABLE KEYS */;
/*!40000 ALTER TABLE `service_reviews` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `services`
--

DROP TABLE IF EXISTS `services`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `services` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `is_active` bit(1) NOT NULL,
  `average_rating` decimal(3,2) DEFAULT NULL,
  `base_price_amount` decimal(12,2) DEFAULT NULL,
  `booking_type` varchar(20) NOT NULL,
  `currency_code` varchar(3) NOT NULL,
  `default_duration_minutes` int NOT NULL,
  `description` text,
  `image_url` varchar(500) DEFAULT NULL,
  `name` varchar(150) NOT NULL,
  `price_unit` varchar(20) NOT NULL,
  `service_code` varchar(32) NOT NULL,
  `short_description` varchar(255) DEFAULT NULL,
  `slug` varchar(150) NOT NULL,
  `total_reviews` int DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `services`
--

LOCK TABLES `services` WRITE;
/*!40000 ALTER TABLE `services` DISABLE KEYS */;
INSERT INTO `services` VALUES (1,NULL,NULL,_binary '',NULL,250000.00,'SHORT','VND',60,'','https://res.cloudinary.com/dxaok6qzg/image/upload/v1782488278/petgo/platform/services/2286bb1a88654523af119f2eebe12ce6.png','Tắm & Vệ sinh cơ bản','SESSION','SPA_BATH','Tắm, vắt tuyến hôi, sấy, chải lông, cắt móng, vệ sinh tai','tam-ve-sinh-co-ban',NULL),(2,NULL,NULL,_binary '',NULL,500000.00,'SHORT','VND',90,'','https://res.cloudinary.com/dxaok6qzg/image/upload/v1782488199/petgo/platform/services/d2fb764e2a5d42c1a4d6d805ba7dbe89.png','Cắt tỉa & Tạo kiểu lông','SESSION','SPA_GROOM','Bao gồm tắm vệ sinh + cắt tỉa tạo kiểu theo yêu cầu','cat-tia-tao-kieu-long',NULL),(3,NULL,NULL,_binary '',NULL,40000.00,'SHORT','VND',30,'','https://res.cloudinary.com/dxaok6qzg/image/upload/v1782488175/petgo/platform/services/7e64022d78bb44b3872c1f0e6619ca1c.png','Cắt móng / Mài móng','SESSION','SPA_NAIL','Cắt gọn và mài mịn móng cho thú cưng','cat-mong-mai-mong',NULL),(4,NULL,NULL,_binary '',NULL,40000.00,'SHORT','VND',30,'','https://res.cloudinary.com/dxaok6qzg/image/upload/v1782488503/petgo/platform/services/5b5b94e27c0b496698ccafefa53f66a6.png','Vệ sinh tai','SESSION','SPA_EAR','Vệ sinh tai, khử mùi hôi tai','ve-sinh-tai',NULL),(5,NULL,NULL,_binary '',NULL,200000.00,'LONG','VND',1440,'','https://res.cloudinary.com/dxaok6qzg/image/upload/v1782488215/petgo/platform/services/d3b1cc04bc1241c9b21947bcd50f4048.png','Khách sạn chó','DAY','HTL_DOG','Trông giữ ban ngày, vệ sinh, ăn 2 bữa','khach-san-cho',NULL),(6,NULL,NULL,_binary '',NULL,120000.00,'LONG','VND',1440,'','https://res.cloudinary.com/dxaok6qzg/image/upload/v1782488236/petgo/platform/services/a5920d7fe23b40ea8041b9cf22e3b849.png','Khách sạn mèo','DAY','HTL_CAT','Phòng riêng có điều hòa, vệ sinh, ăn 3 bữa','khach-san-meo',NULL),(7,NULL,NULL,_binary '',NULL,150000.00,'SHORT','VND',30,'','https://res.cloudinary.com/dxaok6qzg/image/upload/v1782488424/petgo/platform/services/c85d1a66e02b44fb9209110c6cd86f34.png','Khám lâm sàng','VISIT','MED_EXAM','Khám tổng quát, chẩn đoán bệnh lý thường gặp','kham-lam-sang',NULL),(8,NULL,NULL,_binary '',NULL,180000.00,'SHORT','VND',30,'','https://res.cloudinary.com/dxaok6qzg/image/upload/v1782488468/petgo/platform/services/aaa7f440bdde461d889ab26decfcc0aa.png','Tiêm vaccine','VISIT','MED_VAC','Vaccine 5 bệnh/7 bệnh cho chó, 4 bệnh cho mèo','tiem-vaccine',NULL),(9,NULL,NULL,_binary '',NULL,90000.00,'SHORT','VND',20,'','https://res.cloudinary.com/dxaok6qzg/image/upload/v1782488340/petgo/platform/services/77196bd3fd53446f924449a605b0f877.png','Test nhanh bệnh truyền nhiễm','VISIT','MED_TEST','Test Care, Parvo (chó) / Giảm bạch cầu (mèo)','test-nhanh',NULL),(10,NULL,NULL,_binary '',NULL,500000.00,'SHORT','VND',60,'','https://res.cloudinary.com/dxaok6qzg/image/upload/v1782488486/petgo/platform/services/b48b15c66d4d474d95801b50c51ea00d.png','Triệt sản','VISIT','MED_SPAY','Phẫu thuật triệt sản, gây mê an toàn','triet-san',NULL);
/*!40000 ALTER TABLE `services` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `shipping_bookings`
--

DROP TABLE IF EXISTS `shipping_bookings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `shipping_bookings` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `admin_note` text,
  `appointment_date` date DEFAULT NULL,
  `booking_code` varchar(32) NOT NULL,
  `booking_type` varchar(20) NOT NULL,
  `currency_code` varchar(3) DEFAULT NULL,
  `customer_note` text,
  `discount_amount` decimal(12,2) DEFAULT NULL,
  `end_time` time DEFAULT NULL,
  `pet_breed_snapshot` varchar(120) DEFAULT NULL,
  `pet_name_snapshot` varchar(120) NOT NULL,
  `pickup_address` varchar(500) DEFAULT NULL,
  `pickup_latitude` decimal(10,7) DEFAULT NULL,
  `pickup_longitude` decimal(10,7) DEFAULT NULL,
  `price_amount` decimal(12,2) DEFAULT NULL,
  `promo_code` varchar(50) DEFAULT NULL,
  `shipping_fee` decimal(12,2) DEFAULT NULL,
  `start_time` time DEFAULT NULL,
  `status` varchar(30) NOT NULL,
  `time_slot` varchar(50) DEFAULT NULL,
  `total_amount` decimal(12,2) DEFAULT NULL,
  `area_id` bigint DEFAULT NULL,
  `pet_id` bigint NOT NULL,
  `service_id` bigint DEFAULT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKanlf8fyc0iftio1yec3md0e96` (`area_id`),
  KEY `FKk6mulu6p79fpuixxwn9cofpxe` (`pet_id`),
  KEY `FK6q0hkg62uj8y87l8yu6m5dehk` (`service_id`),
  KEY `FKr6qu863j9i8ektqd9vjk4h650` (`user_id`),
  CONSTRAINT `FK6q0hkg62uj8y87l8yu6m5dehk` FOREIGN KEY (`service_id`) REFERENCES `services` (`id`),
  CONSTRAINT `FKanlf8fyc0iftio1yec3md0e96` FOREIGN KEY (`area_id`) REFERENCES `areas` (`id`),
  CONSTRAINT `FKk6mulu6p79fpuixxwn9cofpxe` FOREIGN KEY (`pet_id`) REFERENCES `pets` (`id`),
  CONSTRAINT `FKr6qu863j9i8ektqd9vjk4h650` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `shipping_bookings`
--

LOCK TABLES `shipping_bookings` WRITE;
/*!40000 ALTER TABLE `shipping_bookings` DISABLE KEYS */;
/*!40000 ALTER TABLE `shipping_bookings` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `shipping_fee_configs`
--

DROP TABLE IF EXISTS `shipping_fee_configs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `shipping_fee_configs` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `is_active` bit(1) NOT NULL,
  `fee` decimal(12,2) NOT NULL,
  `from_km` decimal(8,2) NOT NULL,
  `to_km` decimal(8,2) DEFAULT NULL,
  `area_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKgttp6eboq5a9jqdcughs0y9yn` (`area_id`),
  CONSTRAINT `FKgttp6eboq5a9jqdcughs0y9yn` FOREIGN KEY (`area_id`) REFERENCES `areas` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `shipping_fee_configs`
--

LOCK TABLES `shipping_fee_configs` WRITE;
/*!40000 ALTER TABLE `shipping_fee_configs` DISABLE KEYS */;
INSERT INTO `shipping_fee_configs` VALUES (1,NULL,NULL,_binary '',0.00,0.00,3.00,1),(2,NULL,NULL,_binary '',15000.00,3.00,5.00,1),(3,NULL,NULL,_binary '',25000.00,5.00,10.00,1),(4,NULL,NULL,_binary '',35000.00,10.00,15.00,1),(5,NULL,NULL,_binary '',45000.00,15.00,20.00,1),(6,NULL,NULL,_binary '',60000.00,20.00,25.00,1);
/*!40000 ALTER TABLE `shipping_fee_configs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `shop_order_items`
--

DROP TABLE IF EXISTS `shop_order_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `shop_order_items` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `line_total` decimal(12,2) NOT NULL,
  `product_image_snapshot` varchar(500) DEFAULT NULL,
  `product_name_snapshot` varchar(180) NOT NULL,
  `product_sku_snapshot` varchar(100) DEFAULT NULL,
  `quantity` int NOT NULL,
  `unit_price` decimal(12,2) NOT NULL,
  `product_id` bigint NOT NULL,
  `shop_order_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKi0vjwbwbvjgdnetm6cms0pprh` (`product_id`),
  KEY `FKmmrop9hwor1kut8433j0vqtp2` (`shop_order_id`),
  CONSTRAINT `FKi0vjwbwbvjgdnetm6cms0pprh` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`),
  CONSTRAINT `FKmmrop9hwor1kut8433j0vqtp2` FOREIGN KEY (`shop_order_id`) REFERENCES `shop_orders` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `shop_order_items`
--

LOCK TABLES `shop_order_items` WRITE;
/*!40000 ALTER TABLE `shop_order_items` DISABLE KEYS */;
/*!40000 ALTER TABLE `shop_order_items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `shop_order_status_history`
--

DROP TABLE IF EXISTS `shop_order_status_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `shop_order_status_history` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `from_status` varchar(30) DEFAULT NULL,
  `note` varchar(255) DEFAULT NULL,
  `to_status` varchar(30) NOT NULL,
  `changed_by_user_id` bigint DEFAULT NULL,
  `shop_order_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKqnki5qb1l8wa76d7n4ijxkiuo` (`changed_by_user_id`),
  KEY `FKlcaqf8ty0dlk9pbit6dgjybvc` (`shop_order_id`),
  CONSTRAINT `FKlcaqf8ty0dlk9pbit6dgjybvc` FOREIGN KEY (`shop_order_id`) REFERENCES `shop_orders` (`id`),
  CONSTRAINT `FKqnki5qb1l8wa76d7n4ijxkiuo` FOREIGN KEY (`changed_by_user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
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
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `city` varchar(120) DEFAULT NULL,
  `country_code` char(2) NOT NULL,
  `currency_code` char(3) NOT NULL,
  `customer_note` text,
  `discount_amount` decimal(12,2) NOT NULL,
  `district` varchar(120) DEFAULT NULL,
  `internal_note` text,
  `order_code` varchar(32) NOT NULL,
  `payment_method` varchar(30) NOT NULL,
  `province` varchar(120) DEFAULT NULL,
  `receiver_email` varchar(190) DEFAULT NULL,
  `receiver_name` varchar(150) NOT NULL,
  `receiver_phone` varchar(30) NOT NULL,
  `shipping_address` varchar(255) NOT NULL,
  `shipping_fee_amount` decimal(12,2) NOT NULL,
  `status` varchar(30) NOT NULL,
  `subtotal_amount` decimal(12,2) NOT NULL,
  `tax_amount` decimal(12,2) NOT NULL,
  `total_amount` decimal(12,2) NOT NULL,
  `ward` varchar(120) DEFAULT NULL,
  `customer_user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKcifde9vjegw4b11ps8a54twfn` (`customer_user_id`),
  CONSTRAINT `FKcifde9vjegw4b11ps8a54twfn` FOREIGN KEY (`customer_user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `shop_orders`
--

LOCK TABLES `shop_orders` WRITE;
/*!40000 ALTER TABLE `shop_orders` DISABLE KEYS */;
/*!40000 ALTER TABLE `shop_orders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_favorite_services`
--

DROP TABLE IF EXISTS `user_favorite_services`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_favorite_services` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `service_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKmhulia5ynngjct6o5ftgis2iy` (`user_id`,`service_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_favorite_services`
--

LOCK TABLES `user_favorite_services` WRITE;
/*!40000 ALTER TABLE `user_favorite_services` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_favorite_services` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_roles`
--

DROP TABLE IF EXISTS `user_roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_roles` (
  `assigned_at` datetime(6) DEFAULT NULL,
  `role_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`role_id`,`user_id`),
  KEY `FKhfh9dx7w3ubf1co1vdev94g3f` (`user_id`),
  CONSTRAINT `FKh8ciramu9cc9q3qcqiv4ue8a6` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`),
  CONSTRAINT `FKhfh9dx7w3ubf1co1vdev94g3f` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_roles`
--

LOCK TABLES `user_roles` WRITE;
/*!40000 ALTER TABLE `user_roles` DISABLE KEYS */;
INSERT INTO `user_roles` VALUES (NULL,1,1),(NULL,2,2);
/*!40000 ALTER TABLE `user_roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_saved_locations`
--

DROP TABLE IF EXISTS `user_saved_locations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_saved_locations` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `address` varchar(500) DEFAULT NULL,
  `created_at` datetime(6) NOT NULL,
  `latitude` decimal(10,7) NOT NULL,
  `longitude` decimal(10,7) NOT NULL,
  `name` varchar(100) NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_saved_locations`
--

LOCK TABLES `user_saved_locations` WRITE;
/*!40000 ALTER TABLE `user_saved_locations` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_saved_locations` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `address_line1` varchar(255) DEFAULT NULL,
  `address_line2` varchar(255) DEFAULT NULL,
  `avatar_url` varchar(500) DEFAULT NULL,
  `city` varchar(120) DEFAULT NULL,
  `country_code` varchar(2) DEFAULT NULL,
  `cover_url` varchar(500) DEFAULT NULL,
  `date_of_birth` date DEFAULT NULL,
  `deleted_at` datetime(6) DEFAULT NULL,
  `district` varchar(120) DEFAULT NULL,
  `email` varchar(190) NOT NULL,
  `email_verified_at` datetime(6) DEFAULT NULL,
  `full_name` varchar(150) NOT NULL,
  `last_login_at` datetime(6) DEFAULT NULL,
  `otp_code` varchar(10) DEFAULT NULL,
  `otp_expiry_time` datetime(6) DEFAULT NULL,
  `password_hash` varchar(255) NOT NULL,
  `phone_number` varchar(30) DEFAULT NULL,
  `province` varchar(120) DEFAULT NULL,
  `status` varchar(20) DEFAULT NULL,
  `user_code` varchar(32) NOT NULL,
  `ward` varchar(120) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,NULL,NULL,NULL,NULL,'https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&q=80&w=400','Hồ Chí Minh','VN','https://images.unsplash.com/photo-1548199973-03cce0bbc87b?auto=format&fit=crop&q=80&w=1600',NULL,NULL,NULL,'user@petgo.local','2026-06-26 15:35:15.000000','PetGo Sample User',NULL,NULL,NULL,'$2a$10$n89Petg/CbVQPxO1Hzra7ug9/MjwIM5gqQBM2iz9CDnh5ZT6yWwoy','0919000001','Hồ Chí Minh','ACTIVE','USR-SAMPLE-USER',NULL),(2,NULL,NULL,NULL,NULL,'https://images.unsplash.com/photo-1607746882042-944635dfe10e?auto=format&fit=crop&q=80&w=400','Hồ Chí Minh','VN','https://images.unsplash.com/photo-1548199973-03cce0bbc87b?auto=format&fit=crop&q=80&w=1600',NULL,NULL,NULL,'admin@petgo.local','2026-06-26 15:35:15.000000','PetGo Sample Admin','2026-06-26 22:35:42.311008',NULL,NULL,'$2a$10$n89Petg/CbVQPxO1Hzra7ug9/MjwIM5gqQBM2iz9CDnh5ZT6yWwoy','0919000003','Hồ Chí Minh','ACTIVE','USR-SAMPLE-ADMIN',NULL);
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `wallet_settings`
--

DROP TABLE IF EXISTS `wallet_settings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `wallet_settings` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `setting_key` varchar(80) NOT NULL,
  `setting_value` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKicjfk4we2bsoqn71564p81ob2` (`setting_key`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `wallet_settings`
--

LOCK TABLES `wallet_settings` WRITE;
/*!40000 ALTER TABLE `wallet_settings` DISABLE KEYS */;
INSERT INTO `wallet_settings` VALUES (1,NULL,NULL,'WALLET_AUTO_CONFIRM_TOP_UP','false');
/*!40000 ALTER TABLE `wallet_settings` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `wallet_transactions`
--

DROP TABLE IF EXISTS `wallet_transactions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `wallet_transactions` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `amount` decimal(14,2) NOT NULL,
  `balance_after` decimal(14,2) DEFAULT NULL,
  `balance_before` decimal(14,2) DEFAULT NULL,
  `bank_account_holder` varchar(150) DEFAULT NULL,
  `bank_account_number` varchar(50) DEFAULT NULL,
  `bank_name` varchar(120) DEFAULT NULL,
  `checkout_url` varchar(1000) DEFAULT NULL,
  `gateway_name` varchar(50) DEFAULT NULL,
  `gateway_transaction_id` varchar(120) DEFAULT NULL,
  `note` varchar(500) DEFAULT NULL,
  `payment_content` varchar(255) DEFAULT NULL,
  `qr_code_text` varchar(2000) DEFAULT NULL,
  `review_note` varchar(500) DEFAULT NULL,
  `reviewed_at` datetime(6) DEFAULT NULL,
  `status` varchar(30) NOT NULL,
  `transaction_code` varchar(40) NOT NULL,
  `type` varchar(30) NOT NULL,
  `counterparty_user_id` bigint DEFAULT NULL,
  `reviewed_by_admin_id` bigint DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  `wallet_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKpviaukmpulq57esrtiqqii4ra` (`transaction_code`),
  KEY `FKq81bxcnoybccq58n4u57cd7dp` (`counterparty_user_id`),
  KEY `FK4yslu9gumupk9ooogjv8jryfe` (`reviewed_by_admin_id`),
  KEY `FKrtsa3qtjhd0rn4xb92na03vd` (`user_id`),
  KEY `FK8seu7b87ifqi09ghhssusmb0x` (`wallet_id`),
  CONSTRAINT `FK4yslu9gumupk9ooogjv8jryfe` FOREIGN KEY (`reviewed_by_admin_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FK8seu7b87ifqi09ghhssusmb0x` FOREIGN KEY (`wallet_id`) REFERENCES `wallets` (`id`),
  CONSTRAINT `FKq81bxcnoybccq58n4u57cd7dp` FOREIGN KEY (`counterparty_user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKrtsa3qtjhd0rn4xb92na03vd` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `wallet_transactions`
--

LOCK TABLES `wallet_transactions` WRITE;
/*!40000 ALTER TABLE `wallet_transactions` DISABLE KEYS */;
/*!40000 ALTER TABLE `wallet_transactions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `wallets`
--

DROP TABLE IF EXISTS `wallets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `wallets` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `balance` decimal(14,2) NOT NULL,
  `currency_code` varchar(3) NOT NULL,
  `held_balance` decimal(14,2) NOT NULL,
  `is_system` bit(1) NOT NULL,
  `status` varchar(20) NOT NULL,
  `user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKsswfdl9fq40xlkove1y5kc7kv` (`user_id`),
  CONSTRAINT `FKc1foyisidw7wqqrkamafuwn4e` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `wallets`
--

LOCK TABLES `wallets` WRITE;
/*!40000 ALTER TABLE `wallets` DISABLE KEYS */;
INSERT INTO `wallets` VALUES (1,NULL,NULL,0.00,'VND',0.00,_binary '\0','ACTIVE',1),(2,NULL,NULL,0.00,'VND',0.00,_binary '\0','ACTIVE',2),(4,NULL,NULL,0.00,'VND',0.00,_binary '','ACTIVE',NULL);
/*!40000 ALTER TABLE `wallets` ENABLE KEYS */;
UNLOCK TABLES;
SET @@SESSION.SQL_LOG_BIN = @MYSQLDUMP_TEMP_LOG_BIN;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-06-26 22:47:12
