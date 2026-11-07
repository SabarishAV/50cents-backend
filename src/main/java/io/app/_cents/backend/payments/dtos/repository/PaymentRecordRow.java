package io.app._cents.backend.payments.dtos.repository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public interface PaymentRecordRow {
  UUID getId();

  BigDecimal getAmount();

  String getRecipientName();

  String getNote();

  String getPaymentMedium();

  Instant getPaidAt();

  UUID getCategoryId();

  String getCategoryName();

  String getCategoryCode();

  String getCategoryImage();
}
