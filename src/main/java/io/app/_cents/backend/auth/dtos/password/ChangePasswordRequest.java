package io.app._cents.backend.auth.dtos.password;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.ToString;

@Data
@Schema(description = "Request to change or set the account password")
public class ChangePasswordRequest {

  @Schema(
      description =
          "Current password. Required if the account already has a password, "
              + "ignored for accounts without one (for example Google sign-in users).",
      format = "password",
      requiredMode = Schema.RequiredMode.NOT_REQUIRED,
      nullable = true)
  @Size(max = 128, message = "currentPassword must be at most 128 characters")
  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  @ToString.Exclude
  private String currentPassword;

  @Schema(
      description = "The new password (8 to 72 characters)",
      format = "password",
      requiredMode = Schema.RequiredMode.REQUIRED)
  @NotBlank(message = "newPassword is required")
  @Size(min = 8, max = 72, message = "newPassword must be between 8 and 72 characters")
  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  @ToString.Exclude
  private String newPassword;
}
