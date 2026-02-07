package com.ecommerce.platform.domain.product.service;

import com.ecommerce.platform.domain.category.exception.CategoryException;
import com.ecommerce.platform.domain.category.repository.CategoryRepository;
import com.ecommerce.platform.domain.product.dto.ProductCreateRequest;
import com.ecommerce.platform.domain.product.dto.ProductResponse;
import com.ecommerce.platform.domain.product.dto.ProductSearchRequest;
import com.ecommerce.platform.domain.product.dto.ProductUpdateRequest;
import com.ecommerce.platform.domain.product.entity.Product;
import com.ecommerce.platform.domain.product.exception.ProductException;
import com.ecommerce.platform.domain.product.repository.ProductRepository;
import com.ecommerce.platform.global.common.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

  private final ProductRepository productRepository;
  private final CategoryRepository categoryRepository;

  // 상품 등록
  @Transactional
  public ProductResponse createProduct(ProductCreateRequest request) {
    // 카테고리 존재 확인
    if (request.getCategoryId() != null) {
      categoryRepository.findById(request.getCategoryId())
          .orElseThrow(() -> new CategoryException(ErrorCode.CATEGORY_NOT_FOUND));
    }

    // 상품 생성
    Product product = Product.builder()
        .categoryId(request.getCategoryId())
        .name(request.getName())
        .description(request.getDescription())
        .price(request.getPrice())
        .build();

    Product savedProduct = productRepository.save(product);
    return ProductResponse.from(savedProduct);
  }

  // 상품 목록 조회
  public List<ProductResponse> getAllProducts() {
    List<Product> products = productRepository.findAll();
    return products.stream()
        .map(ProductResponse::from)
        .collect(Collectors.toList());
  }

  // 상품 상세 조회
  public ProductResponse getProductById(Long id) {
    Product product = productRepository.findById(id)
        .orElseThrow(() -> new ProductException(ErrorCode.PRODUCT_NOT_FOUND));
    return ProductResponse.from(product);
  }

  public List<ProductResponse> searchProducts(ProductSearchRequest request) {
    List<Product> products;

    // 카테고리 기반 검색
    if (request.getCategoryId() != null) {
      categoryRepository.findById(request.getCategoryId())
          .orElseThrow(() -> new CategoryException(ErrorCode.CATEGORY_NOT_FOUND));

      products = productRepository.findByCategoryIdAndNameContainingAndPriceBetween(
          request.getCategoryId(),
          request.getKeyword() != null ? request.getKeyword() : "",
          request.getMinPrice() != null ? request.getMinPrice() : BigDecimal.ZERO,
          request.getMaxPrice() != null ? request.getMaxPrice() : new BigDecimal("999999999")
      );
    }

    // 키워드만 검색
    else if (request.getKeyword() != null && !request.getKeyword().isEmpty()) {
      products = productRepository.findByNameContaining(request.getKeyword());
    }

    // 가격 범위만 검색
    else if (request.getMinPrice() != null || request.getMaxPrice() != null) {
      BigDecimal minPrice = request.getMinPrice() != null ? request.getMinPrice() : BigDecimal.ZERO;
      BigDecimal maxPrice = request.getMaxPrice() != null ? request.getMaxPrice() : new BigDecimal("999999999");
      products = productRepository.findByPriceBetween(minPrice, maxPrice);
    }

    // 조건 없으면 전체 조회
    else {
      products = productRepository.findAll();
    }

    return products.stream()
        .map(ProductResponse::from)
        .collect(Collectors.toList());
  }

  // 상품 수정
  @Transactional
  public ProductResponse updateProduct(Long productId, ProductUpdateRequest request) {
    Product product = productRepository.findById(productId)
        .orElseThrow(() -> new ProductException(ErrorCode.PRODUCT_NOT_FOUND));

    // 카테고리 변경
    if (request.getCategoryId() != null) {
      categoryRepository.findById(request.getCategoryId())
          .orElseThrow(() -> new CategoryException(ErrorCode.CATEGORY_NOT_FOUND));
      product.updateCategory(request.getCategoryId());
    }

    product.updateProductInfo(
        request.getName(),
        request.getDescription(),
        request.getPrice()
    );

    productRepository.save(product);
    return ProductResponse.from(product);
  }

  // 상품 삭제
  @Transactional
  public void deleteProduct(Long id) {
    if (!productRepository.existsById(id)) {
      throw new ProductException(ErrorCode.PRODUCT_NOT_FOUND);
    }
    productRepository.deleteById(id);
  }

  // OrderService에서 사용하는 메서드 (호환성 유지)
  public Product findOne(Long itemId) {
    return productRepository.findById(itemId)
        .orElseThrow(() -> new ProductException(ErrorCode.PRODUCT_NOT_FOUND));
  }
}