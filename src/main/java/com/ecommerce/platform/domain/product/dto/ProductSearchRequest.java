package com.ecommerce.platform.domain.product.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class ProductSearchRequest {
  private String keyword;
  private BigDecimal minPrice;
  private BigDecimal maxPrice;
  private Long categoryId;
}
