package com.ecommerce.platform.domain.product;

import com.ecommerce.platform.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Getter
@NoArgsConstructor
public class Product extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 255)
  private String name;

  @Column(columnDefinition = "TEXT")
  private String description;

  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal price;

  @Column(nullable = false)
  private Integer stockQuantity = 0;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private ProductStatus status = ProductStatus.AVAILABLE;

  public static Product createProduct(String name, String description, BigDecimal price, Integer stockQuantity) {
    Product product = new Product();
    product.name = name;
    product.description = description;
    product.price = price;
    product.stockQuantity = stockQuantity;
    product.status = ProductStatus.AVAILABLE;
    return product;
  }

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
