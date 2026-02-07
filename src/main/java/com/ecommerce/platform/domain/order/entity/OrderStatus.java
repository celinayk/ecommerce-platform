package com.ecommerce.platform.domain.order.entity;

public enum OrderStatus {
  PENDING("대기중"),
  CONFIRMED("주문확인"),
  SHIPPING("배송중"),
  DELIVERED("배송완료"),
  COMPLETED("주문완료"),
  CANCEL_REQUESTED("취소요청"),
  CANCELED("주문취소");

  private String description;

  OrderStatus(String description) {
    this.description = description;
  }

  public String getDescription() {
    return description;
  }
}
