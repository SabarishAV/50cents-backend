package io.app._cents.backend.users.dtos.update;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description =
        "Partial update for a user. All fields optional, but at least one must be provided.")
public class UpdateUserRequest {

  @Schema(description = "Display name", example = "Alice", maxLength = 255, nullable = true)
  @Size(max = 255, message = "Name must not exceed 255 characters")
  @Pattern(regexp = ".*\\S.*", message = "Name must not be blank")
  private String name;

  @Schema(description = "Unique username", example = "alice", maxLength = 50, nullable = true)
  @Size(max = 50, message = "Username must not exceed 50 characters")
  @Pattern(
      regexp = "^[a-zA-Z0-9_.-]+$",
      message = "Username may only contain letters, numbers, '.', '_' and '-'")
  private String username;

  @Schema(description = "Avatar URL or data URI", maxLength = 2048, nullable = true)
  @Size(max = 2048, message = "Avatar must not exceed 2048 characters")
  @Pattern(regexp = ".*\\S.*", message = "Avatar must not be blank")
  private String avatar;

  @Schema(description = "Bank balance", example = "1500.00", nullable = true)
  @DecimalMin(value = "0", message = "Bank balance must be >= 0")
  private BigDecimal bankBalance;

  @AssertTrue(message = "At least one field must be provided")
  @JsonIgnore
  public boolean isAtLeastOneFieldProvided() {
    return name != null || username != null || avatar != null || bankBalance != null;
  }
}
