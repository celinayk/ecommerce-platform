package com.ecommerce.platform.domain.order.entity;

import com.ecommerce.platform.domain.product.entity.Product;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OrderItem {

  private Long id;
  private Order order;
  private Product product;
  private Long price;
  private Integer quantity;
  private Long subtotal;

  @Builder
  public OrderItem(Order order, Product product, Long price, Integer quantity) {
    this.order = order;
    this.product = product;
    this.price = price;
    this.quantity = quantity;
    this.subtotal = price * quantity;
  }

  // 생성 메서드
  public static OrderItem createOrderItem(Product product, int quantity) {
    // 재고 감소
    product.decreaseStock(quantity);

    return OrderItem.builder()
        .product(product)
        .quantity(quantity)
        .price(product.getPrice())
        .build();
  }

  // 연관관계 편의 메서드
  public void setOrder(Order order) {
    this.order = order;
  }

  // 주문 취소
  public void cancel() {
    getProduct().increaseStock(quantity);
  }
}
