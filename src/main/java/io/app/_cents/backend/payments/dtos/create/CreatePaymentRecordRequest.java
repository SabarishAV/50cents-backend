package io.app._cents.backend.payments.dtos.create;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreatePaymentRecordRequest {

  @NotNull(message = "Amount is required")
  @DecimalMin(value = "0", inclusive = false, message = "Amount must be greater than zero")
  private BigDecimal amount;

  @NotBlank private String recipientName;

  private String note;

  private String paymentMedium;

  private String category;
}
