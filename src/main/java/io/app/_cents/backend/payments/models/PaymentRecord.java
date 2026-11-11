package io.app._cents.backend.payments.models;

import io.app._cents.backend.categories.models.Category;
import io.app._cents.backend.payments.models.enums.TransactionType;
import io.app._cents.backend.users.models.User;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(
    name = "payment_records",
    indexes = {
      @Index(name = "idx_payment_records_user_id_paid_at", columnList = "user_id, paid_at"),
      @Index(name = "idx_payment_records_category_id", columnList = "category_id")
    })
public class PaymentRecord {

  @Id
  @Column(updatable = false, nullable = false)
  private String id;

  @Column(nullable = false, precision = 12, scale = 2)
  private BigDecimal amount;

  @Column(name = "recipient_name")
  private String recipientName;

  @Column(name = "recipient_identifier", length = 255)
  private String recipientIdentifier;

  private String note;

  @Column(name = "payment_medium")
  private String paymentMedium;

  @Enumerated(EnumType.STRING)
  @Column(name = "transaction_type", nullable = false, length = 10)
  private TransactionType transactionType;

  @Column(name = "bank_updated", nullable = false)
  private boolean bankUpdated;

  @Column(name = "paid_at", nullable = false)
  private Instant paidAt;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "category_id", nullable = false)
  @OnDelete(action = OnDeleteAction.RESTRICT)
  private Category category;

  @PrePersist
  public void generateId() {
    if (id == null) {
      id = UUID.randomUUID().toString();
    }
    if (paidAt == null) {
      paidAt = Instant.now();
    }
  }
}
