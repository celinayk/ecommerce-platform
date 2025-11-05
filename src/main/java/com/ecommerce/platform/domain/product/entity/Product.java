package com.ecommerce.platform.domain.product.entity;

import com.ecommerce.platform.domain.common.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Product extends BaseEntity {

  private Long id;
  private String name;
  private String description;
  private Integer price;
  private Integer stockQuantity = 0;
  private ProductStatus status = ProductStatus.AVAILABLE;


  // 재고 감소
  public void decreaseStock(int quantity) {
    if(this.stockQuantity < quantity) {
      throw new IllegalArgumentException("재고가 부족합니다.");
    }
    this.stockQuantity -= quantity;
    if(this.stockQuantity == 0) {
      this.status = ProductStatus.SOLD_OUT;
    }
  }

  // 재고 증가
  public void increaseStock(int quantity) {
    this.stockQuantity += quantity;
    if(this.stockQuantity >0 && this.status == ProductStatus.SOLD_OUT) {
      this.status = ProductStatus.AVAILABLE;
    }
  }

}
