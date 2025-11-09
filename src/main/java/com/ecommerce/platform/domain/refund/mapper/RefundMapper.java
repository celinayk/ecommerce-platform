package com.ecommerce.platform.domain.refund.mapper;

import com.ecommerce.platform.domain.refund.entity.Refund;
import com.ecommerce.platform.domain.refund.entity.RefundStatus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface RefundMapper {

  // 환불 저장
  void insert(Refund refund);

  // 환불 수정
  void update(Refund refund);

  // ID로 환불 조회
  Refund findById(@Param("id") Long id);

  // 전체 환불 조회
  List<Refund> findAll();

  // 환불 삭제
  void deleteById(@Param("id") Long id);

  // 특정 주문의 환불 내역 조회
  List<Refund> findByOrderId(@Param("orderId") Long orderId);

  // 특정 사용자의 환불 내역 조회
  List<Refund> findByUserId(@Param("userId") Long userId);

  // 환불 상태별 조회
  List<Refund> findByStatus(@Param("status") RefundStatus status);

  // 기간별 환불 내역 조회
  List<Refund> findByDateRange(
      @Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate
  );

  // 대기 중인 환불 요청 조회
  List<Refund> findPendingRefunds();

  // 상태별 환불 건수 조회
  Integer countByStatus(@Param("status") RefundStatus status);

  // 사용자의 최근 환불 내역 조회
  List<Refund> findRecentRefundsByUserId(
      @Param("userId") Long userId,
      @Param("limit") int limit
  );

  // 페이징 처리된 전체 환불 조회
  List<Refund> findAllWithPaging(
      @Param("offset") int offset,
      @Param("limit") int limit
  );

  // 전체 환불 건수 조회
  int countAll();
}
