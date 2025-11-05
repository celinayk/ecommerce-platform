package com.ecommerce.platform.domain.order.mapper;

import com.ecommerce.platform.domain.order.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrderMapper {

  // 주문 저장
  void insert(Order order);

  // ID로 주문 조회
  Order findById(@Param("id") Long id);

  // 전체 주문 조회 (페이징)
  List<Order> findAll(@Param("offset") int offset, @Param("limit") int limit);

  // 전체 주문 개수
  int count();

  // 주문 상태 업데이트
  void updateStatus(@Param("id") Long id, @Param("status") String status);

  // 주문 삭제
  void deleteById(@Param("id") Long id);
}