package io.app._cents.backend.common.exceptions;

import org.springframework.http.HttpStatus;

public class BadRequestException extends AppException {

  private static final String CODE = "BAD_REQUEST";

  public BadRequestException(String message) {
    super(HttpStatus.BAD_REQUEST, CODE, message);
  }

  public BadRequestException(String code, String message) {
    super(HttpStatus.BAD_REQUEST, code, message);
  }
}
