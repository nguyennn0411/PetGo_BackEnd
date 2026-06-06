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

SET @@GLOBAL.GTID_PURGED=/*!80000 '+'*/ 'b80684da-b284-11f0-b076-e65896217b68:1-1930';

--
-- Table structure for table `booking_cancellations`
--

DROP TABLE IF EXISTS `booking_cancellations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `booking_cancellations` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `cancelled_at` datetime(6) DEFAULT NULL,
  `reason_code` varchar(50) NOT NULL,
  `reason_text` varchar(255) DEFAULT NULL,
  `refund_amount` decimal(12,2) NOT NULL,
  `refund_status` varchar(20) NOT NULL,
  `booking_id` bigint NOT NULL,
  `cancelled_by_user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKcgnsja4g1nn2fdq6d6efx817v` (`booking_id`),
  KEY `FKk60p6uraj6qhnxvk4p6oqh50j` (`cancelled_by_user_id`),
  CONSTRAINT `FKk60p6uraj6qhnxvk4p6oqh50j` FOREIGN KEY (`cancelled_by_user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKkv5apxsn243tesnsh87iihl32` FOREIGN KEY (`booking_id`) REFERENCES `bookings` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `booking_cancellations`
--

LOCK TABLES `booking_cancellations` WRITE;
/*!40000 ALTER TABLE `booking_cancellations` DISABLE KEYS */;
/*!40000 ALTER TABLE `booking_cancellations` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `booking_locks`
--

DROP TABLE IF EXISTS `booking_locks`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `booking_locks` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `appointment_date` date NOT NULL,
  `buffer_after_minutes` int NOT NULL,
  `duration_minutes` int NOT NULL,
  `end_time` time NOT NULL,
  `expires_at_utc` datetime(6) NOT NULL,
  `start_time` time NOT NULL,
  `status` varchar(20) NOT NULL,
  `provider_id` bigint NOT NULL,
  `provider_service_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK478m6jjy7xda0cgrdrismjjud` (`provider_id`),
  KEY `FKkj5h101oxw4g0jy1t1hpdab1m` (`provider_service_id`),
  KEY `FKqx0rt292lix12pkjjq229fhjn` (`user_id`),
  CONSTRAINT `FK478m6jjy7xda0cgrdrismjjud` FOREIGN KEY (`provider_id`) REFERENCES `provider_profiles` (`id`),
  CONSTRAINT `FKkj5h101oxw4g0jy1t1hpdab1m` FOREIGN KEY (`provider_service_id`) REFERENCES `provider_services` (`id`),
  CONSTRAINT `FKqx0rt292lix12pkjjq229fhjn` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `booking_locks`
--

LOCK TABLES `booking_locks` WRITE;
/*!40000 ALTER TABLE `booking_locks` DISABLE KEYS */;
/*!40000 ALTER TABLE `booking_locks` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `booking_reschedules`
--

DROP TABLE IF EXISTS `booking_reschedules`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `booking_reschedules` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `fee_amount` decimal(12,2) NOT NULL,
  `new_appointment_date` date NOT NULL,
  `new_end_time` time NOT NULL,
  `new_start_time` time NOT NULL,
  `note` varchar(255) DEFAULT NULL,
  `old_appointment_date` date NOT NULL,
  `old_end_time` time NOT NULL,
  `old_start_time` time NOT NULL,
  `status` varchar(20) NOT NULL,
  `booking_id` bigint NOT NULL,
  `requested_by_user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKimp26pnv97ebokeo15s323ja7` (`booking_id`),
  KEY `FKmca10wwvqe9n8obka2dik70bf` (`requested_by_user_id`),
  CONSTRAINT `FKimp26pnv97ebokeo15s323ja7` FOREIGN KEY (`booking_id`) REFERENCES `bookings` (`id`),
  CONSTRAINT `FKmca10wwvqe9n8obka2dik70bf` FOREIGN KEY (`requested_by_user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
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
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `from_status` varchar(30) DEFAULT NULL,
  `note` varchar(255) DEFAULT NULL,
  `to_status` varchar(30) NOT NULL,
  `booking_id` bigint NOT NULL,
  `changed_by_user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKqpj6h79qfscwluo5embxdap7c` (`booking_id`),
  KEY `FKaiby68yx1eddb0rrhlu40ris8` (`changed_by_user_id`),
  CONSTRAINT `FKaiby68yx1eddb0rrhlu40ris8` FOREIGN KEY (`changed_by_user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKqpj6h79qfscwluo5embxdap7c` FOREIGN KEY (`booking_id`) REFERENCES `bookings` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `booking_status_history`
--

LOCK TABLES `booking_status_history` WRITE;
/*!40000 ALTER TABLE `booking_status_history` DISABLE KEYS */;
/*!40000 ALTER TABLE `booking_status_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `bookings`
--

DROP TABLE IF EXISTS `bookings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `bookings` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `appointment_date` date NOT NULL,
  `booking_code` varchar(32) NOT NULL,
  `cancellation_reason_code` varchar(50) DEFAULT NULL,
  `currency_code` varchar(3) NOT NULL,
  `customer_note` text,
  `end_time` time NOT NULL,
  `internal_note` text,
  `membership_discount_amount` decimal(12,2) NOT NULL,
  `pet_breed_snapshot` varchar(120) DEFAULT NULL,
  `pet_name_snapshot` varchar(120) NOT NULL,
  `promo_discount_amount` decimal(12,2) NOT NULL,
  `provider_address_snapshot` varchar(255) DEFAULT NULL,
  `provider_name_snapshot` varchar(180) NOT NULL,
  `provider_phone_snapshot` varchar(30) DEFAULT NULL,
  `reschedule_count` int NOT NULL,
  `service_description_snapshot` varchar(255) DEFAULT NULL,
  `service_duration_minutes_snapshot` int NOT NULL,
  `service_name_snapshot` varchar(150) NOT NULL,
  `start_time` time NOT NULL,
  `status` varchar(30) NOT NULL,
  `subtotal_amount` decimal(12,2) NOT NULL,
  `tax_amount` decimal(12,2) NOT NULL,
  `timezone` varchar(50) NOT NULL,
  `total_amount` decimal(12,2) NOT NULL,
  `availability_slot_id` bigint DEFAULT NULL,
  `customer_user_id` bigint NOT NULL,
  `pet_id` bigint NOT NULL,
  `provider_id` bigint NOT NULL,
  `provider_service_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKkpkp0em9elo7pbty3iyaxw2ty` (`availability_slot_id`),
  KEY `FKiyid09v50r0l4ulr7xw9vn5i6` (`customer_user_id`),
  KEY `FKtqiynyn86mqebi3mcwfn4ecvl` (`pet_id`),
  KEY `FKtmuymbx6nj0bjb7jqgfn9o56h` (`provider_id`),
  KEY `FK9au2xq5yg9etrwkdlk87ggsvd` (`provider_service_id`),
  CONSTRAINT `FK9au2xq5yg9etrwkdlk87ggsvd` FOREIGN KEY (`provider_service_id`) REFERENCES `provider_services` (`id`),
  CONSTRAINT `FKiyid09v50r0l4ulr7xw9vn5i6` FOREIGN KEY (`customer_user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKkpkp0em9elo7pbty3iyaxw2ty` FOREIGN KEY (`availability_slot_id`) REFERENCES `provider_availability_slots` (`id`),
  CONSTRAINT `FKtmuymbx6nj0bjb7jqgfn9o56h` FOREIGN KEY (`provider_id`) REFERENCES `provider_profiles` (`id`),
  CONSTRAINT `FKtqiynyn86mqebi3mcwfn4ecvl` FOREIGN KEY (`pet_id`) REFERENCES `pets` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bookings`
--

LOCK TABLES `bookings` WRITE;
/*!40000 ALTER TABLE `bookings` DISABLE KEYS */;
/*!40000 ALTER TABLE `bookings` ENABLE KEYS */;
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
-- Table structure for table `chat_conversations`
--

DROP TABLE IF EXISTS `chat_conversations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `chat_conversations` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `last_message_at` datetime(6) DEFAULT NULL,
  `last_message_preview` varchar(255) DEFAULT NULL,
  `status` varchar(30) NOT NULL,
  `title` varchar(180) DEFAULT NULL,
  `type` varchar(40) NOT NULL,
  `booking_id` bigint DEFAULT NULL,
  `created_by_user_id` bigint NOT NULL,
  `provider_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKi87ibmpm8biygooyfubit6gyq` (`booking_id`),
  KEY `FK73dlgkwsmmwrm9qud1h69haf5` (`created_by_user_id`),
  KEY `FK2acqlnemlqoaaa5itjxn1b6wy` (`provider_id`),
  CONSTRAINT `FK2acqlnemlqoaaa5itjxn1b6wy` FOREIGN KEY (`provider_id`) REFERENCES `provider_profiles` (`id`),
  CONSTRAINT `FK73dlgkwsmmwrm9qud1h69haf5` FOREIGN KEY (`created_by_user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKi87ibmpm8biygooyfubit6gyq` FOREIGN KEY (`booking_id`) REFERENCES `bookings` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chat_conversations`
--

LOCK TABLES `chat_conversations` WRITE;
/*!40000 ALTER TABLE `chat_conversations` DISABLE KEYS */;
/*!40000 ALTER TABLE `chat_conversations` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `chat_messages`
--

DROP TABLE IF EXISTS `chat_messages`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `chat_messages` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `attachment_url` varchar(500) DEFAULT NULL,
  `content` text NOT NULL,
  `deleted_at` datetime(6) DEFAULT NULL,
  `message_type` varchar(30) NOT NULL,
  `status` varchar(30) NOT NULL,
  `conversation_id` bigint NOT NULL,
  `sender_user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKqgkanrr90j46564w4ww63jcna` (`conversation_id`),
  KEY `FKg9d1odxgyj8y7in19vun7txwq` (`sender_user_id`),
  CONSTRAINT `FKg9d1odxgyj8y7in19vun7txwq` FOREIGN KEY (`sender_user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKqgkanrr90j46564w4ww63jcna` FOREIGN KEY (`conversation_id`) REFERENCES `chat_conversations` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chat_messages`
--

LOCK TABLES `chat_messages` WRITE;
/*!40000 ALTER TABLE `chat_messages` DISABLE KEYS */;
/*!40000 ALTER TABLE `chat_messages` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `chat_participants`
--

DROP TABLE IF EXISTS `chat_participants`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `chat_participants` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `joined_at` datetime(6) NOT NULL,
  `left_at` datetime(6) DEFAULT NULL,
  `role_in_chat` varchar(30) NOT NULL,
  `conversation_id` bigint NOT NULL,
  `last_read_message_id` bigint DEFAULT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKi85lwwmn3y0tntqrpuqxiw1c4` (`conversation_id`),
  KEY `FKfr3fqvw5ctrie6cinhvvycbpg` (`last_read_message_id`),
  KEY `FKbhdyxo0ndtbs1t49l28y21rkw` (`user_id`),
  CONSTRAINT `FKbhdyxo0ndtbs1t49l28y21rkw` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKfr3fqvw5ctrie6cinhvvycbpg` FOREIGN KEY (`last_read_message_id`) REFERENCES `chat_messages` (`id`),
  CONSTRAINT `FKi85lwwmn3y0tntqrpuqxiw1c4` FOREIGN KEY (`conversation_id`) REFERENCES `chat_conversations` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chat_participants`
--

LOCK TABLES `chat_participants` WRITE;
/*!40000 ALTER TABLE `chat_participants` DISABLE KEYS */;
/*!40000 ALTER TABLE `chat_participants` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `favorites`
--

DROP TABLE IF EXISTS `favorites`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `favorites` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `provider_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_favorites_user_provider` (`user_id`,`provider_id`),
  KEY `FK3pn2r7wypdvufuliqfmrt1noh` (`provider_id`),
  CONSTRAINT `FK3pn2r7wypdvufuliqfmrt1noh` FOREIGN KEY (`provider_id`) REFERENCES `provider_profiles` (`id`),
  CONSTRAINT `FKk7du8b8ewipawnnpg76d55fus` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `favorites`
--

LOCK TABLES `favorites` WRITE;
/*!40000 ALTER TABLE `favorites` DISABLE KEYS */;
/*!40000 ALTER TABLE `favorites` ENABLE KEYS */;
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
  `booking_id` bigint DEFAULT NULL,
  `membership_subscription_id` bigint DEFAULT NULL,
  `shop_order_id` bigint DEFAULT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKb9bhb7xre5v64qvjeholh3qj0` (`booking_id`),
  KEY `FKlsc8a4g4j6fpuaiaeuv8aqovo` (`membership_subscription_id`),
  KEY `FKnf9gye2fx8fwi9cx5i9hjnvlj` (`shop_order_id`),
  KEY `FKbwr4d4vyqf2bkoetxtt8j9dx7` (`user_id`),
  CONSTRAINT `FKb9bhb7xre5v64qvjeholh3qj0` FOREIGN KEY (`booking_id`) REFERENCES `bookings` (`id`),
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
  `category` enum('ACCOUNT','BOOKING','MEMBERSHIP','PARTNER','PAYMENT','PROMOTION','SYSTEM') NOT NULL,
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
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pet_photos`
--

LOCK TABLES `pet_photos` WRITE;
/*!40000 ALTER TABLE `pet_photos` DISABLE KEYS */;
INSERT INTO `pet_photos` VALUES (1,NULL,NULL,'https://res.cloudinary.com/dxaok6qzg/image/upload/v1780555706/petgo/pets/avatar/f0b9a5fa89174a5d8e73792752c24cb1.png',_binary '',0,1),(2,NULL,NULL,'https://res.cloudinary.com/dxaok6qzg/image/upload/v1780561209/petgo/pets/avatar/a3f83c310ed642938c12f490d941acdb.png',_binary '',0,2);
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
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pets`
--

LOCK TABLES `pets` WRITE;
/*!40000 ALTER TABLE `pets` DISABLE KEYS */;
INSERT INTO `pets` VALUES (1,NULL,NULL,NULL,NULL,'https://res.cloudinary.com/dxaok6qzg/image/upload/v1780555706/petgo/pets/avatar/f0b9a5fa89174a5d8e73792752c24cb1.png',NULL,NULL,NULL,NULL,'2026-06-04 15:19:48.389747','UNKNOWN',NULL,'hhh','PET-E9E406E1D2','UNKNOWN','DOG','INACTIVE',NULL,NULL,1),(2,NULL,NULL,NULL,NULL,'https://res.cloudinary.com/dxaok6qzg/image/upload/v1780561209/petgo/pets/avatar/a3f83c310ed642938c12f490d941acdb.png',NULL,NULL,NULL,NULL,NULL,'UNKNOWN',NULL,'777','PET-2CD3E61638','UNKNOWN','DOG','ACTIVE',NULL,NULL,1);
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product_categories`
--

LOCK TABLES `product_categories` WRITE;
/*!40000 ALTER TABLE `product_categories` DISABLE KEYS */;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `products`
--

LOCK TABLES `products` WRITE;
/*!40000 ALTER TABLE `products` DISABLE KEYS */;
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
  `booking_id` bigint DEFAULT NULL,
  `invoice_id` bigint DEFAULT NULL,
  `membership_subscription_id` bigint DEFAULT NULL,
  `promo_code_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKkdensx9y4l3f6rsvnlqfo32qp` (`booking_id`),
  KEY `FK84sdgdf5kkdf6px3xuf517ulw` (`invoice_id`),
  KEY `FKseeh8jp7q4xkmb9a24k9bxdwo` (`membership_subscription_id`),
  KEY `FKr52safg3hage6cqkojclv6yn1` (`promo_code_id`),
  KEY `FK8mln1obonekngrm1skqh2m91p` (`user_id`),
  CONSTRAINT `FK84sdgdf5kkdf6px3xuf517ulw` FOREIGN KEY (`invoice_id`) REFERENCES `invoices` (`id`),
  CONSTRAINT `FK8mln1obonekngrm1skqh2m91p` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKkdensx9y4l3f6rsvnlqfo32qp` FOREIGN KEY (`booking_id`) REFERENCES `bookings` (`id`),
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
  `provider_ids` varchar(1000) DEFAULT NULL,
  `provider_service_ids` varchar(1000) DEFAULT NULL,
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
  `provider_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKoclkwb6aq06kietanmwnt1rco` (`created_by_user_id`),
  KEY `FKn3x5ph15snxpkw895n0o0qp3m` (`provider_id`),
  CONSTRAINT `FKn3x5ph15snxpkw895n0o0qp3m` FOREIGN KEY (`provider_id`) REFERENCES `provider_profiles` (`id`),
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
-- Table structure for table `provider_availability_slots`
--

DROP TABLE IF EXISTS `provider_availability_slots`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `provider_availability_slots` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `capacity_booked` int NOT NULL,
  `capacity_total` int NOT NULL,
  `end_time` time NOT NULL,
  `note` varchar(255) DEFAULT NULL,
  `slot_date` date NOT NULL,
  `slot_status` varchar(20) NOT NULL,
  `start_time` time NOT NULL,
  `provider_id` bigint NOT NULL,
  `provider_service_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKf8ye92cni7e8ctxot2g5x7g70` (`provider_id`),
  KEY `FK169q5q8ny4wvv9e08ctulu3ru` (`provider_service_id`),
  CONSTRAINT `FK169q5q8ny4wvv9e08ctulu3ru` FOREIGN KEY (`provider_service_id`) REFERENCES `provider_services` (`id`),
  CONSTRAINT `FKf8ye92cni7e8ctxot2g5x7g70` FOREIGN KEY (`provider_id`) REFERENCES `provider_profiles` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `provider_availability_slots`
--

LOCK TABLES `provider_availability_slots` WRITE;
/*!40000 ALTER TABLE `provider_availability_slots` DISABLE KEYS */;
/*!40000 ALTER TABLE `provider_availability_slots` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `provider_booking_policies`
--

DROP TABLE IF EXISTS `provider_booking_policies`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `provider_booking_policies` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `allow_user_reschedule` bit(1) NOT NULL,
  `cancel_fee_amount` decimal(12,2) NOT NULL,
  `cancel_fee_applies_after_hours` int DEFAULT NULL,
  `cancel_fee_type` varchar(20) NOT NULL,
  `cancel_window_hours` int NOT NULL,
  `max_reschedules_per_booking` int NOT NULL,
  `reschedule_window_hours` int NOT NULL,
  `timezone` varchar(50) NOT NULL,
  `provider_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKr50dyx9y5ntx57o5yc830vv34` (`provider_id`),
  CONSTRAINT `FKftxolhg13j4l5c7hkm75cmglv` FOREIGN KEY (`provider_id`) REFERENCES `provider_profiles` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `provider_booking_policies`
--

LOCK TABLES `provider_booking_policies` WRITE;
/*!40000 ALTER TABLE `provider_booking_policies` DISABLE KEYS */;
/*!40000 ALTER TABLE `provider_booking_policies` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `provider_business_hours`
--

DROP TABLE IF EXISTS `provider_business_hours`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `provider_business_hours` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `break_ends_at` time DEFAULT NULL,
  `break_starts_at` time DEFAULT NULL,
  `is_closed` bit(1) NOT NULL,
  `closes_at` time DEFAULT NULL,
  `opens_at` time DEFAULT NULL,
  `weekday` int NOT NULL,
  `provider_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKambq2lptpnm3vkftorw3q1hbt` (`provider_id`),
  CONSTRAINT `FKambq2lptpnm3vkftorw3q1hbt` FOREIGN KEY (`provider_id`) REFERENCES `provider_profiles` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `provider_business_hours`
--

LOCK TABLES `provider_business_hours` WRITE;
/*!40000 ALTER TABLE `provider_business_hours` DISABLE KEYS */;
INSERT INTO `provider_business_hours` VALUES (1,NULL,NULL,NULL,NULL,_binary '\0','18:00:00','08:00:00',1,1),(2,NULL,NULL,NULL,NULL,_binary '\0','18:00:00','08:00:00',2,1),(3,NULL,NULL,NULL,NULL,_binary '\0','18:00:00','08:00:00',3,1),(4,NULL,NULL,NULL,NULL,_binary '\0','18:00:00','08:00:00',4,1),(5,NULL,NULL,NULL,NULL,_binary '\0','18:00:00','08:00:00',5,1),(6,NULL,NULL,NULL,NULL,_binary '\0','18:00:00','08:00:00',6,1),(7,NULL,NULL,NULL,NULL,_binary '',NULL,NULL,7,1);
/*!40000 ALTER TABLE `provider_business_hours` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `provider_photos`
--

DROP TABLE IF EXISTS `provider_photos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `provider_photos` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `media_type` varchar(20) NOT NULL,
  `photo_url` varchar(500) NOT NULL,
  `is_primary` bit(1) NOT NULL,
  `sort_order` int NOT NULL,
  `provider_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKid1qyjo6yrjso09ghrs3uclp7` (`provider_id`),
  CONSTRAINT `FKid1qyjo6yrjso09ghrs3uclp7` FOREIGN KEY (`provider_id`) REFERENCES `provider_profiles` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `provider_photos`
--

LOCK TABLES `provider_photos` WRITE;
/*!40000 ALTER TABLE `provider_photos` DISABLE KEYS */;
INSERT INTO `provider_photos` VALUES (2,NULL,NULL,'IMAGE','https://res.cloudinary.com/dxaok6qzg/image/upload/v1780554359/petgo/partner/services/d94d2e3c72fc480bbe3f398a749434f2.png',_binary '',0,1),(3,NULL,NULL,'IMAGE','https://res.cloudinary.com/dxaok6qzg/image/upload/v1780554391/petgo/partner/services/566869d0bc6b4b2ab6bffd3b0414d25d.png',_binary '\0',1,1);
/*!40000 ALTER TABLE `provider_photos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `provider_profiles`
--

DROP TABLE IF EXISTS `provider_profiles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `provider_profiles` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `accepts_instant_booking` bit(1) NOT NULL,
  `accepts_membership` bit(1) NOT NULL,
  `average_rating` decimal(3,2) NOT NULL,
  `business_name` varchar(180) NOT NULL,
  `cancellation_free_hours` int NOT NULL,
  `city` varchar(120) DEFAULT NULL,
  `country_code` varchar(2) NOT NULL,
  `cover_image_url` varchar(500) DEFAULT NULL,
  `currency_code` varchar(3) NOT NULL,
  `deleted_at` datetime(6) DEFAULT NULL,
  `description` text,
  `district` varchar(120) DEFAULT NULL,
  `emergency_phone` varchar(30) DEFAULT NULL,
  `is_featured` bit(1) NOT NULL,
  `headline` varchar(255) DEFAULT NULL,
  `is_hot` bit(1) NOT NULL,
  `latitude` decimal(10,7) DEFAULT NULL,
  `longitude` decimal(10,7) DEFAULT NULL,
  `main_image_url` varchar(500) DEFAULT NULL,
  `price_from_amount` decimal(12,2) DEFAULT NULL,
  `primary_address_line1` varchar(255) DEFAULT NULL,
  `primary_address_line2` varchar(255) DEFAULT NULL,
  `provider_code` varchar(32) NOT NULL,
  `provider_type` varchar(30) NOT NULL,
  `province` varchar(120) DEFAULT NULL,
  `service_radius_km` decimal(6,2) DEFAULT NULL,
  `slug` varchar(190) NOT NULL,
  `status` varchar(20) NOT NULL,
  `total_completed_bookings` int NOT NULL,
  `total_reviews` int NOT NULL,
  `verification_status` varchar(20) NOT NULL,
  `ward` varchar(120) DEFAULT NULL,
  `years_experience` int DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK1cw1chybknyjxsb2ihgl2pic5` (`user_id`),
  CONSTRAINT `FKsg72i9r07mvl4th8r2ihxuwg9` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `provider_profiles`
--

LOCK TABLES `provider_profiles` WRITE;
/*!40000 ALTER TABLE `provider_profiles` DISABLE KEYS */;
INSERT INTO `provider_profiles` VALUES (1,NULL,NULL,_binary '',_binary '',4.80,'PetGo Provider',24,NULL,'VN','https://res.cloudinary.com/dxaok6qzg/image/upload/v1780553827/petgo/partner/services/93f26b8f96a9493f9fd71c3b42fdcb08.jpg','VND',NULL,'Tài khoản nhà cung cấp mẫu để kiểm thử luồng đối tác PetGo.',NULL,'0919000002',_binary '',NULL,_binary '',10.7769000,106.7009000,'https://res.cloudinary.com/dxaok6qzg/image/upload/v1780553812/petgo/partner/services/edab46f78d1843048e1745ca9967922e.jpg',200000.00,'HOLA',NULL,'PRV-SAMPLE-PROVIDER','BUSINESS',NULL,8.00,'petgo-sample-provider','ACTIVE',120,25,'VERIFIED','HALO',3,2);
/*!40000 ALTER TABLE `provider_profiles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `provider_schedule_exceptions`
--

DROP TABLE IF EXISTS `provider_schedule_exceptions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `provider_schedule_exceptions` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `ends_at_local` time DEFAULT NULL,
  `local_date` date NOT NULL,
  `max_concurrent_override` int DEFAULT NULL,
  `reason` text,
  `starts_at_local` time DEFAULT NULL,
  `type` varchar(30) NOT NULL,
  `provider_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKgqx2jpln5wqshk4s2a8ntrt41` (`provider_id`),
  CONSTRAINT `FKgqx2jpln5wqshk4s2a8ntrt41` FOREIGN KEY (`provider_id`) REFERENCES `provider_profiles` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `provider_schedule_exceptions`
--

LOCK TABLES `provider_schedule_exceptions` WRITE;
/*!40000 ALTER TABLE `provider_schedule_exceptions` DISABLE KEYS */;
/*!40000 ALTER TABLE `provider_schedule_exceptions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `provider_service_change_requests`
--

DROP TABLE IF EXISTS `provider_service_change_requests`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `provider_service_change_requests` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `admin_message` text,
  `category_ids` varchar(500) DEFAULT NULL,
  `currency_code` varchar(3) DEFAULT NULL,
  `description` text,
  `photo_urls` text,
  `price_amount` decimal(12,2) DEFAULT NULL,
  `price_unit` varchar(40) DEFAULT NULL,
  `request_type` varchar(20) NOT NULL,
  `reviewed_at` datetime(6) DEFAULT NULL,
  `service_name` varchar(150) DEFAULT NULL,
  `status` varchar(20) NOT NULL,
  `submitted_at` datetime(6) DEFAULT NULL,
  `provider_id` bigint NOT NULL,
  `provider_service_id` bigint DEFAULT NULL,
  `reviewer_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKq0asn68boal9pfcsg8gtigrvj` (`provider_id`),
  KEY `FKla02jy0g70p50t5xxlfr7sbie` (`provider_service_id`),
  KEY `FK405o2tol5tr7l0pk0tnhjroar` (`reviewer_id`),
  CONSTRAINT `FK405o2tol5tr7l0pk0tnhjroar` FOREIGN KEY (`reviewer_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKla02jy0g70p50t5xxlfr7sbie` FOREIGN KEY (`provider_service_id`) REFERENCES `provider_services` (`id`),
  CONSTRAINT `FKq0asn68boal9pfcsg8gtigrvj` FOREIGN KEY (`provider_id`) REFERENCES `provider_profiles` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `provider_service_change_requests`
--

LOCK TABLES `provider_service_change_requests` WRITE;
/*!40000 ALTER TABLE `provider_service_change_requests` DISABLE KEYS */;
INSERT INTO `provider_service_change_requests` VALUES (1,NULL,NULL,'Yêu cầu dịch vụ đã được duyệt.','21,1,7','VND','1','https://res.cloudinary.com/dxaok6qzg/image/upload/v1780511727/petgo/partner/services/019883bbb11442d8b9f3902988c1227a.jpg',1000.00,'SESSION','CREATE','2026-06-04 02:11:56.776682','sv1','APPROVED','2026-06-04 01:35:53.863822',1,1,3);
/*!40000 ALTER TABLE `provider_service_change_requests` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `provider_services`
--

DROP TABLE IF EXISTS `provider_services`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `provider_services` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `is_active` bit(1) NOT NULL,
  `approval_status` varchar(20) DEFAULT NULL,
  `booking_buffer_minutes` int NOT NULL,
  `buffer_after_minutes` int NOT NULL,
  `capacity_per_slot` int NOT NULL,
  `category_ids` varchar(500) DEFAULT NULL,
  `currency_code` varchar(3) NOT NULL,
  `custom_name` varchar(150) DEFAULT NULL,
  `description` text,
  `display_order` int NOT NULL,
  `duration_minutes` int NOT NULL,
  `duration_type` varchar(30) NOT NULL,
  `is_featured` bit(1) NOT NULL,
  `photo_urls` text,
  `price_amount` decimal(12,2) NOT NULL,
  `price_unit` varchar(20) NOT NULL,
  `short_description` varchar(255) DEFAULT NULL,
  `provider_id` bigint NOT NULL,
  `service_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKrl8vuw8tonb82kp6aq38i43dt` (`provider_id`),
  KEY `FKei4xs3227pwcwgkxkwwvdj70j` (`service_id`),
  CONSTRAINT `FKei4xs3227pwcwgkxkwwvdj70j` FOREIGN KEY (`service_id`) REFERENCES `services` (`id`),
  CONSTRAINT `FKrl8vuw8tonb82kp6aq38i43dt` FOREIGN KEY (`provider_id`) REFERENCES `provider_profiles` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `provider_services`
--

LOCK TABLES `provider_services` WRITE;
/*!40000 ALTER TABLE `provider_services` DISABLE KEYS */;
INSERT INTO `provider_services` VALUES (1,NULL,NULL,_binary '','APPROVED',0,0,1,'21,1,7','VND','sv1','1',0,60,'FIXED',_binary '\0','https://res.cloudinary.com/dxaok6qzg/image/upload/v1780511727/petgo/partner/services/019883bbb11442d8b9f3902988c1227a.jpg',1000.00,'SESSION','1',1,1);
/*!40000 ALTER TABLE `provider_services` ENABLE KEYS */;
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
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `refresh_tokens`
--

LOCK TABLES `refresh_tokens` WRITE;
/*!40000 ALTER TABLE `refresh_tokens` DISABLE KEYS */;
INSERT INTO `refresh_tokens` VALUES (1,NULL,'2026-07-04 01:33:51.000000','2026-06-04 12:47:06.519365','bc601dcaa96f1ae7b34806b3c4e8560cb5e18577cbaedda33beda6f13cd82ace',2),(2,NULL,'2026-07-04 01:39:43.000000',NULL,'40f37b9c8944613b72cd99b67abe5b2d07c0eba97f2626b4c87616676691886f',3),(3,NULL,'2026-07-04 12:37:29.000000',NULL,'fdd7544f50002c00207bcdadea9ee66843407cf4407c818847fc70070f239c46',1),(4,NULL,'2026-07-04 12:47:06.000000',NULL,'aca87d5a8b183c89de05370bfa1d8dc91b94afbe4c37c87f58e8d4b6837509ee',2),(5,NULL,'2026-07-04 12:47:06.000000',NULL,'cf99317b24b354cc8da87f1b6e76aadb365c8c525a1eaa63928fb0bbe6a5651b',2),(6,NULL,'2026-07-04 13:09:54.000000',NULL,'71f69f29cb00fbc322f195bceeef37290cf828083fa31b52f685aaff50502222',2),(7,NULL,'2026-07-04 13:10:41.000000',NULL,'38cbcbb5edf3167fb775e815d585dff4d036cefcf64b98b815c25ccaa57de363',2),(8,NULL,'2026-07-04 13:18:08.000000',NULL,'59209990135a13f0d946a403d823f4d0c2a613d1fb25b3edb2d55ca15636c40a',1),(9,NULL,'2026-07-04 13:37:01.000000',NULL,'c10c47a6be1cb1a355541921e9e8801578ad7a9e6ad6bcfb2304df1155db86ff',1),(10,NULL,'2026-07-04 13:56:02.000000','2026-06-04 17:44:33.328905','c069268ef1a88ca24296d2f04b698028bfd61d7b06dcd0af859e8e6be3a3cda8',1),(11,NULL,'2026-07-04 13:56:37.000000',NULL,'eaa4d2646cfbf7c3bdc762f8d25eff796c6bb0ef7fd21cece2e259f1d8a9f280',1),(12,NULL,'2026-07-04 14:03:20.000000',NULL,'78498fb6dbc50e27645bdc0ee08b92b7729adce9914d0874cadb25796e0745b6',1),(13,NULL,'2026-07-04 14:04:19.000000',NULL,'1aa7a52ca491f8040e88a415e8c80f512054672ead330122ee50ac0e548983bb',2),(14,NULL,'2026-07-04 15:16:24.000000',NULL,'00640594f45432e7824866f6bd2cdf4287c19082f3ef3cf4c39d77d42abb253c',1),(15,NULL,'2026-07-04 17:44:33.000000',NULL,'c8078a0bed327ca06abb9cf8daaf775e131d21ed0bce646658bf18e8c3a262aa',1),(16,NULL,'2026-07-04 17:44:33.000000',NULL,'f6f9b67fa7a17b00ec9091dc6adb169bf29d6c72b6cd319283d12bc70adb2653',1),(17,NULL,'2026-07-04 18:20:26.000000',NULL,'7a4b97fe84f300e7aea97175365c76f7b254012fe29dce3dd6143f118d2a0e0f',1),(18,NULL,'2026-07-06 14:43:43.000000',NULL,'06687eff66e3f2a5a8e1fe3390ee1816245edc721a4cbe2ea027dbd29ad384b7',1);
/*!40000 ALTER TABLE `refresh_tokens` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `registration_applications`
--

DROP TABLE IF EXISTS `registration_applications`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `registration_applications` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `additional_information` text,
  `admin_message` text,
  `business_address` varchar(500) NOT NULL,
  `business_email` varchar(255) NOT NULL,
  `business_name` varchar(255) NOT NULL,
  `business_phone` varchar(50) NOT NULL,
  `description` text,
  `location_image_urls` text,
  `rejection_reason` text,
  `representative_email` varchar(255) NOT NULL,
  `representative_name` varchar(255) NOT NULL,
  `representative_phone` varchar(50) NOT NULL,
  `reviewed_at` datetime(6) DEFAULT NULL,
  `service_category_ids` varchar(500) DEFAULT NULL,
  `status` enum('APPROVED','AWAITING_APPROVAL','DRAFT','NEEDS_MORE_INFO','REJECTED') NOT NULL,
  `submitted_at` datetime(6) DEFAULT NULL,
  `tax_code` varchar(100) DEFAULT NULL,
  `type` enum('AFFILIATE','PARTNER') NOT NULL,
  `reviewer_id` bigint DEFAULT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_registration_user_type` (`user_id`,`type`),
  KEY `FKp72gkyn95x3qnso2whwilgkis` (`reviewer_id`),
  CONSTRAINT `FK7c6207svb723s2t4y06ppq8q3` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKp72gkyn95x3qnso2whwilgkis` FOREIGN KEY (`reviewer_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `registration_applications`
--

LOCK TABLES `registration_applications` WRITE;
/*!40000 ALTER TABLE `registration_applications` DISABLE KEYS */;
/*!40000 ALTER TABLE `registration_applications` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `review_photos`
--

DROP TABLE IF EXISTS `review_photos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `review_photos` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `photo_url` varchar(500) NOT NULL,
  `sort_order` int NOT NULL,
  `review_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKunrlxq8kevetatdevbd9xbp1` (`review_id`),
  CONSTRAINT `FKunrlxq8kevetatdevbd9xbp1` FOREIGN KEY (`review_id`) REFERENCES `reviews` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `review_photos`
--

LOCK TABLES `review_photos` WRITE;
/*!40000 ALTER TABLE `review_photos` DISABLE KEYS */;
/*!40000 ALTER TABLE `review_photos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reviews`
--

DROP TABLE IF EXISTS `reviews`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reviews` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `comment` text,
  `deleted_at` datetime(6) DEFAULT NULL,
  `rating` int NOT NULL,
  `status` varchar(20) NOT NULL,
  `booking_id` bigint NOT NULL,
  `customer_user_id` bigint NOT NULL,
  `provider_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK28an517hrxtt2bsg93uefugrm` (`booking_id`),
  KEY `FKecvyhigqvrf2vaag56ug4hfoo` (`customer_user_id`),
  KEY `FK34m2xmuydjfidk0o8rtcfllqs` (`provider_id`),
  CONSTRAINT `FK28an517hrxtt2bsg93uefugrm` FOREIGN KEY (`booking_id`) REFERENCES `bookings` (`id`),
  CONSTRAINT `FK34m2xmuydjfidk0o8rtcfllqs` FOREIGN KEY (`provider_id`) REFERENCES `provider_profiles` (`id`),
  CONSTRAINT `FKecvyhigqvrf2vaag56ug4hfoo` FOREIGN KEY (`customer_user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reviews`
--

LOCK TABLES `reviews` WRITE;
/*!40000 ALTER TABLE `reviews` DISABLE KEYS */;
/*!40000 ALTER TABLE `reviews` ENABLE KEYS */;
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
  `code` enum('ADMIN','PROVIDER','USER') NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `name` varchar(100) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `roles`
--

LOCK TABLES `roles` WRITE;
/*!40000 ALTER TABLE `roles` DISABLE KEYS */;
INSERT INTO `roles` VALUES (1,NULL,NULL,'USER','Người dùng hệ thống','User'),(2,NULL,NULL,'PROVIDER','Đối tác cung cấp dịch vụ','Provider'),(3,NULL,NULL,'ADMIN','Quản trị hệ thống','Administrator');
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
) ENGINE=InnoDB AUTO_INCREMENT=33 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `service_categories`
--

