package com.ecommerce.platform.domain.order;

import com.ecommerce.platform.domain.common.BaseEntity;
import com.ecommerce.platform.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor
public class Order extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(nullable = false, unique = true, length = 50)
  private String orderNumber;

  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal totalAmount;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private OrderStatus status = OrderStatus.ORDER;  // 기본값 ORDER

  @Column(length = 20)
  private String paymentMethod;

  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
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
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  // 생성 메서드
  public static Order createOrder(User user, String orderNumber, String paymentMethod) {
    Order order = new Order();
    order.user = user;
    order.orderNumber = orderNumber;
    order.paymentMethod = paymentMethod;
    order.totalAmount = BigDecimal.ZERO;  // 처음엔 0, 나중에 계산
    order.status = OrderStatus.ORDER;
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
