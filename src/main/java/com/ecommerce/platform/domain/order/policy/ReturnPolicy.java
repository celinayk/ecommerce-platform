package com.ecommerce.platform.domain.order.policy;

import com.ecommerce.platform.domain.order.entity.Order;
import com.ecommerce.platform.domain.order.entity.OrderStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ReturnPolicy {
  private static final long RETURN_DEADLINE_DAYS = 7;

  public void validate(Order order) {
    // 1. 상태 확인: DELIVERED에서만 반품 가능
    if (order.getStatus() != OrderStatus.DELIVERED) {
      throw new IllegalStateException(
          order.getStatus().getDescription() + " 상태에서는 반품할 수 없습니다. "
              + "배송 완료 후에만 반품이 가능합니다."
      );
    }

    // 2. 배송완료 시각 존재 여부 확인
    if (order.getDeliveredAt() == null) {
      throw new IllegalStateException("배송완료 시각이 기록되지 않아 반품할 수 없습니다.");
    }

    // 3. 시간 확인: 배송완료 후 7일 이내
    LocalDateTime deadline = order.getDeliveredAt().plusDays(RETURN_DEADLINE_DAYS);
    if (LocalDateTime.now().isAfter(deadline)) {
      throw new IllegalStateException(
          "반품 가능 기간(배송완료 후 " + RETURN_DEADLINE_DAYS + "일)이 지났습니다."
      );
    }
  }

}
