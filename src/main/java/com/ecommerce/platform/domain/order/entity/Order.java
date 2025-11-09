package com.ecommerce.platform.domain.order.entity;

import com.ecommerce.platform.domain.common.BaseEntity;
import com.ecommerce.platform.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class Order extends BaseEntity {

  private Long id;
  private User user;
  private Long totalAmount;
  private OrderStatus status;
  private List<OrderItem> orderItems = new ArrayList<>();

  @Builder
  public Order(User user, Long totalAmount, OrderStatus status) {
    this.user = user;
    this.totalAmount = totalAmount != null ? totalAmount : 0L;
    this.status = status != null ? status : OrderStatus.PENDING;
  }

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
        .reduce(0L, Long::sum);
  }

  // 생성 메서드
  public static Order createOrder(User user, List<OrderItem> orderItems) {
    Order order = Order.builder()
        .user(user)
        .totalAmount(0L)
        .status(OrderStatus.PENDING)
        .build();

    for (OrderItem orderItem : orderItems) {
      order.addOrderItem(orderItem);
    }
    return order;
  }

  // 주문 취소
  public void cancel() {
    if(this.status == OrderStatus.CANCELED) {
      throw new IllegalStateException("이미 취소된 주문입니다.");
    }
    this.status = OrderStatus.CANCELED;
    // 재고 복구
    for (OrderItem orderItem : orderItems) {
      orderItem.cancel();
    }
  }
}
