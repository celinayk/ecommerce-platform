package com.ecommerce.platform.domain.category.repository;

import com.ecommerce.platform.domain.category.entity.Category;
import com.ecommerce.platform.domain.category.mapper.CategoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * CategoryRepository의 MyBatis 구현체
 * 나중에 JPA로 전환 시 이 클래스만 삭제하고 JpaRepository로 교체
 */
@Repository
@RequiredArgsConstructor
public class CategoryRepositoryImpl implements CategoryRepository {

  private final CategoryMapper categoryMapper;

  @Override
  public Category save(Category category) {
    if (category.getId() == null) {
      // 새로운 카테고리 저장
      categoryMapper.insert(category);
    } else {
      // 기존 카테고리 수정
      categoryMapper.update(category);
    }
    return category;
  }

  @Override
  public Optional<Category> findById(Long id) {
    return Optional.ofNullable(categoryMapper.findById(id));
  }

  @Override
  public Optional<Category> findByName(String name) {
    return Optional.ofNullable(categoryMapper.findByName(name));
  }

  @Override
  public List<Category> findAll(int offset, int limit) {
    return categoryMapper.findAll(offset, limit);
  }

  @Override
  public int count() {
    return categoryMapper.count();
  }


  @Override
  public List<Category> findByParentId(Long parentId) {
    return categoryMapper.findByParentId(parentId);
  }

  @Override
  public List<Category> findRootCategories() {
    return categoryMapper.findRootCategories();
  }

  @Override
  public boolean existsById(Long id) {
    return categoryMapper.findById(id) != null;
  }

  @Override
  public void deleteById(Long id) {
    categoryMapper.deleteById(id);
  }
}