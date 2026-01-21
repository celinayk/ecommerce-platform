package com.ecommerce.platform.domain.user.service;

import com.ecommerce.platform.domain.user.dto.UserLoginRequest;
import com.ecommerce.platform.domain.user.dto.UserResponse;
import com.ecommerce.platform.domain.user.dto.UserSignupRequest;
import com.ecommerce.platform.domain.user.entity.User;
import com.ecommerce.platform.domain.user.exception.UserException;
import com.ecommerce.platform.domain.user.repository.UserRepository;
import com.ecommerce.platform.global.common.response.ErrorCode;
import com.ecommerce.platform.global.security.CustomUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;
  private final SecurityContextRepository securityContextRepository;

  // 회원가입
  @Transactional
  public UserResponse signup(UserSignupRequest request) {
    // 이메일 중복 체크
    userRepository.findByEmail(request.getEmail())
        .ifPresent(user -> {
          throw new UserException(ErrorCode.EMAIL_ALREADY_EXISTS);
        });

    // 비밀번호 암호화
    String encodedPassword = passwordEncoder.encode(request.getPassword());

    // DTO -> Entity 변환
    User user = User.builder()
        .email(request.getEmail())
        .password(encodedPassword)
        .name(request.getName())
        .build();

    userRepository.save(user);
    return UserResponse.from(user);
  }

  // ID로 회원 조회
  public UserResponse findById(Long id) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));
    return UserResponse.from(user);
  }

  // 이메일로 회원 조회
  public UserResponse findByEmail(String email) {
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));
    return UserResponse.from(user);
  }

  // 전체 회원 조회
  public List<UserResponse> findAllUser() {
    return userRepository.findAll().stream()
        .map(UserResponse::from)
        .collect(Collectors.toList());
  }

  // 로그인
  public UserResponse login(UserLoginRequest request,
                            HttpServletRequest httpRequest,
                            HttpServletResponse httpResponse) {
    log.info("========== 로그인 시작 ==========");
    log.info("로그인 요청 이메일: {}", request.getEmail());

    // 1. Spring Security 인증
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            request.getEmail(),
            request.getPassword()
        )
    );
    log.info("Spring Security 인증 성공: {}", authentication.getName());
    log.info("인증된 권한: {}", authentication.getAuthorities());

    // 2. 새로운 SecurityContext 생성 및 인증 정보 설정
    SecurityContext context = SecurityContextHolder.createEmptyContext();
    context.setAuthentication(authentication);
    SecurityContextHolder.setContext(context);
    log.info("SecurityContext 생성 및 설정 완료");

    // 3. SecurityContext를 세션에 명시적으로 저장 (Redis에 저장됨)
    securityContextRepository.saveContext(context, httpRequest, httpResponse);
    log.info("SecurityContext를 세션(Redis)에 저장 완료");

    // 4. 세션 정보 로깅
    HttpSession session = httpRequest.getSession(false);
    if (session != null) {
      log.info("세션 ID: {}", session.getId());
      log.info("세션 생성 시간: {}", new java.util.Date(session.getCreationTime()));
      log.info("세션 최대 유효 시간: {}초", session.getMaxInactiveInterval());
    }

    // 5. 인증된 사용자 정보 반환
    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    log.info("로그인 완료 - 사용자: {} (ID: {})", userDetails.getName(), userDetails.getUserId());
    log.info("========== 로그인 종료 ==========");

    return UserResponse.builder()
        .id(userDetails.getUserId())
        .email(userDetails.getEmail())
        .name(userDetails.getName())
        .role(userDetails.getRole())
        .build();
  }

  // 로그아웃
  public void logout(HttpServletRequest request,
                     HttpServletResponse response,
                     Authentication authentication) {
    log.info("========== 로그아웃 시작 ==========");

    if (authentication != null) {
      log.info("로그아웃 사용자: {}", authentication.getName());
    }

    // 1. SecurityContext 초기화
    SecurityContextHolder.clearContext();
    log.info("SecurityContext 초기화 완료");

    // 2. 세션 무효화 (Redis에서 삭제됨)
    HttpSession session = request.getSession(false);
    if (session != null) {
      log.info("세션 ID: {} 무효화", session.getId());
      session.invalidate();
      log.info("세션 무효화 완료 (Redis에서 삭제됨)");
    }

    log.info("========== 로그아웃 종료 ==========");
  }

  // 로그인 상태 확인
  public UserResponse getLoginStatus(Authentication authentication) {
    if (authentication == null || !authentication.isAuthenticated()) {
      throw new UserException(ErrorCode.USER_NOT_FOUND);
    }

    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    return UserResponse.builder()
        .id(userDetails.getUserId())
        .email(userDetails.getEmail())
        .name(userDetails.getName())
        .role(userDetails.getRole())
        .build();
  }
}