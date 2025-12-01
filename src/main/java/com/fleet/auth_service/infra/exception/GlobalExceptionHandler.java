package com.fleet.auth_service.infra.exception;

import com.fleet.auth_service.shared.exception.ExceptionMessage;
import com.fleet.auth_service.shared.exception.InactiveUserException;
import com.fleet.auth_service.shared.exception.ResourceNotFoundException;
import com.fleet.auth_service.shared.exception.UnauthorizedException;
import org.jspecify.annotations.NullMarked;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;
import java.util.stream.Collectors;

@RestControllerAdvice
@ControllerAdvice
public class GlobalExceptionHandler {
  private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  private ExceptionMessage createExceptionMessage(String message, HttpStatus status, String details) {
    return new ExceptionMessage(new Date(), status.value(), status.getReasonPhrase(), message, details);
  }

  @ExceptionHandler(UnauthorizedException.class)
  @NullMarked
  public ResponseEntity<ExceptionMessage> handleUnauthorizedException(UnauthorizedException ex, WebRequest request) {
    return new ResponseEntity<>(createExceptionMessage(ex.getMessage(), HttpStatus.UNAUTHORIZED, request.getDescription(false)), HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(BadCredentialsException.class)
  @NullMarked
  public ResponseEntity<ExceptionMessage> handleBadCredentialsException(BadCredentialsException ex, WebRequest request) {
    return new ResponseEntity<>(createExceptionMessage(ex.getMessage(), HttpStatus.UNAUTHORIZED, request.getDescription(false)), HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(ResourceNotFoundException.class)
  @NullMarked
  public final ResponseEntity<ExceptionMessage> handleAllResourceNotFound(Exception ex, WebRequest request) {
    return new ResponseEntity<>(createExceptionMessage(ex.getMessage(), HttpStatus.NOT_FOUND, request.getDescription(false)), HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(InactiveUserException.class)
  @NullMarked
  public final ResponseEntity<ExceptionMessage> handleInactiveUserException(Exception ex, WebRequest request) {
    return new ResponseEntity<>(createExceptionMessage(ex.getMessage(), HttpStatus.FORBIDDEN, request.getDescription(false)), HttpStatus.FORBIDDEN);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @NullMarked
  public ResponseEntity<ExceptionMessage> handleValidationErrors(MethodArgumentNotValidException ex, WebRequest request) {
    String errors = ex.getBindingResult().getFieldErrors().stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .collect(Collectors.joining(", "));

    logger.debug("Erro de validação de dados: {}", errors);

    return new ResponseEntity<>(
            createExceptionMessage("Erro de validação: " + errors, HttpStatus.BAD_REQUEST, request.getDescription(false)),
            HttpStatus.BAD_REQUEST
    );
  }

  @ExceptionHandler(Exception.class)
  @NullMarked
  public ResponseEntity<ExceptionMessage> handleAllUncaughtExceptions(Exception ex, WebRequest request) {
    logger.error("Erro inesperado no sistema", ex);

    return new ResponseEntity<>(
            createExceptionMessage("Ocorreu um erro interno no servidor. Contate o suporte.", HttpStatus.INTERNAL_SERVER_ERROR, request.getDescription(false)),
            HttpStatus.INTERNAL_SERVER_ERROR
    );
  }
}
