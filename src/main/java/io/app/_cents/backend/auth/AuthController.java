package io.app._cents.backend.auth;

import io.app._cents.backend.auth.dtos.login.LoginUserRequest;
import io.app._cents.backend.auth.dtos.login.LoginUserResponse;
import io.app._cents.backend.auth.dtos.registration.RegisterUserRequest;
import io.app._cents.backend.auth.dtos.registration.RegisterUserResponse;
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
@RequestMapping("/auth/")
@Tag(name = "Auth APIs", description = "Authentication APIs")
public class AuthController {

    @Autowired AuthService authService;

    @PostMapping(value = "/register/")
    @Operation(summary = "Register User")
    public ResponseEntity<RegisterUserResponse> registerUser(@Valid @RequestBody RegisterUserRequest user) {
        return authService.registerUser(user);
    }

    @PostMapping(value = "/login/")
    @Operation(summary = "User Login")
    public ResponseEntity<LoginUserResponse> registerUser(@Valid @RequestBody LoginUserRequest user) {
        return authService.login(user);
    }

}
