package com.ecommerce.platform.domain.refund.repository;

import com.ecommerce.platform.domain.refund.entity.Refund;
import com.ecommerce.platform.domain.refund.entity.RefundStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface RefundRepository extends JpaRepository<Refund, Long> {

  List<Refund> findByOrderId(Long orderId);

  List<Refund> findByUserId(Long userId);

  List<Refund> findByStatus(RefundStatus status);

  Integer countByStatus(RefundStatus status);

  List<Refund> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
}
