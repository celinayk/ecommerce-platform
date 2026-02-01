package com.ecommerce.platform.domain.user.entity;

import com.ecommerce.platform.domain.common.BaseEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class User extends BaseEntity {

  private Long id;
  private String email;
  private String password;
  private String name;
  private Role role = Role.CUSTOMER;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  @Builder // 빌더 패턴으로 객체를 생성할 수 있게 합니다.
  public User(
      String name,
      String email,
      String password
  ) {
    this.name = name;
    this.email = email;
    this.password = password;
    this.role = Role.CUSTOMER; // 기본값 설정
  }

}
