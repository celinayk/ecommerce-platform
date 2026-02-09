package com.ecommerce.platform.domain.stock.entity;

import com.ecommerce.platform.domain.common.BaseEntity;
import com.ecommerce.platform.domain.product.entity.Product;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "stocks")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Stock extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "product_id", nullable = false, unique = true)
  private Product product;

  @Column(nullable = false)
  @Builder.Default
  private Integer quantity = 0;

  public void increase(int amount) {
    if (amount <= 0) {
      throw new IllegalArgumentException("증가 수량은 1 이상이어야 합니다.");
    }
    this.quantity += amount;
  }

  public void decrease(int amount) {
    if (amount <= 0) {
      throw new IllegalArgumentException("감소 수량은 1 이상이어야 합니다.");
    }
    if (this.quantity < amount) {
      throw new IllegalStateException("재고가 부족합니다. 현재 재고: " + this.quantity);
    }
    this.quantity -= amount;
  }

  public boolean hasStock(int amount) {
    return this.quantity >= amount;
  }

  public void updateQuantity(Integer quantity) {
    this.quantity = quantity;
  }
}