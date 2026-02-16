package com.ecommerce.platform.domain.order.policy;

import com.ecommerce.platform.domain.order.entity.Order;
import com.ecommerce.platform.domain.order.entity.OrderStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class CancelPolicy {
  private static final long CANCEL_DEADLINE_HOURS = 24;

  public void validate(Order order) {
    // 1. 상태 확인: CONFIRMED에서만 취소 가능
    if(order.getStatus() != OrderStatus.CONFIRMED) {
      throw new IllegalStateException(
          order.getStatus().getDescription() + " 상태에서는 취소할 수 없습니다. "
              + "배송 후에는 반품을 이용해주세요."
      );
    }

    // 2. 시간 확인: 주문 후 24시간 이내에만 삭제 가능
    LocalDateTime deadline = order.getOrderedAt().plusHours(CANCEL_DEADLINE_HOURS);
    if (LocalDateTime.now().isAfter(deadline)) {
      throw new IllegalStateException(
          "취소 가능 기간(주문 후 " + CANCEL_DEADLINE_HOURS + "시간)이 지났습니다."
      );
    }
  }

}
