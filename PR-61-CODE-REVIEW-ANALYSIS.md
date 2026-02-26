# PR #61 코드리뷰 분석: 취소/반품 도메인 분리

## PR 개요

| 항목 | 내용 |
|------|------|
| **제목** | 취소/반품 도메인 분리 |
| **작성자** | celinayk |
| **브랜치** | `60-취소반품도메인분리` → `main` |
| **변경 파일** | 21개 (+572, -159) |
| **관련 이슈** | #60 취소, 반품 도메인 분리 |

## 변경 사항 요약

Order 도메인에 통합되어 있던 취소/반품 로직을 독립된 도메인 패키지로 분리하는 리팩토링입니다.

### 신규 생성 (14개 파일)
- **Cancel 도메인**: `CancelController`, `CancelService`, `Cancel` 엔티티, `CancelStatus`, `CancelRequest` DTO, `CancelRepository`, `CancelException`
- **Return 도메인**: `ReturnController`, `ReturnService`, `Return` 엔티티, `ReturnStatus`, `ReturnRequest` DTO, `ReturnRepository`, `ReturnException`

### 수정 (4개 파일)
- `OrderController.java` — 취소/반품 엔드포인트 제거 (-37줄)
- `OrderService.java` — 취소/반품 비즈니스 로직 제거 (-108줄)
- `GlobalExceptionHandler.java` — `CancelException`, `ReturnException` 핸들러 추가
- `ErrorCode.java` — 취소(9xxx), 반품(10xxx) 에러코드 8개 추가

### 삭제 (1개 파일)
- `order/dto/CancelRequest.java` — 새로운 cancel 패키지로 이동

---

## 코드리뷰 분석 결과

### 1. 보안 취약점 (Critical / High)

#### 1-1. 사용자 신원 위조 (Identity Spoofing) — `CancelRequest.java`, `ReturnRequest.java`

**심각도: Critical**

```java
// CancelRequest.java (cancel/dto)
@NotNull(message = "사용자 ID는 필수입니다.")
private Long userId;
```

**문제점**: 클라이언트가 요청 본문에 `userId`를 직접 전달하는 구조입니다. 인증된 사용자의 세션/토큰에서 추출하지 않으므로, 공격자가 타인의 `userId`를 넣어 대리 취소/반품 요청이 가능합니다.

**권장 수정안**:
```java
// Controller에서 인증된 사용자 정보를 주입
@PostMapping
public ResponseEntity<OrderResponse> requestCancel(
    @AuthenticationPrincipal UserDetails principal,
    @Valid @RequestBody CancelRequest request
) {
    Long authenticatedUserId = ((CustomUserDetails) principal).getUserId();
    return ResponseEntity.ok(
        cancelService.requestCancel(request.getOrderId(), authenticatedUserId, request)
    );
}
```

`ReturnController`도 동일한 문제가 있으므로 함께 수정 필요합니다.

---

#### 1-2. 주문 소유권 미검증 — `CancelService.java:46-53`, `ReturnService.java:47-52`

**심각도: Critical**

```java
// CancelService.java
Order order = orderRepository.findById(orderId)
    .orElseThrow(() -> new OrderException(ORDER_NOT_FOUND));

User user = userRepository.findById(userId)
    .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

// ❌ order.getUser().getId().equals(user.getId()) 검증 없음
cancelPolicy.validate(order);
```

**문제점**: Order와 User를 각각 조회하지만, 해당 주문이 실제로 그 사용자의 주문인지 확인하지 않습니다. A 사용자가 B 사용자의 주문을 취소/반품할 수 있는 **수평적 권한 상승(IDOR)** 취약점입니다.

**권장 수정안**:
```java
if (!order.getUser().getId().equals(userId)) {
    throw new CancelException(ErrorCode.CANCEL_NOT_ALLOWED,
        "본인의 주문만 취소할 수 있습니다.");
}
```

---

#### 1-3. OrderItem과 Order 연관 검증 누락 — `ReturnService.java:53-64`

**심각도: High**

```java
OrderItem orderItem = orderItemRepository.findById(request.getOrderItemId())
    .orElseThrow(() -> new OrderException(ORDER_NOT_FOUND));

// ❌ orderItem.getOrder().getId().equals(orderId) 검증 없음
```

**문제점**: 조회한 `OrderItem`이 해당 `Order`에 속하는지 검증하지 않습니다. 공격자가 다른 주문의 상품 ID를 넣어 잘못된 반품 레코드를 생성할 수 있습니다.

