package com.fleet.auth_service.shared.exception;

import java.util.Date;

public record ExceptionMessage(Date timestamp, int status, String error, String message, String details) {}
