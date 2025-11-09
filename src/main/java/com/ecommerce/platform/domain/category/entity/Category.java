package com.ecommerce.platform.domain.category.entity;

import com.ecommerce.platform.domain.common.BaseEntity;
import com.ecommerce.platform.domain.product.entity.Product;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class Category extends BaseEntity {

  private Long id;
  private String name;
  private String description;
  private Category parent;
  private List<Product> products = new ArrayList<>();
  private List<Category> children = new ArrayList<>();
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  @Builder
  public Category(String name, String description, Category parent) {
    this.name = name;
    this.description = description;
    this.parent = parent;
  }

  // 비즈니스 로직
  public void updateParent(Category parent) {
    this.parent = parent;
  }

  public void updateCategoryInfo(String name, String description) {
    if (name != null) {
      this.name = name;
    }
    if (description != null) {
      this.description = description;
    }
  }
}
