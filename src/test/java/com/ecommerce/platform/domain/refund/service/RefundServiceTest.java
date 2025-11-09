package com.ecommerce.platform.domain.refund.service;

import com.ecommerce.platform.domain.order.entity.Order;
import com.ecommerce.platform.domain.order.entity.OrderItem;
import com.ecommerce.platform.domain.order.entity.OrderStatus;
import com.ecommerce.platform.domain.order.repository.OrderItemRepository;
import com.ecommerce.platform.domain.order.repository.OrderRepository;
import com.ecommerce.platform.domain.product.entity.Product;
import com.ecommerce.platform.domain.product.repository.ProductRepository;
import com.ecommerce.platform.domain.refund.dto.RefundCreateRequest;
import com.ecommerce.platform.domain.refund.dto.RefundResponse;
import com.ecommerce.platform.domain.refund.entity.RefundStatus;
import com.ecommerce.platform.domain.refund.repository.RefundRepository;
import com.ecommerce.platform.domain.user.entity.User;
import com.ecommerce.platform.domain.user.repository.UserRepository;
import com.ecommerce.platform.global.common.exception.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class RefundServiceTest {

  @Autowired
  RefundService refundService;

  @Autowired
  RefundRepository refundRepository;

  @Autowired
  UserRepository userRepository;

  @Autowired
  OrderRepository orderRepository;

  @Autowired
  OrderItemRepository orderItemRepository;

  @Autowired
  ProductRepository productRepository;

  private User testUser;
  private Order testOrder;
  private Product testProduct;

  @BeforeEach
  void setUp() {
    // 테스트용 사용자 생성
    testUser = User.builder()
        .email("test@example.com")
        .password("password123")
        .name("테스트유저")
        .build();
    testUser = userRepository.save(testUser);

    // 테스트용 상품 생성
    testProduct = Product.builder()
        .name("테스트상품")
        .price(10000L)
        .stock(100L)
        .build();
    testProduct = productRepository.save(testProduct);

    // 테스트용 주문 생성
    testOrder = Order.builder()
        .user(testUser)
        .status(OrderStatus.COMPLETED)
        .totalAmount(10000L)
        .build();
    testOrder = orderRepository.save(testOrder);

    // 테스트용 주문 아이템 생성
    OrderItem orderItem = OrderItem.builder()
        .order(testOrder)
        .product(testProduct)
        .quantity(1)
        .price(10000L)
        .build();
    orderItemRepository.save(orderItem);
  }

  @Test
  @DisplayName("환불을 생성할 수 있다")
  void createRefund() {
    // given
    RefundCreateRequest request = RefundCreateRequest.builder()
        .userId(testUser.getId())
        .orderId(testOrder.getId())
        .reason("단순 변심")
        .build();

    // when
    RefundResponse response = refundService.createRefund(request);

    // then
    assertThat(response.getId()).isNotNull();
    assertThat(response.getReason()).isEqualTo("단순 변심");
    assertThat(response.getStatus()).isEqualTo(RefundStatus.PENDING);
  }

  @Test
  @DisplayName("존재하지 않는 사용자로 환불 요청시 예외가 발생한다")
  void createRefundWithInvalidUser() {
    // given
    RefundCreateRequest request = RefundCreateRequest.builder()
        .userId(999L)
        .orderId(testOrder.getId())
        .reason("단순 변심")
        .build();

    // when & then
    assertThatThrownBy(() -> refundService.createRefund(request))
        .isInstanceOf(CustomException.class);
  }

  @Test
  @DisplayName("존재하지 않는 주문으로 환불 요청시 예외가 발생한다")
  void createRefundWithInvalidOrder() {
    // given
    RefundCreateRequest request = RefundCreateRequest.builder()
        .userId(testUser.getId())
        .orderId(999L)
        .reason("단순 변심")
        .build();

    // when & then
    assertThatThrownBy(() -> refundService.createRefund(request))
        .isInstanceOf(CustomException.class);
  }

  @Test
  @DisplayName("본인의 주문이 아닌 경우 환불 요청시 예외가 발생한다")
  void createRefundWithUnauthorizedUser() {
    // given
    User anotherUser = User.builder()
        .email("another@example.com")
        .password("password123")
        .name("다른유저")
        .build();
    anotherUser = userRepository.save(anotherUser);

    RefundCreateRequest request = RefundCreateRequest.builder()
        .userId(anotherUser.getId())
        .orderId(testOrder.getId())
        .reason("단순 변심")
        .build();

    // when & then
    assertThatThrownBy(() -> refundService.createRefund(request))
        .isInstanceOf(CustomException.class);
  }

  @Test
  @DisplayName("완료되지 않은 주문은 환불 요청할 수 없다")
  void createRefundWithNotCompletedOrder() {
    // given
    Order pendingOrder = Order.builder()
        .user(testUser)
        .status(OrderStatus.PENDING)
        .totalAmount(10000L)
        .build();
    pendingOrder = orderRepository.save(pendingOrder);

    RefundCreateRequest request = RefundCreateRequest.builder()
        .userId(testUser.getId())
        .orderId(pendingOrder.getId())
        .reason("단순 변심")
        .build();

    // when & then
    assertThatThrownBy(() -> refundService.createRefund(request))
        .isInstanceOf(CustomException.class);
  }

  @Test
  @DisplayName("이미 환불 요청된 주문은 다시 환불 요청할 수 없다")
  void createDuplicateRefund() {
    // given
    RefundCreateRequest request = RefundCreateRequest.builder()
        .userId(testUser.getId())
        .orderId(testOrder.getId())
        .reason("단순 변심")
        .build();
    refundService.createRefund(request);

    // when & then
    assertThatThrownBy(() -> refundService.createRefund(request))
        .isInstanceOf(CustomException.class);
  }

  @Test
  @DisplayName("ID로 환불 내역을 조회할 수 있다")
  void findRefundById() {
    // given
    RefundCreateRequest request = RefundCreateRequest.builder()
        .userId(testUser.getId())
        .orderId(testOrder.getId())
        .reason("상품 불량")
        .build();
    RefundResponse created = refundService.createRefund(request);

    // when
    RefundResponse found = refundService.findRefundById(created.getId());

    // then
    assertThat(found.getId()).isEqualTo(created.getId());
    assertThat(found.getReason()).isEqualTo("상품 불량");
  }

  @Test
  @DisplayName("전체 환불 목록을 조회할 수 있다")
  void findAllRefunds() {
    // given
    RefundCreateRequest request1 = RefundCreateRequest.builder()
        .userId(testUser.getId())
        .orderId(testOrder.getId())
        .reason("상품 불량")
        .build();
    refundService.createRefund(request1);

    // when
    List<RefundResponse> refunds = refundService.findAllRefunds();

    // then
    assertThat(refunds).hasSizeGreaterThanOrEqualTo(1);
  }

  @Test
  @DisplayName("특정 사용자의 환불 내역을 조회할 수 있다")
  void findByUserId() {
    // given
    RefundCreateRequest request = RefundCreateRequest.builder()
        .userId(testUser.getId())
        .orderId(testOrder.getId())
        .reason("단순 변심")
        .build();
    refundService.createRefund(request);

    // when
    List<RefundResponse> refunds = refundService.findByUserId(testUser.getId());

    // then
    assertThat(refunds).hasSize(1);
    assertThat(refunds.get(0).getReason()).isEqualTo("단순 변심");
  }

  @Test
  @DisplayName("특정 주문의 환불 내역을 조회할 수 있다")
  void findByOrderId() {
    // given
    RefundCreateRequest request = RefundCreateRequest.builder()
        .userId(testUser.getId())
        .orderId(testOrder.getId())
        .reason("배송 지연")
        .build();
    refundService.createRefund(request);

    // when
    List<RefundResponse> refunds = refundService.findByOrderId(testOrder.getId());

    // then
    assertThat(refunds).hasSize(1);
    assertThat(refunds.get(0).getReason()).isEqualTo("배송 지연");
  }

  @Test
  @DisplayName("환불을 승인할 수 있다")
  void approveRefund() {
    // given
    RefundCreateRequest request = RefundCreateRequest.builder()
        .userId(testUser.getId())
        .orderId(testOrder.getId())
        .reason("상품 불량")
        .build();
    RefundResponse created = refundService.createRefund(request);

    Long originalStock = testProduct.getStock();

    // when
    RefundResponse approved = refundService.approveRefund(created.getId());

    // then
    assertThat(approved.getStatus()).isEqualTo(RefundStatus.APPROVED);

    // 재고 복구 확인
    Product updatedProduct = productRepository.findById(testProduct.getId()).orElseThrow();
    assertThat(updatedProduct.getStock()).isEqualTo(originalStock + 1);
  }

  @Test
  @DisplayName("이미 처리된 환불은 승인할 수 없다")
  void approveAlreadyProcessedRefund() {
    // given
    RefundCreateRequest request = RefundCreateRequest.builder()
        .userId(testUser.getId())
        .orderId(testOrder.getId())
        .reason("상품 불량")
        .build();
    RefundResponse created = refundService.createRefund(request);
    refundService.approveRefund(created.getId());

    // when & then
    assertThatThrownBy(() -> refundService.approveRefund(created.getId()))
        .isInstanceOf(CustomException.class);
  }

  @Test
  @DisplayName("환불을 거절할 수 있다")
  void rejectRefund() {
    // given
    RefundCreateRequest request = RefundCreateRequest.builder()
        .userId(testUser.getId())
        .orderId(testOrder.getId())
        .reason("단순 변심")
        .build();
    RefundResponse created = refundService.createRefund(request);

    // when
    RefundResponse rejected = refundService.rejectRefund(created.getId(), "환불 기간 초과");

    // then
    assertThat(rejected.getStatus()).isEqualTo(RefundStatus.REJECTED);
  }

  @Test
  @DisplayName("이미 처리된 환불은 거절할 수 없다")
  void rejectAlreadyProcessedRefund() {
    // given
    RefundCreateRequest request = RefundCreateRequest.builder()
        .userId(testUser.getId())
        .orderId(testOrder.getId())
        .reason("상품 불량")
        .build();
    RefundResponse created = refundService.createRefund(request);
    refundService.rejectRefund(created.getId(), "사유");

    // when & then
    assertThatThrownBy(() -> refundService.rejectRefund(created.getId(), "사유"))
        .isInstanceOf(CustomException.class);
  }
}
