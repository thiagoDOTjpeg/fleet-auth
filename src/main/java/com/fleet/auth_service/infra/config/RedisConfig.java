package com.fleet.auth_service.infra.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.databind.DefaultTyping;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;

import org.jspecify.annotations.NullMarked;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {


  @Bean
  public ObjectMapper redisObjectMapper() {
    BasicPolymorphicTypeValidator typeValidator = BasicPolymorphicTypeValidator
            .builder()
            .allowIfBaseType(Object.class)
            .build();

    return JsonMapper.builder()
            .activateDefaultTyping(
                    typeValidator,
                    DefaultTyping.NON_FINAL,
                    JsonTypeInfo.As.PROPERTY
            )
            .build();
  }

  @Bean
  @NullMarked
  public RedisTemplate<String, Object> redisTemplate(
          RedisConnectionFactory connectionFactory,
          ObjectMapper redisObjectMapper) {

    RedisTemplate<String, Object> template = new RedisTemplate<>();
    template.setConnectionFactory(connectionFactory);

    StringRedisSerializer stringSerializer = new StringRedisSerializer();

    JacksonJsonRedisSerializer<Object> jsonSerializer =
            new JacksonJsonRedisSerializer<>(redisObjectMapper, Object.class);

    template.setKeySerializer(stringSerializer);
    template.setValueSerializer(jsonSerializer);
    template.setHashKeySerializer(stringSerializer);
    template.setHashValueSerializer(jsonSerializer);

    template.setEnableTransactionSupport(true);
    template.afterPropertiesSet();

    return template;
  }
}