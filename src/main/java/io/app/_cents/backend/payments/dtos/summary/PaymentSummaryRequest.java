package io.app._cents.backend.payments.dtos.summary;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Filters for the payment summary (total credit and debit)")
public record PaymentSummaryRequest(
    @Schema(description = "Start year", example = "2026")
        @NotNull(message = "year is required")
        @Min(value = 2000, message = "year must be 2000 or later")
        @Max(value = 2100, message = "year must be 2100 or earlier")
        Integer year,
    @Schema(description = "Start month (1-12)", example = "11")
        @NotNull(message = "month is required")
        @Min(value = 1, message = "month must be between 1 and 12")
        @Max(value = 12, message = "month must be between 1 and 12")
        Integer month,
    @Schema(
            description =
                "true: only the given month. false: January up to the given month. "
                    + "Ignored when toYear and toMonth are provided",
            example = "true",
            defaultValue = "false")
        boolean onlyThisMonth,
    @Schema(
            description = "End year for a custom range (use together with toMonth)",
            example = "2026")
        @Min(value = 2000, message = "toYear must be 2000 or later")
        @Max(value = 2100, message = "toYear must be 2100 or earlier")
        Integer toYear,
    @Schema(
            description = "End month for a custom range, 1-12 (use together with toYear)",
            example = "12")
        @Min(value = 1, message = "toMonth must be between 1 and 12")
        @Max(value = 12, message = "toMonth must be between 1 and 12")
        Integer toMonth) {

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
      return true; // other validators handle missing values
    }
    return (toYear * 12 + toMonth) >= (year * 12 + month);
  }
}
