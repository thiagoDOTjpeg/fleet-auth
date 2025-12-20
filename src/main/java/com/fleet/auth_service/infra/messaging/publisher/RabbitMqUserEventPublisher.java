package com.fleet.auth_service.infra.messaging.publisher;

import com.fleet.auth_service.application.dto.events.UserRegisteredEvent;
import com.fleet.auth_service.application.ports.output.UserEventPublisher;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RabbitMqUserEventPublisher implements UserEventPublisher {

  private final RabbitTemplate rabbitTemplate;

  @Autowired
  public RabbitMqUserEventPublisher(RabbitTemplate rabbitTemplate) {
    this.rabbitTemplate = rabbitTemplate;
  }

  @Override
  public void publishUserRegistered(UserRegisteredEvent event) {
    rabbitTemplate.convertAndSend("exchange.user", "user.registered", event);
  }
}
