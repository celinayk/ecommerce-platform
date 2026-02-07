package com.ecommerce.platform.domain.product.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
public class ProductUpdateRequest {

  private String name;
  private String description;
  private BigDecimal price;
  private Long categoryId;

  @Builder
  public ProductUpdateRequest(String name, String description, BigDecimal price, Long categoryId) {
    this.name = name;
    this.description = description;
    this.price = price;
    this.categoryId = categoryId;
  }
}
