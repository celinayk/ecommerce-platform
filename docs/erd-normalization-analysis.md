# ERD 설계 분석 및 정규화 검토 보고서

## 1. 전체 스키마 개요

| 테이블 | 역할 | 주요 관계 |
|--------|------|-----------|
| `users` | 사용자 (CUSTOMER, SELLER, ADMIN) | 주문, 장바구니, 환불의 주체 |
| `categories` | 상품 카테고리 (계층구조) | 자기참조 (parent_id) |
| `products` | 상품 정보 | categories N:1 |
| `stocks` | 재고 관리 | products 1:1 |
| `orders` | 주문 | users N:1 |
| `order_items` | 주문 상세 항목 | orders N:1, products N:1 |
| `order_histories` | 주문 상태 변경 이력 | orders N:1, users N:1 |
| `cancels` | 주문 취소 | orders N:1, users N:1 |
| `returns` | 반품 | orders N:1, order_items N:1, users N:1 |
| `refunds` | 환불 | orders N:1, users N:1 |
| `carts` | 장바구니 | users 1:1 |
| `cart_items` | 장바구니 항목 | carts N:1, products N:1 |

---

## 2. 정규화 분석

### 2.1 제1정규형 (1NF) - 원자값 보장

**판정: 모든 테이블 1NF 충족**

| 검토 항목 | 결과 | 비고 |
|-----------|------|------|
| 모든 컬럼이 원자값인가 | 충족 | 다중값 속성 없음 |
| 반복 그룹이 존재하는가 | 없음 | order_items, cart_items로 올바르게 분리 |
| 기본키가 존재하는가 | 충족 | 모든 테이블에 `id` (AUTO_INCREMENT) PK 존재 |

- `orders.status`가 VARCHAR(30)로 문자열 ENUM 관리 → 원자값 조건 충족
- 주소 등 복합 속성이 없어 원자값 위반 없음

---

### 2.2 제2정규형 (2NF) - 부분 함수 종속 제거

**판정: 모든 테이블 2NF 충족**

모든 테이블이 단일 컬럼 대리키(`id`)를 PK로 사용하므로 복합키가 없고, 부분 함수 종속이 구조적으로 발생할 수 없습니다.

| 테이블 | PK | 부분 종속 여부 |
|--------|-----|---------------|
| `order_items` | `id` (대리키) | 없음 - `{order_id, product_id}`가 PK가 아니므로 부분 종속 불가 |
| `cart_items` | `id` (대리키) | 없음 - `UNIQUE(cart_id, product_id)` 제약조건은 후보키이나 PK가 아님 |
| 기타 테이블 | `id` (대리키) | 단일 PK이므로 부분 종속 불가 |

> **참고**: `cart_items`의 `UNIQUE(cart_id, product_id)`는 자연 후보키입니다. 이 후보키 관점에서 `quantity`, `is_selected`가 완전 함수 종속하므로 2NF 위반 없음.

---

### 2.3 제3정규형 (3NF) - 이행적 함수 종속 제거

**판정: 대부분 충족, 일부 경미한 이슈 존재**

#### 충족하는 테이블

| 테이블 | 분석 |
|--------|------|
| `users` | `id → email, password, name, role` - 이행 종속 없음 |
| `categories` | `id → name, description, parent_id` - 이행 종속 없음 |
| `products` | `id → name, description, price, category_id` - 이행 종속 없음 |
| `stocks` | `id → product_id, quantity` - 이행 종속 없음 |
| `order_items` | `id → order_id, product_id, quantity, price` - 이행 종속 없음 |
| `order_histories` | `id → order_id, before/after_status, changed_by, reason` - 이행 종속 없음 |
| `carts` | `id → user_id` - 이행 종속 없음 |
| `cart_items` | `id → cart_id, product_id, quantity, is_selected` - 이행 종속 없음 |

#### 주의가 필요한 부분

**`orders` 테이블 - `total_price` 파생 컬럼**

```
orders.id → orders.total_price
orders.id → order_items (1:N)
order_items → price * quantity (계산 가능)
```

`total_price`는 `order_items`의 `SUM(price * quantity)`로 계산 가능한 **파생 속성(derived attribute)** 입니다.

- **엄밀한 3NF 관점**: `total_price`는 `order_items`에서 계산 가능하므로 중복 데이터
- **실무적 판단**: 주문 합계를 매번 JOIN+SUM하는 것은 성능 부담이 크므로, 비정규화를 통한 **의도적 중복**은 합리적인 설계입니다. 단, `Order.addOrderItem()`에서 `calculateTotalPrice()`를 호출하여 일관성을 유지하고 있어 적절합니다.

**`refunds` 테이블 - `user_id` 간접 도출 가능성**

```
refunds.order_id → orders.user_id
refunds.user_id (별도로 저장)
```

`refunds.user_id`는 `refunds.order_id → orders.user_id`로 간접 도출 가능합니다. 이는 이행적 종속에 해당할 수 있습니다.

