package com.ecommerce.platform.domain.order.service;

import com.ecommerce.platform.domain.order.dto.OrderRequest;
import com.ecommerce.platform.domain.order.dto.OrderResponse;
import com.ecommerce.platform.domain.order.entity.Order;
import com.ecommerce.platform.domain.order.entity.OrderItem;
import com.ecommerce.platform.domain.order.entity.OrderStatus;
import com.ecommerce.platform.domain.order.exception.OrderException;
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

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OrderService {

  private final OrderRepository orderRepository;
  private final OrderItemRepository orderItemRepository;
  private final ProductRepository productRepository;
  private final UserRepository userRepository;

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

    // 주문 생성
    Order order = Order.builder()
        .user(user)
        .status(OrderStatus.PENDING)
        .totalPrice(orderItem.getSubtotal())
        .orderedAt(LocalDateTime.now())
        .build();

    // 주문 저장
    orderRepository.save(order);

    // OrderItem에 orderId 설정 후 저장
    orderItem.setOrder(order);
    orderItemRepository.save(orderItem);

    // 저장된 주문 조회 (orderItems 포함)
    Order savedOrder = orderRepository.findById(order.getId())
        .orElseThrow(() -> new OrderException(ErrorCode.ORDER_NOT_FOUND));
    return OrderResponse.from(savedOrder);
  }

  // 주문 목록 조회 (페이징)
  public Page<OrderResponse> getAllOrders(Pageable pageable) {
    return orderRepository.findAll(pageable)
        .map(OrderResponse::from);
  }

  // 주문 상세 조회
  public OrderResponse getOrderById(Long orderId) {
    Order order = orderRepository.findById(orderId)
        .orElseThrow(() -> new OrderException(ErrorCode.ORDER_NOT_FOUND));
    return OrderResponse.from(order);
  }

  // 주문 취소
  @Transactional
  public void cancelOrder(Long orderId) {
    Order order = orderRepository.findById(orderId)
        .orElseThrow(() -> new OrderException(ErrorCode.ORDER_NOT_FOUND));

    order.cancel();

    // TODO: 재고 복구 로직은 Stock 서비스로 분리 필요
  }
}
