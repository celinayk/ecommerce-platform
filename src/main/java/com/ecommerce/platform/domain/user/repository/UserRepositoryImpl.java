package com.ecommerce.platform.domain.user.repository;

import com.ecommerce.platform.domain.user.entity.User;
import com.ecommerce.platform.domain.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * UserRepository의 MyBatis 구현체
 * 나중에 JPA로 전환 시 이 클래스만 삭제하고 JpaRepository로 교체
 */
@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

  private final UserMapper userMapper;

  @Override
  public User save(User user) {
    if (user.getId() == null) {
      // 새로운 회원 저장
      userMapper.insert(user);
    } else {
      // 기존 회원 수정
      userMapper.update(user);
    }
    return user;
  }

  @Override
  public Optional<User> findById(Long id) {
    return Optional.ofNullable(userMapper.findById(id));
  }

  @Override
  public Optional<User> findByEmail(String email) {
    return Optional.ofNullable(userMapper.findByEmail(email));
  }

  @Override
  public List<User> findAll() {
    return userMapper.findAll();
  }

  @Override
  public boolean existsById(Long id) {
    return userMapper.findById(id) != null;
  }

  @Override
  public void deleteById(Long id) {
    userMapper.deleteById(id);
  }
}