package io.app._cents.backend.common.exceptions;

import org.springframework.http.HttpStatus;

public class NotFoundException extends AppException {
  public NotFoundException(String value) {
    super(HttpStatus.NOT_FOUND, "NOT_FOUND", value + " not found");
  }
}
