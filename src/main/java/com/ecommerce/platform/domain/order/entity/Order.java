package com.ecommerce.platform.domain.order.entity;

import com.ecommerce.platform.domain.common.BaseEntity;
import com.ecommerce.platform.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "orders", indexes = {
    @Index(name = "idx_orders_user", columnList = "user_id"),
    @Index(name = "idx_orders_status", columnList = "status")
})
public class Order extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;


  @Column(name = "total_price", nullable = false, precision = 10, scale = 2)
  private BigDecimal totalPrice;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 30)
  private OrderStatus status;

  @Column(name = "ordered_at", nullable = false)
  private LocalDateTime orderedAt;

  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<OrderItem> orderItems = new ArrayList<>();


  public void addOrderItem(OrderItem orderItem) {
    orderItems.add(orderItem);
    orderItem.setOrder(this);
    calculateTotalPrice();
  }

  private void calculateTotalPrice() {
    this.totalPrice = orderItems.stream()
        .map(OrderItem::getSubtotal)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  public static Order createOrder(User user, List<OrderItem> orderItems) {
    Order order = Order.builder()
        .user(user)
        .totalPrice(BigDecimal.ZERO)
        .status(OrderStatus.PENDING)
        .orderedAt(LocalDateTime.now())
        .build();

    for (OrderItem orderItem : orderItems) {
      order.addOrderItem(orderItem);
    }
    return order;
  }

  public void cancel() {
    if (this.status == OrderStatus.CANCELED) {
      throw new IllegalStateException("이미 취소된 주문입니다.");
    }
    this.status = OrderStatus.CANCELED;
    for (OrderItem orderItem : orderItems) {
      orderItem.cancel();
    }
  }
}
