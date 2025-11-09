package com.ecommerce.platform.domain.user.service;

import com.ecommerce.platform.domain.user.dto.UserResponse;
import com.ecommerce.platform.domain.user.dto.UserSignupRequest;
import com.ecommerce.platform.domain.user.entity.User;
import com.ecommerce.platform.domain.user.repository.UserRepository;
import com.ecommerce.platform.global.common.exception.CustomException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class UserServiceTest {

  @Autowired
  UserService userService;
  @Autowired
  UserRepository userRepository;

  @Test
  void 회원가입() {
    // given
    UserSignupRequest request = new UserSignupRequest();
    request.setEmail("test@example.com");
    request.setPassword("password123");
    request.setName("테스트유저");

    // when
    UserResponse response = userService.signup(request);

    // then
    assertThat(response.getEmail()).isEqualTo("test@example.com");
    assertThat(response.getName()).isEqualTo("테스트유저");
  }

  @Test
  @DisplayName("중복된 이메일로 회원가입시 예외가 발생한다")
  void 중복회원예외() {
    // given
    UserSignupRequest request1 = new UserSignupRequest();
    request1.setEmail("duplicate@example.com");
    request1.setPassword("password123");
    request1.setName("유저1");
    userService.signup(request1);

    UserSignupRequest request2 = new UserSignupRequest();
    request2.setEmail("duplicate@example.com");
    request2.setPassword("password456");
    request2.setName("유저2");

    // when & then
    assertThatThrownBy(() -> userService.signup(request2))
        .isInstanceOf(CustomException.class)
        .hasMessage("이미 존재하는 이메일입니다.");
  }

  @Test
  @DisplayName("전체 회원을 조회할 수 있다")
  void findAllUser() {
    // given
    User user1 = User.builder()
        .email("user1@example.com")
        .password("password1")
        .name("유저1")
        .build();

    User user2 = User.builder()
        .email("user2@example.com")
        .password("password2")
        .name("유저2")
        .build();

    userRepository.save(user1);
    userRepository.save(user2);

    // when
    List<UserResponse> responses = userService.findAllUser();

    // then
    assertThat(responses).hasSizeGreaterThanOrEqualTo(2);
  }
}