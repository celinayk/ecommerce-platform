package com.ecommerce.platform.domain.order.service;

import com.ecommerce.platform.domain.order.dto.CancelRequest;
import com.ecommerce.platform.domain.order.dto.OrderRequest;
import com.ecommerce.platform.domain.order.dto.OrderResponse;
import com.ecommerce.platform.domain.order.entity.OrderStatus;
import com.ecommerce.platform.domain.order.repository.OrderRepository;
import com.ecommerce.platform.domain.product.entity.Product;
import com.ecommerce.platform.domain.product.repository.ProductRepository;
import com.ecommerce.platform.domain.stock.entity.Stock;
import com.ecommerce.platform.domain.stock.repository.StockRepository;
import com.ecommerce.platform.domain.user.entity.User;
import com.ecommerce.platform.domain.user.repository.UserRepository;
import com.ecommerce.platform.domain.order.exception.OrderException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class OrderServiceTest {

  @Autowired
  OrderService orderService;
  @Autowired
  OrderRepository orderRepository;
  @Autowired
  UserRepository userRepository;
  @Autowired
  ProductRepository productRepository;
  @Autowired
  StockRepository stockRepository;

  @Test
  void 주문생성() {
    // given
    User user = createUser();
    Product product = createProduct("테스트상품", new BigDecimal("10000"));
    createStock(product, 100);

    OrderRequest request = createOrderRequest(user.getId(), product.getId(), new BigDecimal("10000"), 2);

    // when
    OrderResponse response = orderService.createOrder(request);

    // then
    assertThat(response.getStatus()).isEqualTo(OrderStatus.PENDING);
    assertThat(response.getOrderItems()).hasSize(1);
    assertThat(response.getTotalPrice()).isEqualByComparingTo(new BigDecimal("20000"));
  }

  @Test
  void 주문생성_재고차감확인() {
    // given
    User user = createUser();
    Product product = createProduct("테스트상품", new BigDecimal("10000"));
    createStock(product, 100);

    OrderRequest request = createOrderRequest(user.getId(), product.getId(), new BigDecimal("10000"), 3);

    // when
    orderService.createOrder(request);

    // then
    Stock stock = stockRepository.findByProductId(product.getId()).orElseThrow();
    assertThat(stock.getQuantity()).isEqualTo(97);
  }

  @Test
  void 주문목록조회() {
    // given
    User user = createUser();
    Product product = createProduct("테스트상품", new BigDecimal("10000"));
    createStock(product, 100);

    for (int i = 0; i < 5; i++) {
      OrderRequest request = createOrderRequest(user.getId(), product.getId(), new BigDecimal("10000"), 1);
      orderService.createOrder(request);
    }

    // when
    Page<OrderResponse> responses = orderService.getAllOrders(PageRequest.of(0, 10));

    // then
    assertThat(responses.getContent()).hasSizeGreaterThanOrEqualTo(5);
  }

  @Test
  void 주문상세조회() {
    // given
    User user = createUser();
    Product product = createProduct("테스트상품", new BigDecimal("10000"));
    createStock(product, 100);

    OrderRequest request = createOrderRequest(user.getId(), product.getId(), new BigDecimal("10000"), 2);
    OrderResponse createdOrder = orderService.createOrder(request);

    // when
    OrderResponse response = orderService.getOrderById(createdOrder.getId());

    // then
    assertThat(response.getId()).isEqualTo(createdOrder.getId());
    assertThat(response.getUserId()).isEqualTo(user.getId());
    assertThat(response.getOrderItems()).hasSize(1);
  }

  @Test
  void 존재하지않는주문조회시예외발생() {
    // when & then
    assertThatThrownBy(() -> orderService.getOrderById(999L))
        .isInstanceOf(OrderException.class);
  }

  @Test
  void 주문확인() {
    // given
    User user = createUser();
    Product product = createProduct("테스트상품", new BigDecimal("10000"));
    createStock(product, 100);

    OrderRequest request = createOrderRequest(user.getId(), product.getId(), new BigDecimal("10000"), 2);
    OrderResponse createdOrder = orderService.createOrder(request);

    // when
    OrderResponse response = orderService.confirmOrder(createdOrder.getId());

    // then
    assertThat(response.getStatus()).isEqualTo(OrderStatus.CONFIRMED);
  }

  @Test
  void 취소요청() {
    // given
    User user = createUser();
    Product product = createProduct("테스트상품", new BigDecimal("10000"));
    createStock(product, 100);

    OrderRequest request = createOrderRequest(user.getId(), product.getId(), new BigDecimal("10000"), 2);
    OrderResponse createdOrder = orderService.createOrder(request);
    orderService.confirmOrder(createdOrder.getId());

    CancelRequest cancelRequest = new CancelRequest();
    ReflectionTestUtils.setField(cancelRequest, "reason", "단순 변심");

    // when
    OrderResponse response = orderService.requestCancel(createdOrder.getId(), cancelRequest);

    // then
    assertThat(response.getStatus()).isEqualTo(OrderStatus.CANCEL_REQUESTED);
  }

  @Test
  void 취소승인_재고복구확인() {
    // given
    User user = createUser();
    Product product = createProduct("테스트상품", new BigDecimal("10000"));
    createStock(product, 100);

    OrderRequest request = createOrderRequest(user.getId(), product.getId(), new BigDecimal("10000"), 3);
    OrderResponse createdOrder = orderService.createOrder(request);
    orderService.confirmOrder(createdOrder.getId());

    CancelRequest cancelRequest = new CancelRequest();
    ReflectionTestUtils.setField(cancelRequest, "reason", "단순 변심");
    orderService.requestCancel(createdOrder.getId(), cancelRequest);

    // when
    OrderResponse response = orderService.approveCancel(createdOrder.getId());

    // then
    assertThat(response.getStatus()).isEqualTo(OrderStatus.CANCELED);

    Stock stock = stockRepository.findByProductId(product.getId()).orElseThrow();
    assertThat(stock.getQuantity()).isEqualTo(100); // 원래 재고로 복구
  }

  @Test
  void 취소요청_PENDING상태에서_예외() {
    // given - PENDING 상태 (confirm 안 함)
    User user = createUser();
    Product product = createProduct("테스트상품", new BigDecimal("10000"));
    createStock(product, 100);

    OrderRequest request = createOrderRequest(user.getId(), product.getId(), new BigDecimal("10000"), 2);
    OrderResponse createdOrder = orderService.createOrder(request);

    CancelRequest cancelRequest = new CancelRequest();
    ReflectionTestUtils.setField(cancelRequest, "reason", "단순 변심");

    // when & then
    assertThatThrownBy(() -> orderService.requestCancel(createdOrder.getId(), cancelRequest))
        .isInstanceOf(IllegalStateException.class);
  }

  @Test
  void 이미취소된주문_재취소요청_예외() {
    // given
    User user = createUser();
    Product product = createProduct("테스트상품", new BigDecimal("10000"));
    createStock(product, 100);

    OrderRequest request = createOrderRequest(user.getId(), product.getId(), new BigDecimal("10000"), 2);
    OrderResponse createdOrder = orderService.createOrder(request);
    orderService.confirmOrder(createdOrder.getId());

    CancelRequest cancelRequest = new CancelRequest();
    ReflectionTestUtils.setField(cancelRequest, "reason", "단순 변심");
    orderService.requestCancel(createdOrder.getId(), cancelRequest);
    orderService.approveCancel(createdOrder.getId());

    // when & then - CANCELED 상태에서 다시 취소 요청
    assertThatThrownBy(() -> orderService.requestCancel(createdOrder.getId(), cancelRequest))
        .isInstanceOf(IllegalStateException.class);
  }

  private User createUser() {
    User user = User.builder()
        .email("test@example.com")
        .password("password123")
        .name("테스트유저")
        .build();
    return userRepository.save(user);
  }

  private Product createProduct(String name, BigDecimal price) {
    Product product = Product.builder()
        .name(name)
        .price(price)
        .build();
    return productRepository.save(product);
  }

  private Stock createStock(Product product, int quantity) {
    Stock stock = Stock.builder()
        .product(product)
        .quantity(quantity)
        .build();
    return stockRepository.save(stock);
  }

  private OrderRequest createOrderRequest(Long userId, Long productId, BigDecimal price, int quantity) {
    OrderRequest request = new OrderRequest();
    ReflectionTestUtils.setField(request, "userId", userId);
    ReflectionTestUtils.setField(request, "productId", productId);
    ReflectionTestUtils.setField(request, "price", price);
    ReflectionTestUtils.setField(request, "quantity", quantity);
    return request;
  }
}