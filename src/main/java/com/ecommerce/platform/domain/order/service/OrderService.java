package com.ecommerce.platform.domain.order.service;

import com.ecommerce.platform.domain.order.dto.CancelRequest;
import com.ecommerce.platform.domain.order.dto.OrderRequest;
import com.ecommerce.platform.domain.order.dto.OrderResponse;
import com.ecommerce.platform.domain.order.dto.ReturnRequest;
import com.ecommerce.platform.domain.order.entity.Order;
import com.ecommerce.platform.domain.order.entity.OrderHistory;
import com.ecommerce.platform.domain.order.entity.OrderItem;
import com.ecommerce.platform.domain.order.entity.OrderStatus;
import com.ecommerce.platform.domain.order.exception.OrderException;
import com.ecommerce.platform.domain.order.policy.CancelPolicy;
import com.ecommerce.platform.domain.order.policy.OrderTransitionPolicy;
import com.ecommerce.platform.domain.order.policy.ReturnPolicy;
import com.ecommerce.platform.domain.order.repository.OrderHistoryRepository;
import com.ecommerce.platform.domain.order.repository.OrderItemRepository;
import com.ecommerce.platform.domain.order.repository.OrderRepository;
import com.ecommerce.platform.domain.payment.service.PaymentService;
import com.ecommerce.platform.domain.product.entity.Product;
import com.ecommerce.platform.domain.product.exception.ProductException;
import com.ecommerce.platform.domain.product.repository.ProductRepository;
import com.ecommerce.platform.domain.stock.service.StockService;
import com.ecommerce.platform.domain.user.entity.User;
import com.ecommerce.platform.domain.user.exception.UserException;
import com.ecommerce.platform.domain.user.repository.UserRepository;
import com.ecommerce.platform.global.common.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.ecommerce.platform.domain.order.entity.OrderStatus.*;
import static com.ecommerce.platform.global.common.response.ErrorCode.ORDER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class OrderService {

  private final OrderRepository orderRepository;
  private final OrderItemRepository orderItemRepository;
  private final ProductRepository productRepository;
  private final UserRepository userRepository;
  private final OrderTransitionPolicy transitionPolicy;
  private final CancelPolicy cancelPolicy;
  private final ReturnPolicy returnPolicy;
  private final StockService stockService;
  private final OrderHistoryRepository orderHistoryRepository;
  private final PaymentService paymentService;

  private void recordHistory(Order order, OrderStatus before, OrderStatus after, String reason) {
    OrderHistory history = OrderHistory.builder()
        .order(order)
        .beforeStatus(before)
        .afterStatus(after)
        .reason(reason)
        .build();
    orderHistoryRepository.save(history);
  }


  // 주문 생성
  @Transactional
  public OrderResponse createOrder(OrderRequest request) {
    // 엔티티 조회
    User user = userRepository.findById(request.getUserId())
        .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

    Product product = productRepository.findById(request.getProductId())
        .orElseThrow(() -> new ProductException(ErrorCode.PRODUCT_NOT_FOUND));

    // 재고차감
    stockService.deduct(product.getId(), request.getQuantity());


    // OrderItem 생성
    OrderItem orderItem = OrderItem.builder()
        .product(product)
        .price(request.getPrice())
        .quantity(request.getQuantity())
        .build();

    // 주문 생성 + OrderItem 연결 (cascade로 함께 저장됨)
    Order order = Order.builder()
        .user(user)
        .status(OrderStatus.PENDING)
        .totalPrice(BigDecimal.ZERO)
        .orderedAt(LocalDateTime.now())
        .build();

    order.addOrderItem(orderItem);
    orderRepository.save(order);

    return OrderResponse.from(order);
  }

  // 주문 목록 조회 (페이징)
  @Transactional(readOnly = true)
  public Page<OrderResponse> getAllOrders(Pageable pageable) {
    return orderRepository.findAll(pageable)
        .map(OrderResponse::from);
  }

  // 특정 사용자 주문 목록 조회
  @Transactional(readOnly = true)
  public Page<OrderResponse> getOrdersByUserId(Long userId, Pageable pageable) {
    return orderRepository.findByUserId(userId, pageable)
        .map(OrderResponse::from);
  }

  // 주문 상세 조회
  @Transactional(readOnly = true)
  public OrderResponse getOrderById(Long orderId) {
    Order order = orderRepository.findById(orderId)
        .orElseThrow(() -> new OrderException(ORDER_NOT_FOUND));
    return OrderResponse.from(order);
  }

  @Transactional
  public OrderResponse requestCancel(Long orderId, CancelRequest request) {
    Order order = orderRepository.findById(orderId)
        .orElseThrow(() -> new OrderException(ORDER_NOT_FOUND));

    cancelPolicy.validate(order);
    transitionPolicy.validateTransition(order.getStatus(), CANCEL_REQUESTED);

    order.changeStatus(CANCEL_REQUESTED);
    return OrderResponse.from(order);
  }

  @Transactional
  public OrderResponse requestReturn(Long orderId, ReturnRequest request) {
    Order order = orderRepository.findById(orderId)
        .orElseThrow(() -> new OrderException(ORDER_NOT_FOUND));

    returnPolicy.validate(order);
    transitionPolicy.validateTransition(order.getStatus(), RETURN_REQUESTED);

    order.changeStatus(RETURN_REQUESTED);
    return OrderResponse.from(order);
  }


  // 취소 승인 (CANCEL_REQUESTED → CANCELED + 재고 복구)
  @Transactional
  public OrderResponse approveCancel(Long orderId) {
    Order order = orderRepository.findById(orderId)
        .orElseThrow(() -> new OrderException(ORDER_NOT_FOUND));

    transitionPolicy.validateTransition(order.getStatus(), CANCELED);

    // 재고 복구: 주문 내 모든 아이템의 수량만큼 복구
    for (OrderItem orderItem : order.getOrderItems()) {
      stockService.restore(orderItem.getProduct().getId(), orderItem.getQuantity());
    }

    order.changeStatus(CANCELED);
    paymentService.cancelPayment(order);
    return OrderResponse.from(order);
  }

  // 반품 승인 (RETURN_REQUESTED → RETURN_IN_PROGRESS)
  @Transactional
  public OrderResponse approveReturn(Long orderId) {
    Order order = orderRepository.findById(orderId)
        .orElseThrow(() -> new OrderException(ORDER_NOT_FOUND));

    transitionPolicy.validateTransition(order.getStatus(), RETURN_IN_PROGRESS);
    order.changeStatus(RETURN_IN_PROGRESS);
    return OrderResponse.from(order);
  }

  // 반품 완료 (RETURN_IN_PROGRESS → RETURN_COMPLETED + 재고 복구)
  @Transactional
  public OrderResponse completeReturn(Long orderId) {
    Order order = orderRepository.findById(orderId)
        .orElseThrow(() -> new OrderException(ORDER_NOT_FOUND));

    transitionPolicy.validateTransition(order.getStatus(), RETURN_COMPLETED);

    // 재고 복구: 반품 물건이 돌아왔으니 재고 복구
    for (OrderItem item : order.getOrderItems()) {
      stockService.restore(item.getProduct().getId(), item.getQuantity());
    }

    order.changeStatus(RETURN_COMPLETED);
    paymentService.refundPayment(order);
    return OrderResponse.from(order);
  }


}
