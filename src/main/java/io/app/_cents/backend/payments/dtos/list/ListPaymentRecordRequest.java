package io.app._cents.backend.payments.dtos.list;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.app._cents.backend.payments.models.enums.TransactionType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.time.Year;
import lombok.Data;

@Data
@Schema(description = "Filters and pagination for listing payment records")
public class ListPaymentRecordRequest {

  @Schema(
      description = "Year of the payment records to fetch (defaults to current year)",
      example = "2026",
      minimum = "1970",
      maximum = "2100",
      nullable = true)
  @Min(value = 1970, message = "year must be >= 1970")
  @Max(value = 2100, message = "year must be <= 2100")
  private Integer year = Year.now().getValue();

  @Schema(
      description = "Month (1–12). Defaults to the current month.",
      example = "11",
      minimum = "1",
      maximum = "12",
      nullable = true)
  @Min(value = 1, message = "month must be between 1 and 12")
  @Max(value = 12, message = "month must be between 1 and 12")
  private Integer month = java.time.LocalDate.now().getMonthValue();

  @Schema(
      description =
          "End year for a custom range. Must be sent together with toMonth. "
              + "When both are provided, onlyThisMonth is ignored.",
      example = "2026",
      minimum = "2000",
      maximum = "2100",
      nullable = true)
  @Min(value = 2000, message = "toYear must be 2000 or later")
  @Max(value = 2100, message = "toYear must be 2100 or earlier")
  private Integer toYear;

  @Schema(
      description =
          "End month (1–12) for a custom range. Must be sent together with toYear. "
              + "When both are provided, onlyThisMonth is ignored.",
      example = "12",
      minimum = "1",
      maximum = "12",
      nullable = true)
  @Min(value = 1, message = "toMonth must be between 1 and 12")
  @Max(value = 12, message = "toMonth must be between 1 and 12")
  private Integer toMonth;

  @Schema(
      description = "If true, restrict results to the current month only",
      example = "false",
      defaultValue = "false",
      nullable = true)
  private Boolean onlyThisMonth = false;

  @Schema(
      description = "Whether money went out (DEBIT) or came in (CREDIT)",
      example = "DEBIT",
      requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  private TransactionType transactionType;

  @Schema(
      description = "Page number.",
      example = "1",
      minimum = "1",
      defaultValue = "1",
      nullable = true)
  @Min(value = 1, message = "pageNumber must be >= 1")
  private Integer pageNumber = 1;

  @Schema(
      description = "Page size (1–100). Defaults to 15.",
      example = "15",
      minimum = "1",
      maximum = "100",
      defaultValue = "15",
      nullable = true)
  @Min(value = 1, message = "size must be >= 1")
  @Max(value = 100, message = "size must be <= 100")
  private Integer pageSize = 15;

  @JsonIgnore
  @Schema(hidden = true)
  @AssertTrue(message = "toYear and toMonth must be provided together")
  public boolean isCustomRangeComplete() {
    return (toYear == null) == (toMonth == null);
  }

  @JsonIgnore
  @Schema(hidden = true)
  @AssertTrue(message = "end of range must not be before the start")
  public boolean isCustomRangeOrdered() {
    if (toYear == null || toMonth == null || year == null || month == null) {
      return true;
    }
    return (toYear * 12 + toMonth) >= (year * 12 + month);
  }
}
