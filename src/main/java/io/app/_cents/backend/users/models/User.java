package io.app._cents.backend.users.models;

import io.app._cents.backend.users.types.UserRole;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Entity
@Table(name = "users")
public class User {
  @Id
  @Column(updatable = false, nullable = false)
  private String id;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false, unique = true)
  private String username;

  @Column(nullable = false, unique = true)
  private String email;

  @Column(name = "google_id", unique = true)
  private String googleId;

  @Column private String password;

  @Column(columnDefinition = "TEXT")
  private String avatar;

  @Column(name = "bank_balance", nullable = false, precision = 12, scale = 2)
  private BigDecimal bankBalance;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  @JdbcTypeCode(SqlTypes.VARCHAR)
  private UserRole role;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  @PrePersist
  public void generateId() {
    if (id == null) {
      id = UUID.randomUUID().toString();
    }
    if (createdAt == null) {
      createdAt = Instant.now();
    }
    if (bankBalance == null) {
      bankBalance = BigDecimal.ZERO;
    }
  }
}
