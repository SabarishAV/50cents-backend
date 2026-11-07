package io.app._cents.backend.auth.controllers;

import io.app._cents.backend.auth.dtos.google.login.GoogleLoginRequest;
import io.app._cents.backend.auth.dtos.login.LoginUserRequest;
import io.app._cents.backend.auth.dtos.login.LoginUserResponse;
import io.app._cents.backend.auth.dtos.registration.RegisterUserRequest;
import io.app._cents.backend.auth.services.AuthService;
import io.app._cents.backend.auth.services.GoogleAuthService;
import io.app._cents.backend.common.dtos.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Tag(name = "Auth APIs", description = "Authentication APIs")
public class AuthController {

  @Autowired private AuthService authService;
  @Autowired private GoogleAuthService googleAuthService;

  @PostMapping(value = "/register")
  @Operation(summary = "Register User", description = "User registration")
  public ResponseEntity<Response> registerUser(@Valid @RequestBody RegisterUserRequest user) {
    return authService.registerUser(user);
  }

  @PostMapping(value = "/login")
  @Operation(summary = "User Login", description = "User login with username or email")
  public ResponseEntity<LoginUserResponse> registerUser(@Valid @RequestBody LoginUserRequest user) {
    return authService.login(user);
  }

  @PostMapping("/google")
  @Operation(
      summary = "Sign up or log in with Google",
      description =
          "Send the Google ID token obtained from Google Sign-In on the client. "
              + "The backend verifies it, then logs in the matching user. "
              + "If the Google account is new, a user is created automatically. "
              + "If an email/password account with the same email exists, it is linked to Google. "
              + "Returns the app's own JWT.")
  public ResponseEntity<LoginUserResponse> googleLogin(
      @Valid @RequestBody GoogleLoginRequest request) {
    return googleAuthService.loginWithGoogle(request.getIdToken());
  }
}
