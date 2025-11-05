package com.ecommerce.platform.domain.category.service;

import com.ecommerce.platform.domain.category.entity.Category;
import com.ecommerce.platform.domain.category.repository.CategoryRepository;
import com.ecommerce.platform.global.common.exception.CustomException;
import com.ecommerce.platform.global.common.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * CategoryService
 * Repository 인터페이스를 사용하므로 MyBatis -> JPA 전환 시 코드 변경 불필요
 */
@Service
@RequiredArgsConstructor
public class CategoryService {

  private final CategoryRepository categoryRepository;

  @Transactional
  public Category createCategory(Category category) {
    // 카테고리 이름 중복 체크
    categoryRepository.findByName(category.getName())
        .ifPresent(c -> {
          throw new CustomException(ErrorCode.DUPLICATE_RESOURCE);
        });

    return categoryRepository.save(category);
  }


  public Category findById(Long id) {
    return categoryRepository.findById(id)
        .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));
  }


  public Category findByName(String name) {
    return categoryRepository.findByName(name)
        .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));
  }


  public List<Category> findAll() {
    return categoryRepository.findAll();
  }


  public List<Category> findByParentId(Long parentId) {
    return categoryRepository.findByParentId(parentId);
  }


  public List<Category> findRootCategories() {
    return categoryRepository.findRootCategories();
  }


  @Transactional
  public Category updateCategory(Long id, Category updateCategory) {
    // 카테고리 존재 확인
    Category existingCategory = findById(id);

    // 카테고리 정보 업데이트
    existingCategory.setName(updateCategory.getName());
    existingCategory.setDescription(updateCategory.getDescription());
    existingCategory.setParent(updateCategory.getParent());

    return categoryRepository.save(existingCategory);
  }


  @Transactional
  public void deleteCategory(Long id) {
    // 카테고리 존재 확인
    if (!categoryRepository.existsById(id)) {
      throw new CustomException(ErrorCode.CATEGORY_NOT_FOUND);
    }

    // 자식 카테고리가 있는지 확인
    List<Category> children = categoryRepository.findByParentId(id);
    if (!children.isEmpty()) {
      throw new CustomException(ErrorCode.HAS_CHILD_CATEGORIES);
    }

    categoryRepository.deleteById(id);
  }
}