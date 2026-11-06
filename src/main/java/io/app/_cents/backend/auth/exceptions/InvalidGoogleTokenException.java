package io.app._cents.backend.auth.exceptions;

import org.springframework.security.authentication.BadCredentialsException;

public class InvalidGoogleTokenException extends BadCredentialsException {
    public InvalidGoogleTokenException(String message) {
        super(message);
    }

    public InvalidGoogleTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}
