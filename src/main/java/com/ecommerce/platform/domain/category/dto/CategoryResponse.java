package com.ecommerce.platform.domain.category.dto;

import com.ecommerce.platform.domain.category.entity.Category;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CategoryResponse {
  private Long id;
  private String name;
  private String description;
  private Long parentId;
  private String parentName;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public static CategoryResponse from(Category category) {
    return CategoryResponse.builder()
        .id(category.getId())
        .name(category.getName())
        .description(category.getDescription())
        .parentId(category.getParent() != null ? category.getParent().getId() : null)
        .parentName(category.getParent() != null ? category.getParent().getName() : null)
        .createdAt(category.getCreatedAt())
        .updatedAt(category.getUpdatedAt())
        .build();
  }
}
