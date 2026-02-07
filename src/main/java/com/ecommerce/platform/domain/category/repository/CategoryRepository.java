package com.ecommerce.platform.domain.category.repository;

import com.ecommerce.platform.domain.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

  Optional<Category> findByName(String name);

  List<Category> findByParentId(Long parentId);

  @Query("SELECT c FROM Category c WHERE c.parent IS NULL")
  List<Category> findRootCategories();
}
