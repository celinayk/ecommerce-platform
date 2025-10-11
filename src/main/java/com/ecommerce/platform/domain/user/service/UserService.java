package com.ecommerce.platform.domain.user.service;

import com.ecommerce.platform.domain.user.entity.User;
import com.ecommerce.platform.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/*
 * @Transactional(readOnly = true): 데이터의 변경이 없는 읽기 전용 메서드에 사용
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

  private final UserRepository userRepository;

  // 회원가입
  @Transactional
  public User signup(User user) {
    // 이메일 중복 체크
    if (userRepository.findByEmail(user.getEmail()) != null) {
      throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
    }
    return userRepository.save(user);
  }

  // 이메일로 회원 조회
  public User findByEmail(String email) {
    User user = userRepository.findByEmail(email);
    if (user == null) {
      throw new IllegalArgumentException("존재하지 않는 회원입니다.");
    }
    return user;
  }

  // 전체 회원 조회
  public List<User> findAllUser() {
    return userRepository.findAll();
  }
}
