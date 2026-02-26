package com.ecommerce.platform.domain.cancel.service;

import com.ecommerce.platform.domain.cancel.entity.Cancel;
import com.ecommerce.platform.domain.cancel.entity.CancelStatus;
import com.ecommerce.platform.domain.cancel.exception.CancelException;
import com.ecommerce.platform.domain.cancel.repository.CancelRepository;
import com.ecommerce.platform.domain.cancel.dto.CancelRequest;
import com.ecommerce.platform.domain.order.dto.OrderResponse;
import com.ecommerce.platform.domain.order.entity.Order;
import com.ecommerce.platform.domain.order.entity.OrderItem;
import com.ecommerce.platform.domain.order.exception.OrderException;
import com.ecommerce.platform.domain.order.policy.CancelPolicy;
import com.ecommerce.platform.domain.order.policy.OrderTransitionPolicy;
import com.ecommerce.platform.domain.order.repository.OrderRepository;
import com.ecommerce.platform.domain.payment.service.PaymentService;
import com.ecommerce.platform.domain.stock.service.StockService;
import com.ecommerce.platform.domain.user.entity.User;
import com.ecommerce.platform.domain.user.exception.UserException;
import com.ecommerce.platform.domain.user.repository.UserRepository;
import com.ecommerce.platform.global.common.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.ecommerce.platform.domain.order.entity.OrderStatus.CANCELED;
import static com.ecommerce.platform.domain.order.entity.OrderStatus.CANCEL_REQUESTED;
import static com.ecommerce.platform.global.common.response.ErrorCode.ORDER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class CancelService {

  private final CancelRepository cancelRepository;
  private final OrderRepository orderRepository;
  private final StockService stockService;
  private final CancelPolicy cancelPolicy;
  private final OrderTransitionPolicy transitionPolicy;
  private final PaymentService paymentService;
  private final UserRepository userRepository;


  @Transactional
  public OrderResponse requestCancel(Long orderId,  Long userId, CancelRequest request) {
    Order order = orderRepository.findById(orderId)
        .orElseThrow(() -> new OrderException(ORDER_NOT_FOUND));

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

    cancelPolicy.validate(order);
    transitionPolicy.validateTransition(order.getStatus(), CANCEL_REQUESTED);

    order.changeStatus(CANCEL_REQUESTED);

    Cancel cancel = Cancel.builder()
        .order(order)
        .requestedBy(user)
        .reason(request.getReason())
        .status(CancelStatus.REQUESTED)
        .requestedAt(LocalDateTime.now())
        .build();
    cancelRepository.save(cancel);
    return OrderResponse.from(order);
  }

  // 취소 승인 (CANCEL_REQUESTED → CANCELED + 재고 복구)
  @Transactional
  public OrderResponse approveCancel(Long cancelId) {
    Cancel cancel = cancelRepository.findById(cancelId)
        .orElseThrow(() -> new CancelException(ErrorCode.CANCEL_NOT_FOUND));

    cancel.approve();  // 상태 변경 + approvedAt 기록

    Order order = cancel.getOrder();
    transitionPolicy.validateTransition(order.getStatus(), CANCELED);
    for (OrderItem orderItem : order.getOrderItems()) {
      stockService.restore(orderItem.getProduct().getId(), orderItem.getQuantity());
    }
    order.changeStatus(CANCELED);
    paymentService.cancelPayment(order);
    return OrderResponse.from(order);
  }
}
