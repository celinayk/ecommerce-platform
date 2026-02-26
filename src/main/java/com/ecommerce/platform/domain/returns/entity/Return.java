package com.ecommerce.platform.domain.returns.entity;

import com.ecommerce.platform.domain.common.BaseEntity;
import com.ecommerce.platform.domain.order.entity.Order;
import com.ecommerce.platform.domain.order.entity.OrderItem;
import com.ecommerce.platform.domain.returns.exception.ReturnException;
import com.ecommerce.platform.domain.user.entity.User;
import com.ecommerce.platform.global.common.response.ErrorCode;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "returns", indexes = {
    @Index(name = "idx_returns_order", columnList = "order_id")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Return extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "order_id", nullable = false)
  private Order order;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "order_item_id", nullable = false)
  private OrderItem orderItem;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "requested_by", nullable = false)
  private User requestedBy;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "approved_by")
  private User approvedBy;

  @Column(length = 200)
  private String reason;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private ReturnStatus status;

  @Column(name = "requested_at", nullable = false)
  private LocalDateTime requestedAt;

  @Column(name = "approved_at")
  private LocalDateTime approvedAt;

  public void approve() {
    if (this.status != ReturnStatus.REQUESTED) {
      throw new ReturnException(ErrorCode.RETURN_ALREADY_PROCESSED);
    }
    this.status = ReturnStatus.APPROVED;
    this.approvedAt = LocalDateTime.now();
  }

  public void complete() {
    if (this.status != ReturnStatus.APPROVED) {
      throw new ReturnException(ErrorCode.RETURN_ALREADY_PROCESSED);
    }
    this.status = ReturnStatus.COMPLETED;
  }

  public void reject() {
    if (this.status != ReturnStatus.REQUESTED) {
      throw new ReturnException(ErrorCode.RETURN_ALREADY_PROCESSED);
    }
    this.status = ReturnStatus.REJECTED;
  }
}