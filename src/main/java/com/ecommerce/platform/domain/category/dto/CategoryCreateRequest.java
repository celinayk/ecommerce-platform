package com.ecommerce.platform.domain.category.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CategoryCreateRequest {

  private String name;

  private String description;

  private Long parentId;  // 최상위 카테고리면 null, 하위 카테고리면 부모 ID
}
