-- ============================================================
-- E-Commerce Platform - Complete Database Schema
-- MySQL Workbench Reverse Engineering용 DDL Script
--
-- 사용법 (MySQL Workbench에서 ERD 생성):
--   1. MySQL Workbench 실행
--   2. File > New Model
--   3. File > Import > Reverse Engineer MySQL Create Script...
--   4. 이 파일(ecommerce_schema.sql)을 선택
--   5. "Execute >" 클릭 → "Next >" → "Finish"
--   6. ERD가 자동 생성됩니다
--   7. Model > Diagram Properties 에서 레이아웃 조정 가능
-- ============================================================

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;

-- -----------------------------------------------------------
-- Table: users
-- 사용자 계정 정보
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS `users` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `email` VARCHAR(255) NOT NULL,
  `password` VARCHAR(255) NOT NULL,
  `name` VARCHAR(100) NOT NULL,
  `role` VARCHAR(20) NOT NULL COMMENT 'CUSTOMER, SELLER, ADMIN',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `uk_users_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -----------------------------------------------------------
-- Table: sellers
-- 판매자 정보 (users 1:1)
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS `sellers` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL,
  `shop_name` VARCHAR(100) NOT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `uk_sellers_user_id` (`user_id`),
  CONSTRAINT `fk_sellers_user`
    FOREIGN KEY (`user_id`)
    REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -----------------------------------------------------------
-- Table: categories
-- 상품 카테고리 (self-referential 계층 구조)
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS `categories` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(100) NOT NULL,
  `description` VARCHAR(500) NULL,
  `parent_id` BIGINT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  INDEX `idx_categories_parent` (`parent_id`),
  CONSTRAINT `fk_categories_parent`
    FOREIGN KEY (`parent_id`)
    REFERENCES `categories` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -----------------------------------------------------------
-- Table: products
-- 상품 정보
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS `products` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `seller_id` BIGINT NOT NULL,
  `category_id` BIGINT NULL,
  `name` VARCHAR(200) NOT NULL,
  `description` TEXT NULL,
  `price` DECIMAL(10,2) NOT NULL,
  `stock_quantity` BIGINT NOT NULL DEFAULT 0,
  `status` VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE' COMMENT 'AVAILABLE, SOLD_OUT',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  INDEX `idx_products_seller` (`seller_id`),
  INDEX `idx_products_category` (`category_id`),
  INDEX `idx_products_status` (`status`),
  CONSTRAINT `fk_products_seller`
    FOREIGN KEY (`seller_id`)
    REFERENCES `sellers` (`id`),
  CONSTRAINT `fk_products_category`
    FOREIGN KEY (`category_id`)
    REFERENCES `categories` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -----------------------------------------------------------
-- Table: stocks
-- 상품 재고 관리
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS `stocks` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `product_id` BIGINT NOT NULL,
  `quantity` INT NOT NULL DEFAULT 0,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `uk_stocks_product_id` (`product_id`),
  CONSTRAINT `fk_stocks_product`
    FOREIGN KEY (`product_id`)
    REFERENCES `products` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -----------------------------------------------------------
-- Table: orders
-- 주문 정보
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS `orders` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL,
  `seller_id` BIGINT NOT NULL,
  `status` VARCHAR(30) NOT NULL COMMENT 'PENDING, CONFIRMED, SHIPPING, DELIVERED, COMPLETED, CANCEL_REQUESTED, CANCELED',
  `total_price` DECIMAL(10,2) NOT NULL,
  `ordered_at` DATETIME NOT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  INDEX `idx_orders_user` (`user_id`),
  INDEX `idx_orders_seller` (`seller_id`),
  INDEX `idx_orders_status` (`status`),
  CONSTRAINT `fk_orders_user`
    FOREIGN KEY (`user_id`)
    REFERENCES `users` (`id`),
  CONSTRAINT `fk_orders_seller`
    FOREIGN KEY (`seller_id`)
    REFERENCES `sellers` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -----------------------------------------------------------
-- Table: order_items
-- 주문 상세 항목
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS `order_items` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `order_id` BIGINT NOT NULL,
  `product_id` BIGINT NOT NULL,
  `quantity` INT NOT NULL,
  `price` DECIMAL(10,2) NOT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  INDEX `idx_order_items_order` (`order_id`),
  INDEX `idx_order_items_product` (`product_id`),
  CONSTRAINT `fk_order_items_order`
    FOREIGN KEY (`order_id`)
    REFERENCES `orders` (`id`),
  CONSTRAINT `fk_order_items_product`
    FOREIGN KEY (`product_id`)
    REFERENCES `products` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -----------------------------------------------------------
-- Table: order_histories
-- 주문 상태 변경 이력
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS `order_histories` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `order_id` BIGINT NOT NULL,
  `before_status` VARCHAR(30) NULL,
  `after_status` VARCHAR(30) NOT NULL,
  `changed_by` BIGINT NULL,
  `reason` VARCHAR(200) NULL,
  `changed_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  INDEX `idx_order_histories_order` (`order_id`),
  INDEX `idx_order_histories_changed_by` (`changed_by`),
  CONSTRAINT `fk_order_histories_order`
    FOREIGN KEY (`order_id`)
    REFERENCES `orders` (`id`),
  CONSTRAINT `fk_order_histories_user`
    FOREIGN KEY (`changed_by`)
    REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -----------------------------------------------------------
