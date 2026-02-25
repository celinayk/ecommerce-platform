package com.ecommerce.platform.domain.refund.entity;

import com.ecommerce.platform.domain.common.BaseEntity;
import com.ecommerce.platform.domain.order.entity.Order;
import com.ecommerce.platform.domain.payment.entity.Payment;
import com.ecommerce.platform.domain.refund.exception.RefundException;
import com.ecommerce.platform.domain.user.entity.User;
import com.ecommerce.platform.global.common.response.ErrorCode;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "refunds")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Refund extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "order_id", nullable = false)
  private Order order;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "payment_id")
  private Payment payment;

  @Column(length = 500)
  private String reason;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private RefundStatus status;


  public void approve() {
    if (this.status != RefundStatus.PENDING) {
      throw new RefundException(ErrorCode.REFUND_ALREADY_PROCESSED);
    }
    this.status = RefundStatus.APPROVED;
  }

  public void reject() {
    if (this.status != RefundStatus.PENDING) {
      throw new RefundException(ErrorCode.REFUND_ALREADY_PROCESSED);
    }
    this.status = RefundStatus.REJECTED;
  }
}
