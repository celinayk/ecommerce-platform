package com.ecommerce.platform.global.security;

import com.ecommerce.platform.domain.user.entity.Role;
import com.ecommerce.platform.domain.user.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;

/**
 * Spring Security가 사용하는 UserDetails 구현체
 * User 엔티티를 Spring Security가 이해할 수 있는 형태로 변환
 */
@Getter
public class CustomUserDetails implements UserDetails, Serializable {

  private static final long serialVersionUID = 1L;

  private final Long userId;
  private final String email;
  private final String password;
  private final String name;
  private final Role role;

  public CustomUserDetails(Long userId, String email, String password, String name, Role role) {
    this.userId = userId;
    this.email = email;
    this.password = password;
    this.name = name;
    this.role = role;
  }

  public static CustomUserDetails from(User user) {
    return new CustomUserDetails(
        user.getId(),
        user.getEmail(),
        user.getPassword(),
        user.getName(),
        user.getRole()
    );
  }

  /**
   * 권한 반환 (ROLE_ 접두사 자동 추가됨)
   */
  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return Collections.singletonList(
        new SimpleGrantedAuthority("ROLE_" + role.name())
    );
  }

  /**
   * 비밀번호 반환
   */
  @Override
  public String getPassword() {
    return password;
  }

  /**
   * 사용자명 반환 (이메일 사용)
   */
  @Override
  public String getUsername() {
    return email;
  }

  /**
   * 계정 만료 여부
   */
  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  /**
   * 계정 잠김 여부
   */
  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  /**
   * 비밀번호 만료 여부
   */
  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  /**
   * 계정 활성화 여부
   */
  @Override
  public boolean isEnabled() {
    return true;
  }
}