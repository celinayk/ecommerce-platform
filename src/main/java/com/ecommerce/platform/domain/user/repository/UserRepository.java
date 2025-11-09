package com.ecommerce.platform.domain.user.repository;

import com.ecommerce.platform.domain.user.entity.User;

import java.util.List;
import java.util.Optional;

/**
 * User Repository 인터페이스
 * MyBatis -> JPA 전환 시 Service 코드 변경 없이 구현체만 교체 가능
 */
public interface UserRepository {

  /**
   * 회원 저장
   */
  User save(User user);

  /**
   * ID로 회원 조회
   */
  Optional<User> findById(Long id);

  /**
   * 이메일로 회원 조회
   */
  Optional<User> findByEmail(String email);

  /**
   * 전체 회원 조회
   */
  List<User> findAll();

  /**
   * 회원 존재 여부 확인
   */
  boolean existsById(Long id);

  /**
   * 회원 삭제
   */
  void deleteById(Long id);
}
