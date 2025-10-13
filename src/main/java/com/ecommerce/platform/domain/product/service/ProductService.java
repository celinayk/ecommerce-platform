package com.ecommerce.platform.domain.product.service;

import com.ecommerce.platform.domain.product.entity.Product;
import com.ecommerce.platform.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

  private final ProductRepository productRepository;

  // 상품 등록
  @Transactional
  public Product saveProduct(Product product) {
    return productRepository.save(product);
  }


  public Product findOne(Long itemId) {
    return productRepository.findById(itemId)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다."));
  }

  // 전체 상품 조회
  public List<Product> findAllProducts() {
    return productRepository.findAll();
  }


}