LOCK TABLES `service_categories` WRITE;
/*!40000 ALTER TABLE `service_categories` DISABLE KEYS */;
INSERT INTO `service_categories` VALUES (1,_binary '','Các dịch vụ vệ sinh, tắm gội, cắt tỉa và chăm sóc ngoại hình cho thú cưng.','Chăm sóc & spa thú cưng',NULL),(2,_binary '','Dịch vụ khách sạn, daycare, trông giữ tại nhà và chăm sóc trong thời gian chủ vắng mặt.','Lưu trú & trông giữ',NULL),(3,_binary '','Dịch vụ kiểm tra sức khỏe, tiêm phòng, tư vấn và chăm sóc y tế cơ bản cho thú cưng.','Sức khỏe & thú y',NULL),(4,_binary '','Dịch vụ huấn luyện vâng lời, chỉnh hành vi và phát triển kỹ năng xã hội cho thú cưng.','Huấn luyện & hành vi',NULL),(5,_binary '','Dịch vụ đưa đón, dắt đi dạo, chăm sóc tận nơi và hỗ trợ sinh hoạt hằng ngày.','Di chuyển & hỗ trợ tại nhà',NULL),(6,_binary '','Tắm, sấy, vệ sinh tai, cắt móng và chăm sóc vệ sinh định kỳ.','Tắm gội & vệ sinh cơ bản',1),(7,_binary '','Cắt lông, tạo kiểu, tỉa lông theo giống và nhu cầu thẩm mỹ.','Cắt tỉa & tạo kiểu',1),(8,_binary '','Chăm sóc da lông, khử mùi, dưỡng lông và các gói thư giãn chuyên sâu.','Spa trị liệu',1),(9,_binary '','Lưu trú qua đêm hoặc nhiều ngày tại cơ sở chăm sóc thú cưng.','Khách sạn thú cưng',2),(10,_binary '','Trông giữ, cho ăn, vui chơi và theo dõi thú cưng trong ngày.','Daycare ban ngày',2),(11,_binary '','Người chăm sóc đến nhà khách hoặc nhận chăm thú cưng tại nhà riêng theo thỏa thuận.','Trông giữ tại nhà',2),(12,_binary '','Khám tổng quát, tư vấn dinh dưỡng, chăm sóc phòng bệnh và theo dõi sức khỏe.','Khám & tư vấn sức khỏe',3),(13,_binary '','Tiêm vaccine, tẩy giun, phòng ve rận và các gói phòng bệnh định kỳ.','Tiêm phòng & phòng ký sinh',3),(14,_binary '','Hỗ trợ uống thuốc, thay băng, theo dõi phục hồi theo hướng dẫn chuyên môn.','Chăm sóc sau điều trị',3),(15,_binary '','Dạy lệnh cơ bản, đi vệ sinh đúng chỗ, đi dây dắt và kỹ năng sinh hoạt.','Huấn luyện cơ bản',4),(16,_binary '','Hỗ trợ giảm sủa, cắn phá, lo âu xa chủ, hung hăng hoặc sợ hãi quá mức.','Chỉnh hành vi',4),(17,_binary '','Đưa đón thú cưng đến spa, phòng khám, khách sạn hoặc địa điểm theo lịch hẹn.','Đưa đón thú cưng',5),(18,_binary '','Dắt chó đi dạo, vận động, vui chơi và theo dõi tình trạng trong buổi đi dạo.','Dắt đi dạo & vận động',5),(19,_binary '','Tắm bằng sản phẩm phù hợp, sấy khô và chải lông cơ bản.','Tắm sấy chó mèo',6),(20,_binary '','Vệ sinh tai, cắt móng, mài móng và hỗ trợ vệ sinh tuyến hôi khi phù hợp.','Vệ sinh tai, móng, tuyến hôi',6),(21,_binary '','Cắt tỉa theo đặc điểm giống, độ dài lông và yêu cầu của chủ nuôi.','Cắt tỉa theo giống',7),(22,_binary '','Tạo điểm nhấn thẩm mỹ, nhuộm lông an toàn và trang trí phụ kiện.','Nhuộm lông & phụ kiện',7),(23,_binary '','Phòng lưu trú, cho ăn, vệ sinh và theo dõi dành cho chó.','Lưu trú chó',9),(24,_binary '','Phòng lưu trú yên tĩnh, vệ sinh khay cát, cho ăn và theo dõi dành cho mèo.','Lưu trú mèo',9),(25,_binary '','Trông giữ và chăm sóc trong một buổi, phù hợp lịch làm việc ngắn.','Daycare nửa ngày',10),(26,_binary '','Kiểm tra thể trạng, cân nặng, da lông, răng miệng và tư vấn chăm sóc.','Khám tổng quát',12),(27,_binary '','Tư vấn khẩu phần, chế độ ăn, kiểm soát cân nặng và chăm sóc theo độ tuổi.','Tư vấn dinh dưỡng',12),(28,_binary '','Tiêm phòng theo lịch và tư vấn nhắc lịch vaccine cho thú cưng.','Tiêm vaccine định kỳ',13),(29,_binary '','Dạy ngồi, nằm, đứng, gọi tên, đi cạnh chủ và phản hồi hiệu lệnh.','Lệnh cơ bản cho chó',15),(30,_binary '','Huấn luyện thói quen đi vệ sinh đúng vị trí và giảm sự cố trong nhà.','Đi vệ sinh đúng chỗ',15),(31,_binary '','Đưa đón thú cưng đến điểm hẹn và bàn giao theo lịch đặt trước.','Đưa đón spa/phòng khám',17),(32,_binary '','Dắt chó đi dạo theo lịch, kèm cập nhật tình trạng sau buổi đi.','Dắt chó đi dạo',18);
/*!40000 ALTER TABLE `service_categories` ENABLE KEYS */;
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
  `base_price_amount` decimal(12,2) DEFAULT NULL,
  `currency_code` varchar(3) NOT NULL,
  `default_duration_minutes` int NOT NULL,
  `description` text,
  `name` varchar(150) NOT NULL,
  `price_unit` varchar(20) NOT NULL,
  `requires_consultation` bit(1) NOT NULL,
  `service_code` varchar(32) NOT NULL,
  `short_description` varchar(255) DEFAULT NULL,
  `slug` varchar(150) NOT NULL,
  `category_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKfffr4emayc2n4uq3yv618d9j0` (`category_id`),
  CONSTRAINT `FKfffr4emayc2n4uq3yv618d9j0` FOREIGN KEY (`category_id`) REFERENCES `service_categories` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `services`
