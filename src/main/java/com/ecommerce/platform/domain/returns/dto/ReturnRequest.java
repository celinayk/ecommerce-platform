package com.ecommerce.platform.domain.returns.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReturnRequest {

  @NotNull(message = "주문 ID는 필수입니다.")
  private Long orderId;

  @NotNull(message = "사용자 ID는 필수입니다.")
  private Long userId;

  @NotNull(message = "반품할 주문 항목 ID는 필수입니다.")
  private Long orderItemId;

  @NotBlank(message = "반품 사유는 필수입니다.")
  private String reason;
}