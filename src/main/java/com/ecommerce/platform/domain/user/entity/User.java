package com.ecommerce.platform.domain.user.entity;

import com.ecommerce.platform.domain.common.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class User extends BaseEntity {

  private Long id;
  private String email;
  private String password;
  private String name;
  private Role role = Role.USER;

}
