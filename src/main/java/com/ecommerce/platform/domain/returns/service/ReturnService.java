package com.ecommerce.platform.domain.returns.service;

import com.ecommerce.platform.domain.order.dto.OrderResponse;
import com.ecommerce.platform.domain.order.entity.Order;
import com.ecommerce.platform.domain.order.entity.OrderItem;
import com.ecommerce.platform.domain.order.exception.OrderException;
import com.ecommerce.platform.domain.order.policy.OrderTransitionPolicy;
import com.ecommerce.platform.domain.order.policy.ReturnPolicy;
import com.ecommerce.platform.domain.order.repository.OrderItemRepository;
import com.ecommerce.platform.domain.order.repository.OrderRepository;
import com.ecommerce.platform.domain.payment.service.PaymentService;
import com.ecommerce.platform.domain.returns.dto.ReturnRequest;
import com.ecommerce.platform.domain.returns.entity.Return;
import com.ecommerce.platform.domain.returns.entity.ReturnStatus;
import com.ecommerce.platform.domain.returns.exception.ReturnException;
import com.ecommerce.platform.domain.returns.repository.ReturnRepository;
import com.ecommerce.platform.domain.stock.service.StockService;
import com.ecommerce.platform.domain.user.entity.User;
import com.ecommerce.platform.domain.user.exception.UserException;
import com.ecommerce.platform.domain.user.repository.UserRepository;
import com.ecommerce.platform.global.common.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.ecommerce.platform.domain.order.entity.OrderStatus.*;
import static com.ecommerce.platform.global.common.response.ErrorCode.ORDER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class ReturnService {

  private final ReturnRepository returnRepository;
  private final OrderRepository orderRepository;
  private final OrderItemRepository orderItemRepository;
  private final UserRepository userRepository;
  private final ReturnPolicy returnPolicy;
  private final OrderTransitionPolicy transitionPolicy;
  private final StockService stockService;
  private final PaymentService paymentService;

  // 반품 신청 (DELIVERED → RETURN_REQUESTED)
  @Transactional
  public OrderResponse requestReturn(Long orderId, Long userId, ReturnRequest request) {
    Order order = orderRepository.findById(orderId)
        .orElseThrow(() -> new OrderException(ORDER_NOT_FOUND));

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

    OrderItem orderItem = orderItemRepository.findById(request.getOrderItemId())
        .orElseThrow(() -> new OrderException(ORDER_NOT_FOUND));

    returnPolicy.validate(order);
    transitionPolicy.validateTransition(order.getStatus(), RETURN_REQUESTED);

    order.changeStatus(RETURN_REQUESTED);

    Return returnEntity = Return.builder()
        .order(order)
        .orderItem(orderItem)
        .requestedBy(user)
        .reason(request.getReason())
        .status(ReturnStatus.REQUESTED)
        .requestedAt(LocalDateTime.now())
        .build();

    returnRepository.save(returnEntity);

    return OrderResponse.from(order);
  }

  // 반품 승인 (RETURN_REQUESTED → RETURN_IN_PROGRESS)
  @Transactional
  public OrderResponse approveReturn(Long returnId) {
    Return ret = returnRepository.findById(returnId)
        .orElseThrow(() -> new ReturnException(ErrorCode.RETURN_NOT_FOUND));

    ret.approve();

    Order order = ret.getOrder();
    transitionPolicy.validateTransition(order.getStatus(), RETURN_IN_PROGRESS);
    order.changeStatus(RETURN_IN_PROGRESS);

    return OrderResponse.from(order);
  }

  // 반품 완료 (RETURN_IN_PROGRESS → RETURN_COMPLETED + 재고 복구 + 환불 생성)
  @Transactional
  public OrderResponse completeReturn(Long returnId) {
    Return ret = returnRepository.findById(returnId)
        .orElseThrow(() -> new ReturnException(ErrorCode.RETURN_NOT_FOUND));

    ret.complete();

    Order order = ret.getOrder();
    transitionPolicy.validateTransition(order.getStatus(), RETURN_COMPLETED);

    stockService.restore(ret.getOrderItem().getProduct().getId(), ret.getOrderItem().getQuantity());

    order.changeStatus(RETURN_COMPLETED);
    paymentService.refundPayment(order);

    return OrderResponse.from(order);
  }
}