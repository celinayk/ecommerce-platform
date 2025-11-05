package com.ecommerce.platform.domain.category.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CategoryUpdateRequest {

  private String name;
  private String description;
  private Long parentId;
}
