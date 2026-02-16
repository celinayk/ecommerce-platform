package com.ecommerce.platform.domain.order.entity;

import java.util.Set;

public enum OrderStatus {
  PENDING("대기중"),
  CONFIRMED("주문확인"),
  SHIPPING("배송중"),
  DELIVERED("배송완료"),
  COMPLETED("주문완료"),
  CANCEL_REQUESTED("취소요청"),
  CANCELED("주문취소"),
  FAILED("주문실패"),
  RETURN_REQUESTED("반품요청"),
  RETURN_IN_PROGRESS("반품진행중"),
  RETURN_COMPLETED("반품완료");

  private String description;

  OrderStatus(String description) {
    this.description = description;
  }

  public String getDescription() {
    return description;
  }


}
