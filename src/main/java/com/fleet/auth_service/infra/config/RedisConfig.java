package com.fleet.auth_service.infra.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fleet.auth_service.infra.config.properties.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

  private final RedisProperties redisProperties;

  public RedisConfig(RedisProperties redisProperties) {
    this.redisProperties = redisProperties;
  }

  @Bean
  public ObjectMapper redisObjectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());

    BasicPolymorphicTypeValidator typeValidator = BasicPolymorphicTypeValidator
            .builder()
            .allowIfBaseType(Object.class)
            .build();

    objectMapper.activateDefaultTyping(
            typeValidator,
            ObjectMapper.DefaultTyping.NON_FINAL,
            JsonTypeInfo.As.PROPERTY
    );

    return objectMapper;
  }

  @Bean
  public RedisTemplate<String, Object> redisTemplate(
          RedisConnectionFactory connectionFactory,
          ObjectMapper redisObjectMapper) {

    RedisTemplate<String, Object> template = new RedisTemplate<>();
    template.setConnectionFactory(connectionFactory);

    StringRedisSerializer stringSerializer = new StringRedisSerializer();

    RedisSerializer<Object> jsonSerializer = RedisSerializer.json();

    template.setKeySerializer(stringSerializer);
    template.setValueSerializer(jsonSerializer);
    template.setHashKeySerializer(stringSerializer);
    template.setHashValueSerializer(jsonSerializer);

    template.setEnableTransactionSupport(true);
    template.afterPropertiesSet();

    return template;
  }

  public Long getSessionTtl() {
    return redisProperties.getSessionTtl();
  }

  public Long getTokenBlacklistTtl() {
    return redisProperties.getTokenBlacklistTtl();
  }
}