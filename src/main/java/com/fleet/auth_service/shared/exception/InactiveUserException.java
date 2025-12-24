package com.fleet.auth_service.shared.exception;

public class InactiveUserException extends RuntimeException {
  public InactiveUserException(String message) {
    super(message);
  }
}
