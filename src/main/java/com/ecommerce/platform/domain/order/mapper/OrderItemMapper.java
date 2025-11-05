package com.ecommerce.platform.domain.order.mapper;

import com.ecommerce.platform.domain.order.entity.OrderItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrderItemMapper {

  // 주문 항목 저장
  void insert(OrderItem orderItem);

  // 주문 ID로 주문 항목 조회
  List<OrderItem> findByOrderId(@Param("orderId") Long orderId);

  // ID로 주문 항목 조회
  OrderItem findById(@Param("id") Long id);

  // 주문 항목 삭제
  void deleteById(@Param("id") Long id);

  // 주문 ID로 전체 주문 항목 삭제
  void deleteByOrderId(@Param("orderId") Long orderId);
}