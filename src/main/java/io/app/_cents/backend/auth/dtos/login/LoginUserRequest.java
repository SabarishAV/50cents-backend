package io.app._cents.backend.auth.dtos.login;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginUserRequest {

  @NotBlank private String usernameOrEmail;

  @NotBlank private String password;
}
