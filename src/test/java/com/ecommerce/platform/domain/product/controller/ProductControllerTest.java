package com.ecommerce.platform.domain.product.controller;

import com.ecommerce.platform.domain.product.dto.ProductRequest;
import com.ecommerce.platform.domain.product.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ProductControllerTest {

  @Autowired
  MockMvc mockMvc;
  @Autowired
  ObjectMapper objectMapper;
  @Autowired
  ProductRepository productRepository;

  @Test
  void 상품등록_성공() throws Exception {
    // given
    ProductRequest request = new ProductRequest();
    request.setName("테스트상품");
    request.setDescription("상품 설명입니다");
    request.setPrice(10000);
    request.setStockQuantity(100);

    // when & then
    mockMvc.perform(post("/api/products")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name").value("테스트상품"))
        .andExpect(jsonPath("$.price").value(10000));
  }

  @Test
  void 상품등록_상품명필수검증() throws Exception {
    // given
    ProductRequest request = new ProductRequest();
    request.setName(""); // 빈 상품명
    request.setDescription("상품 설명입니다");
    request.setPrice(10000);
    request.setStockQuantity(100);

    // when & then
    mockMvc.perform(post("/api/products")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
        .andExpect(jsonPath("$.errors.name").exists());
  }

  @Test
  void 상품등록_상품명길이검증() throws Exception {
    // given
    ProductRequest request = new ProductRequest();
    request.setName("a".repeat(101)); // 101자 (최대 100자)
    request.setDescription("상품 설명입니다");
    request.setPrice(10000);
    request.setStockQuantity(100);

    // when & then
    mockMvc.perform(post("/api/products")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
        .andExpect(jsonPath("$.errors.name").exists());
  }

  @Test
  void 상품등록_설명필수검증() throws Exception {
    // given
    ProductRequest request = new ProductRequest();
    request.setName("테스트상품");
    request.setDescription(""); // 빈 설명
    request.setPrice(10000);
    request.setStockQuantity(100);

    // when & then
    mockMvc.perform(post("/api/products")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
        .andExpect(jsonPath("$.errors.description").exists());
  }

  @Test
  void 상품등록_설명길이검증() throws Exception {
    // given
    ProductRequest request = new ProductRequest();
    request.setName("테스트상품");
    request.setDescription("a".repeat(501)); // 501자 (최대 500자)
    request.setPrice(10000);
    request.setStockQuantity(100);

    // when & then
    mockMvc.perform(post("/api/products")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
        .andExpect(jsonPath("$.errors.description").exists());
  }

  @Test
  void 상품등록_가격필수검증() throws Exception {
    // given
    String requestBody = """
        {
          "name": "테스트상품",
          "description": "상품 설명입니다",
          "price": null,
          "stockQuantity": 100
        }
        """;

    // when & then
    mockMvc.perform(post("/api/products")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
        .andExpect(jsonPath("$.errors.price").exists());
  }

  @Test
  void 상품등록_가격최소값검증() throws Exception {
    // given
    ProductRequest request = new ProductRequest();
    request.setName("테스트상품");
    request.setDescription("상품 설명입니다");
    request.setPrice(-1000); // 음수
    request.setStockQuantity(100);

    // when & then
    mockMvc.perform(post("/api/products")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
        .andExpect(jsonPath("$.errors.price").exists());
  }

  @Test
  void 상품등록_가격최대값검증() throws Exception {
    // given
    ProductRequest request = new ProductRequest();
    request.setName("테스트상품");
    request.setDescription("상품 설명입니다");
    request.setPrice(100000001); // 1억원 초과
    request.setStockQuantity(100);

    // when & then
    mockMvc.perform(post("/api/products")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
        .andExpect(jsonPath("$.errors.price").exists());
  }

  @Test
  void 상품등록_재고필수검증() throws Exception {
    // given
    String requestBody = """
        {
          "name": "테스트상품",
          "description": "상품 설명입니다",
          "price": 10000,
          "stockQuantity": null
        }
        """;

    // when & then
    mockMvc.perform(post("/api/products")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
        .andExpect(jsonPath("$.errors.stockQuantity").exists());
  }

  @Test
  void 상품등록_재고최소값검증() throws Exception {
    // given
    ProductRequest request = new ProductRequest();
    request.setName("테스트상품");
    request.setDescription("상품 설명입니다");
    request.setPrice(10000);
    request.setStockQuantity(-10); // 음수

    // when & then
    mockMvc.perform(post("/api/products")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
        .andExpect(jsonPath("$.errors.stockQuantity").exists());
  }

  @Test
  void 상품수정_검증() throws Exception {
    // given
    ProductRequest request = new ProductRequest();
    request.setName(""); // 빈 상품명
    request.setDescription("수정된 설명");
    request.setPrice(-1000); // 음수 가격
    request.setStockQuantity(50);

    // when & then
    mockMvc.perform(put("/api/products/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
        .andExpect(jsonPath("$.errors").exists());
  }
}