- **잠재적 문제**: 환불 요청자(refunds.user_id)와 주문자(orders.user_id)가 항상 동일인이라면 중복
- **설계 의도가 합리적인 경우**: 관리자가 대리 환불을 처리할 수 있는 시나리오라면 별도 저장이 정당화됨

---

### 2.4 BCNF (Boyce-Codd 정규형)

**판정: 모든 테이블 BCNF 충족**

BCNF 위반 조건: 결정자가 후보키가 아닌 함수 종속이 존재하는 경우

| 테이블 | 후보키 | BCNF 위반 여부 |
|--------|--------|---------------|
| `users` | `{id}`, `{email}` | 없음 - email은 UNIQUE로 후보키, id도 후보키 |
| `stocks` | `{id}`, `{product_id}` | 없음 - product_id UNIQUE로 후보키 |
| `carts` | `{id}`, `{user_id}` | 없음 - user_id UNIQUE로 후보키 |
| `cart_items` | `{id}`, `{cart_id, product_id}` | 없음 - 두 후보키 모두 결정자 역할 충족 |
| 나머지 테이블 | `{id}` | 단일 후보키이므로 BCNF 자동 충족 |

---

## 3. 설계 우수 사항

### 3.1 올바른 테이블 분리

- **주문-주문항목 분리** (`orders` / `order_items`): 1NF 반복 그룹 제거를 위한 교과서적 분리
- **장바구니-장바구니항목 분리** (`carts` / `cart_items`): 동일한 원칙 적용
- **취소/반품/환불 분리** (`cancels` / `returns` / `refunds`): 서로 다른 비즈니스 프로세스를 독립 테이블로 관리

### 3.2 적절한 참조 무결성

- 모든 외래키에 `FOREIGN KEY CONSTRAINT` 명시
- `categories.parent_id` → `ON DELETE CASCADE` (하위 카테고리 연쇄 삭제)
- `products.category_id` → `ON DELETE SET NULL` (카테고리 삭제 시 상품 보존)

### 3.3 인덱스 전략

- `orders(user_id)`, `orders(status)` - 사용자별/상태별 주문 조회 최적화
- `order_items(order_id)` - 주문별 항목 조회 최적화
- `refunds(order_id, user_id, status)` - 환불 조회 다각도 최적화
- `cart_items(cart_id)` - 장바구니 항목 조회 최적화

### 3.4 주문 시점 가격 스냅샷

`order_items.price`에 주문 시점의 가격을 별도 저장하여, 상품 가격 변경이 기존 주문에 영향을 미치지 않도록 설계. 이는 정규화 원칙상 중복이지만 **비즈니스 필수 요구사항**으로 올바른 설계입니다.

### 3.5 감사 추적 (Audit Trail)

- `order_histories`: 주문 상태 변경 이력을 `before_status` → `after_status`로 기록
- `BaseEntity`의 `created_at`, `updated_at`: 모든 테이블에 감사 타임스탬프

---

## 4. 발견된 문제점 및 개선 제안

### 4.1 [중요] `products` 테이블에 `seller_id` 부재

**현상**: `users` 테이블에 `SELLER` 역할이 정의되어 있지만, `products` 테이블에 판매자 참조가 없습니다.

```sql
-- 현재 products 테이블
CREATE TABLE products (
   id BIGINT AUTO_INCREMENT PRIMARY KEY,
   name VARCHAR(200) NOT NULL,
   description TEXT,
   price DECIMAL(10, 2) NOT NULL,
   category_id BIGINT,          -- 카테고리만 존재
   -- seller_id 부재!
);
```

**영향**: 어떤 판매자의 상품인지 식별 불가 → 판매자별 상품 관리, 정산 등 핵심 기능 구현 불가

**개선안**:
```sql
ALTER TABLE products ADD COLUMN seller_id BIGINT NOT NULL;
ALTER TABLE products ADD CONSTRAINT fk_product_seller
    FOREIGN KEY (seller_id) REFERENCES users(id);
CREATE INDEX idx_products_seller ON products(seller_id);
```

---

### 4.2 [중요] `refunds` 테이블에 금액 정보 부재

**현상**: 환불 테이블에 환불 금액(`amount`) 컬럼이 없습니다.

```sql
-- 현재 refunds 테이블 - amount 없음
CREATE TABLE refunds (
   id BIGINT AUTO_INCREMENT PRIMARY KEY,
   user_id BIGINT NOT NULL,
   order_id BIGINT NOT NULL,
   reason VARCHAR(500),
   status VARCHAR(20) NOT NULL DEFAULT 'PENDING'
);
```

**영향**: 부분 환불 불가, 환불 금액 추적 불가

**개선안**:
```sql
ALTER TABLE refunds ADD COLUMN amount DECIMAL(10, 2) NOT NULL;
```

---

### 4.3 [중요] `cancels`와 `returns`에 대한 JPA Entity 미구현

**현상**: `cancels`와 `returns`는 SQL 마이그레이션으로 테이블이 생성되어 있지만, 대응하는 JPA Entity 클래스가 존재하지 않습니다.

