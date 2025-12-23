package com.fleet.auth_service.infra.exception;

import com.fleet.auth_service.shared.exception.*;
import org.jspecify.annotations.NullMarked;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.accept.MissingApiVersionException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestCookieException;
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

  @ExceptionHandler(MissingRequestCookieException.class)
  @NullMarked
  public ResponseEntity<ExceptionMessage> handleAllMissignRequestCookieExceptions(MissingRequestCookieException ex, WebRequest request) {
    return new ResponseEntity<>(createExceptionMessage(ex.getMessage(), HttpStatus.BAD_REQUEST, request.getDescription(false)), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(UnauthorizedException.class)
  @NullMarked
  public ResponseEntity<ExceptionMessage> handleUnauthorizedException(UnauthorizedException ex, WebRequest request) {
    return new ResponseEntity<>(createExceptionMessage(ex.getMessage(), HttpStatus.UNAUTHORIZED, request.getDescription(false)), HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(BadRequestException.class)
  @NullMarked
  public ResponseEntity<ExceptionMessage> handleAllBadRequestExceptions(BadRequestException ex, WebRequest request) {
    return new ResponseEntity<>(createExceptionMessage(ex.getMessage(), HttpStatus.BAD_REQUEST, request.getDescription(false)), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  @NullMarked
  public ResponseEntity<ExceptionMessage> handleAllIllegalArgumentExceptions(IllegalArgumentException ex, WebRequest request) {
    return new ResponseEntity<>(createExceptionMessage(ex.getMessage(), HttpStatus.BAD_REQUEST, request.getDescription(false)), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(MissingApiVersionException.class)
  @NullMarked
  public ResponseEntity<ExceptionMessage> handleMissingApiVersionException(MissingApiVersionException ex, WebRequest request) {
    return new ResponseEntity<>(createExceptionMessage("Por favor adicione o Header API-version com a versão desejada da API", HttpStatus.BAD_REQUEST, request.getDescription(false)), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(ConflictException.class)
  @NullMarked
  public ResponseEntity<ExceptionMessage> handleConflictException(ConflictException ex, WebRequest request) {
    return new ResponseEntity<>(createExceptionMessage(ex.getMessage(), HttpStatus.CONFLICT, request.getDescription(false)), HttpStatus.CONFLICT);
  }

  @ExceptionHandler(BadCredentialsException.class)
  @NullMarked
  public ResponseEntity<ExceptionMessage> handleBadCredentialsException(BadCredentialsException ex, WebRequest request) {
    return new ResponseEntity<>(createExceptionMessage(ex.getMessage(), HttpStatus.UNAUTHORIZED, request.getDescription(false)), HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(ResourceNotFoundException.class)
  @NullMarked
  public final ResponseEntity<ExceptionMessage> handleAllResourceNotFound(ResourceNotFoundException ex, WebRequest request) {
    return new ResponseEntity<>(createExceptionMessage(ex.getMessage(), HttpStatus.NOT_FOUND, request.getDescription(false)), HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(InactiveUserException.class)
  @NullMarked
  public final ResponseEntity<ExceptionMessage> handleInactiveUserException(InactiveUserException ex, WebRequest request) {
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

  @ExceptionHandler(HttpMessageNotReadableException.class)
  @NullMarked
  public ResponseEntity<ExceptionMessage> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex, WebRequest request) {
    String errorMessage = "Corpo da requisição inválido ou malformado.";

    if (ex.getMessage() != null && ex.getMessage().contains("Missing property")) {
      errorMessage = "O campo 'metadata' é obrigatório quando 'userType' é informado.";
    }

    logger.debug("Erro de desserialização JSON: {}", ex.getMessage());

    return new ResponseEntity<>(
            createExceptionMessage(errorMessage, HttpStatus.BAD_REQUEST, request.getDescription(false)),
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
