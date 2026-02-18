package com.ecommerce.platform.domain.product.service;

import com.ecommerce.platform.domain.category.entity.Category;
import com.ecommerce.platform.domain.category.repository.CategoryRepository;
import com.ecommerce.platform.domain.product.dto.ProductCreateRequest;
import com.ecommerce.platform.domain.product.dto.ProductResponse;
import com.ecommerce.platform.domain.product.dto.ProductUpdateRequest;
import com.ecommerce.platform.domain.product.entity.Product;
import com.ecommerce.platform.domain.product.repository.ProductRepository;
import com.ecommerce.platform.global.common.exception.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class ProductServiceTest {

  @Autowired
  ProductService productService;
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
  void 상품등록() {
    // given
    ProductCreateRequest request = ProductCreateRequest.builder()

        .name("테스트 상품")
        .description("테스트 상품 설명")
        .price(new BigDecimal("10000"))
        .categoryId(1L)
        .build();

    // when
    ProductResponse response = productService.createProduct(request);

    // then
    assertThat(response.getName()).isEqualTo("테스트 상품");
    assertThat(response.getPrice()).isEqualByComparingTo(new BigDecimal("10000"));
  }

  @Test
  void 상품목록조회() {
    // given
    for (int i = 1; i <= 15; i++) {
      Product product = Product.builder()
  
          .name("상품" + i)
          .price(new BigDecimal(1000 * i))
          .build();
      productRepository.save(product);
    }

    // when
    List<ProductResponse> responses = productService.getAllProducts();

    // then
    assertThat(responses).hasSizeGreaterThanOrEqualTo(15);
  }

  @Test
  void id로상품조회() {
    // given
    Product product = Product.builder()

        .name("조회 테스트 상품")
        .price(new BigDecimal("5000"))
        .build();
    Product savedProduct = productRepository.save(product);

    // when
    ProductResponse response = productService.getProductById(savedProduct.getId());

    // then
    assertThat(response.getId()).isEqualTo(savedProduct.getId());
    assertThat(response.getName()).isEqualTo("조회 테스트 상품");
  }

  @Test
  void 존재하지않는상품조회시예외발생() {
    // when & then
    assertThatThrownBy(() -> productService.getProductById(999L))
        .isInstanceOf(CustomException.class);
  }

  @Test
  void 상품수정() {
    // given
    Product product = Product.builder()

        .name("수정 전 상품")
        .price(new BigDecimal("5000"))
        .build();
    Product savedProduct = productRepository.save(product);

    ProductUpdateRequest request = ProductUpdateRequest.builder()
        .name("수정 후 상품")
        .description("수정된 설명")
        .price(new BigDecimal("7000"))
        .categoryId(1L)
        .build();

    // when
    ProductResponse response = productService.updateProduct(savedProduct.getId(), request);

    // then
    assertThat(response.getName()).isEqualTo("수정 후 상품");
    assertThat(response.getDescription()).isEqualTo("수정된 설명");
    assertThat(response.getPrice()).isEqualByComparingTo(new BigDecimal("7000"));
  }

  @Test
  void 상품삭제() {
    // given
    Product product = Product.builder()

        .name("삭제할 상품")
        .price(new BigDecimal("3000"))
        .build();
    Product savedProduct = productRepository.save(product);
    Long productId = savedProduct.getId();

    // when
    productService.deleteProduct(productId);

    // then
    assertThat(productRepository.findById(productId)).isEmpty();
  }

  @Test
  void 존재하지않는상품시예외발생() {
    // when & then
    assertThatThrownBy(() -> productService.deleteProduct(999L))
        .isInstanceOf(CustomException.class);
  }
}