package io.app._cents.backend.users;

import io.app._cents.backend.common.dtos.Response;
import io.app._cents.backend.common.utils.CurrentUser;
import io.app._cents.backend.users.dtos.update.UpdateUserRequest;
import io.app._cents.backend.users.models.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@Tag(name = "User APIs", description = "User APIs")
public class UserController {

  @Autowired private UsersService usersService;

  @PatchMapping
  @Operation(summary = "Update user", description = "Update logged in user")
  public ResponseEntity<Response> updateUser(@Valid @RequestBody UpdateUserRequest req) {
    return usersService.update(CurrentUser.id(), req);
  }

  @GetMapping
  @Operation(summary = "Get user", description = "Get details of logged in user")
  public ResponseEntity<User> getUser() {
    return usersService.getUser(CurrentUser.id());
  }
}
