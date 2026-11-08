package io.app._cents.backend.payments.dtos.create;

import io.app._cents.backend.payments.models.enums.TransactionType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Schema(description = "Request payload to create a payment record")
public class CreatePaymentRecordRequest {

  @Schema(
      description = "Payment amount (must be > 0)",
      example = "42.50",
      minimum = "0",
      exclusiveMinimum = true,
      requiredMode = Schema.RequiredMode.REQUIRED)
  @NotNull(message = "Amount is required")
  @DecimalMin(value = "0", inclusive = false, message = "Amount must be greater than zero")
  private BigDecimal amount;

  @Schema(
      description = "Name of the payment recipient",
      example = "Coffee Shop",
      maxLength = 255,
      requiredMode = Schema.RequiredMode.REQUIRED)
  @NotBlank(message = "Recipient name is required")
  @Size(max = 255, message = "Recipient name must not exceed 255 characters")
  private String recipientName;

  @Schema(
      description = "Optional note about the payment",
      example = "Morning coffee",
      maxLength = 2048,
      nullable = true,
      requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @Size(max = 2048, message = "Note must not exceed 2048 characters")
  private String note;

  @Schema(
      description = "Payment medium (e.g. CARD, CASH, UPI)",
      example = "CARD",
      nullable = true,
      requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  private String paymentMedium;

  @Schema(
      description = "Whether money went out (DEBIT) or came in (CREDIT)",
      example = "DEBIT",
      requiredMode = Schema.RequiredMode.REQUIRED)
  @NotNull(message = "Transaction type is required")
  private TransactionType transactionType;

  @Schema(
      description = "Category code (e.g. FOOD, TRAVEL)",
      example = "FOOD",
      maxLength = 50,
      nullable = true,
      requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @Size(max = 50, message = "Category must not exceed 50 characters")
  private String category;

  @Schema(
      description = "Whether the user's bank balance should be updated with this payment.",
      example = "false",
      defaultValue = "false",
      requiredMode = Schema.RequiredMode.REQUIRED)
  private boolean updateBankBalance;

  @Schema(
      description =
          "When the payment was made. Optional — defaults to now if omitted or null. "
              + "Accepts ISO-8601 instant (e.g. 2026-11-07T10:15:30Z) or epoch seconds.",
      example = "2026-11-07T10:15:30Z",
      type = "string",
      format = "date-time",
      nullable = true,
      requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @PastOrPresent(message = "Paid at must not be in the future")
  private Instant paidAt;
}
