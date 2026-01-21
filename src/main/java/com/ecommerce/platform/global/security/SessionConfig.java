package com.ecommerce.platform.global.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * Redis 세션 설정
 * - 세션 만료 시간: 30분 (1800초)
 * - 세션 직렬화: Java Serialization
 */
@Configuration
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 1800)
public class SessionConfig {

  @Bean
  public RedisSerializer<Object> springSessionDefaultRedisSerializer() {
    return RedisSerializer.java();
  }
}