package com.ecommerce.platform.domain.user.repository;

import com.ecommerce.platform.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

  // 이메일로 회원 찾기
  User findByEmail(String email);


}
