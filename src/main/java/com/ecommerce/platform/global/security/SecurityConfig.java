package com.ecommerce.platform.global.security;

import com.ecommerce.platform.global.common.response.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final ObjectMapper objectMapper;

  /**
   * 인증 없이 접근 가능한 경로
   */
  public static final String[] PERMIT_ALL_PATHS = {
      "/api/users/signup",
      "/api/users/login",
      "/api/users/logout",
      "/api/products/**",
      "/api/categories/**"
  };

  /**
   * 비밀번호 암호화 인코더
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  /**
   * 인증 관리자 - 로그인 처리에 사용
   */
  @Bean
  public AuthenticationManager authenticationManager(
      AuthenticationConfiguration authenticationConfiguration
  ) throws Exception {
    return authenticationConfiguration.getAuthenticationManager();
  }

  /**
   * SecurityContext를 HTTP 세션에 저장/로드하는 Repository
   */
  @Bean
  public SecurityContextRepository securityContextRepository() {
    return new HttpSessionSecurityContextRepository();
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        // CSRF 비활성화 (REST API)
        .csrf(csrf -> csrf.disable())

        // SecurityContext 저장소 설정 (명시적 지정)
        .securityContext(context -> context
            .securityContextRepository(securityContextRepository())
        )

        // 세션 관리 설정
        .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
            .maximumSessions(1)                // 동시 세션 1개 제한
            .maxSessionsPreventsLogin(false)   // 새 로그인 시 기존 세션 만료
        )

        // 요청 권한 설정
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(PERMIT_ALL_PATHS).permitAll()
            .anyRequest().authenticated()
        )

        // 폼 로그인 비활성화 (REST API)
        .formLogin(form -> form.disable())

        // HTTP Basic 비활성화
        .httpBasic(basic -> basic.disable())

        // 인증/인가 예외 처리
        .exceptionHandling(ex -> ex
            .authenticationEntryPoint((request, response, authException) -> {
              response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
              response.setContentType("application/json;charset=UTF-8");
              ApiResponse<?> errorResponse = ApiResponse.error("UNAUTHORIZED", "인증이 필요합니다.");
              response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
            })
            .accessDeniedHandler((request, response, accessDeniedException) -> {
              response.setStatus(HttpServletResponse.SC_FORBIDDEN);
              response.setContentType("application/json;charset=UTF-8");
              ApiResponse<?> errorResponse = ApiResponse.error("FORBIDDEN", "접근 권한이 없습니다.");
              response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
            })
        );

    return http.build();
  }
}