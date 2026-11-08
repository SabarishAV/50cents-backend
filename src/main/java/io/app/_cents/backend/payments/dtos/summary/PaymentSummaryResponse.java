package io.app._cents.backend.payments.dtos.summary;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "Total income and spending for the selected period")
public class PaymentSummaryResponse {

  @Schema(description = "Sum of all CREDIT payments", example = "45000.00")
  private final BigDecimal totalCredit;

  @Schema(description = "Sum of all DEBIT payments", example = "18250.50")
  private final BigDecimal totalDebit;

  @Schema(description = "totalCredit minus totalDebit", example = "26749.50")
  private final BigDecimal net;

  public static PaymentSummaryResponse of(BigDecimal credit, BigDecimal debit) {
    return new PaymentSummaryResponse(credit, debit, credit.subtract(debit));
  }
}
