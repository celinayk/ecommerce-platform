package com.ecommerce.platform.domain.order.controller;

import com.ecommerce.platform.domain.order.dto.OrderRequest;
import com.ecommerce.platform.domain.order.dto.OrderResponse;
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



}