package io.app._cents.backend.users.dtos.create;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreateUseRequest {

    @NotBlank
    @Size(max = 100)
    private String name;

    @Size(min = 3, max = 30)
    @Pattern(regexp = "^[a-zA-Z0-9._]+$", message = "only letters, numbers, . and _ allowed")
    private String username;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 8, max = 72, message = "password must be 8 to 72 characters")
    private String password;

    private String avatar;
}
