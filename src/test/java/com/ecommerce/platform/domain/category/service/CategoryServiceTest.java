package com.ecommerce.platform.domain.category.service;

import com.ecommerce.platform.domain.category.entity.Category;
import com.ecommerce.platform.domain.category.repository.CategoryRepository;
import com.ecommerce.platform.global.common.exception.CustomException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class CategoryServiceTest {

  @Autowired
  CategoryService categoryService;

  @Autowired
  CategoryRepository categoryRepository;

  @Test
  @DisplayName("카테고리를 생성할 수 있다")
  void createCategory() {
    // given
    Category category = Category.builder()
        .name("전자기기")
        .description("전자기기 카테고리")
        .build();

    // when
    Category savedCategory = categoryService.createCategory(category);

    // then
    assertThat(savedCategory.getId()).isNotNull();
    assertThat(savedCategory.getName()).isEqualTo("전자기기");
    assertThat(savedCategory.getDescription()).isEqualTo("전자기기 카테고리");
  }

  @Test
  @DisplayName("중복된 카테고리 이름으로 생성시 예외가 발생한다")
  void createDuplicateCategory() {
    // given
    Category category1 = Category.builder()
        .name("패션")
        .description("패션 카테고리")
        .build();
    categoryService.createCategory(category1);

    Category category2 = Category.builder()
        .name("패션")
        .description("다른 패션 카테고리")
        .build();

    // when & then
    assertThatThrownBy(() -> categoryService.createCategory(category2))
        .isInstanceOf(CustomException.class);
  }

  @Test
  @DisplayName("하위 카테고리를 생성할 수 있다")
  void createSubCategory() {
    // given
    Category parent = Category.builder()
        .name("전자기기")
        .description("전자기기 카테고리")
        .build();
    Category savedParent = categoryService.createCategory(parent);

    Category child = Category.builder()
        .name("노트북")
        .description("노트북 카테고리")
        .parent(savedParent)
        .build();

    // when
    Category savedChild = categoryService.createCategory(child);

    // then
    assertThat(savedChild.getParent()).isNotNull();
    assertThat(savedChild.getParent().getId()).isEqualTo(savedParent.getId());
  }

  @Test
  @DisplayName("ID로 카테고리를 조회할 수 있다")
  void findById() {
    // given
    Category category = Category.builder()
        .name("도서")
        .description("도서 카테고리")
        .build();
    Category savedCategory = categoryService.createCategory(category);

    // when
    Category foundCategory = categoryService.findById(savedCategory.getId());

    // then
    assertThat(foundCategory.getName()).isEqualTo("도서");
  }

  @Test
  @DisplayName("존재하지 않는 ID로 조회시 예외가 발생한다")
  void findByIdNotFound() {
    // when & then
    assertThatThrownBy(() -> categoryService.findById(999L))
        .isInstanceOf(CustomException.class);
  }

  @Test
  @DisplayName("이름으로 카테고리를 조회할 수 있다")
  void findByName() {
    // given
    Category category = Category.builder()
        .name("스포츠")
        .description("스포츠 카테고리")
        .build();
    categoryService.createCategory(category);

    // when
    Category foundCategory = categoryService.findByName("스포츠");

    // then
    assertThat(foundCategory.getName()).isEqualTo("스포츠");
  }

  @Test
  @DisplayName("전체 카테고리를 조회할 수 있다")
  void findAll() {
    // given
    Category category1 = Category.builder()
        .name("가전제품")
        .description("가전제품 카테고리")
        .build();
    Category category2 = Category.builder()
        .name("가구")
        .description("가구 카테고리")
        .build();

    categoryService.createCategory(category1);
    categoryService.createCategory(category2);

    // when
    List<Category> categories = categoryService.findAll();

    // then
    assertThat(categories).hasSizeGreaterThanOrEqualTo(2);
  }

  @Test
  @DisplayName("최상위 카테고리만 조회할 수 있다")
  void findRootCategories() {
    // given
    Category parent = Category.builder()
        .name("의류")
        .description("의류 카테고리")
        .build();
    Category savedParent = categoryService.createCategory(parent);

    Category child = Category.builder()
        .name("남성의류")
        .description("남성의류 카테고리")
        .parent(savedParent)
        .build();
    categoryService.createCategory(child);

    // when
    List<Category> rootCategories = categoryService.findRootCategories();

    // then
    assertThat(rootCategories).allMatch(c -> c.getParent() == null);
  }

  @Test
  @DisplayName("특정 부모의 하위 카테고리를 조회할 수 있다")
  void findByParentId() {
    // given
    Category parent = Category.builder()
        .name("식품")
        .description("식품 카테고리")
        .build();
    Category savedParent = categoryService.createCategory(parent);

    Category child1 = Category.builder()
        .name("과일")
        .description("과일 카테고리")
        .parent(savedParent)
        .build();
    Category child2 = Category.builder()
        .name("채소")
        .description("채소 카테고리")
        .parent(savedParent)
        .build();

    categoryService.createCategory(child1);
    categoryService.createCategory(child2);

    // when
    List<Category> children = categoryService.findByParentId(savedParent.getId());

    // then
    assertThat(children).hasSize(2);
    assertThat(children).extracting("name").containsExactlyInAnyOrder("과일", "채소");
  }

  @Test
  @DisplayName("카테고리를 수정할 수 있다")
  void updateCategory() {
    // given
    Category category = Category.builder()
        .name("원래이름")
        .description("원래설명")
        .build();
    Category savedCategory = categoryService.createCategory(category);

    Category updateCategory = Category.builder()
        .name("변경된이름")
        .description("변경된설명")
        .build();

    // when
    Category updatedCategory = categoryService.updateCategory(savedCategory.getId(), updateCategory);

    // then
    assertThat(updatedCategory.getName()).isEqualTo("변경된이름");
    assertThat(updatedCategory.getDescription()).isEqualTo("변경된설명");
  }

  @Test
  @DisplayName("카테고리를 삭제할 수 있다")
  void deleteCategory() {
    // given
    Category category = Category.builder()
        .name("삭제할카테고리")
        .description("삭제할 카테고리")
        .build();
    Category savedCategory = categoryService.createCategory(category);

    // when
    categoryService.deleteCategory(savedCategory.getId());

    // then
    assertThatThrownBy(() -> categoryService.findById(savedCategory.getId()))
        .isInstanceOf(CustomException.class);
  }

  @Test
  @DisplayName("하위 카테고리가 있는 카테고리는 삭제할 수 없다")
  void deleteParentCategoryWithChildren() {
    // given
    Category parent = Category.builder()
        .name("부모카테고리")
        .description("부모 카테고리")
        .build();
    Category savedParent = categoryService.createCategory(parent);

    Category child = Category.builder()
        .name("자식카테고리")
        .description("자식 카테고리")
        .parent(savedParent)
        .build();
    categoryService.createCategory(child);

    // when & then
    assertThatThrownBy(() -> categoryService.deleteCategory(savedParent.getId()))
        .isInstanceOf(CustomException.class);
  }
}