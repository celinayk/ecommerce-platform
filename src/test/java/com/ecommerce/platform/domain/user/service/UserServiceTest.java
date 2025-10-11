package com.ecommerce.platform.domain.user.service;

import com.ecommerce.platform.domain.user.entity.User;
import com.ecommerce.platform.domain.user.repository.UserRepository;
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
  @DisplayName("회원가입을 할 수 있다")
  void signup() {
    // given
    User user = User.createUser("test@example.com", "password123", "테스트유저");

    // when
    User savedUser = userService.signup(user);

    // then
    assertThat(savedUser.getId()).isEqualTo(user.getId());
    assertThat(savedUser.getEmail()).isEqualTo("test@example.com");
    assertThat(savedUser.getName()).isEqualTo("테스트유저");
  }

  @Test
  @DisplayName("중복된 이메일로 회원가입시 예외가 발생한다")
  void signup_duplicateEmail() {
    // given
    User user1 = User.createUser("duplicate@example.com", "password123", "유저1");
    userService.signup(user1);

    User user2 = User.createUser("duplicate@example.com", "password456", "유저2");

    // when & then
    assertThatThrownBy(() -> userService.signup(user2))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("이미 존재하는 이메일입니다.");
  }



  @Test
  @DisplayName("전체 회원을 조회할 수 있다")
  void findAllUser() {
    // given
    User user1 = User.createUser("user1@example.com", "password1", "유저1");
    User user2 = User.createUser("user2@example.com", "password2", "유저2");
    userRepository.save(user1);
    userRepository.save(user2);

    // when
    List<User> users = userService.findAllUser();

    // then
    assertThat(users).hasSizeGreaterThanOrEqualTo(2);
  }
}