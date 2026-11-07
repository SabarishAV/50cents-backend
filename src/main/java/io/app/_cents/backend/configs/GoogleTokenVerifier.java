package io.app._cents.backend.configs;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import io.app._cents.backend.auth.exceptions.InvalidGoogleTokenException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class GoogleTokenVerifier {

  public record GoogleUser(String sub, String email, String name, String picture) {}

  private final GoogleIdTokenVerifier verifier;

  public GoogleTokenVerifier(@Value("${google.client-id}") String clientId) {
    this.verifier =
        new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), GsonFactory.getDefaultInstance())
            .setAudience(Collections.singletonList(clientId))
            .build();
  }

  public GoogleUser verify(String idToken) {
    if (idToken == null || idToken.isBlank()) {
      throw new InvalidGoogleTokenException("Google token is missing");
    }

    GoogleIdToken token;
    try {
      token = verifier.verify(idToken);
    } catch (IllegalArgumentException | GeneralSecurityException | IOException e) {
      throw new InvalidGoogleTokenException("Google token could not be verified", e);
    }

    if (token == null) {
      throw new InvalidGoogleTokenException("Google token is invalid or expired");
    }

    GoogleIdToken.Payload p = token.getPayload();
    if (!Boolean.TRUE.equals(p.getEmailVerified())) {
      throw new InvalidGoogleTokenException("Google email is not verified");
    }

    return new GoogleUser(
        p.getSubject(), p.getEmail(), (String) p.get("name"), (String) p.get("picture"));
  }
}
