package com.ecommerce.platform.domain.refund.service;

import com.ecommerce.platform.domain.order.entity.Order;
import com.ecommerce.platform.domain.order.entity.OrderStatus;
import com.ecommerce.platform.domain.order.exception.OrderException;
import com.ecommerce.platform.domain.order.repository.OrderRepository;
import com.ecommerce.platform.domain.refund.dto.RefundCreateRequest;
import com.ecommerce.platform.domain.refund.dto.RefundResponse;
import com.ecommerce.platform.domain.refund.entity.Refund;
import com.ecommerce.platform.domain.refund.entity.RefundStatus;
import com.ecommerce.platform.domain.refund.exception.RefundException;
import com.ecommerce.platform.domain.refund.repository.RefundRepository;
import com.ecommerce.platform.domain.user.entity.User;
import com.ecommerce.platform.domain.user.exception.UserException;
import com.ecommerce.platform.domain.user.repository.UserRepository;
import com.ecommerce.platform.global.common.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RefundService {

  private final RefundRepository refundRepository;
  private final OrderRepository orderRepository;
  private final UserRepository userRepository;

  @Transactional
  public RefundResponse createRefund(RefundCreateRequest refundCreateRequest) {
    // 1. 사용자 조회
    User user = userRepository.findById(refundCreateRequest.getUserId())
        .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

    // 2. 주문 조회
    Order order = orderRepository.findById(refundCreateRequest.getOrderId())
        .orElseThrow(() -> new OrderException(ErrorCode.ORDER_NOT_FOUND));

    // 3. 본인 주문 확인
    if (!order.getUser().getId().equals(user.getId())) {
      throw new RefundException(ErrorCode.UNAUTHORIZED_REFUND);
    }

    // 4. 주문이 완료된 상태인지 확인
    if (order.getStatus() != OrderStatus.COMPLETED) {
      throw new RefundException(ErrorCode.ORDER_NOT_COMPLETED);
    }

    // 5. 이미 환불 요청된 주문인지 확인
    List<Refund> existingRefunds = refundRepository.findByOrderId(order.getId());
    if (!existingRefunds.isEmpty()) {
      throw new RefundException(ErrorCode.REFUND_ALREADY_EXISTS);
    }

    // 6. 환불 생성
    Refund refund = Refund.builder()
        .user(user)
        .order(order)
        .reason(refundCreateRequest.getReason())
        .build();

    Refund savedRefund = refundRepository.save(refund);
    return RefundResponse.from(savedRefund);
  }

  // 환불 상세 조회
  public RefundResponse findRefundById(Long refundId) {
    Refund refund = refundRepository.findById(refundId)
        .orElseThrow(() -> new RefundException(ErrorCode.REFUND_NOT_FOUND));
    return RefundResponse.from(refund);
  }

  // 전체 환불 목록 조회 (List)
  public List<RefundResponse> findAllRefunds() {
    List<Refund> refunds = refundRepository.findAll();  // Repository 호출
    return refunds.stream()                              // DTO 변환 (비즈니스 로직)
        .map(RefundResponse::from)
        .collect(Collectors.toList());
  }

  // 전체 환불 목록 조회 (페이징)
  public Page<RefundResponse> findAllRefunds(Pageable pageable) {
    Page<Refund> refunds = refundRepository.findAll(pageable);
    return refunds.map(RefundResponse::from);
  }

  // 특정 사용자의 환불 내역 조회
  public List<RefundResponse> findByUserId(Long userId) {
    List<Refund> refunds = refundRepository.findByUserId(userId);
    return refunds.stream()
        .map(RefundResponse::from)
        .collect(Collectors.toList());
  }

  // 특정 주문의 환불 내역 조회
  public List<RefundResponse> findByOrderId(Long orderId) {
    List<Refund> refunds = refundRepository.findByOrderId(orderId);
    return refunds.stream()
        .map(RefundResponse::from)
        .collect(Collectors.toList());
  }

  // 환불 승인
  @Transactional
  public RefundResponse approveRefund(Long refundId) {
    // 1. 환불 조회
    Refund refund = refundRepository.findById(refundId)
        .orElseThrow(() -> new RefundException(ErrorCode.REFUND_NOT_FOUND));

    // 2. 환불 승인
    refund.approve();

    // TODO: 재고 복구 로직은 Stock 도메인 구현 후 추가 필요

    // 3. 환불 저장
    Refund savedRefund = refundRepository.save(refund);

    return RefundResponse.from(savedRefund);
  }

  // 환불 거절
  @Transactional
  public RefundResponse rejectRefund(Long refundId, String rejectReason) {
    // 환불 조회
    Refund refund = refundRepository.findById(refundId)
        .orElseThrow(() -> new RefundException(ErrorCode.REFUND_NOT_FOUND));

    // 환불 상태 확인
    refund.reject();

    // 환불 저장
    Refund savedRefund = refundRepository.save(refund);

    return RefundResponse.from(refund);
  }




}
