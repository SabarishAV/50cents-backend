package io.app._cents.backend.auth.dtos.password;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Request to start a password reset")
public class ForgotPasswordRequest {

  @Schema(
      description = "The username or email address of the account",
      example = "john@example.com",
      requiredMode = Schema.RequiredMode.REQUIRED)
  @NotBlank(message = "identifier is required")
  @Size(max = 254, message = "identifier must be at most 254 characters")
  private String identifier;
}
