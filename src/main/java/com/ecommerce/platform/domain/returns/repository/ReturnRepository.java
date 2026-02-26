package com.ecommerce.platform.domain.returns.repository;

import com.ecommerce.platform.domain.returns.entity.Return;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReturnRepository extends JpaRepository<Return, Long> {
  List<Return> findByOrderId(Long orderId);
  List<Return> findByOrderItemId(Long orderItemId);  // 아이템 단위 반품이라 추가
}
