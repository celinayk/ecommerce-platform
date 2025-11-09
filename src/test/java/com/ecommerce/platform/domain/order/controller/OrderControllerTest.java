package com.ecommerce.platform.domain.order.controller;

import com.ecommerce.platform.domain.order.dto.OrderRequest;
import com.ecommerce.platform.domain.order.repository.OrderRepository;
import com.ecommerce.platform.domain.product.entity.Product;
import com.ecommerce.platform.domain.product.entity.ProductStatus;
import com.ecommerce.platform.domain.product.repository.ProductRepository;
import com.ecommerce.platform.domain.user.entity.User;
import com.ecommerce.platform.domain.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class OrderControllerTest {

  @Autowired
  MockMvc mockMvc;
  @Autowired
  ObjectMapper objectMapper;
  @Autowired
  OrderRepository orderRepository;
  @Autowired
  UserRepository userRepository;
  @Autowired
  ProductRepository productRepository;

  private User testUser;
  private Product testProduct;

  @BeforeEach
  void setUp() {
    // 테스트용 사용자 생성
    testUser = User.builder()
        .email("test@example.com")
        .password("password123")
        .name("테스트유저")
        .build();
    userRepository.save(testUser);

    // 테스트용 상품 생성
    testProduct = Product.builder()
        .name("테스트상품")
        .description("상품 설명")
        .price(10000L)
        .stock(100L)
        .build();
    productRepository.save(testProduct);
  }

  @Test
  void 주문생성_성공() throws Exception {
    // given
    OrderRequest request = new OrderRequest();
    request.setUserId(testUser.getId());
    request.setProductId(testProduct.getId());
    request.setCount(2);

    // when & then
    mockMvc.perform(post("/api/orders")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.userId").value(testUser.getId()))
        .andExpect(jsonPath("$.totalAmount").value(20000));
  }

  @Test
  void 주문생성_userId필수검증() throws Exception {
    // given
    String requestBody = """
        {
          "userId": null,
          "productId": 1,
          "count": 2
        }
        """;

    // when & then
    mockMvc.perform(post("/api/orders")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
        .andExpect(jsonPath("$.errors.userId").exists());
  }

  @Test
  void 주문생성_userId양수검증() throws Exception {
    // given
    OrderRequest request = new OrderRequest();
    request.setUserId(0L); // 0은 양수 아님
    request.setProductId(testProduct.getId());
    request.setCount(2);

    // when & then
    mockMvc.perform(post("/api/orders")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
        .andExpect(jsonPath("$.errors.userId").exists());
  }

  @Test
  void 주문생성_userId음수검증() throws Exception {
    // given
    OrderRequest request = new OrderRequest();
    request.setUserId(-1L); // 음수
    request.setProductId(testProduct.getId());
    request.setCount(2);

    // when & then
    mockMvc.perform(post("/api/orders")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
        .andExpect(jsonPath("$.errors.userId").exists());
  }

  @Test
  void 주문생성_productId필수검증() throws Exception {
    // given
    String requestBody = """
        {
          "userId": 1,
          "productId": null,
          "count": 2
        }
        """;

    // when & then
    mockMvc.perform(post("/api/orders")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
        .andExpect(jsonPath("$.errors.productId").exists());
  }

  @Test
  void 주문생성_productId양수검증() throws Exception {
    // given
    OrderRequest request = new OrderRequest();
    request.setUserId(testUser.getId());
    request.setProductId(0L); // 0은 양수 아님
    request.setCount(2);

    // when & then
    mockMvc.perform(post("/api/orders")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
        .andExpect(jsonPath("$.errors.productId").exists());
  }

  @Test
  void 주문생성_productId음수검증() throws Exception {
    // given
    OrderRequest request = new OrderRequest();
    request.setUserId(testUser.getId());
    request.setProductId(-1L); // 음수
    request.setCount(2);

    // when & then
    mockMvc.perform(post("/api/orders")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
        .andExpect(jsonPath("$.errors.productId").exists());
  }

  @Test
  void 주문생성_count필수검증() throws Exception {
    // given
    String requestBody = """
        {
          "userId": 1,
          "productId": 1,
          "count": null
        }
        """;

    // when & then
    mockMvc.perform(post("/api/orders")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
        .andExpect(jsonPath("$.errors.count").exists());
  }

  @Test
  void 주문생성_count최소값검증_0() throws Exception {
    // given
    OrderRequest request = new OrderRequest();
    request.setUserId(testUser.getId());
    request.setProductId(testProduct.getId());
    request.setCount(0); // 0은 최소값 미만

    // when & then
    mockMvc.perform(post("/api/orders")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
        .andExpect(jsonPath("$.errors.count").exists());
  }

  @Test
  void 주문생성_count최소값검증_음수() throws Exception {
    // given
    OrderRequest request = new OrderRequest();
    request.setUserId(testUser.getId());
    request.setProductId(testProduct.getId());
    request.setCount(-5); // 음수

    // when & then
    mockMvc.perform(post("/api/orders")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
        .andExpect(jsonPath("$.errors.count").exists());
  }

  @Test
  void 주문생성_count최대값검증() throws Exception {
    // given
    OrderRequest request = new OrderRequest();
    request.setUserId(testUser.getId());
    request.setProductId(testProduct.getId());
    request.setCount(1001); // 1000 초과

    // when & then
    mockMvc.perform(post("/api/orders")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
        .andExpect(jsonPath("$.errors.count").exists());
  }
}
