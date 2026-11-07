package io.app._cents.backend.categories.dtos.predict;

import io.app._cents.backend.payments.models.enums.TransactionType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to predict a category for a payment based on recipient and note")
public class PredictCategoryRequest {

  @Schema(
      description = "Name of the payment recipient",
      example = "Coffee Shop",
      maxLength = 255,
      requiredMode = Schema.RequiredMode.REQUIRED)
  @NotBlank(message = "Recipient is required")
  @Size(max = 255, message = "Recipient must not exceed 255 characters")
  private String recipient;

  @Schema(
      description = "Whether money went out (DEBIT) or came in (CREDIT)",
      example = "DEBIT",
      requiredMode = Schema.RequiredMode.REQUIRED)
  @NotNull(message = "Transaction type is required")
  private TransactionType transactionType;

  @Schema(
      description = "Optional note about the payment",
      example = "Morning coffee",
      maxLength = 2048,
      nullable = true,
      requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @Size(max = 2048, message = "Note must not exceed 2048 characters")
  private String note;
}
