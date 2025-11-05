package com.ecommerce.platform.domain.category.entity;

import com.ecommerce.platform.domain.common.BaseEntity;
import com.ecommerce.platform.domain.product.entity.Product;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class Category extends BaseEntity {

  private Long id;
  private String name;
  private String description;
  private Category parent;
  private List<Product> products = new ArrayList<>();

}
