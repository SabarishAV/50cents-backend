package io.app._cents.backend.auth.controllers;

import io.app._cents.backend.auth.dtos.google.login.GoogleLoginRequest;
import io.app._cents.backend.auth.dtos.login.LoginUserRequest;
import io.app._cents.backend.auth.dtos.login.LoginUserResponse;
import io.app._cents.backend.auth.dtos.otp.RequestOtpRequest;
import io.app._cents.backend.auth.dtos.otp.VerifyOtpRequest;
import io.app._cents.backend.auth.dtos.password.ChangePasswordRequest;
import io.app._cents.backend.auth.dtos.password.ForgotPasswordRequest;
import io.app._cents.backend.auth.dtos.password.ForgotPasswordVerifyRequest;
import io.app._cents.backend.auth.dtos.registration.RegisterUserRequest;
import io.app._cents.backend.auth.services.AuthService;
import io.app._cents.backend.auth.services.GoogleAuthService;
import io.app._cents.backend.common.dtos.Response;
import io.app._cents.backend.common.utils.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

  @PostMapping("/otp/request")
  @Operation(
      summary = "Request OTP for login",
      description = "Sends a one-time code to the given email address.")
  public ResponseEntity<Response> emailOtpRequest(@Valid @RequestBody RequestOtpRequest req) {
    return authService.emailOtpRequest(req.getEmail());
  }

  @PostMapping("/otp/verify")
  @Operation(summary = "Verify OTP", description = "Verifies OTP for login")
  public ResponseEntity<LoginUserResponse> emailOtpVerify(
      @Valid @RequestBody VerifyOtpRequest req) {
    return authService.emailOtpVerify(req);
  }

  @PutMapping("/password")
  @Operation(
      summary = "Change password",
      description =
          "Changes the password of the authenticated user. If the account already has a password, "
              + "the current password is required and must be correct, and the new password must "
              + "differ from it. Accounts without a password (for example Google sign-in users) "
              + "can omit the current password to set one for the first time.")
  public ResponseEntity<Response> changePassword(@Valid ChangePasswordRequest req) {
    return authService.changePassword(CurrentUser.id(), req);
  }

  @PostMapping("/forgot-password/request")
  @Operation(
      summary = "Request OTP to reset password",
      description = "Sends a one-time code to the given email address to reset password.")
  public ResponseEntity<Response> forgotPasswordRequest(@Valid ForgotPasswordRequest req) {
    return authService.forgotPasswordRequest(req);
  }

  @PostMapping("/forgot-password/verify")
  @Operation(
      summary = "Verify OTP to reset password",
      description = "Verifies OTP to reset password")
  public ResponseEntity<Response> forgotPasswordVerify(@Valid ForgotPasswordVerifyRequest req) {
    return authService.forgotPasswordVerify(req);
  }
}
