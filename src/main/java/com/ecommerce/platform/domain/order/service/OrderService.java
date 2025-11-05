package com.ecommerce.platform.domain.order.service;

import com.ecommerce.platform.domain.order.dto.OrderRequest;
import com.ecommerce.platform.domain.order.dto.OrderResponse;
import com.ecommerce.platform.domain.order.entity.Order;
import com.ecommerce.platform.domain.order.entity.OrderItem;
import com.ecommerce.platform.domain.order.entity.OrderStatus;
import com.ecommerce.platform.domain.order.mapper.OrderItemMapper;
import com.ecommerce.platform.domain.order.mapper.OrderMapper;
import com.ecommerce.platform.domain.product.entity.Product;
import com.ecommerce.platform.domain.product.mapper.ProductMapper;
import com.ecommerce.platform.domain.user.entity.User;
import com.ecommerce.platform.domain.user.mapper.UserMapper;
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

  private final OrderMapper orderMapper;
  private final OrderItemMapper orderItemMapper;
  private final ProductMapper productMapper;
  private final UserMapper userMapper;

  // 주문 생성
  @Transactional
  public OrderResponse createOrder(OrderRequest request) {
    // 엔티티 조회
    User user = userMapper.findById(request.getUserId());
    if (user == null) {
      throw new CustomException(ErrorCode.USER_NOT_FOUND);
    }

    Product product = productMapper.findById(request.getProductId());
    if (product == null) {
      throw new CustomException(ErrorCode.PRODUCT_NOT_FOUND);
    }

    // 재고 확인
    if (product.getStockQuantity() < request.getCount()) {
      throw new CustomException(ErrorCode.OUT_OF_STOCK);
    }

    // 재고 감소
    productMapper.decreaseStock(product.getId(), request.getCount());

    // 주문 생성
    Order order = new Order();
    order.setUser(user);
    order.setStatus(OrderStatus.ORDER);

    // OrderItem 생성
    OrderItem orderItem = new OrderItem();
    orderItem.setProduct(product);
    orderItem.setPrice(product.getPrice());
    orderItem.setQuantity(request.getCount());
    orderItem.setSubtotal(product.getPrice() * request.getCount());

    // 총액 계산
    order.setTotalAmount(orderItem.getSubtotal());

    // 주문 저장
    orderMapper.insert(order);

    // OrderItem에 orderId 설정 후 저장
    orderItem.setOrder(order);
    orderItemMapper.insert(orderItem);

    // 저장된 주문 조회 (orderItems 포함)
    Order savedOrder = orderMapper.findById(order.getId());
    return OrderResponse.from(savedOrder);
  }

  // 주문 목록 조회 (페이징)
  public Page<OrderResponse> getAllOrders(Pageable pageable) {
    int offset = (int) pageable.getOffset();
    int limit = pageable.getPageSize();

    List<Order> orders = orderMapper.findAll(offset, limit);
    int total = orderMapper.count();

    List<OrderResponse> content = orders.stream()
        .map(OrderResponse::from)
        .collect(Collectors.toList());

    return new PageImpl<>(content, pageable, total);
  }

  // 주문 상세 조회
  public OrderResponse getOrderById(Long orderId) {
    Order order = orderMapper.findById(orderId);
    if (order == null) {
      throw new CustomException(ErrorCode.ORDER_NOT_FOUND);
    }
    return OrderResponse.from(order);
  }

  // 주문 취소
  @Transactional
  public void cancelOrder(Long orderId) {
    Order order = orderMapper.findById(orderId);
    if (order == null) {
      throw new CustomException(ErrorCode.ORDER_NOT_FOUND);
    }

    if (order.getStatus() == OrderStatus.CANCEL) {
      throw new IllegalStateException("이미 취소된 주문입니다.");
    }

    // 주문 상태 변경
    orderMapper.updateStatus(orderId, OrderStatus.CANCEL.name());

    // 재고 복구
    List<OrderItem> orderItems = orderItemMapper.findByOrderId(orderId);
    for (OrderItem orderItem : orderItems) {
      productMapper.increaseStock(orderItem.getProduct().getId(), orderItem.getQuantity());
    }
  }
}
