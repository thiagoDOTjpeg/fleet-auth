package com.fleet.auth_service.infra.config;

import com.rabbitmq.client.AMQP.Exchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
  @Bean
  public Exchange exchange() {
    return new Exchange();
  }
}
