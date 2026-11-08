package io.app._cents.backend.auth.dtos.password;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.ToString;

@Data
@Schema(description = "Request to reset the password using the OTP sent by email")
public class ForgotPasswordVerifyRequest {

  @Schema(
      description = "The same username or email used to request the OTP",
      example = "john@example.com",
      requiredMode = Schema.RequiredMode.REQUIRED)
  @NotBlank(message = "identifier is required")
  @Size(max = 254, message = "identifier must be at most 254 characters")
  private String identifier;

  @Schema(
      description = "The 6-digit OTP received by email",
      example = "123456",
      requiredMode = Schema.RequiredMode.REQUIRED)
  @NotBlank(message = "otp is required")
  @Pattern(regexp = "^\\d{6}$", message = "otp must be exactly 6 digits")
  private String otp;

  @Schema(
      description = "The new password (8 to 72 characters)",
      format = "password",
      requiredMode = Schema.RequiredMode.REQUIRED)
  @NotBlank(message = "Password is required")
  @Size(min = 8, max = 72, message = "Password must be between 8 and 72 characters")
  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  @ToString.Exclude
  private String password;
}
