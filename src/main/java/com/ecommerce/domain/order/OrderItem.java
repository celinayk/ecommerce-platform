package com.ecommerce.domain.order;

import com.ecommerce.domain.product.Product;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@Getter
@NoArgsConstructor
public class OrderItem {

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

  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal subtotal;

  // 생성 메서드
  public static OrderItem createOrderItem(Product product, int quantity) {
    // 재고 감소
    product.decreaseStock(quantity);

    OrderItem orderItem = new OrderItem();
    orderItem.product = product;
    orderItem.price = product.getPrice();
    orderItem.quantity = quantity;
    orderItem.subtotal = product.getPrice().multiply(BigDecimal.valueOf(quantity));
    return orderItem;
  }

  // 주문 취소
  public void cancel() {
    getProduct().increaseStock(quantity);
  }

  // 연관관계 편의 메서드
  protected void setOrder(Order order) {
    this.order = order;
  }

}
