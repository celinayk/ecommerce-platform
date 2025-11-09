package com.ecommerce.platform.domain.refund.controller;

import com.ecommerce.platform.domain.refund.dto.RefundCreateRequest;
import com.ecommerce.platform.domain.refund.dto.RefundResponse;
import com.ecommerce.platform.domain.refund.entity.Refund;
import com.ecommerce.platform.domain.refund.service.RefundService;
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
@RequestMapping("/api/refunds")
@RequiredArgsConstructor
public class RefundController {

  private final RefundService refundService;

  // 환불 생성 (환불 요청)
  @PostMapping
  public ResponseEntity<RefundResponse> createRefund(@Valid @RequestBody RefundCreateRequest request) {
    RefundResponse response = refundService.createRefund(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  // 환불 상세 조회
  @GetMapping("/{id}")
  public ResponseEntity<RefundResponse> getRefundById(@PathVariable Long id) {
    RefundResponse response = refundService.findRefundById(id);
    return ResponseEntity.ok(response);
  }

  // 전체 환불 목록 조회
  @GetMapping
  public ResponseEntity<Page<RefundResponse>> getAllRefunds(
      @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
    Page<RefundResponse> refunds = refundService.findAllRefunds(pageable);
    return ResponseEntity.ok(refunds);
  }

  // 특정 사용자의 환불 내역 조회
  @GetMapping("/user/{userId}")
  public ResponseEntity<?> getRefundsByUserId(@PathVariable Long userId) {
    return ResponseEntity.ok(refundService.findByUserId(userId));
  }

  // 특정 주문의 환불 내역 조회
  @GetMapping("/order/{orderId}")
  public ResponseEntity<?> getRefundsByOrderId(@PathVariable Long orderId) {
    return ResponseEntity.ok(refundService.findByOrderId(orderId));
  }

  // 환불 승인 (관리자)
  @PutMapping("/{id}/approve")
  public ResponseEntity<RefundResponse> approveRefund(@PathVariable Long id) {
    RefundResponse response = refundService.approveRefund(id);
    return ResponseEntity.ok(response);
  }

  // 환불 거절 (관리자)
  @PutMapping("/{id}/reject")
  public ResponseEntity<RefundResponse> rejectRefund(
      @PathVariable Long id,
      @RequestBody(required = false) String rejectReason) {
    RefundResponse response = refundService.rejectRefund(id, rejectReason);
    return ResponseEntity.ok(response);
  }

}
