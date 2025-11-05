package com.ecommerce.platform.domain.product.service;

import com.ecommerce.platform.domain.product.dto.ProductRequest;
import com.ecommerce.platform.domain.product.dto.ProductResponse;
import com.ecommerce.platform.domain.product.entity.Product;
import com.ecommerce.platform.domain.product.mapper.ProductMapper;
import com.ecommerce.platform.global.common.exception.CustomException;
import com.ecommerce.platform.global.common.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

  private final ProductMapper productMapper;

  // 상품 등록
  @Transactional
  public ProductResponse createProduct(ProductRequest request) {
    Product product = new Product();
    product.setName(request.getName());
    product.setDescription(request.getDescription());
    product.setPrice(request.getPrice());
    product.setStockQuantity(request.getStockQuantity());

    productMapper.insert(product);
    return ProductResponse.from(product);
  }

  // 상품 목록 조회 (페이징)
  public Page<ProductResponse> getAllProducts(Pageable pageable) {
    int offset = (int) pageable.getOffset();
    int limit = pageable.getPageSize();

    List<Product> products = productMapper.findAll(offset, limit);
    int total = productMapper.count();

    List<ProductResponse> content = products.stream()
        .map(ProductResponse::from)
        .collect(Collectors.toList());

    return new PageImpl<>(content, pageable, total);
  }

  // 상품 상세 조회
  public ProductResponse getProductById(Long id) {
    Product product = productMapper.findById(id);
    if (product == null) {
      throw new CustomException(ErrorCode.PRODUCT_NOT_FOUND);
    }
    return ProductResponse.from(product);
  }

  // 상품 수정
  @Transactional
  public ProductResponse updateProduct(Long id, ProductRequest request) {
    Product product = productMapper.findById(id);
    if (product == null) {
      throw new CustomException(ErrorCode.PRODUCT_NOT_FOUND);
    }

    product.setName(request.getName());
    product.setDescription(request.getDescription());
    product.setPrice(request.getPrice());
    product.setStockQuantity(request.getStockQuantity());

    productMapper.update(product);
    return ProductResponse.from(product);
  }

  // 상품 삭제
  @Transactional
  public void deleteProduct(Long id) {
    Product product = productMapper.findById(id);
    if (product == null) {
      throw new CustomException(ErrorCode.PRODUCT_NOT_FOUND);
    }
    productMapper.deleteById(id);
  }

  // OrderService에서 사용하는 메서드 (호환성 유지)
  public Product findOne(Long itemId) {
    Product product = productMapper.findById(itemId);
    if (product == null) {
      throw new CustomException(ErrorCode.PRODUCT_NOT_FOUND);
    }
    return product;
  }
}