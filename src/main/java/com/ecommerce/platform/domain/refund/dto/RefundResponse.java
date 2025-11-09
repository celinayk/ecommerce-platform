package com.ecommerce.platform.domain.refund.dto;

import com.ecommerce.platform.domain.refund.entity.Refund;
import com.ecommerce.platform.domain.refund.entity.RefundStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class RefundResponse {
  private Long id;
  private Long userId;
  private String userName;
  private Long orderId;
  private String reason;
  private RefundStatus status;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public static RefundResponse from(Refund refund) {
    return RefundResponse.builder()
        .id(refund.getId())
        .userId(refund.getUser() != null ? refund.getUser().getId() : null)
        .userName(refund.getUser() != null ? refund.getUser().getName() : null)
        .orderId(refund.getOrder() != null ? refund.getOrder().getId() : null)
        .reason(refund.getReason())
        .status(refund.getStatus())
        .createdAt(refund.getCreatedAt())
        .updatedAt(refund.getUpdatedAt())
        .build();
  }
}
