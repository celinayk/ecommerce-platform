package com.ecommerce.platform.domain.category.service;

import com.ecommerce.platform.domain.category.dto.CategoryResponse;
import com.ecommerce.platform.domain.category.entity.Category;
import com.ecommerce.platform.domain.category.exception.CategoryException;
import com.ecommerce.platform.domain.category.repository.CategoryRepository;
import com.ecommerce.platform.global.common.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

  private final CategoryRepository categoryRepository;

  @Transactional
  public Category createCategory(Category category) {
    // 카테고리 이름 중복 체크
    categoryRepository.findByName(category.getName())
        .ifPresent(c -> {
          throw new CategoryException(ErrorCode.DUPLICATE_RESOURCE);
        });

    return categoryRepository.save(category);
  }


  public Category findById(Long id) {
    return categoryRepository.findById(id)
        .orElseThrow(() -> new CategoryException(ErrorCode.CATEGORY_NOT_FOUND));
  }


  public Category findByName(String name) {
    return categoryRepository.findByName(name)
        .orElseThrow(() -> new CategoryException(ErrorCode.CATEGORY_NOT_FOUND));
  }


  // 페이징 처리
  public Page<CategoryResponse> findAll(Pageable pageable) {
    return categoryRepository.findAll(pageable)
        .map(CategoryResponse::from);
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
    existingCategory.updateCategoryInfo(updateCategory.getName(), updateCategory.getDescription());
    existingCategory.updateParent(updateCategory.getParent());

    return categoryRepository.save(existingCategory);
  }


  @Transactional
  public void deleteCategory(Long id) {
    // 카테고리 존재 확인
    if (!categoryRepository.existsById(id)) {
      throw new CategoryException(ErrorCode.CATEGORY_NOT_FOUND);
    }

    // 자식 카테고리가 있는지 확인
    List<Category> children = categoryRepository.findByParentId(id);
    if (!children.isEmpty()) {
      throw new CategoryException(ErrorCode.HAS_CHILD_CATEGORIES);
    }

    categoryRepository.deleteById(id);
  }
}
