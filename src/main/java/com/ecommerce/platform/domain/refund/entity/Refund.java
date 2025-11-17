package com.ecommerce.platform.domain.refund.entity;

import com.ecommerce.platform.domain.common.BaseEntity;
import com.ecommerce.platform.domain.order.entity.Order;
import com.ecommerce.platform.domain.refund.exception.RefundException;
import com.ecommerce.platform.domain.user.entity.User;
import com.ecommerce.platform.global.common.response.ErrorCode;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class Refund extends BaseEntity {
  private Long id;
  private User user;
  private Order order;
  private String reason;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private RefundStatus status;

  @Builder
  public Refund(User user, Order order, String reason) {
    this.user = user;
    this.order = order;
    this.reason = reason;
    this.status = RefundStatus.PENDING;
  }

  // 비즈니스 로직 -> 상태값을 바꾸는거라 엔티티 클래스에 작성
  public void approve() {
    if(this.status != RefundStatus.PENDING) {
      throw new RefundException(ErrorCode.REFUND_ALREADY_PROCESSED);
    }
    this.status = RefundStatus.APPROVED;
  }

  public void reject() {
    if(this.status != RefundStatus.PENDING) {
      throw new RefundException(ErrorCode.REFUND_ALREADY_PROCESSED);
    }
    this.status = RefundStatus.REJECTED;
  }

}
