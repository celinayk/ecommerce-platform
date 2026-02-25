package com.ecommerce.platform.domain.payment.service;

import com.ecommerce.platform.domain.order.entity.Order;
import com.ecommerce.platform.domain.order.repository.OrderRepository;
import com.ecommerce.platform.domain.payment.dto.PaymentResponse;
import com.ecommerce.platform.domain.payment.entity.Payment;
import com.ecommerce.platform.domain.payment.entity.PaymentMethod;
import com.ecommerce.platform.domain.payment.entity.PaymentStatus;
import com.ecommerce.platform.domain.payment.exception.PaymentException;
import com.ecommerce.platform.domain.payment.repository.PaymentRepository;
import com.ecommerce.platform.domain.refund.entity.Refund;
import com.ecommerce.platform.domain.refund.entity.RefundStatus;
import com.ecommerce.platform.domain.refund.repository.RefundRepository;
import com.ecommerce.platform.global.common.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.ecommerce.platform.domain.order.entity.OrderStatus.CONFIRMED;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentService {

  private final PaymentRepository paymentRepository;
  private final RefundRepository refundRepository;
  private final OrderRepository orderRepository;

  // 1. confirmOrder(주문확인) 시 호출 — Payment 생성 + 즉시 COMPLETED
  @Transactional
  public PaymentResponse confirmPayment(Long orderId, PaymentMethod method) {
    // 1. 주문 조회
    Order order = orderRepository.findById(orderId)
        .orElseThrow(() -> new PaymentException(ErrorCode.ORDER_NOT_FOUND));

    // 2. 이미 결제된 주문인지 확인
    if (paymentRepository.existsByOrderId(order.getId())) {
      throw new PaymentException(ErrorCode.PAYMENT_ALREADY_EXISTS);
    }

    // 3. Payment 생성
    Payment payment = Payment.builder()
        .order(order)
        .amount(order.getTotalPrice())
        .method(method)
        .status(PaymentStatus.PENDING)
        .build();

    // 4. PENDING → COMPLETED + paidAt 기록
    payment.complete();

    // 5. Order → CONFIRMED
    order.changeStatus(CONFIRMED);

    // 5. 반환
    return PaymentResponse.from(paymentRepository.save(payment));
  }


  // 2. approveCancel 시 호출 — Payment CANCELED + Refund 자동 생성
  @Transactional
  public void cancelPayment(Order order) {
    // 1. 이 주문의 Payment 조회 (없으면 미결제 주문이므로 그냥 리턴)
    Payment payment = paymentRepository.findByOrderId(order.getId())
        .orElse(null);
    if (payment == null) return;

    // 2. 상태 변경
    payment.cancel();

    // 3. Refund 자동 생성
    Refund refund = Refund.builder()
        .user(order.getUser())
        .order(order)
        .payment(payment)
        .reason("주문 취소에 따른 자동 환불")
        .status(RefundStatus.PENDING)
        .build();

    refundRepository.save(refund);

  }

  // 3. completeReturn 시 호출 — Payment REFUNDED + Refund 자동 생성
  @Transactional
  public void refundPayment(Order order) {
    // 1. 이 주문의 Payment 조회 (없으면 예외)
    Payment payment = paymentRepository.findByOrderId(order.getId())
        .orElseThrow(() -> new PaymentException(ErrorCode.PAYMENT_NOT_FOUND));

    Refund refund = Refund.builder()
        .user(order.getUser())
        .order(order)
        .payment(payment)
        .reason("반품 완료에 따른 자동 환불")
        .status(RefundStatus.PENDING)
        .build();

    refundRepository.save(refund);
  }

  public PaymentResponse getPaymentById(Long id) {
    Payment payment = paymentRepository.findById(id)
        .orElseThrow(() -> new PaymentException(ErrorCode.PAYMENT_NOT_FOUND));
    return PaymentResponse.from(payment);
  }

  public PaymentResponse getPaymentByOrderId(Long orderId) {
    Payment payment = paymentRepository.findByOrderId(orderId)
        .orElseThrow(() -> new PaymentException(ErrorCode.PAYMENT_NOT_FOUND));
    return PaymentResponse.from(payment);
  }
}
