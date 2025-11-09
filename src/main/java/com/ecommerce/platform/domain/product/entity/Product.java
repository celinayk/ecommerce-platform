package com.ecommerce.platform.domain.product.entity;

import com.ecommerce.platform.domain.category.entity.Category;
import com.ecommerce.platform.domain.common.BaseEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class Product extends BaseEntity {

  private Long id;
  private String name;
  private String description;
  private Long price;
  private Long stock;
  private Category category;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private ProductStatus status = ProductStatus.AVAILABLE;

  @Builder
  public Product(
      Category category,
      String name,
      String description,
      Long price,
      Long stock
  ) {
    this.category = category;
    this.name = name;
    this.description = description;
    this.price = price;
    this.stock = stock;
  }

  public void updateCategory(Category category) {
    this.category = category;
  }

  // 재고 감소
  public void decreaseStock(int quantity) {
    if(this.stock < quantity) {
      throw new IllegalArgumentException("재고가 부족합니다.");
    }
    this.stock -= quantity;
    if(this.stock == 0) {
      this.status = ProductStatus.SOLD_OUT;
    }
  }

  // 재고 증가
  public void increaseStock(int quantity) {
    this.stock += quantity;
    if(this.stock > 0 && this.status == ProductStatus.SOLD_OUT) {
      this.status = ProductStatus.AVAILABLE;
    }
  }

  // 상품 정보 수정
  public void updateProductInfo(String name, String description, Long price, Long stock) {
    if (name != null) {
      this.name = name;
    }
    if (description != null) {
      this.description = description;
    }
    if (price != null) {
      this.price = price;
    }
    if (stock != null) {
      this.stock = stock;
      // 재고에 따른 상태 업데이트
      if (stock == 0) {
        this.status = ProductStatus.SOLD_OUT;
      } else if (this.status == ProductStatus.SOLD_OUT) {
        this.status = ProductStatus.AVAILABLE;
      }
    }
  }

}
