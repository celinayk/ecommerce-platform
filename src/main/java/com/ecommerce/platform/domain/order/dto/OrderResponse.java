package com.ecommerce.platform.domain.order.dto;

import com.ecommerce.platform.domain.order.entity.Order;
import com.ecommerce.platform.domain.order.entity.OrderItem;
import com.ecommerce.platform.domain.order.entity.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class OrderResponse {

  private Long id;
  private Long userId;
  private String userName;
  private BigDecimal totalPrice;
  private OrderStatus status;
  private LocalDateTime orderedAt;
  private List<OrderItemInfo> orderItems;
  private LocalDateTime createdAt;

  @Getter
  @AllArgsConstructor
  public static class OrderItemInfo {
    private Long productId;
    private String productName;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal subtotal;

    public static OrderItemInfo from(OrderItem orderItem) {
      return new OrderItemInfo(
          orderItem.getProduct().getId(),
          orderItem.getProduct().getName(),
          orderItem.getPrice(),
          orderItem.getQuantity(),
          orderItem.getSubtotal()
      );
    }
  }

  public static OrderResponse from(Order order) {
    return new OrderResponse(
        order.getId(),
        order.getUser().getId(),
        order.getUser().getName(),
        order.getTotalPrice(),
        order.getStatus(),
        order.getOrderedAt(),
        order.getOrderItems().stream()
            .map(OrderItemInfo::from)
            .collect(Collectors.toList()),
        order.getCreatedAt()
    );
  }
}