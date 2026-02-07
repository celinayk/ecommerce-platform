package com.ecommerce.platform.domain.order.repository;

import com.ecommerce.platform.domain.order.entity.OrderHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderHistoryRepository extends JpaRepository<OrderHistory, Long> {

  List<OrderHistory> findByOrderIdOrderByChangedAtDesc(Long orderId);
}
