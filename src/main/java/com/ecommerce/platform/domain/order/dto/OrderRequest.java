package com.ecommerce.platform.domain.order.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OrderRequest {

  @NotNull(message = "사용자 ID는 필수입니다.")
  @Positive(message = "사용자 ID는 양수여야 합니다.")
  private Long userId;

  @NotNull(message = "상품 ID는 필수입니다.")
  @Positive(message = "상품 ID는 양수여야 합니다.")
  private Long productId;

  @NotNull(message = "주문 수량은 필수입니다.")
  @Min(value = 1, message = "주문 수량은 최소 1개 이상이어야 합니다.")
  @Max(value = 1000, message = "주문 수량은 최대 1000개까지 가능합니다.")
  private Integer count;
}