- `V8__create_cancels.sql` → Entity 없음
- `V9__create_returns.sql` → Entity 없음

**영향**: 애플리케이션에서 해당 테이블을 ORM으로 접근할 수 없으며, 취소/반품 기능이 완성되지 않은 상태

---

### 4.4 [경미] `refunds`와 `cancels`의 역할 중복 가능성

**현상**: 취소(`cancels`)와 환불(`refunds`) 테이블이 분리되어 있으나, 비즈니스 흐름상 취소 → 환불이 연결되어야 합니다.

```
cancels 테이블: order_id, status, reason, requested_by, approved_by
refunds 테이블: order_id, user_id, reason, status
```

**문제**: `refunds`에 `cancel_id` 또는 `return_id`와의 연결 관계가 없어, 어떤 취소/반품에 의한 환불인지 추적이 불가합니다.

**개선안**:
```sql
ALTER TABLE refunds ADD COLUMN cancel_id BIGINT NULL;
ALTER TABLE refunds ADD COLUMN return_id BIGINT NULL;
ALTER TABLE refunds ADD CONSTRAINT fk_refund_cancel
    FOREIGN KEY (cancel_id) REFERENCES cancels(id);
ALTER TABLE refunds ADD CONSTRAINT fk_refund_return
    FOREIGN KEY (return_id) REFERENCES returns(id);
```

---

### 4.5 [경미] `stocks` 테이블의 1:1 관계 재검토

**현상**: `stocks`와 `products`가 1:1 관계 (`UNIQUE(product_id)`)

```sql
CREATE TABLE stocks (
   id BIGINT AUTO_INCREMENT PRIMARY KEY,   -- 별도 대리키
   product_id BIGINT NOT NULL UNIQUE,      -- 사실상 1:1
   quantity INT NOT NULL DEFAULT 0
);
```

**분석**: 현재 구조에서 `stocks`는 `products`에 `quantity` 컬럼을 추가하는 것과 동일한 효과입니다. 분리한 것 자체는 문제가 아니지만:

- **분리 유지 근거**: 재고에 대한 독립적 락(Lock) 관리, 향후 다중 창고(warehouse) 확장 시 1:N 전환 가능
- **통합 근거**: 현재 단일 재고만 관리하므로 JOIN 비용만 증가

현재 설계는 **확장성 관점에서 합리적**이므로 유지해도 무방합니다.

---

### 4.6 [경미] `Product` 엔티티에서 `category_id`를 ID만 보관

**현상**: `Product.java`에서 `categoryId`를 `Long` 타입으로 직접 관리하며 `@ManyToOne` JPA 관계를 사용하지 않습니다.

```java
// Product.java
@Column(name = "category_id")
private Long categoryId;  // FK를 ID 값으로만 관리
```

반면 다른 엔티티들(`Order`, `Refund`, `Cart` 등)은 `@ManyToOne`으로 연관관계를 매핑합니다.

**영향**: 일관성 문제. Product에서 Category를 로드하려면 별도 쿼리가 필요하며, JPA의 영속성 컨텍스트 관리와 Lazy Loading 이점을 활용하지 못합니다.

---

### 4.7 [권장] 누락된 인덱스

| 테이블 | 권장 인덱스 | 이유 |
|--------|------------|------|
| `products` | `idx_products_category (category_id)` | 카테고리별 상품 조회 |
| `order_histories` | `idx_order_histories_changed_at (changed_at)` | 시간순 이력 조회 |
| `returns` | `idx_returns_order_item (order_item_id)` | 주문항목별 반품 조회 |
| `cancels` | `idx_cancels_status (status)` | 상태별 취소 조회 |

---

## 5. 정규화 종합 평가

| 정규형 | 충족 여부 | 비고 |
|--------|-----------|------|
| **1NF** | **충족** | 모든 속성 원자값, 반복 그룹 없음 |
| **2NF** | **충족** | 단일 대리키 사용으로 부분 종속 불가 |
| **3NF** | **대부분 충족** | `orders.total_price` (의도적 비정규화), `refunds.user_id` (잠재적 이행 종속) |
| **BCNF** | **충족** | 결정자가 모두 후보키 |

### 최종 평가

이 ERD는 **전반적으로 잘 설계**되어 있습니다.

**강점**:
- 1NF~BCNF까지 정규화 원칙을 충실히 준수
- 비정규화가 필요한 곳(`total_price`, `order_items.price`)에서만 합리적으로 적용
- 취소/반품/환불의 업무 흐름을 독립 테이블로 분리한 점은 SRP(단일 책임 원칙)에 부합
- 계층형 카테고리, 주문 이력 추적 등 실무 요구사항 반영

**개선 필요**:
- `products.seller_id` 추가 (비즈니스 핵심 누락)
- `refunds.amount` 추가 (환불 금액 관리)
- `refunds` → `cancels`/`returns` 연결 관계 추가
- `cancels`, `returns` JPA Entity 구현
- `Product` 엔티티의 카테고리 연관관계 매핑 일관성