**권장 수정안**:
```java
if (!orderItem.getOrder().getId().equals(orderId)) {
    throw new ReturnException(ErrorCode.RETURN_NOT_ALLOWED,
        "해당 주문에 포함되지 않은 상품입니다.");
}
```

---

### 2. 비즈니스 로직 결함 (Medium)

#### 2-1. 취소 승인 시 OrderTransitionPolicy 미적용 — `CancelService.java:76-81`

**심각도: Medium**

```java
public OrderResponse approveCancel(Long cancelId) {
    Cancel cancel = cancelRepository.findById(cancelId)
        .orElseThrow(() -> new CancelException(ErrorCode.CANCEL_NOT_FOUND));

    cancel.approve();

    Order order = cancel.getOrder();
    // ❌ transitionPolicy.validateTransition(order.getStatus(), CANCELED) 호출 없음
    order.changeStatus(CANCELED);
    paymentService.cancelPayment(order);
}
```

**문제점**: 기존 `OrderService.approveCancel()`에서는 `transitionPolicy.validateTransition()`을 호출하여 상태 전이 가능 여부를 검증했으나, 새 코드에서는 이 검증이 누락되었습니다. 비정상 상태에서의 취소가 허용될 수 있습니다.

---

#### 2-2. 환불 처리의 비멱등성 — `ReturnService.java:101-104`

**심각도: Medium**

```java
order.changeStatus(RETURN_COMPLETED);
paymentService.refundPayment(order);  // 외부 결제 시스템 호출
```

**문제점**: `paymentService.refundPayment()`가 트랜잭션 내부에서 호출됩니다. 환불 API 호출이 성공한 후 트랜잭션이 롤백되면, 실제 환불은 되었으나 DB 상태는 이전으로 돌아가는 **결제-주문 상태 불일치**가 발생합니다.

**권장 개선안**:
- 이벤트 기반 처리 (`@TransactionalEventListener`)로 트랜잭션 커밋 후 환불 실행
- 또는 환불 레코드를 먼저 저장하고, 별도 프로세스에서 실제 환불 처리 (Outbox 패턴)

---

#### 2-3. Return 엔티티의 부정확한 에러코드 — `Return.java:65-68`

**심각도: Low**

```java
public void approve() {
    if (this.status != ReturnStatus.REQUESTED) {
        throw new ReturnException(ErrorCode.RETURN_ALREADY_PROCESSED);
    }
    // ...
}

public void complete() {
    if (this.status != ReturnStatus.APPROVED) {
        throw new ReturnException(ErrorCode.RETURN_ALREADY_PROCESSED);  // ❌ 부정확
    }
    // ...
}
```

**문제점**: `complete()` 메서드에서 `APPROVED`가 아닌 경우도 `RETURN_ALREADY_PROCESSED` 에러를 던집니다. REQUESTED 상태에서 complete를 호출하면 "이미 처리됨"이 아니라 "아직 승인되지 않음"이 적절합니다.

**권장 수정안**: `RETURN_NOT_ALLOWED` 에러코드를 사용하거나 상태별 세분화된 에러코드 적용

---

### 3. 설계/아키텍처 이슈

#### 3-1. HTTP 응답 코드 부적절 — `CancelController.java`, `ReturnController.java`

**심각도: Low**

```java
@PostMapping
public ResponseEntity<OrderResponse> requestCancel(...) {
    return ResponseEntity.ok(...);  // 200 OK 반환
}
```

**문제점**: 리소스 생성(Cancel/Return 엔티티) 요청에 `200 OK`를 반환합니다. REST 규약상 `201 Created`가 적절합니다.

---

#### 3-2. Cancel 엔티티의 DB 제약 조건 부재 — `Cancel.java`

**심각도: Low**

```java
private String reason;      // ❌ @Column(nullable = false) 누락
private LocalDateTime requestedAt;  // ❌ @Column(nullable = false) 누락
```

**문제점**: `reason`과 `requestedAt`은 비즈니스상 필수 값이지만 DB 레벨에서 NOT NULL 제약이 없습니다. 기존 migration(`V8__create_cancels.sql`)에서도 `reason`은 nullable이므로 일관성을 검토해야 합니다.

---

#### 3-3. GlobalExceptionHandler 코드 중복 — `GlobalExceptionHandler.java:135-163`

**심각도: Low (코드 품질)**

