package com.ecommerce.platform.domain.user.repository;

import com.ecommerce.platform.domain.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class UserRepositoryTest {

  @Autowired
  private UserRepository userRepository;

  @Test
  @Transactional
  void 회원저장() {
    // given
    User user = new User();
    user.setEmail("test@example.com");
    user.setPassword("password123");
    user.setName("testuser");

    // when
    User savedUser = userRepository.save(user);
    User findUser = userRepository.findById(savedUser.getId()).get();

    // then
    assertThat(findUser.getId()).isEqualTo(user.getId());
    assertThat(findUser.getEmail()).isEqualTo(user.getEmail());
    assertThat(findUser).isEqualTo(user);
  }

  @Test
  @Transactional
  void 이메일로_회원조회() {
    // given
    User user = new User();
    user.setEmail("find@example.com");
    user.setPassword("password123");
    user.setName("finduser");
    userRepository.save(user);

    // when
    User foundUser = userRepository.findByEmail("find@example.com");

    // then
    assertThat(foundUser).isNotNull();
    assertThat(foundUser.getEmail()).isEqualTo("find@example.com");
    assertThat(foundUser.getName()).isEqualTo("finduser");
  }

  @Test
  @Transactional
  void 존재하지않는_이메일조회() {
    // when
    User foundUser = userRepository.findByEmail("nonexistent@example.com");

    // then
    assertThat(foundUser).isNull();
  }

  @Test
  @Transactional
  void 전체회원조회() {
    // given
    User user1 = new User();
    user1.setEmail("user1@example.com");
    user1.setPassword("password1");
    user1.setName("user1");

    User user2 = new User();
    user2.setEmail("user2@example.com");
    user2.setPassword("password2");
    user2.setName("user2");

    userRepository.save(user1);
    userRepository.save(user2);

    // when
    List<User> users = userRepository.findAll();

    // then
    assertThat(users).hasSizeGreaterThanOrEqualTo(2);
  }
}
