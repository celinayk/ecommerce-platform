package com.ecommerce.platform.domain.product.repository;

import com.ecommerce.platform.domain.product.entity.Product;
import com.ecommerce.platform.domain.product.entity.ProductStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ProductRepositoryTest {

  @Autowired
  private ProductRepository productRepository;

  @Test
  @Transactional
  void 상품저장() {
    // given
    Product product = new Product();
    product.setName("테스트 상품");
    product.setDescription("상품 설명");
    product.setPrice(10000);
    product.setStockQuantity(100);
    product.setStatus(ProductStatus.AVAILABLE);

    // when
    Product savedProduct = productRepository.save(product);
    Product findProduct = productRepository.findById(savedProduct.getId()).get();

    // then
    assertThat(findProduct.getId()).isEqualTo(product.getId());
    assertThat(findProduct.getName()).isEqualTo(product.getName());
    assertThat(findProduct).isEqualTo(product); //JPA 엔티티 동일성 보장
  }

  @Test
  @Transactional
  void ID로_상품조회() {
    // given
    Product product = new Product();
    product.setName("조회 상품");
    product.setDescription("설명");
    product.setPrice(5000);
    product.setStockQuantity(50);
    Product savedProduct = productRepository.save(product);

    // when
    Product foundProduct = productRepository.findById(savedProduct.getId()).orElse(null);

    // then
    assertThat(foundProduct).isNotNull();
    assertThat(foundProduct.getName()).isEqualTo("조회 상품");
    assertThat(foundProduct.getPrice()).isEqualTo(BigDecimal.valueOf(5000));
  }

  @Test
  @Transactional
  void 전체상품조회() {
    // given
    Product product1 = new Product();
    product1.setName("상품1");
    product1.setDescription("설명1");
    product1.setPrice(1000);
    product1.setStockQuantity(10);

    Product product2 = new Product();
    product2.setName("상품2");
    product2.setDescription("설명2");
    product2.setPrice(2000);
    product2.setStockQuantity(20);

    productRepository.save(product1);
    productRepository.save(product2);

    // when
    List<Product> products = productRepository.findAll();

    // then
    assertThat(products).hasSizeGreaterThanOrEqualTo(2);
  }

  @Test
  @Transactional
  void 상품삭제() {
    // given
    Product product = new Product();
    product.setName("삭제 상품");
    product.setDescription("설명");
    product.setPrice(3000);
    product.setStockQuantity(30);
    Product savedProduct = productRepository.save(product);

    // when
    productRepository.delete(savedProduct);

    // then
    assertThat(productRepository.findById(savedProduct.getId())).isEmpty();
  }
}