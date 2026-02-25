package com.ecommerce.platform.domain.payment.controller;

import com.ecommerce.platform.domain.payment.dto.PaymentCreateRequest;
import com.ecommerce.platform.domain.payment.dto.PaymentResponse;
import com.ecommerce.platform.domain.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

  private final PaymentService paymentService;

  // 결제 승인 > 토스페이먼츠 추가 예정

  // 결제 조회
  @GetMapping("/{id}")
  public ResponseEntity<PaymentResponse> getPayment(@PathVariable Long id) {
    return ResponseEntity.ok(paymentService.getPaymentById(id));
  }

  @GetMapping("/order/{orderId}")
  public ResponseEntity<PaymentResponse> getPaymentByOrder(@PathVariable Long orderId) {
    return ResponseEntity.ok(paymentService.getPaymentByOrderId(orderId));
  }

  @PostMapping("/confirm")
  public ResponseEntity<PaymentResponse> confirmPayment(
      @Valid @RequestBody PaymentCreateRequest request) {
    return ResponseEntity.ok(
        paymentService.confirmPayment(request.getOrderId(), request.getPaymentMethod())
    );
  }

}
