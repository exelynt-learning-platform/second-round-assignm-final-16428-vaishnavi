package com.exelyent.task.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.exelyent.task.dto.ApiResponse;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // ─── 400 Bad Request ────────────────────────────────────────────────────

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse.Error> handleValidationErrors(
            MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String field = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            fieldErrors.put(field, message);
        });

        ApiResponse.Error error = new ApiResponse.Error();
        error.setSuccess(false);
        error.setStatus(400);
        error.setMessage("Validation failed");
        error.setErrors(fieldErrors);
        error.setTimestamp(LocalDateTime.now());

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse.Error> handleBusinessException(BusinessException ex) {
        return ResponseEntity.badRequest().body(buildError(400, ex.getMessage()));
    }

    // ─── 401 Unauthorized ───────────────────────────────────────────────────

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse.Error> handleBadCredentials(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(buildError(401, "Invalid username or password"));
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ApiResponse.Error> handleDisabledAccount(DisabledException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(buildError(401, "Account is disabled"));
    }

    // ─── 403 Forbidden ──────────────────────────────────────────────────────

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse.Error> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(buildError(403, "Access denied: you don't have permission to perform this action"));
    }

    // ─── 404 Not Found ──────────────────────────────────────────────────────

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse.Error> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(buildError(404, ex.getMessage()));
    }

    // ─── 409 Conflict ───────────────────────────────────────────────────────

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiResponse.Error> handleDuplicate(DuplicateResourceException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(buildError(409, ex.getMessage()));
    }

    // ─── 402 Payment Required ───────────────────────────────────────────────

    @ExceptionHandler(PaymentException.class)
    public ResponseEntity<ApiResponse.Error> handlePaymentException(PaymentException ex) {
        log.error("Payment error: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED)
                .body(buildError(402, ex.getMessage()));
    }

    // ─── 500 Internal Server Error ──────────────────────────────────────────

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse.Error> handleGlobalException(
            Exception ex, WebRequest request) {
        log.error("Unhandled exception at {}: {}", request.getDescription(false), ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(buildError(500, "An unexpected error occurred. Please try again later."));
    }

    // ─── Helper ─────────────────────────────────────────────────────────────

    private ApiResponse.Error buildError(int status, String message) {
        ApiResponse.Error error = new ApiResponse.Error();
        error.setSuccess(false);
        error.setStatus(status);
        error.setMessage(message);
        error.setTimestamp(LocalDateTime.now());
        return error;
    }
}