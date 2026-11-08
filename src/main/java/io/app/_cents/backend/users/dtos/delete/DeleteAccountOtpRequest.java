package io.app._cents.backend.users.dtos.delete;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.ToString;

@Data
@Schema(description = "Request to send an OTP for account deletion")
public class DeleteAccountOtpRequest {

  @Schema(
      description =
          "Current password. Required for users who signed up with a password, "
              + "ignored for Google sign-in users.",
      format = "password",
      requiredMode = Schema.RequiredMode.NOT_REQUIRED,
      nullable = true)
  @Size(max = 128, message = "password must be at most 128 characters")
  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  @ToString.Exclude
  private String password;
}
