package com.ecommerce.platform.domain.product.controller;

import com.ecommerce.platform.domain.category.entity.Category;
import com.ecommerce.platform.domain.category.repository.CategoryRepository;
import com.ecommerce.platform.domain.product.dto.ProductCreateRequest;
import com.ecommerce.platform.domain.product.dto.ProductUpdateRequest;
import com.ecommerce.platform.domain.product.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

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
  @Autowired
  CategoryRepository categoryRepository;

  @BeforeEach
  void setUp() {
    if (!categoryRepository.existsById(1L)) {
      Category category = Category.builder()
          .name("전자기기")
          .build();

      categoryRepository.save(category);
    }
  }


  @Test
  void 상품등록_성공() throws Exception {
    // given
    ProductCreateRequest request = ProductCreateRequest.builder()
        .sellerId(1L)
        .name("테스트상품")
        .description("상품 설명입니다")
        .price(new BigDecimal("10000"))
        .categoryId(1L)
        .build();

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
    ProductCreateRequest request = ProductCreateRequest.builder()
        .sellerId(1L)
        .name("") // 빈 상품명
        .description("상품 설명입니다")
        .price(new BigDecimal("10000"))
        .categoryId(1L)
        .build();

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
    ProductCreateRequest request = ProductCreateRequest.builder()
        .sellerId(1L)
        .name("a".repeat(101)) // 101자 (최대 100자)
        .description("상품 설명입니다")
        .price(new BigDecimal("10000"))
        .categoryId(1L)
        .build();

    // when & then
    mockMvc.perform(post("/api/products")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
        .andExpect(jsonPath("$.errors.name").exists());
  }

  @Test
  void 상품등록_설명길이검증() throws Exception {
    // given
    ProductCreateRequest request = ProductCreateRequest.builder()
        .sellerId(1L)
        .name("테스트상품")
        .description("a".repeat(501)) // 501자 (최대 500자)
        .price(new BigDecimal("10000"))
        .categoryId(1L)
        .build();

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
          "sellerId": 1,
          "name": "테스트상품",
          "description": "상품 설명입니다",
          "price": null
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
  void 상품수정_검증() throws Exception {
    // given
    ProductUpdateRequest request = ProductUpdateRequest.builder()
        .name("") // 빈 상품명
        .description("수정된 설명")
        .price(new BigDecimal("-1000")) // 음수 가격
        .categoryId(1L)
        .build();

    // when & then
    mockMvc.perform(put("/api/products/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
        .andExpect(jsonPath("$.errors").exists());
  }
}