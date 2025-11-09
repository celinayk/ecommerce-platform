package com.ecommerce.platform.domain.refund.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RefundCreateRequest {

  @NotNull(message = "사용자 ID는 필수입니다.")
  private Long userId;

  @NotNull(message = "주문 ID는 필수입니다.")
  private Long orderId;

  @NotNull(message = "환불 사유는 필수입니다.")
  private String reason;

  @Builder
  public RefundCreateRequest(Long userId, Long orderId, String reason) {
    this.userId = userId;
    this.orderId = orderId;
    this.reason = reason;
  }
}
