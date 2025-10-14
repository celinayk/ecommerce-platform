package com.ecommerce.platform.domain.order.service;

import com.ecommerce.platform.domain.order.entity.Order;
import com.ecommerce.platform.domain.order.entity.OrderItem;
import com.ecommerce.platform.domain.order.repository.OrderRepository;
import com.ecommerce.platform.domain.product.entity.Product;
import com.ecommerce.platform.domain.product.repository.ProductRepository;
import com.ecommerce.platform.domain.user.entity.User;
import com.ecommerce.platform.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

  private final OrderRepository orderRepository;
  private final ProductRepository productRepository;
  private final UserRepository userRepository;

  // 주문
  @Transactional
  public Long order(Long userId, Long productId, int count) {
    // 엔티티 조회
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
    Product product = productRepository.findById(productId)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다."));

    // 주문 상품 엔티티 생성
    OrderItem orderItem = OrderItem.createOrderItem(product, count);
    List<OrderItem> orderItems = new ArrayList<>();
    orderItems.add(orderItem);

    // 주문 엔티티 생성
    Order order = Order.createOrder(user, orderItems);

    // 주문 저장
    orderRepository.save(order);

    return order.getId();
  }

  // 주문 취소
  @Transactional
  public void cancelOrder(Long orderId) {
    Order order = orderRepository.findById(orderId)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문입니다."));
    order.cancel();
  }

  // 주문 조회
  public Order findOrder(Long orderId) {
    return orderRepository.findById(orderId)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문입니다."));
  }
}
