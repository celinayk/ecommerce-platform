package com.ecommerce.platform.domain.order.service;

import com.ecommerce.platform.domain.order.entity.Order;
import com.ecommerce.platform.domain.order.entity.OrderItem;
import com.ecommerce.platform.domain.order.entity.OrderStatus;
import com.ecommerce.platform.domain.order.repository.OrderRepository;
import com.ecommerce.platform.domain.product.entity.Product;
import com.ecommerce.platform.domain.product.entity.ProductStatus;
import com.ecommerce.platform.domain.product.repository.ProductRepository;
import com.ecommerce.platform.domain.user.entity.User;
import com.ecommerce.platform.domain.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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


  @Test
  void 상품주문() {
    // given
    User user = createUser();
    Product product = createProduct("테스트상품", 10000, 10);
    int orderCount = 2;

    // when
    Long orderId = orderService.order(user.getId(), product.getId(), orderCount);

    // then
    Order findOrder = orderRepository.findById(orderId).orElseThrow();
    assertThat(findOrder.getStatus()).isEqualTo(OrderStatus.ORDER);
    assertThat(findOrder.getOrderItems()).hasSize(1);
    assertThat(findOrder.getTotalAmount()).isEqualTo(20000);
    assertThat(product.getStockQuantity()).isEqualTo(8); // 재고 감소 확인
  }

  @Test
  void 주문취소() {
    // given
    User user = createUser();
    Product product = createProduct("테스트상품", 10000, 10);

    Long orderId = orderService.order(user.getId(), product.getId(), 2);

    // when
    orderService.cancelOrder(orderId);

    // then
    Order findOrder = orderRepository.findById(orderId).orElseThrow();
    assertThat(findOrder.getStatus()).isEqualTo(OrderStatus.CANCEL);
    assertThat(product.getStockQuantity()).isEqualTo(10); // 재고 복구 확인
  }

  @Test
  void 주문취소_중복취소예외() {
    // given
    User user = createUser();
    Product product = createProduct("테스트상품", 10000, 10);

    Long orderId = orderService.order(user.getId(), product.getId(), 2);
    orderService.cancelOrder(orderId);

    // when & then
    assertThatThrownBy(() -> orderService.cancelOrder(orderId))
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("이미 취소된 주문입니다.");
  }

  @Test
  void 재고수량초과() {
    // given
    User user = createUser();
    Product product = createProduct("테스트상품", 10000, 10);

    int orderCount = 11; // 재고보다 많은 수량

    // when & then
    assertThatThrownBy(() -> orderService.order(user.getId(), product.getId(), orderCount))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("재고가 부족합니다.");
  }

  @Test
  void 주문조회() {
    // given
    User user = createUser();
    Product product = createProduct("테스트상품", 10000, 10);
    Long orderId = orderService.order(user.getId(), product.getId(), 2);

    // when
    Order findOrder = orderService.findOrder(orderId);

    // then
    assertThat(findOrder.getId()).isEqualTo(orderId);
    assertThat(findOrder.getUser()).isEqualTo(user);
    assertThat(findOrder.getOrderItems()).hasSize(1);
  }

  private User createUser() {
    User user = new User();
    user.setEmail("test@example.com");
    user.setPassword("password123");
    user.setName("테스트유저");
    userRepository.save(user);
    return user;
  }

  private Product createProduct(String name, Integer price, int stockQuantity) {
    Product product = new Product();
    product.setName(name);
    product.setPrice(price);
    product.setStockQuantity(stockQuantity);
    product.setStatus(ProductStatus.AVAILABLE);
    productRepository.save(product);
    return product;
  }
}