package com.ecommerce.platform.domain.order.repository;

import com.ecommerce.platform.domain.order.entity.Order;
import com.ecommerce.platform.domain.order.entity.OrderStatus;

import java.util.List;
import java.util.Optional;

/**
 * Order Repository 인터페이스
 * MyBatis -> JPA 전환 시 Service 코드 변경 없이 구현체만 교체 가능
 */
public interface OrderRepository {

  /**
   * 주문 저장
   */
  Order save(Order order);

  /**
   * ID로 주문 조회
   */
  Optional<Order> findById(Long id);

  /**
   * 전체 주문 조회 (페이징)
   */
  List<Order> findAll(int offset, int limit);

  /**
   * 전체 주문 개수
   */
  int count();

  /**
   * 주문 상태 업데이트
   */
  void updateStatus(Long id, OrderStatus status);

  /**
   * 주문 존재 여부 확인
   */
  boolean existsById(Long id);

  /**
   * 주문 삭제
   */
  void deleteById(Long id);
}