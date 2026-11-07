package io.app._cents.backend.categories.dtos.admin.update;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateCategoryRequest {

  @Size(max = 255, message = "Name must not exceed 255 characters")
  @Pattern(regexp = ".*\\S.*", message = "Name must not be blank")
  private String name;

  @Size(max = 50, message = "Code must not exceed 50 characters")
  @Pattern(
      regexp = "^[A-Za-z0-9_-]+$",
      message = "Code may only contain letters, numbers, underscores and hyphens")
  private String code;

  @Size(max = 2048, message = "Image must not exceed 2048 characters")
  @Pattern(regexp = ".*\\S.*", message = "Image must not be blank")
  private String image;

  @AssertTrue(message = "At least one field (name, code or image) must be provided")
  @JsonIgnore
  public boolean isAtLeastOneFieldProvided() {
    return name != null || code != null || image != null;
  }
}
