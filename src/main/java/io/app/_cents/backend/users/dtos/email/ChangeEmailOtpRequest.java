package io.app._cents.backend.users.dtos.email;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Request to send an OTP to a new email address")
public class ChangeEmailOtpRequest {

  @Schema(
      description = "The new email address to receive the OTP",
      example = "newemail@example.com",
      requiredMode = Schema.RequiredMode.REQUIRED)
  @NotBlank(message = "email is required")
  @Email(message = "email must be a valid email address")
  @Size(max = 254, message = "email must be at most 254 characters")
  private String email;
}
