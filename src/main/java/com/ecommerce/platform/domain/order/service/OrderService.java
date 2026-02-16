package com.ecommerce.platform.domain.order.service;

import com.ecommerce.platform.domain.order.dto.CancelRequest;
import com.ecommerce.platform.domain.order.dto.OrderRequest;
import com.ecommerce.platform.domain.order.dto.OrderResponse;
import com.ecommerce.platform.domain.order.dto.ReturnRequest;
import com.ecommerce.platform.domain.order.entity.Order;
import com.ecommerce.platform.domain.order.entity.OrderItem;
import com.ecommerce.platform.domain.order.entity.OrderStatus;
import com.ecommerce.platform.domain.order.exception.OrderException;
import com.ecommerce.platform.domain.order.policy.CancelPolicy;
import com.ecommerce.platform.domain.order.policy.OrderTransitionPolicy;
import com.ecommerce.platform.domain.order.policy.ReturnPolicy;
import com.ecommerce.platform.domain.order.repository.OrderItemRepository;
import com.ecommerce.platform.domain.order.repository.OrderRepository;
import com.ecommerce.platform.domain.product.entity.Product;
import com.ecommerce.platform.domain.product.exception.ProductException;
import com.ecommerce.platform.domain.product.repository.ProductRepository;
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

import static com.ecommerce.platform.domain.order.entity.OrderStatus.CANCEL_REQUESTED;
import static com.ecommerce.platform.domain.order.entity.OrderStatus.RETURN_REQUESTED;
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

  // 주문 생성
  @Transactional
  public OrderResponse createOrder(OrderRequest request) {
    // 엔티티 조회
    User user = userRepository.findById(request.getUserId())
        .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

    Product product = productRepository.findById(request.getProductId())
        .orElseThrow(() -> new ProductException(ErrorCode.PRODUCT_NOT_FOUND));


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
}
