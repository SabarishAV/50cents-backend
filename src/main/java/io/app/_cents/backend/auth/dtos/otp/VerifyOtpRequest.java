package io.app._cents.backend.auth.dtos.otp;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Verify a one-time password (OTP) and obtain auth tokens")
public class VerifyOtpRequest {

  @Schema(
      description = "Email address the OTP was sent to",
      example = "user@example.com",
      maxLength = 255,
      requiredMode = Schema.RequiredMode.REQUIRED)
  @NotBlank(message = "Email is required")
  @Email(message = "Email must be valid")
  @Size(max = 255, message = "Email must not exceed 255 characters")
  private String email;

  @Schema(
      description = "The 6-digit one-time code",
      example = "123456",
      minLength = 6,
      maxLength = 6,
      pattern = "^\\d{6}$",
      requiredMode = Schema.RequiredMode.REQUIRED)
  @NotBlank(message = "OTP is required")
  @Pattern(regexp = "^\\d{6}$", message = "OTP must be exactly 6 digits")
  private String otp;
}
