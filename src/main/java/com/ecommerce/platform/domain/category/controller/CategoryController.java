package com.ecommerce.platform.domain.category.controller;

import com.ecommerce.platform.domain.category.dto.CategoryCreateRequest;
import com.ecommerce.platform.domain.category.dto.CategoryResponse;
import com.ecommerce.platform.domain.category.dto.CategoryUpdateRequest;
import com.ecommerce.platform.domain.category.entity.Category;
import com.ecommerce.platform.domain.category.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

  private final CategoryService categoryService;

  // 카테고리 생성
  @PostMapping
  public ResponseEntity<CategoryResponse> createCategory(@Valid @RequestBody CategoryCreateRequest request) {
    Category category = Category.builder()
        .name(request.getName())
        .description(request.getDescription())
        .build();

    // 부모 카테고리가 있으면 설정
    if (request.getParentId() != null) {
      Category parent = categoryService.findById(request.getParentId());
      category.updateParent(parent);
    }

    Category created = categoryService.createCategory(category);
    return ResponseEntity.status(HttpStatus.CREATED).body(CategoryResponse.from(created));
  }

  // 카테고리 상세 조회 (ID)
  @GetMapping("/{id}")
  public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable Long id) {
    Category category = categoryService.findById(id);
    return ResponseEntity.ok(CategoryResponse.from(category));
  }

  // 전체 카테고리 조회 -> 페이징 처리
  @GetMapping
  public ResponseEntity<Page<CategoryResponse>> getAllCategories(
      @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
  ) {
    Page<CategoryResponse> responses = categoryService.findAll(pageable);
    return ResponseEntity.ok(responses);
  }

  // 최상위 카테고리 조회
  @GetMapping("/root")
  public ResponseEntity<List<CategoryResponse>> getRootCategories() {
    List<Category> categories = categoryService.findRootCategories();
    List<CategoryResponse> responses = categories.stream()
        .map(CategoryResponse::from)
        .collect(Collectors.toList());
    return ResponseEntity.ok(responses);
  }

  // 부모 카테고리별 자식 카테고리 조회
  @GetMapping("/parent/{parentId}")
  public ResponseEntity<List<CategoryResponse>> getCategoriesByParentId(@PathVariable Long parentId) {
    List<Category> categories = categoryService.findByParentId(parentId);
    List<CategoryResponse> responses = categories.stream()
        .map(CategoryResponse::from)
        .collect(Collectors.toList());
    return ResponseEntity.ok(responses);
  }

  // 카테고리 수정
  @PutMapping("/{id}")
  public ResponseEntity<CategoryResponse> updateCategory(
      @PathVariable Long id,
      @Valid @RequestBody CategoryUpdateRequest request) {

    Category updateCategory = Category.builder()
        .name(request.getName())
        .description(request.getDescription())
        .build();

    // 부모 카테고리 설정
    if (request.getParentId() != null) {
      Category parent = categoryService.findById(request.getParentId());
      updateCategory.updateParent(parent);
    }

    Category updated = categoryService.updateCategory(id, updateCategory);
    return ResponseEntity.ok(CategoryResponse.from(updated));
  }

  // 카테고리 삭제
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
    categoryService.deleteCategory(id);
    return ResponseEntity.noContent().build();
  }
}