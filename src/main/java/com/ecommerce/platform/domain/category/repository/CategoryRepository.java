package com.ecommerce.platform.domain.category.repository;

import com.ecommerce.platform.domain.category.entity.Category;

import java.util.List;
import java.util.Optional;

/**
 * Category Repository 인터페이스
 * MyBatis -> JPA 전환 시 Service 코드 변경 없이 구현체만 교체 가능
 */
public interface CategoryRepository {

  Category save(Category category);

  Optional<Category> findById(Long id);

  Optional<Category> findByName(String name);

  List<Category> findAll();

  List<Category> findByParentId(Long parentId);

  List<Category> findRootCategories();

  boolean existsById(Long id);

  void deleteById(Long id);
}