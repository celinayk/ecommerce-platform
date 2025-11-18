package com.ecommerce.platform.domain.order.service;

import com.ecommerce.platform.domain.order.dto.OrderRequest;
import com.ecommerce.platform.domain.order.dto.OrderResponse;
import com.ecommerce.platform.domain.order.entity.Order;
import com.ecommerce.platform.domain.order.entity.OrderItem;
import com.ecommerce.platform.domain.order.entity.OrderStatus;
import com.ecommerce.platform.domain.order.repository.OrderItemRepository;
import com.ecommerce.platform.domain.order.repository.OrderRepository;
import com.ecommerce.platform.domain.product.entity.Product;
import com.ecommerce.platform.domain.product.repository.ProductRepository;
import com.ecommerce.platform.domain.user.entity.User;
import com.ecommerce.platform.domain.user.repository.UserRepository;
import com.ecommerce.platform.global.common.exception.CustomException;
import com.ecommerce.platform.global.common.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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
        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

    Product product = productRepository.findById(request.getProductId())
        .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

    // 재고 감소 (도메인 로직)
    product.decreaseStock(request.getCount());
    productRepository.save(product);

    // OrderItem 생성
    OrderItem orderItem = OrderItem.builder()
        .product(product)
        .price(product.getPrice())
        .quantity(request.getCount())
        .build();

    // 주문 생성
    Order order = Order.builder()
        .user(user)
        .status(OrderStatus.PENDING)
        .totalAmount(orderItem.getSubtotal())
        .build();

    // 주문 저장
    orderRepository.save(order);

    // OrderItem에 orderId 설정 후 저장
    orderItem.setOrder(order);
    orderItemRepository.save(orderItem);

    // 저장된 주문 조회 (orderItems 포함)
    Order savedOrder = orderRepository.findById(order.getId())
        .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));
    return OrderResponse.from(savedOrder);
  }

  // 주문 목록 조회 (페이징)
  public Page<OrderResponse> getAllOrders(Pageable pageable) {
    int offset = (int) pageable.getOffset();
    int limit = pageable.getPageSize();

    List<Order> orders = orderRepository.findAll(offset, limit);
    int total = orderRepository.count();

    List<OrderResponse> content = orders.stream()
        .map(OrderResponse::from)
        .collect(Collectors.toList());

    return new PageImpl<>(content, pageable, total);
  }

  // 주문 상세 조회
  public OrderResponse getOrderById(Long orderId) {
    Order order = orderRepository.findById(orderId)
        .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));
    return OrderResponse.from(order);
  }

  // 주문 취소
  @Transactional
  public void cancelOrder(Long orderId) {
    Order order = orderRepository.findById(orderId)
        .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));

    if (order.getStatus() == OrderStatus.CANCELED) {
      throw new IllegalStateException("이미 취소된 주문입니다.");
    }

    // 주문 상태 변경
    orderRepository.updateStatus(orderId, OrderStatus.CANCELED);

    // 재고 복구 (도메인 로직)
    List<OrderItem> orderItems = orderItemRepository.findByOrderId(orderId);
    for (OrderItem orderItem : orderItems) {
      Product product = productRepository.findById(orderItem.getProduct().getId())
          .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));
      product.increaseStock(orderItem.getQuantity());
      productRepository.save(product);
    }
  }
}
