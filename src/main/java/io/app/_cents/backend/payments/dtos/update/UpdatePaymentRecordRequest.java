package io.app._cents.backend.payments.dtos.update;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.app._cents.backend.payments.models.enums.TransactionType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdatePaymentRecordRequest {

  private static final String NOT_BLANK = ".*\\S.*";

  // All constraints below pass on null, so every field stays optional.

  @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
  @DecimalMax(value = "9999999999.99", message = "Amount is too large")
  @Digits(
      integer = 10,
      fraction = 2,
      message = "Amount must have at most 10 integer digits and 2 decimal places")
  private BigDecimal amount;

  @Size(max = 255, message = "Recipient name must not exceed 255 characters")
  @Pattern(regexp = NOT_BLANK, message = "Recipient name must not be blank")
  private String recipientName;

  @Size(max = 255, message = "Recipient Identifier must not exceed 255 characters")
  @Pattern(regexp = NOT_BLANK, message = "Recipient Identifier must not be blank")
  private String recipientIdentifier;

  @Size(max = 1000, message = "Note must not exceed 1000 characters")
  private String note;

  @Size(max = 50, message = "Payment medium must not exceed 50 characters")
  @Pattern(regexp = NOT_BLANK, message = "Payment medium must not be blank")
  private String paymentMedium;

  @Schema(
      description = "Whether money went out (DEBIT) or came in (CREDIT)",
      example = "DEBIT",
      requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  private TransactionType transactionType;

  @Size(max = 64, message = "Category id must not exceed 64 characters")
  @Pattern(regexp = NOT_BLANK, message = "Category id must not be blank")
  private String categoryId;

  @JsonIgnore
  @AssertTrue(message = "At least one field must be provided for update")
  public boolean isAtLeastOneFieldPresent() {
    return amount != null
        || recipientName != null
        || recipientIdentifier != null
        || note != null
        || paymentMedium != null
        || transactionType != null
        || categoryId != null;
  }
}
