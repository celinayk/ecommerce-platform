package com.ecommerce.platform.domain.order.entity;

import com.ecommerce.platform.domain.common.BaseEntity;
import com.ecommerce.platform.domain.product.entity.Product;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items", indexes = {
    @Index(name = "idx_order_items_order", columnList = "order_id")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class OrderItem extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "order_id", nullable = false)
  private Order order;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "product_id", nullable = false)
  private Product product;

  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal price;

  @Column(nullable = false)
  private Integer quantity;

  public BigDecimal getSubtotal() {
    return price.multiply(BigDecimal.valueOf(quantity));
  }

  public static OrderItem createOrderItem(Product product, BigDecimal price, int quantity) {
    return OrderItem.builder()
        .product(product)
        .quantity(quantity)
        .price(price)
        .build();
  }

  public void setOrder(Order order) {
    this.order = order;
  }

  public void cancel() {
    // TODO: 재고 복구 로직은 Stock 도메인 구현 후 추가 필요
  }
}
