package com.ecommerce.platform.domain.user.controller;

import com.ecommerce.platform.domain.user.dto.UserSignupRequest;
import com.ecommerce.platform.domain.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserControllerTest {

  @Autowired
  MockMvc mockMvc;
  @Autowired
  ObjectMapper objectMapper;
  @Autowired
  UserRepository userRepository;

  @Test
  void 회원가입_성공() throws Exception {
    // given
    UserSignupRequest request = new UserSignupRequest();
    request.setEmail("test@example.com");
    request.setPassword("password123");
    request.setName("테스트유저");

    // when & then
    mockMvc.perform(post("/api/users/signup")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.email").value("test@example.com"))
        .andExpect(jsonPath("$.name").value("테스트유저"));
  }

  @Test
  void 회원가입_이메일필수검증() throws Exception {
    // given
    UserSignupRequest request = new UserSignupRequest();
    request.setEmail(""); // 빈 이메일
    request.setPassword("password123");
    request.setName("테스트유저");

    // when & then
    mockMvc.perform(post("/api/users/signup")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
        .andExpect(jsonPath("$.errors.email").exists());
  }

  @Test
  void 회원가입_이메일형식검증() throws Exception {
    // given
    UserSignupRequest request = new UserSignupRequest();
    request.setEmail("invalid-email"); // 잘못된 이메일 형식
    request.setPassword("password123");
    request.setName("테스트유저");

    // when & then
    mockMvc.perform(post("/api/users/signup")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
        .andExpect(jsonPath("$.errors.email").exists());
  }

  @Test
  void 회원가입_비밀번호필수검증() throws Exception {
    // given
    UserSignupRequest request = new UserSignupRequest();
    request.setEmail("test@example.com");
    request.setPassword(""); // 빈 비밀번호
    request.setName("테스트유저");

    // when & then
    mockMvc.perform(post("/api/users/signup")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
        .andExpect(jsonPath("$.errors.password").exists());
  }

  @Test
  void 회원가입_비밀번호길이검증() throws Exception {
    // given
    UserSignupRequest request = new UserSignupRequest();
    request.setEmail("test@example.com");
    request.setPassword("short"); // 8자 미만
    request.setName("테스트유저");

    // when & then
    mockMvc.perform(post("/api/users/signup")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
        .andExpect(jsonPath("$.errors.password").exists());
  }

  @Test
  void 회원가입_이름필수검증() throws Exception {
    // given
    UserSignupRequest request = new UserSignupRequest();
    request.setEmail("test@example.com");
    request.setPassword("password123");
    request.setName(""); // 빈 이름

    // when & then
    mockMvc.perform(post("/api/users/signup")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
        .andExpect(jsonPath("$.errors.name").exists());
  }

  @Test
  void 로그인_이메일필수검증() throws Exception {
    // given
    String requestBody = """
        {
          "email": "",
          "password": "password123"
        }
        """;

    // when & then
    mockMvc.perform(post("/api/users/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
        .andExpect(jsonPath("$.errors.email").exists());
  }

  @Test
  void 로그인_이메일형식검증() throws Exception {
    // given
    String requestBody = """
        {
          "email": "invalid-email",
          "password": "password123"
        }
        """;

    // when & then
    mockMvc.perform(post("/api/users/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
        .andExpect(jsonPath("$.errors.email").exists());
  }

  @Test
  void 로그인_비밀번호필수검증() throws Exception {
    // given
    String requestBody = """
        {
          "email": "test@example.com",
          "password": ""
        }
        """;

    // when & then
    mockMvc.perform(post("/api/users/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
        .andExpect(jsonPath("$.errors.password").exists());
  }
}