--

LOCK TABLES `services` WRITE;
/*!40000 ALTER TABLE `services` DISABLE KEYS */;
INSERT INTO `services` VALUES (1,NULL,NULL,_binary '',1000.00,'VND',60,'1','sv1','SESSION',_binary '\0','CSV-7352C8CC','1','sv1',7);
/*!40000 ALTER TABLE `services` ENABLE KEYS */;
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
INSERT INTO `user_roles` VALUES (NULL,1,1),(NULL,2,2),(NULL,3,3);
/*!40000 ALTER TABLE `user_roles` ENABLE KEYS */;
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
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,NULL,NULL,NULL,NULL,'https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&q=80&w=400','Hồ Chí Minh','VN','https://images.unsplash.com/photo-1548199973-03cce0bbc87b?auto=format&fit=crop&q=80&w=1600',NULL,NULL,NULL,'user@petgo.local','2026-06-03 18:32:17.000000','PetGo Sample User','2026-06-06 14:43:43.723151',NULL,NULL,'$2a$10$n89Petg/CbVQPxO1Hzra7ug9/MjwIM5gqQBM2iz9CDnh5ZT6yWwoy','0919000001','Hồ Chí Minh','ACTIVE','USR-SAMPLE-USER',NULL),(2,NULL,NULL,NULL,NULL,'https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?auto=format&fit=crop&q=80&w=400','Hồ Chí Minh','VN','https://images.unsplash.com/photo-1548199973-03cce0bbc87b?auto=format&fit=crop&q=80&w=1600',NULL,NULL,NULL,'provider@petgo.local','2026-06-03 18:32:17.000000','PetGo Sample Provider Owner','2026-06-04 14:04:19.101463',NULL,NULL,'$2a$10$n89Petg/CbVQPxO1Hzra7ug9/MjwIM5gqQBM2iz9CDnh5ZT6yWwoy','0919000002','Hồ Chí Minh','ACTIVE','USR-SAMPLE-PROVIDER',NULL),(3,NULL,NULL,NULL,NULL,'https://images.unsplash.com/photo-1607746882042-944635dfe10e?auto=format&fit=crop&q=80&w=400','Hồ Chí Minh','VN','https://images.unsplash.com/photo-1548199973-03cce0bbc87b?auto=format&fit=crop&q=80&w=1600',NULL,NULL,NULL,'admin@petgo.local','2026-06-03 18:32:17.000000','PetGo Sample Admin','2026-06-04 01:39:43.394633',NULL,NULL,'$2a$10$n89Petg/CbVQPxO1Hzra7ug9/MjwIM5gqQBM2iz9CDnh5ZT6yWwoy','0919000003','Hồ Chí Minh','ACTIVE','USR-SAMPLE-ADMIN',NULL);
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
  `qr_code_text` varchar(2000) DEFAULT NULL,
  `review_note` varchar(500) DEFAULT NULL,
  `reviewed_at` datetime(6) DEFAULT NULL,
  `status` varchar(30) NOT NULL,
  `transaction_code` varchar(40) NOT NULL,
  `type` varchar(30) NOT NULL,
  `counterparty_user_id` bigint DEFAULT NULL,
  `reviewed_by_admin_id` bigint DEFAULT NULL,
  `user_id` bigint NOT NULL,
  `wallet_id` bigint NOT NULL,
  `payment_content` varchar(255) DEFAULT NULL,
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
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `wallet_transactions`
--

