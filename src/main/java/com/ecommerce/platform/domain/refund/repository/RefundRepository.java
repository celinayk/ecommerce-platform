package com.ecommerce.platform.domain.refund.repository;

import com.ecommerce.platform.domain.refund.entity.Refund;
import com.ecommerce.platform.domain.refund.entity.RefundStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RefundRepository {

  Refund save(Refund refund);

  Optional<Refund> findById(Long id);

  List<Refund> findAll();

  Page<Refund> findAll(Pageable pageable);

  void deleteById(Long id);

  boolean existsById(Long id);

  // 특정 주문의 환불 내역 조회
  List<Refund> findByOrderId(Long orderId);

  // 특정 사용자의 환불 내역 조회
  List<Refund> findByUserId(Long userId);

  // 환불 상태별 조회
  List<Refund> findByStatus(RefundStatus status);

  // 기간별 환불 내역 조회 (환불 요청일 기준)
  List<Refund> findByDateRange(LocalDateTime startDate, LocalDateTime endDate);

  // 대기 중인 환불 요청 조회 (처리 필요)
  List<Refund> findPendingRefunds();

  // 상태별 환불 건수 조회
  Integer countByStatus(RefundStatus status);

  // 사용자의 최근 환불 내역 조회
  List<Refund> findRecentRefundsByUserId(Long userId, int limit);

}
