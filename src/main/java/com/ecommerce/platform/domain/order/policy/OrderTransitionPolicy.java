package com.ecommerce.platform.domain.order.policy;

import com.ecommerce.platform.domain.order.entity.OrderStatus;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

import static com.ecommerce.platform.domain.order.entity.OrderStatus.*;

@Component
public class OrderTransitionPolicy {

  private static final Map<OrderStatus, Set<OrderStatus>> ALLOWED_TRANSITIONS = Map.ofEntries(
      // PENDING 상태에서는 → CONFIRMED 또는 FAILED로만 갈 수 있음
      Map.entry(PENDING,              Set.of(CONFIRMED, FAILED)),
      Map.entry(CONFIRMED,            Set.of(SHIPPING, CANCEL_REQUESTED)),
      Map.entry(CANCEL_REQUESTED,     Set.of(CANCELED, CONFIRMED)),

      // 배송중엔 취소 불가
      Map.entry(SHIPPING,             Set.of(DELIVERED)),
      Map.entry(DELIVERED,            Set.of(COMPLETED, RETURN_REQUESTED)),
      Map.entry(RETURN_REQUESTED,     Set.of(RETURN_IN_PROGRESS, DELIVERED)),
      Map.entry(RETURN_IN_PROGRESS,   Set.of(RETURN_COMPLETED)),

      // 최종 상태들
      Map.entry(COMPLETED,            Set.of()),
      Map.entry(CANCELED,             Set.of()),
      Map.entry(RETURN_COMPLETED,     Set.of()),
      Map.entry(FAILED,               Set.of())
  );

  public void validateTransition(OrderStatus from, OrderStatus to) {
    Set<OrderStatus> allowed = ALLOWED_TRANSITIONS.getOrDefault(from, Set.of());
    if(!allowed.contains(to)) {
      throw new IllegalStateException(
          from.getDescription() + " 상태에서 "
          + to.getDescription() + "(으)로 변경할 수 없습니다."
      );
    }
  }
}
