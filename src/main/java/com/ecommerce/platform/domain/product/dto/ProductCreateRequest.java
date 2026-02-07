package com.ecommerce.platform.domain.product.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
public class ProductCreateRequest {

  @NotNull(message = "판매자 ID는 필수입니다.")
  private Long sellerId;

  @NotBlank(message = "상품명은 필수입니다.")
  @Size(min = 1, max = 100, message = "상품명은 1-100자 이내여야 합니다.")
  private String name;

  @Size(max = 500, message = "상품 설명은 500자 이내여야 합니다.")
  private String description;

  @NotNull(message = "가격은 필수입니다.")
  private BigDecimal price;

  private Long categoryId;

  @Builder
  public ProductCreateRequest(Long sellerId, String name, String description, BigDecimal price, Long categoryId) {
    this.sellerId = sellerId;
    this.name = name;
    this.description = description;
    this.price = price;
    this.categoryId = categoryId;
  }
}