package com.ecommerce.platform.domain.order.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReturnRequest {
  @NotNull(message = "반품할 주문항목을 선택해주세요")
  private Long orderItemId;

  @NotBlank(message = "반품 사유를 입력해주세요")
  private String reason;
}