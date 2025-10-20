package com.ecommerce.platform.domain.product.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProductRequest {

  @NotBlank(message = "상품명은 필수입니다.")
  @Size(min = 1, max = 100, message = "상품명은 1-100자 이내여야 합니다.")
  private String name;

  @NotBlank(message = "상품 설명은 필수입니다.")
  @Size(max = 500, message = "상품 설명은 500자 이내여야 합니다.")
  private String description;

  @NotNull(message = "가격은 필수입니다.")
  @Min(value = 0, message = "가격은 0원 이상이어야 합니다.")
  @Max(value = 100000000, message = "가격은 1억원을 초과할 수 없습니다.")
  private Integer price;

  @NotNull(message = "재고 수량은 필수입니다.")
  @Min(value = 0, message = "재고 수량은 0 이상이어야 합니다.")
  private Integer stockQuantity;
}