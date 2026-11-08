package io.app._cents.backend.users.dtos.email;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Schema(description = "Request to verify the OTP sent to the new email address")
public class VerifyChangeEmailOtpRequest {

  @Schema(
      description = "The 6-digit OTP received on the new email address",
      example = "123456",
      requiredMode = Schema.RequiredMode.REQUIRED)
  @NotBlank(message = "otp is required")
  @Pattern(regexp = "^\\d{6}$", message = "otp must be exactly 6 digits")
  private String otp;
}
