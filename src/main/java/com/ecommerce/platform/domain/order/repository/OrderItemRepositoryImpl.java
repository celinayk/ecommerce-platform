package com.ecommerce.platform.domain.order.repository;

import com.ecommerce.platform.domain.order.entity.OrderItem;
import com.ecommerce.platform.domain.order.mapper.OrderItemMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * OrderItemRepository의 MyBatis 구현체
 * 나중에 JPA로 전환 시 이 클래스만 삭제하고 JpaRepository로 교체
 */
@Repository
@RequiredArgsConstructor
public class OrderItemRepositoryImpl implements OrderItemRepository {

  private final OrderItemMapper orderItemMapper;

  @Override
  public OrderItem save(OrderItem orderItem) {
    if (orderItem.getId() == null) {
      // 새로운 주문 항목 저장
      orderItemMapper.insert(orderItem);
    }
    return orderItem;
  }

  @Override
  public Optional<OrderItem> findById(Long id) {
    return Optional.ofNullable(orderItemMapper.findById(id));
  }

  @Override
  public List<OrderItem> findByOrderId(Long orderId) {
    return orderItemMapper.findByOrderId(orderId);
  }

  @Override
  public boolean existsById(Long id) {
    return orderItemMapper.findById(id) != null;
  }

  @Override
  public void deleteById(Long id) {
    orderItemMapper.deleteById(id);
  }

  @Override
  public void deleteByOrderId(Long orderId) {
    orderItemMapper.deleteByOrderId(orderId);
  }
}