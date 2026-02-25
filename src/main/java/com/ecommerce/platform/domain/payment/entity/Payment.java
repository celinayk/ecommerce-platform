package com.ecommerce.platform.domain.payment.entity;

import com.ecommerce.platform.domain.common.BaseEntity;
import com.ecommerce.platform.domain.order.entity.Order;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "payments", indexes = {
    @Index(name = "idx_payments_order", columnList = "order_id"),
    @Index(name = "idx_payments_status", columnList = "status")
})
public class Payment extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "order_id", nullable = false, unique = true)
  private Order order;

  @Column(name = "amount", nullable = false, precision = 10, scale = 2)
  private BigDecimal amount;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 30)
  private PaymentStatus status;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 30)
  private PaymentMethod method;

  @Column(name = "paid_at")
  private LocalDateTime paidAt;


  @Column(name = "canceled_at")
  private LocalDateTime canceledAt;

  @Column(name = "refunded_at")
  private LocalDateTime refundedAt;

  // PENDING → COMPLETED
  public void complete() {
    if (this.status != PaymentStatus.PENDING) {
      // throw new PaymentException(ErrorCode.PAYMENT_ALREADY_PROCESSED);
    }
    this.status = PaymentStatus.COMPLETED;
    this.paidAt = LocalDateTime.now();
  }

  // COMPLETED → CANCELED
  public void cancel() {
    if (this.status != PaymentStatus.COMPLETED) {
      // throw new PaymentException(ErrorCode.PAYMENT_CANNOT_BE_CANCELED);
    }
    this.status = PaymentStatus.CANCELED;
    this.canceledAt = LocalDateTime.now();
  }

  // COMPLETED → REFUNDED
  public void markAsRefunded() {
    if (this.status != PaymentStatus.COMPLETED) {
      // throw new PaymentException(ErrorCode.PAYMENT_CANNOT_BE_REFUNDED);
    }
    this.status = PaymentStatus.REFUNDED;
    this.refundedAt = LocalDateTime.now();
  }
}
