package com.ecommerce.platform.domain.product.repository;

import com.ecommerce.platform.domain.category.entity.Category;
import com.ecommerce.platform.domain.product.entity.Product;

import java.util.List;
import java.util.Optional;

/**
 * Product Repository 인터페이스
 * MyBatis -> JPA 전환 시 Service 코드 변경 없이 구현체만 교체 가능
 */
public interface ProductRepository {


  Product save(Product product);

  Optional<Product> findById(Long id);

  List<Product> findAll();

  // 카테고리 + 상품명 키워드 + 가격 범위 (전체 조건)
  List<Product> findByCategoryAndNameContainingAndPriceBetween(
      Category category, String keyword, Long minPrice, Long maxPrice);

  // 카테고리별 조회
  List<Product> findByCategory(Category category);

  // 상품명 키워드 검색 (LIKE 검색)
  List<Product> findByNameContaining(String keyword);

  // 가격 범위 조회
  List<Product> findByPriceBetween(Long minPrice, Long maxPrice);

  // 카테고리 + 가격 범위
  List<Product> findByCategoryAndPriceBetween(Category category, Long minPrice, Long maxPrice);

  // 상품명 키워드 + 가격 범위
  List<Product> findByNameContainingAndPriceBetween(String keyword, Long minPrice, Long maxPrice);

  // 카테고리 + 상품명 키워드
  List<Product> findByCategoryAndNameContaining(Category category, String keyword);


  int count();

  boolean existsById(Long id);


  void deleteById(Long id);

}