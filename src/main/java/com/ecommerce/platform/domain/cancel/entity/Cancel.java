package com.ecommerce.platform.domain.cancel.entity;


import com.ecommerce.platform.domain.cancel.exception.CancelException;
import com.ecommerce.platform.domain.common.BaseEntity;
import com.ecommerce.platform.global.common.response.ErrorCode;
import com.ecommerce.platform.domain.order.entity.Order;
import com.ecommerce.platform.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "cancels", indexes = {
    @Index(name = "idx_cancels_order", columnList = "order_id")
})
public class Cancel extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "order_id", nullable = false)
  private Order order;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 30)
  private CancelStatus status;

  private String reason;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "requested_by", nullable = false)
  private User requestedBy;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "approved_by")
  private User approvedBy;

  @Column(name = "requested_at")
  private LocalDateTime requestedAt;

  @Column(name = "approved_at")
  private LocalDateTime approvedAt;

  public void approve() {
    if (this.status != CancelStatus.REQUESTED) {
      throw new CancelException(ErrorCode.CANCEL_ALREADY_PROCESSED);
    }
    this.status = CancelStatus.APPROVED;
    this.approvedAt = LocalDateTime.now();
  }

  public void reject() {
    if (this.status != CancelStatus.REQUESTED) {
      throw new CancelException(ErrorCode.CANCEL_ALREADY_PROCESSED);
    }
    this.status = CancelStatus.REJECTED;
  }


}
