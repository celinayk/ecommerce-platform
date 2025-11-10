package com.ecommerce.platform.domain.product.dto;

import jakarta.validation.constraints.Min;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductUpdateRequest {

  private String name;
  private String description;

  @Min(value = 0, message = "가격은 0 이상이어야 합니다.")
  private Long price;

  @Min(value = 0, message = "재고는 0 이상이어야 합니다.")
  private Long stock;

  private Long categoryId;

  @Builder
  public ProductUpdateRequest(String name, String description, Long price, Long stock, Long categoryId) {
    this.name = name;
    this.description = description;
    this.price = price;
    this.stock = stock;
    this.categoryId = categoryId;
  }
}
