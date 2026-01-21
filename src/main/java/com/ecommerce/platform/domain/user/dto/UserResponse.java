package com.ecommerce.platform.domain.user.dto;

import com.ecommerce.platform.domain.user.entity.Role;
import com.ecommerce.platform.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UserResponse {

  private Long id;
  private String email;
  private String name;
  private Role role;

  public static UserResponse from(User user) {
    return UserResponse.builder()
        .id(user.getId())
        .email(user.getEmail())
        .name(user.getName())
        .role(user.getRole())
        .build();
  }
}