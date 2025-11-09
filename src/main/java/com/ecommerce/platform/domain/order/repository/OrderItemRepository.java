package com.ecommerce.platform.domain.order.repository;

import com.ecommerce.platform.domain.order.entity.OrderItem;

import java.util.List;
import java.util.Optional;

/**
 * OrderItem Repository 인터페이스
 * MyBatis -> JPA 전환 시 Service 코드 변경 없이 구현체만 교체 가능
 */
public interface OrderItemRepository {

  /**
   * 주문 항목 저장
   */
  OrderItem save(OrderItem orderItem);

  /**
   * ID로 주문 항목 조회
   */
  Optional<OrderItem> findById(Long id);

  /**
   * 주문 ID로 주문 항목 조회
   */
  List<OrderItem> findByOrderId(Long orderId);

  /**
   * 주문 항목 존재 여부 확인
   */
  boolean existsById(Long id);

  /**
   * 주문 항목 삭제
   */
  void deleteById(Long id);

  /**
   * 주문 ID로 전체 주문 항목 삭제
   */
  void deleteByOrderId(Long orderId);
}