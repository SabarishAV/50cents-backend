package io.app._cents.backend.auth.services;

import io.app._cents.backend.auth.dtos.login.LoginUserResponse;
import io.app._cents.backend.configs.GoogleTokenVerifier;
import io.app._cents.backend.configs.jwt.JwtService;
import io.app._cents.backend.users.UsersService;
import io.app._cents.backend.users.models.User;
import io.app._cents.backend.users.types.UserRole;
import java.util.concurrent.ThreadLocalRandom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class GoogleAuthService {

  private static final Logger logger = LoggerFactory.getLogger(GoogleAuthService.class);

  @Autowired private GoogleTokenVerifier googleTokenVerifier;

  @Autowired private UsersService usersService;
  @Autowired private JwtService jwtService;

  public ResponseEntity<LoginUserResponse> loginWithGoogle(String idToken) {
    GoogleTokenVerifier.GoogleUser g = googleTokenVerifier.verify(idToken);

    User user =
        usersService
            .findUserByGoogleIdOrEmail(g.sub(), g.email())
            .map(existing -> linkGoogle(existing, g))
            .orElseGet(() -> createGoogleUser(g));

    String token =
        jwtService.generateToken(
            user.getId(),
            user.getName(),
            user.getUsername(),
            user.getEmail(),
            user.getAvatar(),
            user.getRole());

    return new ResponseEntity<>(new LoginUserResponse(true, token), HttpStatus.OK);
  }

  private User linkGoogle(User existing, GoogleTokenVerifier.GoogleUser g) {
    existing.setGoogleId(g.sub());
    if (existing.getAvatar() == null) {
      existing.setAvatar(g.picture());
    }
    return usersService.save(existing);
  }

  private User createGoogleUser(GoogleTokenVerifier.GoogleUser g) {
    User newUser =
        User.builder()
            .name(g.name() != null ? g.name() : g.email().split("@")[0])
            .username(generateUsername(g.email()))
            .email(g.email())
            .googleId(g.sub())
            .avatar(g.picture())
            .role(UserRole.USER)
            .build();
    return usersService.save(newUser);
  }

  private String generateUsername(String email) {
    String base = email.split("@")[0].toLowerCase().replaceAll("[^a-z0-9_.]", "");
    if (base.isBlank()) {
      base = "user";
    }
    String candidate = base;
    while (usersService.existsByUsername(candidate)) {
      candidate = base + ThreadLocalRandom.current().nextInt(1000, 10000);
    }
    return candidate;
  }
}