```java
// CancelException, ReturnException, OrderException, UserException... 모두 동일한 패턴
Map<String, Object> errorResponse = new HashMap<>();
errorResponse.put("timestamp", LocalDateTime.now());
errorResponse.put("httpStatus", e.getErrorCode().getHttpStatus());
errorResponse.put("code", e.getErrorCode().getCode());
errorResponse.put("message", e.getMessage());
```

**문제점**: 10개 이상의 핸들러가 동일한 응답 구조를 반복합니다. 하지만 이는 기존 코드의 문제이며 이 PR의 범위를 벗어납니다.

**향후 개선안**: 공통 헬퍼 메서드 추출 또는 `CustomException` 기반 단일 핸들러로 통합

---

#### 3-4. OrderService에서 recordHistory 메서드 제거

**심각도: Medium (기능 누락)**

```java
// 제거된 코드 (OrderService.java)
private void recordHistory(Order order, OrderStatus before, OrderStatus after, String reason) {
    OrderHistory history = OrderHistory.builder()
        .order(order)
        .beforeStatus(before)
        .afterStatus(after)
        .reason(reason)
        .build();
    orderHistoryRepository.save(history);
}
```

**문제점**: 주문 이력 기록 메서드가 삭제되었으나 새로운 CancelService/ReturnService에서 이력 기록이 이루어지지 않습니다. 취소/반품 상태 변경에 대한 감사 이력(audit trail)이 유실됩니다.

---

#### 3-5. Order 도메인 의존성 — `CancelService`, `ReturnService`

**심각도: Info (아키텍처)**

새로 분리된 Cancel/Return 서비스가 여전히 `OrderRepository`, `OrderTransitionPolicy`, `CancelPolicy`, `ReturnPolicy` 등 Order 도메인 컴포넌트에 직접 의존합니다. 완전한 도메인 분리를 위해서는 도메인 이벤트 기반의 느슨한 결합이 필요합니다만, 현 단계에서는 합리적인 트레이드오프입니다.

---

### 4. 테스트 부족

#### 4-1. 새 도메인에 대한 테스트 미작성

**심각도: Medium**

`CancelService`, `ReturnService`에 대한 단위 테스트가 없습니다. 기존 `OrderServiceTest`에서 import 경로만 변경된 상태이며, 해당 테스트도 이동된 메서드에 대한 것인지 확인이 필요합니다.

**필요한 테스트**:
- 취소 요청 → 정상 처리
- 타인의 주문 취소 시도 → 거부 (현재 검증 없음)
- 이미 처리된 취소 재요청 → 에러
- 반품 요청/승인/완료 플로우
- OrderItem-Order 불일치 시 거부

---

## 리뷰 요약 및 우선순위

| 우선순위 | 이슈 | 분류 | 조치 |
|---------|------|------|------|
| **P0** | 사용자 신원 위조 (userId 클라이언트 제공) | 보안 | 머지 전 필수 수정 |
| **P0** | 주문 소유권 미검증 (IDOR) | 보안 | 머지 전 필수 수정 |
| **P1** | OrderItem-Order 연관 검증 누락 | 보안 | 머지 전 필수 수정 |
| **P1** | 취소 승인 시 TransitionPolicy 미적용 | 비즈니스 로직 | 머지 전 수정 권장 |
| **P1** | 주문 이력(OrderHistory) 기록 누락 | 기능 누락 | 머지 전 수정 권장 |
| **P2** | 환불 처리 비멱등성 (트랜잭션 내 외부 호출) | 설계 | 추후 개선 가능 |
| **P2** | CancelService/ReturnService 단위 테스트 미작성 | 테스트 | 추후 보완 가능 |
| **P3** | HTTP 201 Created 미사용 | REST 규약 | 추후 개선 |
| **P3** | Return 엔티티 부정확한 에러코드 | 코드 품질 | 추후 개선 |
| **P3** | Cancel 엔티티 DB NOT NULL 제약 누락 | 데이터 무결성 | 추후 개선 |
| **P3** | GlobalExceptionHandler 코드 중복 | 코드 품질 | 추후 리팩토링 |

---

## 최종 의견

도메인 분리 방향은 적절하지만, **3건의 보안 취약점(P0-P1)이 머지 전 반드시 해결**되어야 합니다. 특히 사용자 인증 정보를 클라이언트에서 받는 구조와 주문 소유권 미검증은 프로덕션 배포 시 심각한 데이터 무결성/보안 문제를 야기할 수 있습니다.

비즈니스 로직 관련 이슈(TransitionPolicy 미적용, OrderHistory 누락)도 기존 동작과의 일관성을 유지하기 위해 이번 PR에서 함께 수정하는 것을 권장합니다.
