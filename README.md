# 🛒 E-Commerce Platform

  Spring Boot 기반 종합 온라인 쇼핑몰 플랫폼

  ## 📋 목차
  - [프로젝트 개요](#-프로젝트-개요)
  - [기술 스택](#-기술-스택)
  - [시스템 아키텍처](#️-시스템-아키텍처)
  - [주요 기능](#-주요-기능)
  - [API 명세서](#-api-명세서)
  - [에러 처리](#-에러-처리)
  - [실행 방법](#-실행-방법)
  - [향후 계획](#-향후-계획)

  ---

  ## 🎯 프로젝트 개요

  실무 수준의 이커머스 플랫폼을 구축하는 프로젝트입니다.
  회원 관리, 상품 관리, 주문 처리 등 온라인 쇼핑몰의 핵심 기능을 구현하며,
  추후 Redis, Kafka, ElasticSearch 등을 활용한 고급 기능을 추가할 예정입니다.

  ### 프로젝트 목표
  - ✅ RESTful API 설계 및 구현
  - ✅ 체계적인 예외 처리 및 Validation
  - ✅ 도메인 주도 설계 (DDD) 적용
  - 🔜 Spring Security + JWT 인증/인가
  - 🔜 Redis 기반 장바구니 및 캐싱
  - 🔜 Kafka 이벤트 기반 아키텍처
  - 🔜 토스페이먼츠 결제 연동

  ---

  ## 🛠 기술 스택

  ### Backend
  - **Language**: Java 21
  - **Framework**: Spring Boot 3.5.6
  - **Persistence**: MyBatis 3.x (SQL Mapper)
  - **Validation**: Jakarta Bean Validation
  - **Build Tool**: Gradle

  ### Database
  - **Development**: MySQL 8.0
  - **Test**: H2 (In-Memory)

  ### 예정 기술
  - **Cache**: Redis
  - **Message Queue**: Apache Kafka
  - **Payment**: 토스페이먼츠 API
  - **Container**: Docker, Docker Compose

  ---


  ## ✨ 주요 기능

  ### 현재 구현된 기능

  #### 👤 회원 관리
  - [x] 회원가입 (이메일 중복 체크)
  - [x] 로그인 (이메일/비밀번호 검증)
  - [x] 회원 조회 (ID, 이메일)
  - [x] 전체 회원 목록 조회

  #### 🛍️ 상품 관리
  - [x] 상품 등록
  - [x] 상품 목록 조회 (페이징, 정렬)
  - [x] 상품 상세 조회
  - [x] 상품 수정
  - [x] 상품 삭제
  - [x] 재고 관리 (자동 증감)

  #### 📦 주문 관리
  - [x] 주문 생성 (재고 자동 차감)
  - [x] 주문 목록 조회 (페이징, 정렬)
  - [x] 주문 상세 조회
  - [x] 주문 취소 (재고 자동 복구)

  #### 💰 환불 관리
  - [x] 환불 요청 (주문 검증)
  - [x] 환불 목록 조회 (전체, 사용자별, 주문별)
  - [x] 환불 상세 조회
  - [x] 환불 승인 (재고 복구)
  - [x] 환불 거절 (사유 입력)

  #### 🔧 공통 기능
  - [x] 체계적인 예외 처리 (CustomException, ErrorCode)
  - [x] 입력값 검증 (Bean Validation)
  - [x] 빌더 패턴 적용 (모든 엔티티)
  - [x] 통합 테스트 (Repository, Service, Controller)

  ---

  ## 📡 API 명세서

  Base URL: `http://localhost:8080`

  ### 👤 User API

  | Method | Endpoint | Description | Request Body | Response | Error |
  |--------|----------|-------------|--------------|----------|-------|
  | POST | `/api/users/signup` | 회원가입 | `email`, `password`(min 8), `name` | 201 Created | 400 (중복 이메일) |
  | POST | `/api/users/login` | 로그인 | `email`, `password` | 200 OK | 401 (인증 실패) |
  | GET | `/api/users/{id}` | ID로 회원 조회 | - | 200 OK | 404 (회원 없음) |
  | GET | `/api/users/email/{email}` | 이메일로 회원 조회 | - | 200 OK | 404 (회원 없음) |
  | GET | `/api/users` | 전체 회원 조회 | - | 200 OK | - |

  <details>
  <summary><b>Request/Response 예시</b></summary>

  **회원가입 (POST /api/users/signup)**
  ```json
  // Request
  {
    "email": "user@example.com",
    "password": "password123",
    "name": "홍길동"
  }

  // Response (201 Created)
  {
    "id": 1,
    "email": "user@example.com",
    "name": "홍길동"
  }
  ```

  **로그인 (POST /api/users/login)**
  ```json
  // Request
  {
    "email": "user@example.com",
    "password": "password123"
  }

  // Response (200 OK)
  {
    "id": 1,
    "email": "user@example.com",
    "name": "홍길동"
  }
  ```
  </details>

---

  ### 🛍️ Product API

  | Method | Endpoint | Description | Request Body | Response | Error |
  |--------|----------|-------------|--------------|----------|-------|
  | POST | `/api/products` | 상품 등록 | `name`(1-100), `description`(max 500), `price`(0-1억), `stockQuantity`(≥0) | 201 Created | 400 (Validation) |
  | GET | `/api/products` | 상품 목록 (페이징) | Query: `page`, `size`, `sort` | 200 OK | - |
  | GET | `/api/products/{id}` | 상품 상세 조회 | - | 200 OK | 404 (상품 없음) |
  | PUT | `/api/products/{id}` | 상품 수정 | `name`, `description`, `price`, `stockQuantity` | 200 OK | 404 (상품 없음) |
  | DELETE | `/api/products/{id}` | 상품 삭제 | - | 204 No Content | 404 (상품 없음) |

  <details>
  <summary><b>Request/Response 예시</b></summary>

  **상품 등록 (POST /api/products)**
  ```json
  // Request
  {
    "name": "무선 이어폰",
    "description": "고음질 블루투스 이어폰",
    "price": 89000,
    "stockQuantity": 100
  }

  // Response (201 Created)
  {
    "id": 1,
    "name": "무선 이어폰",
    "description": "고음질 블루투스 이어폰",
    "price": 89000,
    "stock": 100,
    "status": "AVAILABLE"
  }
  ```

  **상품 목록 조회 (GET /api/products?page=0&size=10&sort=id,desc)**
  ```json
  // Response (200 OK)
  {
    "content": [
      {
        "id": 1,
        "name": "무선 이어폰",
        "description": "고음질 블루투스 이어폰",
        "price": 89000,
        "stock": 100,
        "status": "AVAILABLE"
      }
    ],
    "pageable": {
      "pageNumber": 0,
      "pageSize": 10
    },
    "totalElements": 1,
    "totalPages": 1
  }
  ```
  </details>

---

  ### 📦 Order API

  | Method | Endpoint | Description | Request Body | Response | Error |
  |--------|----------|-------------|--------------|----------|-------|
  | POST | `/api/orders` | 주문 생성 (재고 차감) | `userId`, `productId`, `count`(1-1000) | 201 Created | 400 (재고 부족), 404 (회원/상품 없음) |
  | GET | `/api/orders` | 주문 목록 (페이징) | Query: `page`, `size`, `sort` | 200 OK | - |
  | GET | `/api/orders/{id}` | 주문 상세 조회 | - | 200 OK | 404 (주문 없음) |
  | POST | `/api/orders/{id}/cancel` | 주문 취소 (재고 복구) | - | 204 No Content | 404 (주문 없음), 400 (이미 취소) |

  <details>
  <summary><b>Request/Response 예시</b></summary>

  **주문 생성 (POST /api/orders)**
  ```json
  // Request
  {
    "userId": 1,
    "productId": 1,
    "count": 2
  }

  // Response (201 Created)
  {
    "id": 1,
    "userId": 1,
    "status": "PENDING",
    "totalAmount": 178000,
    "orderItems": [
      {
        "productId": 1,
        "productName": "무선 이어폰",
        "quantity": 2,
        "price": 89000,
        "subtotal": 178000
      }
    ]
  }
  ```

  **주문 목록 조회 (GET /api/orders?page=0&size=10)**
  ```json
  // Response (200 OK)
  {
    "content": [
      {
        "id": 1,
        "userId": 1,
        "status": "PENDING",
        "totalAmount": 178000,
        "orderItems": [...]
      }
    ],
    "totalElements": 1,
    "totalPages": 1
  }
  ```
  </details>

---

  ### 💰 Refund API

  | Method | Endpoint | Description | Request Body | Response | Error |
  |--------|----------|-------------|--------------|----------|-------|
  | POST | `/api/refunds` | 환불 요청 | `userId`, `orderId`, `reason` | 201 Created | 400 (이미 환불됨), 404 (회원/주문 없음) |
  | GET | `/api/refunds/{id}` | 환불 상세 조회 | - | 200 OK | 404 (환불 없음) |
  | GET | `/api/refunds` | 전체 환불 목록 (페이징) | Query: `page`, `size`, `sort` | 200 OK | - |
  | GET | `/api/refunds/user/{userId}` | 특정 사용자 환불 내역 | - | 200 OK | - |
  | GET | `/api/refunds/order/{orderId}` | 특정 주문 환불 내역 | - | 200 OK | - |
  | PUT | `/api/refunds/{id}/approve` | 환불 승인 (관리자) | - | 200 OK | 404 (환불 없음), 400 (이미 처리됨) |
  | PUT | `/api/refunds/{id}/reject` | 환불 거절 (관리자) | `rejectReason` (optional) | 200 OK | 404 (환불 없음), 400 (이미 처리됨) |

  <details>
  <summary><b>Request/Response 예시</b></summary>

  **환불 요청 (POST /api/refunds)**
  ```json
  // Request
  {
    "userId": 1,
    "orderId": 1,
    "reason": "상품 불량"
  }

  // Response (201 Created)
  {
    "id": 1,
    "userId": 1,
    "orderId": 1,
    "reason": "상품 불량",
    "status": "PENDING",
    "createdAt": "2025-11-09T23:55:00"
  }
  ```

  **환불 승인 (PUT /api/refunds/{id}/approve)**
  ```json
  // Response (200 OK)
  {
    "id": 1,
    "userId": 1,
    "orderId": 1,
    "reason": "상품 불량",
    "status": "APPROVED",
    "createdAt": "2025-11-09T23:55:00",
    "updatedAt": "2025-11-09T23:56:00"
  }
  ```

  **환불 거절 (PUT /api/refunds/{id}/reject)**
  ```json
  // Request (Optional)
  "반품 기간 초과"

  // Response (200 OK)
  {
    "id": 1,
    "userId": 1,
    "orderId": 1,
    "reason": "상품 불량",
    "status": "REJECTED",
    "rejectReason": "반품 기간 초과",
    "createdAt": "2025-11-09T23:55:00",
    "updatedAt": "2025-11-09T23:56:00"
  }
  ```
  </details>
