package com.ecommerce.platform.domain.order.entity;

import com.ecommerce.platform.domain.product.entity.Product;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OrderItem {

  private Long id;
  private Order order;
  private Product product;
  private Integer price;
  private Integer quantity;
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
