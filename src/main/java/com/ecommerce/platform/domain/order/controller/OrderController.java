package com.ecommerce.platform.domain.order.controller;

import com.ecommerce.platform.domain.order.dto.CancelRequest;
import com.ecommerce.platform.domain.order.dto.OrderRequest;
import com.ecommerce.platform.domain.order.dto.OrderResponse;
import com.ecommerce.platform.domain.order.dto.ReturnRequest;
import com.ecommerce.platform.domain.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

  private final OrderService orderService;

  // 주문 생성
  @PostMapping
  public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody OrderRequest request) {
    OrderResponse response = orderService.createOrder(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  // 주문 목록 조회 (페이징, 정렬)
  @GetMapping
  public ResponseEntity<Page<OrderResponse>> getAllOrders(
      @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
    Page<OrderResponse> responses = orderService.getAllOrders(pageable);
    return ResponseEntity.ok(responses);
  }

  // 특정 사용자 주문 목록 조회
  @GetMapping("/users/{userId}")
  public ResponseEntity<Page<OrderResponse>> getOrdersByUserId(
      @PathVariable Long userId,
      @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
    Page<OrderResponse> responses = orderService.getOrdersByUserId(userId, pageable);
    return ResponseEntity.ok(responses);
  }

  // 주문 상세 조회
  @GetMapping("/{id}")
  public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long id) {
    OrderResponse response = orderService.getOrderById(id);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/{id}/cancel")
  public ResponseEntity<OrderResponse> requestCancel(
      @PathVariable Long id,
      @Valid @RequestBody CancelRequest request
  ) {
    OrderResponse response = orderService.requestCancel(id, request);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/{id}/return")
  public ResponseEntity<OrderResponse> requestReturn(
      @PathVariable Long id,
      @Valid @RequestBody ReturnRequest request
  ) {
    OrderResponse response = orderService.requestReturn(id, request);
    return ResponseEntity.ok(response);
  }

  // 주문 확인 (관리자)
  @PostMapping("/{id}/confirm")
  public ResponseEntity<OrderResponse> confirmOrder(@PathVariable Long id) {
    return ResponseEntity.ok(orderService.confirmOrder(id));
  }

  // 취소 승인 (관리자)
  @PostMapping("/{id}/approve-cancel")
  public ResponseEntity<OrderResponse> approveCancel(@PathVariable Long id) {
    return ResponseEntity.ok(orderService.approveCancel(id));
  }

  // 반품 승인 (관리자)
  @PostMapping("/{id}/approve-return")
  public ResponseEntity<OrderResponse> approveReturn(@PathVariable Long id) {
    return ResponseEntity.ok(orderService.approveReturn(id));
  }

  // 반품 완료 (관리자)
  @PostMapping("/{id}/complete-return")
  public ResponseEntity<OrderResponse> completeReturn(@PathVariable Long id) {
    return ResponseEntity.ok(orderService.completeReturn(id));
  }

}