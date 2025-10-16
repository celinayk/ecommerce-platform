package com.ecommerce.platform.domain.user.dto;

import com.ecommerce.platform.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserResponse {

  private Long id;
  private String email;
  private String name;

  public static UserResponse from(User user) {
    return new UserResponse(
        user.getId(),
        user.getEmail(),
        user.getName()
    );
  }
}