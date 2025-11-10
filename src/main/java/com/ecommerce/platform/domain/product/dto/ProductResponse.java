package com.ecommerce.platform.domain.product.dto;

import com.ecommerce.platform.domain.product.entity.Product;
import com.ecommerce.platform.domain.product.entity.ProductStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ProductResponse {

  private Long id;
  private String name;
  private String description;
  private Long price;
  private Long stock;
  private ProductStatus status;
  private Long categoryId;
  private String categoryName;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public static ProductResponse from(Product product) {
    return ProductResponse.builder()
        .id(product.getId())
        .name(product.getName())
        .description(product.getDescription())
        .price(product.getPrice())
        .stock(product.getStock())
        .status(product.getStatus())
        .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
        .categoryName(product.getCategory() != null ? product.getCategory().getName() : null)
        .createdAt(product.getCreatedAt())
        .updatedAt(product.getUpdatedAt())
        .build();
  }
}