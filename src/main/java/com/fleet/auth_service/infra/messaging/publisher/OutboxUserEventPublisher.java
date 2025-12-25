package com.fleet.auth_service.infra.messaging.publisher;

import com.fleet.auth_service.application.dto.events.UserRegisteredEvent;
import com.fleet.auth_service.application.ports.output.UserEventPublisher;
import com.fleet.auth_service.domain.model.OutboxEvent;
import com.fleet.auth_service.infra.repository.OutboxEventRepository;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
public class OutboxUserEventPublisher implements UserEventPublisher {
  private final OutboxEventRepository outboxRepository;
  private final ObjectMapper objectMapper;

  public OutboxUserEventPublisher(OutboxEventRepository outboxRepository, ObjectMapper objectMapper) {
    this.outboxRepository = outboxRepository;
    this.objectMapper = objectMapper;
  }

  @Override
  public void publishUserRegistered(UserRegisteredEvent event) {
    try {
      String payloadJson = objectMapper.writeValueAsString(event);

      OutboxEvent outboxEvent = new OutboxEvent(
              "USER",
              event.userId().toString(),
              "user.registered",
              payloadJson
      );

      outboxRepository.save(outboxEvent);

    } catch (Exception e) {
      throw new RuntimeException("Error to serialize outbox event", e);
    }
  }
}
