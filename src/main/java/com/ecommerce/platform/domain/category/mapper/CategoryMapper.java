package com.ecommerce.platform.domain.category.mapper;

import com.ecommerce.platform.domain.category.entity.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CategoryMapper {

  // 카테고리 저장
  void insert(Category category);

  // ID로 카테고리 조회
  Category findById(@Param("id") Long id);

  // 이름으로 카테고리 조회
  Category findByName(@Param("name") String name);

  // 전체 카테고리 조회
  List<Category> findAll();

  // 부모 카테고리로 자식 카테고리 조회
  List<Category> findByParentId(@Param("parentId") Long parentId);

  // 최상위 카테고리 조회 (parent_id가 NULL인 카테고리)
  List<Category> findRootCategories();

  // 카테고리 수정
  void update(Category category);

  // 카테고리 삭제
  void deleteById(@Param("id") Long id);
}