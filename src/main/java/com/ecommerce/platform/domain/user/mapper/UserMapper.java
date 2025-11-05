package com.ecommerce.platform.domain.user.mapper;

import com.ecommerce.platform.domain.user.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {

  // 회원 저장
  void insert(User user);

  // ID로 회원 조회
  User findById(@Param("id") Long id);

  // 이메일로 회원 조회
  User findByEmail(@Param("email") String email);

  // 전체 회원 조회
  List<User> findAll();

  // 회원 수정
  void update(User user);

  // 회원 삭제
  void deleteById(@Param("id") Long id);
}