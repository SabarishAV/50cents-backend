package io.app._cents.backend.common.exceptions;

import org.springframework.http.HttpStatus;

public class InvalidOtpException extends AppException {

  private static final String CODE = "INVALID_OTP";

  public InvalidOtpException() {
    super(HttpStatus.BAD_REQUEST, CODE, "Invalid or expired OTP");
  }

  public InvalidOtpException(String message) {
    super(HttpStatus.BAD_REQUEST, CODE, message);
  }
}
