package com.ecommerce.platform.domain.cancel.repository;

import com.ecommerce.platform.domain.cancel.entity.Cancel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CancelRepository extends JpaRepository<Cancel, Long> {
  List<Cancel> findByOrderId(Long orderId);
}
