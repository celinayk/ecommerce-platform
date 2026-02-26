package com.ecommerce.platform.domain.cancel.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CancelRequest {

  @NotNull(message = "주문 ID는 필수입니다.")
  private Long orderId;

  @NotNull(message = "사용자 ID는 필수입니다.")
  private Long userId;

  @NotBlank(message = "취소 사유는 필수입니다.")
  private String reason;

}