package com.fleet.auth_service.domain.service;

import com.fleet.auth_service.domain.model.UserSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class RedisService {
  private final RedisTemplate<String, Object> redisTemplate;
  private static final String SESSION_PREFIX = "session:";

  @Autowired
  public RedisService(RedisTemplate<String, Object> redisTemplate) {
    this.redisTemplate = redisTemplate;
  }

  public void set(String key, Object value, Duration ttl) {
    redisTemplate.opsForValue().set(key, value, ttl.toMillis(), TimeUnit.MILLISECONDS);
  }

  public void set(String key, Object value) {
    redisTemplate.opsForValue().set(key, value);
  }

  public void saveSession(UUID userId, UserSession session, Duration ttl) {
    this.set(SESSION_PREFIX + userId, session, ttl);
  }

  public <T> Optional<T> get(String key, Class<T> targetClass) {
    Object value = redisTemplate.opsForValue().get(key);

    if (value == null) {
      return Optional.empty();
    }

    try {
      return Optional.of(targetClass.cast(value));
    } catch (ClassCastException e) {
      return Optional.empty();
    }
  }

  public void delete(String key) {
    redisTemplate.delete(key);
  }

  public Boolean hasKey(String key) {
    return redisTemplate.hasKey(key);
  }
}
