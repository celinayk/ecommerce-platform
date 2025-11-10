package com.ecommerce.platform.domain.product.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductSearchRequest {
  private String keyword;
  private Long minPrice;
  private Long maxPrice;
  private Long categoryId;
}
