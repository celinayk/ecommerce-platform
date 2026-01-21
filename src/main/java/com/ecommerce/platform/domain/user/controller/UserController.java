package com.ecommerce.platform.domain.user.controller;

import com.ecommerce.platform.domain.user.dto.UserLoginRequest;
import com.ecommerce.platform.domain.user.dto.UserResponse;
import com.ecommerce.platform.domain.user.dto.UserSignupRequest;
import com.ecommerce.platform.domain.user.service.UserService;
import com.ecommerce.platform.global.common.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  // 회원가입
  @PostMapping("/signup")
  public ResponseEntity<ApiResponse<UserResponse>> signup(@Valid @RequestBody UserSignupRequest request) {
    UserResponse response = userService.signup(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(response));
  }

  // 로그인
  @PostMapping("/login")
  public ResponseEntity<ApiResponse<UserResponse>> login(
      @Valid @RequestBody UserLoginRequest request,
      HttpServletRequest httpRequest,
      HttpServletResponse httpResponse
  ) {
    UserResponse response = userService.login(request, httpRequest, httpResponse);
    return ResponseEntity.ok(ApiResponse.ok(response));
  }

  // 로그아웃
  @PostMapping("/logout")
  public ResponseEntity<ApiResponse<Void>> logout(
      HttpServletRequest request,
      HttpServletResponse response,
      Authentication authentication
  ) {
    userService.logout(request, response, authentication);
    return ResponseEntity.ok(ApiResponse.ok());
  }

  // 로그인 상태 확인
  @GetMapping("/status")
  public ResponseEntity<ApiResponse<UserResponse>> checkStatus(Authentication authentication) {
    UserResponse response = userService.getLoginStatus(authentication);
    return ResponseEntity.ok(ApiResponse.ok(response));
  }

  // ID로 회원 조회
  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long id) {
    UserResponse response = userService.findById(id);
    return ResponseEntity.ok(ApiResponse.ok(response));
  }

  // 이메일로 회원 조회
  @GetMapping("/email/{email}")
  public ResponseEntity<ApiResponse<UserResponse>> getUserByEmail(@PathVariable String email) {
    UserResponse response = userService.findByEmail(email);
    return ResponseEntity.ok(ApiResponse.ok(response));
  }

  // 전체 회원 조회
  @GetMapping
  public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
    List<UserResponse> responses = userService.findAllUser();
    return ResponseEntity.ok(ApiResponse.ok(responses));
  }
}