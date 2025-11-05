package com.ecommerce.platform.domain.order.entity;

import com.ecommerce.platform.domain.common.BaseEntity;
import com.ecommerce.platform.domain.user.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class Order extends BaseEntity {

  private Long id;
  private User user;
  private Integer totalAmount;
  private OrderStatus status = OrderStatus.ORDER;
  private List<OrderItem> orderItems = new ArrayList<>();

  // 연관관계 편의 메서드
  public void addOrderItem(OrderItem orderItem) {
    orderItems.add(orderItem);
    orderItem.setOrder(this);
    calculateTotalAmount();  // 자동으로 총액 재계산
  }

  // 총 금액 계산
  private void calculateTotalAmount() {
    this.totalAmount = orderItems.stream()
        .map(OrderItem::getSubtotal)
        .reduce(0, Integer::sum);
  }

  // 생성 메서드
  public static Order createOrder(User user, List<OrderItem> orderItems) {
    Order order = new Order();
    order.setUser(user);
    order.setTotalAmount(0);
    for (OrderItem orderItem : orderItems) {
      order.addOrderItem(orderItem);
    }
    order.setStatus(OrderStatus.ORDER);
    return order;
  }

  // 주문 취소
  public void cancel() {
    if(this.status == OrderStatus.CANCEL) {
      throw new IllegalStateException("이미 취소된 주문입니다.");
    }
    this.status = OrderStatus.CANCEL;
    // 재고 복구
    for (OrderItem orderItem : orderItems) {
      orderItem.cancel();
    }
  }
}
