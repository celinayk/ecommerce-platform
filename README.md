# 🛒 E-Commerce Platform

  Spring Boot 기반의 이커머스 플랫폼으로, 단순 CRUD 구현에서 시작해 고가용성 백엔드 시스템으로 진화하는 과정을 담은 프로젝트입니다.

## 🚀 Project Journey: From v1 to v2

### 🔹 v1. Fundamentals (2025.10 - 2025.11)
**"비즈니스 로직의 이해와 데이터 접근 기초 확립"**
* **핵심 기술**: Spring Boot 3.x, MyBatis, MySQL
* **학습 성과**: SQL Mapper를 통한 DB 핸들링, 기초 CRUD API 설계 학습
* **기록**: 기본적인 도메인 기능(회원, 상품, 주문, 환불)을 완성했습니다.

### 🔹 v2. Technical Deep-Dive (2026.02)
**"객체 지향적 설계와 고도화된 기술 도입"**
* **핵심 기술**: Spring Data JPA, QueryDSL, Redis, Docker
* **전환 배경**: MyBatis의 유지보수 생산성 한계를 극복하기 위해 JPA를 도입하고, 확장 가능한 아키텍처로 재설계했습니다.

  ## 📋 목차
  - [프로젝트 개요](#-프로젝트-개요)
  - [기술 스택](#-기술-스택)
  - [시스템 아키텍처](#️-시스템-아키텍처)
  - [ERD 다이어그램](#-erd-다이어그램)
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

  ## 📊 ERD 다이어그램
  <img width="1390" height="701" alt="스크린샷 2025-11-18 오후 8 51 54" src="https://github.com/user-attachments/assets/4d1f02a7-6964-4a6a-ab03-a2e548a135ce" />


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

  #### 📂 카테고리 관리
   - [x] 카테고리 생성 (계층 구조 지원)
   - [x] 카테고리 상세 조회
   - [x] 전체 카테고리 조회 (페이징)
   - [x] 최상위 카테고리 조회
   - [x] 부모별 자식 카테고리 조회
   - [x] 카테고리 수정
   - [x] 카테고리 삭제 (자식 존재 시 제한)

  #### 📦 주문 관리
  - [x] 주문 생성 (재고 자동 차감)
  - [x] 주문 목록 조회 (페이징, 정렬)
  - [x] 주문 상세 조회
  - [x] 주문 취소 (재고 자동 복구)

  #### 💰 환불 관리
  - [x] 환불 요청 (주문 검증)
  - [x] 환불 목록 조회 (페이징, 전체/사용자별/주문별)
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
  | POST | `/api/products` | 상품 등록 | `name`(1-100), `description`(max 500), `price`(0-1억), `stock`(≥0), `categoryId`(필수) | 201 Created | 400 (Validation), 404 (카테고리 없음) |
  | GET | `/api/products` | 전체 상품 목록 | - | 200 OK | - |
  | GET | `/api/products/{id}` | 상품 상세 조회 | - | 200 OK | 404 (상품 없음) |
  | PUT | `/api/products/{id}` | 상품 수정 | `name`, `description`, `price`, `stock`, `categoryId` | 200 OK | 404 (상품/카테고리 없음) |
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
    "stock": 100,
    "categoryId": 1
  }

  // Response (201 Created)
  {
    "id": 1,
    "name": "무선 이어폰",
    "description": "고음질 블루투스 이어폰",
    "price": 89000,
    "stock": 100,
    "status": "AVAILABLE",
    "categoryId": 1,
    "categoryName": "전자제품",
    "createdAt": "2025-11-10T12:00:00",
    "updatedAt": "2025-11-10T12:00:00"
  }
  ```

  **전체 상품 목록 조회 (GET /api/products)**
  ```json
  // Response (200 OK)
  [
    {
      "id": 1,
      "name": "무선 이어폰",
      "description": "고음질 블루투스 이어폰",
      "price": 89000,
      "stock": 100,
      "status": "AVAILABLE",
      "categoryId": 1,
      "categoryName": "전자제품",
      "createdAt": "2025-11-10T12:00:00",
      "updatedAt": "2025-11-10T12:00:00"
    }
  ]
  ```

  **상품 상세 조회 (GET /api/products/{id})**
  ```json
  // Response (200 OK)
  {
    "id": 1,
    "name": "무선 이어폰",
    "description": "고음질 블루투스 이어폰",
    "price": 89000,
    "stock": 100,
    "status": "AVAILABLE",
    "categoryId": 1,
    "categoryName": "전자제품",
    "createdAt": "2025-11-10T12:00:00",
    "updatedAt": "2025-11-10T12:00:00"
  }
  ```
  </details>

---

  ### 📂 Category API

  | Method | Endpoint | Description | Request Body | Response | Error |
  |--------|----------|-------------|--------------|----------|-------|
  | POST | `/api/categories` | 카테고리 생성 | `name`(필수), `description`, `parentId`(optional) | 201 Created | 400 (중복 이름) |
  | GET | `/api/categories/{id}` | 카테고리 상세 조회 | - | 200 OK | 404 (카테고리 없음) |
  | GET | `/api/categories` | 전체 카테고리 조회 (페이징) | Query: `page`, `size`, `sort` | 200 OK | - |
  | GET | `/api/categories/parent/{parentId}` | 부모별 자식 카테고리 조회 | - | 200 OK | - |
  | PUT | `/api/categories/{id}` | 카테고리 수정 | `name`, `description`, `parentId` | 200 OK | 404 (카테고리 없음) |
  | DELETE | `/api/categories/{id}` | 카테고리 삭제 | - | 204 No Content | 400 (자식 있음), 404 (카테고리 없음) |

  <details>
  <summary><b>Request/Response 예시</b></summary>

  **카테고리 생성 (POST /api/categories)**
  ```json
  // Request - 최상위 카테고리
  {
    "name": "전자제품",
    "description": "전자제품 카테고리"
  }

  // Request - 하위 카테고리
  {
    "name": "노트북",
    "description": "노트북 카테고리",
    "parentId": 1
  }

  // Response (201 Created)
  {
    "id": 2,
    "name": "노트북",
    "description": "노트북 카테고리",
    "parentId": 1,
    "parentName": "전자제품",
    "createdAt": "2025-11-10T12:00:00",
    "updatedAt": "2025-11-10T12:00:00"
  }
  ```

  **전체 카테고리 조회 (GET /api/categories?page=0&size=10)**
  ```json
  // Response (200 OK)
  {
    "content": [
      {
        "id": 1,
        "name": "전자제품",
        "description": "전자제품 카테고리",
        "parentId": null,
        "parentName": null,
        "createdAt": "2025-11-10T12:00:00",
        "updatedAt": "2025-11-10T12:00:00"
      },
      {
        "id": 2,
        "name": "노트북",
        "description": "노트북 카테고리",
        "parentId": 1,
        "parentName": "전자제품",
        "createdAt": "2025-11-10T12:00:00",
        "updatedAt": "2025-11-10T12:00:00"
      }
    ],
    "pageable": {
      "pageNumber": 0,
      "pageSize": 10,
      "sort": {
        "sorted": true,
        "unsorted": false
      }
    },
    "totalElements": 2,
    "totalPages": 1,
    "last": true,
    "first": true
  }
  ```

  **최상위 카테고리 조회 (GET /api/categories/root)**
  ```json
  // Response (200 OK)
  [
    {
      "id": 1,
      "name": "전자제품",
      "description": "전자제품 카테고리",
      "parentId": null,
      "parentName": null,
      "createdAt": "2025-11-10T12:00:00",
      "updatedAt": "2025-11-10T12:00:00"
    },
    {
      "id": 3,
      "name": "의류",
      "description": "의류 카테고리",
      "parentId": null,
      "parentName": null,
      "createdAt": "2025-11-10T12:00:00",
      "updatedAt": "2025-11-10T12:00:00"
    }
  ]
  ```

  **부모별 자식 카테고리 조회 (GET /api/categories/parent/1)**
  ```json
  // Response (200 OK)
  [
    {
      "id": 2,
      "name": "노트북",
      "description": "노트북 카테고리",
      "parentId": 1,
      "parentName": "전자제품",
      "createdAt": "2025-11-10T12:00:00",
      "updatedAt": "2025-11-10T12:00:00"
    },
    {
      "id": 4,
      "name": "스마트폰",
      "description": "스마트폰 카테고리",
      "parentId": 1,
      "parentName": "전자제품",
      "createdAt": "2025-11-10T12:00:00",
      "updatedAt": "2025-11-10T12:00:00"
    }
  ]
  ```

  **카테고리 수정 (PUT /api/categories/2)**
  ```json
  // Request
  {
    "name": "노트북(수정)",
    "description": "수정된 설명",
    "parentId": 1
  }

  // Response (200 OK)
  {
    "id": 2,
    "name": "노트북(수정)",
    "description": "수정된 설명",
    "parentId": 1,
    "parentName": "전자제품",
    "createdAt": "2025-11-10T12:00:00",
    "updatedAt": "2025-11-10T12:01:00"
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
    "userName": "홍길동",
    "totalAmount": 178000,
    "status": "PENDING",
    "orderItems": [
      {
        "productId": 1,
        "productName": "무선 이어폰",
        "price": 89000,
        "quantity": 2,
        "subtotal": 178000
      }
    ],
    "createdAt": "2025-11-10T12:00:00"
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
        "userName": "홍길동",
        "totalAmount": 178000,
        "status": "PENDING",
        "orderItems": [
          {
            "productId": 1,
            "productName": "무선 이어폰",
            "price": 89000,
            "quantity": 2,
            "subtotal": 178000
          }
        ],
        "createdAt": "2025-11-10T12:00:00"
      }
    ],
    "pageable": {
      "pageNumber": 0,
      "pageSize": 10,
      "sort": {
        "sorted": true,
        "unsorted": false
      }
    },
    "totalElements": 1,
    "totalPages": 1,
    "last": true,
    "first": true
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
    "userName": "홍길동",
    "orderId": 1,
    "reason": "상품 불량",
    "status": "PENDING",
    "createdAt": "2025-11-10T12:00:00",
    "updatedAt": "2025-11-10T12:00:00"
  }
  ```

  **환불 상세 조회 (GET /api/refunds/{id})**
  ```json
  // Response (200 OK)
  {
    "id": 1,
    "userId": 1,
    "userName": "홍길동",
    "orderId": 1,
    "reason": "상품 불량",
    "status": "PENDING",
    "createdAt": "2025-11-10T12:00:00",
    "updatedAt": "2025-11-10T12:00:00"
  }
  ```

  **전체 환불 목록 조회 (GET /api/refunds?page=0&size=10)**
  ```json
  // Response (200 OK)
  {
    "content": [
      {
        "id": 1,
        "userId": 1,
        "userName": "홍길동",
        "orderId": 1,
        "reason": "상품 불량",
        "status": "PENDING",
        "createdAt": "2025-11-10T12:00:00",
        "updatedAt": "2025-11-10T12:00:00"
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

  **환불 승인 (PUT /api/refunds/{id}/approve)**
  ```json
  // Response (200 OK)
  {
    "id": 1,
    "userId": 1,
    "userName": "홍길동",
    "orderId": 1,
    "reason": "상품 불량",
    "status": "APPROVED",
    "createdAt": "2025-11-10T12:00:00",
    "updatedAt": "2025-11-10T12:05:00"
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
    "userName": "홍길동",
    "orderId": 1,
    "reason": "상품 불량",
    "status": "REJECTED",
    "createdAt": "2025-11-10T12:00:00",
    "updatedAt": "2025-11-10T12:05:00"
  }
  ```
  </details>

---
