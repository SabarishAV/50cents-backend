package io.app._cents.backend.auth.exceptions;

import io.app._cents.backend.common.exceptions.AppException;
import org.springframework.http.HttpStatus;

public class UserAlreadyExistsException extends AppException {
    public UserAlreadyExistsException(String message) {
        super(HttpStatus.CONFLICT, "USER_ALREADY_EXISTS", message);
    }
}
