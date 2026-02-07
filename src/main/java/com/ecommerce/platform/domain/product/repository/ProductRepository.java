package com.ecommerce.platform.domain.product.repository;

import com.ecommerce.platform.domain.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

  List<Product> findByCategoryIdAndNameContainingAndPriceBetween(
      Long categoryId, String keyword, BigDecimal minPrice, BigDecimal maxPrice);

  List<Product> findByCategoryId(Long categoryId);

  List<Product> findByNameContaining(String keyword);

  List<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

  List<Product> findByCategoryIdAndPriceBetween(Long categoryId, BigDecimal minPrice, BigDecimal maxPrice);

  List<Product> findByNameContainingAndPriceBetween(String keyword, BigDecimal minPrice, BigDecimal maxPrice);

  List<Product> findByCategoryIdAndNameContaining(Long categoryId, String keyword);
}
