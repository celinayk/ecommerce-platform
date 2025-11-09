package com.ecommerce.platform.domain.product.repository;

import com.ecommerce.platform.domain.product.entity.Product;

import java.util.List;
import java.util.Optional;

/**
 * Product Repository 인터페이스
 * MyBatis -> JPA 전환 시 Service 코드 변경 없이 구현체만 교체 가능
 */
public interface ProductRepository {

  /**
   * 상품 저장
   */
  Product save(Product product);

  /**
   * ID로 상품 조회
   */
  Optional<Product> findById(Long id);

  /**
   * 전체 상품 조회 (페이징)
   */
  List<Product> findAll(int offset, int limit);

  /**
   * 전체 상품 개수
   */
  int count();

  /**
   * 상품 존재 여부 확인
   */
  boolean existsById(Long id);

  /**
   * 상품 삭제
   */
  void deleteById(Long id);

  /**
   * 재고 감소
   */
  void decreaseStock(Long id, int quantity);

  /**
   * 재고 증가
   */
  void increaseStock(Long id, int quantity);
}