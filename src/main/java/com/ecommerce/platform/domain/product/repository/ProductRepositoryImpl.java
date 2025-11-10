package com.ecommerce.platform.domain.product.repository;

import com.ecommerce.platform.domain.category.entity.Category;
import com.ecommerce.platform.domain.product.entity.Product;
import com.ecommerce.platform.domain.product.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * ProductRepository의 MyBatis 구현체
 * 나중에 JPA로 전환 시 이 클래스만 삭제하고 JpaRepository로 교체
 */
@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {

  private final ProductMapper productMapper;

  @Override
  public Product save(Product product) {
    if (product.getId() == null) {
      // 새로운 상품 저장
      productMapper.insert(product);
    } else {
      // 기존 상품 수정
      productMapper.update(product);
    }
    return product;
  }

  @Override
  public Optional<Product> findById(Long id) {
    return Optional.ofNullable(productMapper.findById(id));
  }

  @Override
  public List<Product> findAll() {
    return productMapper.findAll();
  }

  @Override
  public List<Product> findByCategoryAndNameContainingAndPriceBetween(Category category, String keyword, Long minPrice, Long maxPrice) {
    return productMapper.findByCategoryIdAndNameContainingAndPriceBetween(
        category.getId(), keyword, minPrice, maxPrice
    );
  }

  @Override
  public List<Product> findByCategory(Category category) {
    return productMapper.findByCategoryId(category.getId());
  }

  @Override
  public List<Product> findByNameContaining(String keyword) {
    return productMapper.findByNameContaining(keyword);
  }

  @Override
  public List<Product> findByPriceBetween(Long minPrice, Long maxPrice) {
    return productMapper.findByPriceBetween(minPrice, maxPrice);
  }

  @Override
  public List<Product> findByCategoryAndPriceBetween(Category category, Long minPrice, Long maxPrice) {
    return productMapper.findByCategoryIdAndPriceBetween(category.getId(), minPrice, maxPrice);
  }

  @Override
  public List<Product> findByNameContainingAndPriceBetween(String keyword, Long minPrice, Long maxPrice) {
    return productMapper.findByNameContainingAndPriceBetween(keyword, minPrice, maxPrice);
  }

  @Override
  public List<Product> findByCategoryAndNameContaining(Category category, String keyword) {
    return productMapper.findByCategoryIdAndNameContaining(category.getId(), keyword);
  }

  @Override
  public int count() {
    return productMapper.count();
  }

  @Override
  public boolean existsById(Long id) {
    return productMapper.findById(id) != null;
  }

  @Override
  public void deleteById(Long id) {
    productMapper.deleteById(id);
  }

}