package com.ecommerce.platform.domain.product.dto;

import com.ecommerce.platform.domain.product.entity.Product;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class ProductResponse {

  private Long id;
  private Long sellerId;
  private Long categoryId;
  private String name;
  private String description;
  private BigDecimal price;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public static ProductResponse from(Product product) {
    return ProductResponse.builder()
        .id(product.getId())
        .categoryId(product.getCategoryId())
        .name(product.getName())
        .description(product.getDescription())
        .price(product.getPrice())
        .createdAt(product.getCreatedAt())
        .updatedAt(product.getUpdatedAt())
        .build();
  }
}