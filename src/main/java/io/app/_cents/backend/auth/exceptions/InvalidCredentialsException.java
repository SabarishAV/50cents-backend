package io.app._cents.backend.auth.exceptions;

import io.app._cents.backend.common.exceptions.AppException;
import org.springframework.http.HttpStatus;

public class InvalidCredentialsException extends AppException {
  public InvalidCredentialsException() {
    super(HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS", "Invalid credentials");
  }
}
