package io.app._cents.backend.auth.dtos.registration;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "RegisterUserRequest", description = "Payload used to register a new user account.")
public class RegisterUserRequest {

  @Schema(
      description = "Full display name of the user.",
      example = "Jane Doe",
      requiredMode = Schema.RequiredMode.REQUIRED,
      maxLength = 100)
  @NotBlank
  @Size(max = 100)
  private String name;

  @Schema(
      description = "Unique username. Only letters, numbers, dots and underscores allowed.",
      example = "jane.doe_01",
      requiredMode = Schema.RequiredMode.REQUIRED,
      minLength = 3,
      maxLength = 30,
      pattern = "^[a-zA-Z0-9._]+$")
  @NotBlank
  @Size(min = 3, max = 30)
  @Pattern(regexp = "^[a-zA-Z0-9._]+$", message = "only letters, numbers, . and _ allowed")
  private String username;

  @Schema(
      description = "Unique email address of the user.",
      example = "jane.doe@example.com",
      requiredMode = Schema.RequiredMode.REQUIRED,
      format = "email")
  @NotBlank
  @Email
  private String email;

  @Schema(
      description = "Password for the account. Must be between 6 and 72 characters.",
      example = "S3cureP@ss",
      requiredMode = Schema.RequiredMode.REQUIRED,
      minLength = 6,
      maxLength = 72,
      format = "password",
      accessMode = Schema.AccessMode.WRITE_ONLY)
  @NotBlank
  @Size(min = 6, max = 72, message = "password must be 6 to 72 characters")
  private String password;

  @Schema(
      description = "Optional URL to the user's avatar image.",
      example = "https://cdn.example.com/avatars/jane.png",
      requiredMode = Schema.RequiredMode.NOT_REQUIRED,
      nullable = true)
  private String avatar;

  @Schema(
      description = "Optional UPI ID for payments (e.g. `name@bank`).",
      example = "jane.doe@okhdfcbank",
      requiredMode = Schema.RequiredMode.NOT_REQUIRED,
      nullable = true,
      pattern = "^[a-zA-Z0-9._-]+@[a-zA-Z]+$")
  @Pattern(regexp = "^[a-zA-Z0-9._-]+@[a-zA-Z]+$", message = "invalid UPI ID format")
  private String upiId;
}
