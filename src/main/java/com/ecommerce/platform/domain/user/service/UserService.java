package com.ecommerce.platform.domain.user.service;

import com.ecommerce.platform.domain.user.dto.UserLoginRequest;
import com.ecommerce.platform.domain.user.dto.UserResponse;
import com.ecommerce.platform.domain.user.dto.UserSignupRequest;
import com.ecommerce.platform.domain.user.entity.User;
import com.ecommerce.platform.domain.user.mapper.UserMapper;
import com.ecommerce.platform.global.common.exception.CustomException;
import com.ecommerce.platform.global.common.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserMapper userMapper;

  // 회원가입
  @Transactional
  public UserResponse signup(UserSignupRequest request) {
    // 이메일 중복 체크
    if (userMapper.findByEmail(request.getEmail()) != null) {
      throw new CustomException(ErrorCode.EMAIL_ALREADY_EXISTS);
    }

    // DTO -> Entity 변환
    User user = new User();
    user.setEmail(request.getEmail());
    user.setPassword(request.getPassword());
    user.setName(request.getName());

    userMapper.insert(user);
    return UserResponse.from(user);
  }

  // ID로 회원 조회
  public UserResponse findById(Long id) {
    User user = userMapper.findById(id);
    if (user == null) {
      throw new CustomException(ErrorCode.USER_NOT_FOUND);
    }
    return UserResponse.from(user);
  }

  // 이메일로 회원 조회
  public UserResponse findByEmail(String email) {
    User user = userMapper.findByEmail(email);
    if (user == null) {
      throw new CustomException(ErrorCode.USER_NOT_FOUND);
    }
    return UserResponse.from(user);
  }

  // 전체 회원 조회
  public List<UserResponse> findAllUser() {
    return userMapper.findAll().stream()
        .map(UserResponse::from)
        .collect(Collectors.toList());
  }

  // 로그인
  public UserResponse login(UserLoginRequest request) {
    // 이메일로 사용자 조회
    User user = userMapper.findByEmail(request.getEmail());
    if (user == null) {
      throw new CustomException(ErrorCode.PASSWORD_UNMATCHED);
    }

    // 비밀번호 확인 (현재는 평문 비교, 나중에 암호화 추가 예정)
    if (!user.getPassword().equals(request.getPassword())) {
      throw new CustomException(ErrorCode.PASSWORD_UNMATCHED);
    }

    return UserResponse.from(user);
  }
}
