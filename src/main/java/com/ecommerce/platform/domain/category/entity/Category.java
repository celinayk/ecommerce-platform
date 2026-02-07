package com.ecommerce.platform.domain.category.entity;

import com.ecommerce.platform.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "categories")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 100)
  private String name;

  @Column(length = 500)
  private String description;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "parent_id")
  private Category parent;

  @Builder
  public Category(String name, String description, Category parent) {
    this.name = name;
    this.description = description;
    this.parent = parent;
  }

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
