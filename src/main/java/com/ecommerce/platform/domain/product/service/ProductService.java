package com.ecommerce.platform.domain.product.service;

import com.ecommerce.platform.domain.product.dto.ProductRequest;
import com.ecommerce.platform.domain.product.dto.ProductResponse;
import com.ecommerce.platform.domain.product.entity.Product;
import com.ecommerce.platform.domain.product.repository.ProductRepository;
import com.ecommerce.platform.global.common.exception.CustomException;
import com.ecommerce.platform.global.common.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

  private final ProductRepository productRepository;

  // 상품 등록
  @Transactional
  public ProductResponse createProduct(ProductRequest request) {
    Product product = new Product();
    product.setName(request.getName());
    product.setDescription(request.getDescription());
    product.setPrice(request.getPrice());
    product.setStockQuantity(request.getStockQuantity());

    Product savedProduct = productRepository.save(product);
    return ProductResponse.from(savedProduct);
  }

  // 상품 목록 조회 (페이징)
  public Page<ProductResponse> getAllProducts(Pageable pageable) {
    return productRepository.findAll(pageable)
        .map(ProductResponse::from);
  }

  // 상품 상세 조회
  public ProductResponse getProductById(Long id) {
    Product product = productRepository.findById(id)
        .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));
    return ProductResponse.from(product);
  }

  // 상품 수정
  @Transactional
  public ProductResponse updateProduct(Long id, ProductRequest request) {
    Product product = productRepository.findById(id)
        .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

    product.setName(request.getName());
    product.setDescription(request.getDescription());
    product.setPrice(request.getPrice());
    product.setStockQuantity(request.getStockQuantity());

    return ProductResponse.from(product);
  }

  // 상품 삭제
  @Transactional
  public void deleteProduct(Long id) {
    Product product = productRepository.findById(id)
        .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));
    productRepository.delete(product);
  }

  // OrderService에서 사용하는 메서드 (호환성 유지)
  public Product findOne(Long itemId) {
    return productRepository.findById(itemId)
        .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));
  }
}