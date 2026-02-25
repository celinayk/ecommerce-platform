package com.ecommerce.platform.domain.payment.dto;

import com.ecommerce.platform.domain.payment.entity.Payment;
import com.ecommerce.platform.domain.payment.entity.PaymentMethod;
import com.ecommerce.platform.domain.payment.entity.PaymentStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class PaymentResponse {

  private Long id;
  private Long orderId;
  private BigDecimal amount;
  private PaymentStatus status;
  private PaymentMethod method;
  private LocalDateTime paidAt;
  private LocalDateTime canceledAt;
  private LocalDateTime refundedAt;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public static PaymentResponse from(Payment payment) {
    return PaymentResponse.builder()
        .id(payment.getId())
        .orderId(payment.getOrder().getId())
        .amount(payment.getAmount())
        .status(payment.getStatus())
        .method(payment.getMethod())
        .paidAt(payment.getPaidAt())
        .canceledAt(payment.getCanceledAt())
        .refundedAt(payment.getRefundedAt())
        .createdAt(payment.getCreatedAt())
        .updatedAt(payment.getUpdatedAt())
        .build();
  }
}