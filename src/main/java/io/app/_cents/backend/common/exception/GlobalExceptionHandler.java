package io.app._cents.backend.common.exception;

import io.app._cents.backend.common.exception.types.ErrorResponse;
import io.jsonwebtoken.JwtException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(AppException.class)
    public ResponseEntity<Object> handleApp(AppException ex, WebRequest request) {
        return build(ex.getStatus(), ex.getCode(), ex.getMessage(), null, request);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraint(ConstraintViolationException ex, WebRequest request) {
        Map<String, String> fields = ex.getConstraintViolations().stream()
                .collect(Collectors.toMap(
                        v -> v.getPropertyPath().toString(),
                        ConstraintViolation::getMessage,
                        (a, b) -> a));
        return build(HttpStatus.BAD_REQUEST, "VALIDATION_FAILED", "Validation failed", fields, request);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleIntegrity(DataIntegrityViolationException ex, WebRequest request) {
        log.warn("Data integrity violation", ex);
        return build(HttpStatus.CONFLICT, "CONFLICT", "Resource already exists or violates a constraint", null, request);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleDenied(AccessDeniedException     ex, WebRequest request) {
        return build(HttpStatus.FORBIDDEN, "FORBIDDEN", "Access denied", null, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleUnknown(Exception ex, WebRequest request) {
        log.error("Unhandled exception", ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "Something went wrong", null, request);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Object> handleAuth(AuthenticationException ex, WebRequest request) {
        return build(HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS", "Invalid credentials", null, request);
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<Object> handleJwt(JwtException ex, WebRequest request) {
        return build(HttpStatus.UNAUTHORIZED, "INVALID_TOKEN", "Invalid or expired token", null, request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        Map<String, String> fields = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        f -> Objects.toString(f.getDefaultMessage(), "Invalid value"),
                        (a, b) -> a));
        return build(HttpStatus.BAD_REQUEST, "VALIDATION_FAILED", "Validation failed", fields, request);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception ex, Object body, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
        HttpStatus status = HttpStatus.valueOf(statusCode.value());
        String code = status == HttpStatus.NOT_FOUND ? "NOT_FOUND" : "BAD_REQUEST";
        return build(status, code, status.getReasonPhrase(), null, request);
    }

    private ResponseEntity<Object> build(HttpStatus status, String code, String message,
                                         Map<String, String> fields, WebRequest request) {
        String path = ((ServletWebRequest) request).getRequest().getRequestURI();
        ErrorResponse body = new ErrorResponse(
                false,
                status.value(),
                code,
                message,
                (fields == null || fields.isEmpty()) ? null : fields,
                path,
                Instant.now(),
                MDC.get("traceId")
        );
        return ResponseEntity.status(status).body(body);
    }

}