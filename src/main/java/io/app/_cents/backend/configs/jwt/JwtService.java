package io.app._cents.backend.configs.jwt;

import io.app._cents.backend.users.types.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.function.Function;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

  @Value("${jwt.secret}")
  private String secretKey;

  @PostConstruct
  void validateSecret() {
    if (secretKey == null || secretKey.length() < 6) {
      throw new IllegalStateException("JWT_SECRET must be at least 6 characters");
    }
  }

  public String generateToken(
      String userId, String name, String username, String email, String avatar, UserRole role) {

    Date now = new Date();

    return Jwts.builder()
        .subject(username)
        .claim("userId", userId)
        .claim("name", name)
        .claim("username", username)
        .claim("email", email)
        .claim("avatar", avatar)
        .claim("role", role)
        .issuedAt(now)
        .expiration(null)
        .signWith(getKey())
        .compact();
  }

  private SecretKey getKey() {
    try {
      byte[] hash =
          MessageDigest.getInstance("SHA-256").digest(secretKey.getBytes(StandardCharsets.UTF_8));
      return Keys.hmacShaKeyFor(hash);
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalStateException(e);
    }
  }

  public String extractUserName(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
    final Claims claims = extractAllClaims(token);
    return claimResolver.apply(claims);
  }

  private Claims extractAllClaims(String token) {
    return Jwts.parser().verifyWith(getKey()).build().parseSignedClaims(token).getPayload();
  }

  public boolean validateToken(String token, UserDetails userDetails) {
    final String userName = extractUserName(token);
    return (userName.equals(userDetails.getUsername()) && !isTokenExpired(token));
  }

  /**
   * Returns true only if the token has an 'exp' claim that is in the past. Tokens without an 'exp'
   * claim are treated as non-expiring by design. No exp for tokens for now
   */
  private boolean isTokenExpired(String token) {
    Date expiration = extractExpiration(token);
    return expiration != null && expiration.before(new Date());
  }

  private Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  public String extractUserId(String token) {
    return extractClaim(token, claims -> claims.get("userId", String.class));
  }

  public UserRole extractRole(String token) {
    String role = extractClaim(token, claims -> claims.get("role", String.class));
    return role == null ? null : UserRole.valueOf(role);
  }
}
