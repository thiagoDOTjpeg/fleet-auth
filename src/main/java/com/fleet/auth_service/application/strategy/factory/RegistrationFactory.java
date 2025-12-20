package com.fleet.auth_service.application.strategy.factory;

import com.fleet.auth_service.application.strategy.*;
import com.fleet.auth_service.domain.enums.UserType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class RegistrationFactory {
  private final Map<UserType, RegistrationStrategy> strategies;

  @Autowired
  public RegistrationFactory(List<RegistrationStrategy> strategyList) {
    this.strategies = strategyList.stream()
            .collect(Collectors.toMap(
                    RegistrationStrategy::getUserType,
                    Function.identity()
            ));
  }

  public RegistrationStrategy getStrategy(UserType userType) {
    RegistrationStrategy strategy = this.strategies.get(userType);
    if(strategy == null) throw new IllegalArgumentException("Invalid user type");
    return strategy;
  }
}
