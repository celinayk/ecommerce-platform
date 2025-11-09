package com.ecommerce.platform.domain.product.service;

import com.ecommerce.platform.domain.product.dto.ProductRequest;
import com.ecommerce.platform.domain.product.dto.ProductResponse;
import com.ecommerce.platform.domain.product.entity.Product;
import com.ecommerce.platform.domain.product.entity.ProductStatus;
import com.ecommerce.platform.domain.product.repository.ProductRepository;
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
class ProductServiceTest {

  @Autowired
  ProductService productService;
  @Autowired
  ProductRepository productRepository;

  @Test
  void 상품등록() {
    // given
    ProductRequest request = new ProductRequest();
    request.setName("테스트 상품");
    request.setDescription("테스트 상품 설명");
    request.setPrice(10000);
    request.setStockQuantity(100);

    // when
    ProductResponse response = productService.createProduct(request);

    // then
    assertThat(response.getName()).isEqualTo("테스트 상품");
    assertThat(response.getPrice()).isEqualTo(10000L);
    assertThat(response.getStock()).isEqualTo(100L);
    assertThat(response.getStatus()).isEqualTo(ProductStatus.AVAILABLE);
  }

  @Test
  void 상품목록조회() {
    // given
    for (int i = 1; i <= 15; i++) {
      Product product = Product.builder()
          .name("상품" + i)
          .price((long) (1000 * i))
          .stock(10L)
          .build();
      productRepository.save(product);
    }

    Pageable pageable = PageRequest.of(0, 10);

    // when
    Page<ProductResponse> responses = productService.getAllProducts(pageable);

    // then
    assertThat(responses.getContent()).hasSize(10);
    assertThat(responses.getTotalElements()).isGreaterThanOrEqualTo(15);
  }

  @Test
  void id로상품조회() {
    // given
    Product product = Product.builder()
        .name("조회 테스트 상품")
        .price(5000L)
        .stock(50L)
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
        .price(5000L)
        .stock(50L)
        .build();
    Product savedProduct = productRepository.save(product);

    ProductRequest request = new ProductRequest();
    request.setName("수정 후 상품");
    request.setDescription("수정된 설명");
    request.setPrice(7000);
    request.setStockQuantity(70);

    // when
    ProductResponse response = productService.updateProduct(savedProduct.getId(), request);

    // then
    assertThat(response.getName()).isEqualTo("수정 후 상품");
    assertThat(response.getDescription()).isEqualTo("수정된 설명");
    assertThat(response.getPrice()).isEqualTo(7000L);
    assertThat(response.getStock()).isEqualTo(70L);
  }

  @Test
  void 상품삭제() {
    // given
    Product product = Product.builder()
        .name("삭제할 상품")
        .price(3000L)
        .stock(30L)
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