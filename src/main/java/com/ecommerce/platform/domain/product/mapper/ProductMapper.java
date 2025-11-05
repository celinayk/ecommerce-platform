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
  List<Product> findAll(@Param("offset") int offset, @Param("limit") int limit);

  // 전체 상품 개수
  int count();

  // 상품 수정
  void update(Product product);

  // 상품 삭제
  void deleteById(@Param("id") Long id);

  // 재고 감소
  void decreaseStock(@Param("id") Long id, @Param("quantity") int quantity);

  // 재고 증가
  void increaseStock(@Param("id") Long id, @Param("quantity") int quantity);
}