package io.app._cents.backend.payments.dtos.repository;

import java.math.BigDecimal;

public interface PaymentTotalsRow {
  BigDecimal getTotalCredit();

  BigDecimal getTotalDebit();
}
