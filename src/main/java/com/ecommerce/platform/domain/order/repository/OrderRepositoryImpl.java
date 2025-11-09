package com.ecommerce.platform.domain.order.repository;

import com.ecommerce.platform.domain.order.entity.Order;
import com.ecommerce.platform.domain.order.entity.OrderStatus;
import com.ecommerce.platform.domain.order.mapper.OrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * OrderRepository의 MyBatis 구현체
 * 나중에 JPA로 전환 시 이 클래스만 삭제하고 JpaRepository로 교체
 */
@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {

  private final OrderMapper orderMapper;

  @Override
  public Order save(Order order) {
    if (order.getId() == null) {
      // 새로운 주문 저장
      orderMapper.insert(order);
    } else {
      // 기존 주문은 상태 업데이트만 가능
      orderMapper.updateStatus(order.getId(), order.getStatus().name());
    }
    return order;
  }

  @Override
  public Optional<Order> findById(Long id) {
    return Optional.ofNullable(orderMapper.findById(id));
  }

  @Override
  public List<Order> findAll(int offset, int limit) {
    return orderMapper.findAll(offset, limit);
  }

  @Override
  public int count() {
    return orderMapper.count();
  }

  @Override
  public void updateStatus(Long id, OrderStatus status) {
    orderMapper.updateStatus(id, status.name());
  }

  @Override
  public boolean existsById(Long id) {
    return orderMapper.findById(id) != null;
  }

  @Override
  public void deleteById(Long id) {
    orderMapper.deleteById(id);
  }
}