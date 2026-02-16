package com.ecommerce.platform.domain.order.repository;

import com.ecommerce.platform.domain.order.entity.Order;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OrderRepository extends JpaRepository<Order, Long> {

  @Query(value = "select o from Order o join fetch o.user where o.user.id = :userId",
      countQuery = "select count(o) from Order o where o.user.id = :userId")
  Page<Order> findByUserId(@Param("userId") Long userId, Pageable pageable);

}
