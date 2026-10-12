package io.app._cents.backend.auth.dtos.registration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RegisterUserResponse {

    private boolean success;

    private String message;
}
