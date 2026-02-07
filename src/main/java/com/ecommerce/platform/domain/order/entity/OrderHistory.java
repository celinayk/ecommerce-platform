package com.ecommerce.platform.domain.order.entity;

import com.ecommerce.platform.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "order_histories", indexes = {
    @Index(name = "idx_order_histories_order", columnList = "order_id")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class OrderHistory {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "order_id", nullable = false)
  private Order order;

  @Enumerated(EnumType.STRING)
  @Column(name = "before_status", length = 30)
  private OrderStatus beforeStatus;

  @Enumerated(EnumType.STRING)
  @Column(name = "after_status", nullable = false, length = 30)
  private OrderStatus afterStatus;

  // 상태를 변경한 주체
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "changed_by")
  private User changedBy;

  @Column(length = 200)
  private String reason;

  @CreationTimestamp
  @Column(name = "changed_at", nullable = false, updatable = false)
  private LocalDateTime changedAt;
}

