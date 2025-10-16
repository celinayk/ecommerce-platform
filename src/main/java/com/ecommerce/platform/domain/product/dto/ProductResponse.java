package com.ecommerce.platform.domain.product.dto;

import com.ecommerce.platform.domain.product.entity.Product;
import com.ecommerce.platform.domain.product.entity.ProductStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProductResponse {

  private Long id;
  private String name;
  private String description;
  private Integer price;
  private Integer stockQuantity;
  private ProductStatus status;

  public static ProductResponse from(Product product) {
    return new ProductResponse(
        product.getId(),
        product.getName(),
        product.getDescription(),
        product.getPrice(),
        product.getStockQuantity(),
        product.getStatus()
    );
  }
}