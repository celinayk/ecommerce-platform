package com.ecommerce.platform.domain.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserSignupRequest {

  private String email;
  private String password;
  private String name;
}