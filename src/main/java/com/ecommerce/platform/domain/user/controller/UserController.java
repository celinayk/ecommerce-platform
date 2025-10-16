package com.ecommerce.platform.domain.user.controller;

import com.ecommerce.platform.domain.user.dto.UserLoginRequest;
import com.ecommerce.platform.domain.user.dto.UserResponse;
import com.ecommerce.platform.domain.user.dto.UserSignupRequest;
import com.ecommerce.platform.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  // 회원가입
  @PostMapping("/signup")
  public ResponseEntity<UserResponse> signup(@RequestBody UserSignupRequest request) {
    UserResponse response = userService.signup(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  // 로그인
  @PostMapping("/login")
  public ResponseEntity<UserResponse> login(@RequestBody UserLoginRequest request) {
    UserResponse response = userService.login(request);
    return ResponseEntity.ok(response);
  }

  // ID로 회원 조회
  @GetMapping("/{id}")
  public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
    UserResponse response = userService.findById(id);
    return ResponseEntity.ok(response);
  }

  // 이메일로 회원 조회
  @GetMapping("/email/{email}")
  public ResponseEntity<UserResponse> getUserByEmail(@PathVariable String email) {
    UserResponse response = userService.findByEmail(email);
    return ResponseEntity.ok(response);
  }

  // 전체 회원 조회
  @GetMapping
  public ResponseEntity<List<UserResponse>> getAllUsers() {
    List<UserResponse> responses = userService.findAllUser();
    return ResponseEntity.ok(responses);
  }
}