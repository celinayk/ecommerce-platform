package com.ecommerce.platform.domain.order.entity;

import com.ecommerce.platform.domain.product.entity.Product;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@Getter
@Setter
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

  @Column(nullable = false)
  private Integer price;

  @Column(nullable = false)
  private Integer quantity;

  @Column(nullable = false)
  private Integer subtotal;

  // 생성 메서드
  public static OrderItem createOrderItem(Product product, int quantity) {
    // 재고 감소
    product.decreaseStock(quantity);
    OrderItem orderItem = new OrderItem();
    orderItem.setProduct(product);
    orderItem.setQuantity(quantity);
    orderItem.setPrice(product.getPrice());
    orderItem.setSubtotal(product.getPrice() * quantity);
    return orderItem;
  }

  // 주문 취소
  public void cancel() {
    getProduct().increaseStock(quantity);
  }




}
