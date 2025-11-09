package com.ecommerce.platform.domain.order.service;

import com.ecommerce.platform.domain.order.dto.OrderRequest;
import com.ecommerce.platform.domain.order.dto.OrderResponse;
import com.ecommerce.platform.domain.order.entity.Order;
import com.ecommerce.platform.domain.order.entity.OrderStatus;
import com.ecommerce.platform.domain.order.repository.OrderRepository;
import com.ecommerce.platform.domain.product.entity.Product;
import com.ecommerce.platform.domain.product.entity.ProductStatus;
import com.ecommerce.platform.domain.product.repository.ProductRepository;
import com.ecommerce.platform.domain.user.entity.User;
import com.ecommerce.platform.domain.user.repository.UserRepository;
import com.ecommerce.platform.global.common.exception.CustomException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

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


  @Test
  void 주문생성() {
    // given
    User user = createUser();
    Product product = createProduct("테스트상품", 10000, 10);

    OrderRequest request = new OrderRequest();
    request.setUserId(user.getId());
    request.setProductId(product.getId());
    request.setCount(2);

    // when
    OrderResponse response = orderService.createOrder(request);

    // then
    assertThat(response.getStatus()).isEqualTo(OrderStatus.PENDING);
    assertThat(response.getOrderItems()).hasSize(1);
    assertThat(response.getTotalAmount()).isEqualTo(20000L);

    // 재고 감소 확인 - DB에서 다시 조회
    Product updatedProduct = productRepository.findById(product.getId()).orElseThrow();
    assertThat(updatedProduct.getStock()).isEqualTo(8L);
  }

  @Test
  void 주문목록조회() {
    // given
    User user = createUser();
    Product product = createProduct("테스트상품", 10000, 10);

    for (int i = 0; i < 5; i++) {
      OrderRequest request = new OrderRequest();
      request.setUserId(user.getId());
      request.setProductId(product.getId());
      request.setCount(1);
      orderService.createOrder(request);
    }

    Pageable pageable = PageRequest.of(0, 10);

    // when
    Page<OrderResponse> responses = orderService.getAllOrders(pageable);

    // then
    assertThat(responses.getContent()).hasSizeGreaterThanOrEqualTo(5);
  }

  @Test
  void 주문상세조회() {
    // given
    User user = createUser();
    Product product = createProduct("테스트상품", 10000, 10);

    OrderRequest request = new OrderRequest();
    request.setUserId(user.getId());
    request.setProductId(product.getId());
    request.setCount(2);
    OrderResponse createdOrder = orderService.createOrder(request);

    // when
    OrderResponse response = orderService.getOrderById(createdOrder.getId());

    // then
    assertThat(response.getId()).isEqualTo(createdOrder.getId());
    assertThat(response.getUserId()).isEqualTo(user.getId());
    assertThat(response.getOrderItems()).hasSize(1);
  }

  @Test
  void 주문취소() {
    // given
    User user = createUser();
    Product product = createProduct("테스트상품", 10000, 10);

    OrderRequest request = new OrderRequest();
    request.setUserId(user.getId());
    request.setProductId(product.getId());
    request.setCount(2);
    OrderResponse createdOrder = orderService.createOrder(request);

    // when
    orderService.cancelOrder(createdOrder.getId());

    // then
    OrderResponse canceledOrder = orderService.getOrderById(createdOrder.getId());
    assertThat(canceledOrder.getStatus()).isEqualTo(OrderStatus.CANCELED);

    // 재고 복구 확인 - DB에서 다시 조회
    Product updatedProduct = productRepository.findById(product.getId()).orElseThrow();
    assertThat(updatedProduct.getStock()).isEqualTo(10L);
  }

  @Test
  void 주문취소_중복취소예외() {
    // given
    User user = createUser();
    Product product = createProduct("테스트상품", 10000, 10);

    OrderRequest request = new OrderRequest();
    request.setUserId(user.getId());
    request.setProductId(product.getId());
    request.setCount(2);
    OrderResponse createdOrder = orderService.createOrder(request);

    orderService.cancelOrder(createdOrder.getId());

    // when & then
    assertThatThrownBy(() -> orderService.cancelOrder(createdOrder.getId()))
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("이미 취소된 주문입니다.");
  }

  @Test
  void 재고수량초과() {
    // given
    User user = createUser();
    Product product = createProduct("테스트상품", 10000, 10);

    OrderRequest request = new OrderRequest();
    request.setUserId(user.getId());
    request.setProductId(product.getId());
    request.setCount(11); // 재고보다 많은 수량

    // when & then
    assertThatThrownBy(() -> orderService.createOrder(request))
        .isInstanceOf(CustomException.class);
  }

  @Test
  void 존재하지않는주문조회시예외발생() {
    // when & then
    assertThatThrownBy(() -> orderService.getOrderById(999L))
        .isInstanceOf(CustomException.class);
  }

  private User createUser() {
    User user = User.builder()
        .email("test@example.com")
        .password("password123")
        .name("테스트유저")
        .build();
    userRepository.save(user);
    return user;
  }

  private Product createProduct(String name, Integer price, int stockQuantity) {
    Product product = Product.builder()
        .name(name)
        .price(price.longValue())
        .stock((long) stockQuantity)
        .build();
    productRepository.save(product);
    return product;
  }
}