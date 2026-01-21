package com.ecommerce.platform.global.security;

import com.ecommerce.platform.domain.user.entity.User;
import com.ecommerce.platform.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Spring Security가 사용자 인증 시 호출하는 서비스
 * 이메일(username)로 사용자를 조회하여 UserDetails 반환
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;

  /**
   * Spring Security가 인증 시 호출
   * @param username 실제로는 이메일 주소
   * @return UserDetails 구현체
   * @throws UsernameNotFoundException 사용자를 찾을 수 없을 때
   */
  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = userRepository.findByEmail(username)
        .orElseThrow(() -> new UsernameNotFoundException(
            "사용자를 찾을 수 없습니다: " + username
        ));

    return CustomUserDetails.from(user);
  }
}