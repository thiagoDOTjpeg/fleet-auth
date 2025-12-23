package com.fleet.auth_service.domain.service;

import com.fleet.auth_service.domain.model.UserSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RedisService {
  private final RedisTemplate<String, Object> redisTemplate;

  private static final String SESSION_KEY_PREFIX = "session:";
  private static final String USER_SESSIONS_INDEX_PREFIX = "user_sessions_index:";

  @Autowired
  public RedisService(RedisTemplate<String, Object> redisTemplate) {
    this.redisTemplate = redisTemplate;
  }

  public void addBlacklistJti(UUID userId, UUID tokenJti, Duration ttl) {
    String blacklistKey = "black_list:" + userId + ":" + tokenJti;

    redisTemplate.opsForValue().set(blacklistKey, true, ttl);
  }

  public void saveSession(UUID userId, UserSession session, Duration ttl) {
    String sessionKey = getSessionKey(userId, session.getSessionId());
    String indexKey = getIndexKey(userId);

    redisTemplate.opsForValue().set(sessionKey, session, ttl);

    redisTemplate.opsForSet().add(indexKey, session.getSessionId().toString());

    redisTemplate.expire(indexKey, ttl);
  }

  public void deleteSession(UUID userId, UUID sessionId) {
    String sessionKey = getSessionKey(userId, sessionId);
    String indexKey = getIndexKey(userId);

    redisTemplate.delete(sessionKey);

    redisTemplate.opsForSet().remove(indexKey, sessionId.toString());
  }

  public void deleteAllSession(UUID userId) {
    String indexKey = getIndexKey(userId);

    Set<Object> activeSessionIds = redisTemplate.opsForSet().members(indexKey);

    if (activeSessionIds != null && !activeSessionIds.isEmpty()) {
      Set<String> keysToDelete = activeSessionIds.stream()
              .map(sessionId -> getSessionKey(userId, UUID.fromString(sessionId.toString())))
              .collect(Collectors.toSet());

      redisTemplate.delete(keysToDelete);
    }

    redisTemplate.delete(indexKey);
  }

  public <T> Optional<T> get(String key, Class<T> targetClass) {
    Object value = redisTemplate.opsForValue().get(key);
    if (value == null) return Optional.empty();
    try {
      return Optional.of(targetClass.cast(value));
    } catch (ClassCastException e) {
      return Optional.empty();
    }
  }

  private String getSessionKey(UUID userId, UUID sessionId) {
    return SESSION_KEY_PREFIX + userId + ":" + sessionId;
  }

  private String getIndexKey(UUID userId) {
    return USER_SESSIONS_INDEX_PREFIX + userId;
  }
}