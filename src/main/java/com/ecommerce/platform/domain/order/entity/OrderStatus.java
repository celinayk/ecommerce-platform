package com.ecommerce.platform.domain.order.entity;

public enum OrderStatus {
  PENDING("대기중"),
  COMPLETED("주문완료"),
  CANCELED("주문취소");

  private String description;

  OrderStatus(String description) {
    this.description = description;
  }

  public String getDescription() {
    return description;
  }
}