-- Table: cancels
-- 주문 취소 요청/승인
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS `cancels` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `order_id` BIGINT NOT NULL,
  `status` VARCHAR(20) NOT NULL COMMENT 'REQUESTED, APPROVED, COMPLETED, REJECTED',
  `reason` VARCHAR(200) NULL,
  `requested_by` BIGINT NOT NULL,
  `approved_by` BIGINT NULL,
  `requested_at` DATETIME NOT NULL,
  `approved_at` DATETIME NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  INDEX `idx_cancels_order` (`order_id`),
  INDEX `idx_cancels_requested_by` (`requested_by`),
  INDEX `idx_cancels_approved_by` (`approved_by`),
  CONSTRAINT `fk_cancels_order`
    FOREIGN KEY (`order_id`)
    REFERENCES `orders` (`id`),
  CONSTRAINT `fk_cancels_requested_by`
    FOREIGN KEY (`requested_by`)
    REFERENCES `users` (`id`),
  CONSTRAINT `fk_cancels_approved_by`
    FOREIGN KEY (`approved_by`)
    REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -----------------------------------------------------------
-- Table: returns
-- 반품 요청/처리
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS `returns` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `order_id` BIGINT NOT NULL,
  `order_item_id` BIGINT NOT NULL,
  `status` VARCHAR(20) NOT NULL COMMENT 'REQUESTED, APPROVED, PICKED_UP, INSPECTED, COMPLETED, REJECTED',
  `reason` VARCHAR(200) NULL,
  `requested_by` BIGINT NOT NULL,
  `approved_by` BIGINT NULL,
  `requested_at` DATETIME NOT NULL,
  `approved_at` DATETIME NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  INDEX `idx_returns_order` (`order_id`),
  INDEX `idx_returns_order_item` (`order_item_id`),
  INDEX `idx_returns_requested_by` (`requested_by`),
  INDEX `idx_returns_approved_by` (`approved_by`),
  CONSTRAINT `fk_returns_order`
    FOREIGN KEY (`order_id`)
    REFERENCES `orders` (`id`),
  CONSTRAINT `fk_returns_order_item`
    FOREIGN KEY (`order_item_id`)
    REFERENCES `order_items` (`id`),
  CONSTRAINT `fk_returns_requested_by`
    FOREIGN KEY (`requested_by`)
    REFERENCES `users` (`id`),
  CONSTRAINT `fk_returns_approved_by`
    FOREIGN KEY (`approved_by`)
    REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -----------------------------------------------------------
-- Table: refunds
-- 환불 처리
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS `refunds` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `order_id` BIGINT NOT NULL,
  `cancel_id` BIGINT NULL,
  `return_id` BIGINT NULL,
  `amount` DECIMAL(10,2) NOT NULL,
  `status` VARCHAR(20) NOT NULL COMMENT 'PENDING, COMPLETED, FAILED',
  `refunded_at` DATETIME NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  INDEX `idx_refunds_order` (`order_id`),
  INDEX `idx_refunds_cancel` (`cancel_id`),
  INDEX `idx_refunds_return` (`return_id`),
  CONSTRAINT `fk_refunds_order`
    FOREIGN KEY (`order_id`)
    REFERENCES `orders` (`id`),
  CONSTRAINT `fk_refunds_cancel`
    FOREIGN KEY (`cancel_id`)
    REFERENCES `cancels` (`id`),
  CONSTRAINT `fk_refunds_return`
    FOREIGN KEY (`return_id`)
    REFERENCES `returns` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -----------------------------------------------------------
-- Table: carts
-- 장바구니
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS `carts` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `uk_carts_user_id` (`user_id`),
  CONSTRAINT `fk_carts_user`
    FOREIGN KEY (`user_id`)
    REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -----------------------------------------------------------
-- Table: cart_items
-- 장바구니 상세 항목
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS `cart_items` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `cart_id` BIGINT NOT NULL,
  `product_id` BIGINT NOT NULL,
  `quantity` INT NOT NULL,
  `is_selected` TINYINT(1) NOT NULL DEFAULT 1,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  INDEX `idx_cart_items_cart` (`cart_id`),
  INDEX `idx_cart_items_product` (`product_id`),
  CONSTRAINT `fk_cart_items_cart`
    FOREIGN KEY (`cart_id`)
    REFERENCES `carts` (`id`),
  CONSTRAINT `fk_cart_items_product`
    FOREIGN KEY (`product_id`)
    REFERENCES `products` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