LOCK TABLES `wallet_transactions` WRITE;
/*!40000 ALTER TABLE `wallet_transactions` DISABLE KEYS */;
INSERT INTO `wallet_transactions` VALUES (1,NULL,NULL,50000.00,NULL,NULL,NULL,NULL,NULL,'https://pay.payos.vn/web/619c056729aa496c81b6c5eefed953c5','PayOS','1780572331868616','','00020101021238570010A000000727012700069704220113VQRQAJMBU20840208QRIBFTTA53037045405500005802VN62210817PetGo Wallet null6304CE62',NULL,NULL,'PAYMENT_PENDING','TOPUP-A9F4E87C0D29','TOP_UP',NULL,NULL,1,1,NULL);
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
  `status` varchar(20) NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKsswfdl9fq40xlkove1y5kc7kv` (`user_id`),
  CONSTRAINT `FKc1foyisidw7wqqrkamafuwn4e` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `wallets`
--

LOCK TABLES `wallets` WRITE;
/*!40000 ALTER TABLE `wallets` DISABLE KEYS */;
INSERT INTO `wallets` VALUES (1,NULL,NULL,0.00,'VND','ACTIVE',1),(2,NULL,NULL,0.00,'VND','ACTIVE',2),(3,NULL,NULL,0.00,'VND','ACTIVE',3);
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

-- Dump completed on 2026-06-06 14:54:09
