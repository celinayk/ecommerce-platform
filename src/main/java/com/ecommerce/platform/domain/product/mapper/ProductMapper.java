package com.ecommerce.platform.domain.product.mapper;

import com.ecommerce.platform.domain.product.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProductMapper {

  // 상품 저장
  void insert(Product product);

  // ID로 상품 조회
  Product findById(@Param("id") Long id);

  // 전체 상품 조회
  List<Product> findAll();

  // 전체 상품 개수
  int count();

  // 상품 수정
  void update(Product product);

  // 상품 삭제
  void deleteById(@Param("id") Long id);


  // 카테고리 + 상품명 키워드 + 가격 범위로 검색
  List<Product> findByCategoryIdAndNameContainingAndPriceBetween(
      @Param("categoryId") Long categoryId,
      @Param("keyword") String keyword,
      @Param("minPrice") Long minPrice,
      @Param("maxPrice") Long maxPrice
  );

  // 카테고리별 조회
  List<Product> findByCategoryId(@Param("categoryId") Long categoryId);

  // 상품명 키워드 검색
  List<Product> findByNameContaining(@Param("keyword") String keyword);

  // 가격 범위 조회
  List<Product> findByPriceBetween(@Param("minPrice") Long minPrice, @Param("maxPrice") Long maxPrice);

  // 카테고리 + 가격 범위
  List<Product> findByCategoryIdAndPriceBetween(
      @Param("categoryId") Long categoryId,
      @Param("minPrice") Long minPrice,
      @Param("maxPrice") Long maxPrice
  );

  // 상품명 키워드 + 가격 범위
  List<Product> findByNameContainingAndPriceBetween(
      @Param("keyword") String keyword,
      @Param("minPrice") Long minPrice,
      @Param("maxPrice") Long maxPrice
  );

  // 카테고리 + 상품명 키워드
  List<Product> findByCategoryIdAndNameContaining(
      @Param("categoryId") Long categoryId,
      @Param("keyword") String keyword
  );
}