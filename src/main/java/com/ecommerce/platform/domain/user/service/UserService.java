package com.ecommerce.platform.domain.user.service;

import com.ecommerce.platform.domain.user.dto.UserLoginRequest;
import com.ecommerce.platform.domain.user.dto.UserResponse;
import com.ecommerce.platform.domain.user.dto.UserSignupRequest;
import com.ecommerce.platform.domain.user.entity.User;
import com.ecommerce.platform.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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
  public UserResponse signup(UserSignupRequest request) {
    // 이메일 중복 체크
    if (userRepository.findByEmail(request.getEmail()) != null) {
      throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
    }

    // DTO -> Entity 변환
    User user = new User();
    user.setEmail(request.getEmail());
    user.setPassword(request.getPassword());
    user.setName(request.getName());

    User savedUser = userRepository.save(user);
    return UserResponse.from(savedUser);
  }

  // ID로 회원 조회
  public UserResponse findById(Long id) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
    return UserResponse.from(user);
  }

  // 이메일로 회원 조회
  public UserResponse findByEmail(String email) {
    User user = userRepository.findByEmail(email);
    if (user == null) {
      throw new IllegalArgumentException("존재하지 않는 회원입니다.");
    }
    return UserResponse.from(user);
  }

  // 전체 회원 조회
  public List<UserResponse> findAllUser() {
    return userRepository.findAll().stream()
        .map(UserResponse::from)
        .collect(Collectors.toList());
  }

  // 로그인
  public UserResponse login(UserLoginRequest request) {
    // 이메일로 사용자 조회
    User user = userRepository.findByEmail(request.getEmail());
    if (user == null) {
      throw new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다.");
    }

    // 비밀번호 확인 (현재는 평문 비교, 나중에 암호화 추가 예정)
    if (!user.getPassword().equals(request.getPassword())) {
      throw new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다.");
    }

    return UserResponse.from(user);
  }
}
