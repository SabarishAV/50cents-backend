package io.app._cents.backend.payments;

import io.app._cents.backend.common.dtos.Response;
import io.app._cents.backend.common.pagination.PageResponse;
import io.app._cents.backend.common.utils.CurrentUser;
import io.app._cents.backend.payments.dtos.create.CreatePaymentRecordRequest;
import io.app._cents.backend.payments.dtos.list.ListPaymentRecordRequest;
import io.app._cents.backend.payments.dtos.summary.PaymentSummaryRequest;
import io.app._cents.backend.payments.dtos.summary.PaymentSummaryResponse;
import io.app._cents.backend.payments.dtos.update.UpdatePaymentRecordRequest;
import io.app._cents.backend.payments.models.PaymentRecord;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment-records")
@Tag(name = "Payment Record APIs", description = "User's payment record APIs")
public class PaymentRecordsController {

  @Autowired PaymentRecordsService paymentRecordsService;

  @PostMapping()
  @Operation(summary = "Create record", description = "Create payment record")
  public ResponseEntity<Response> createPaymentRecord(
      @Valid @RequestBody CreatePaymentRecordRequest payment) {
    return paymentRecordsService.createPaymentRecord(payment);
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Delete record", description = "Delete payment record")
  public ResponseEntity<Response> deletePaymentRecord(
      @Parameter(description = "Payment record ID", required = true) @PathVariable String id) {
    return paymentRecordsService.deletePaymentRecord(id);
  }

  @PatchMapping("/{id}")
  @Operation(summary = "Update record", description = "Update payment record")
  public ResponseEntity<Response> updatePaymentRecord(
      @Parameter(description = "Payment record ID", required = true) @PathVariable String id,
      @Valid @RequestBody UpdatePaymentRecordRequest body) {
    return paymentRecordsService.updatePaymentRecord(id, body);
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get record", description = "Get payment record")
  public ResponseEntity<PaymentRecord> getPaymentRecord(
      @Parameter(description = "Payment record ID", required = true) @PathVariable String id) {
    return paymentRecordsService.getById(id);
  }

  @GetMapping()
  @Operation(summary = "List records", description = "List payment records of user")
  public ResponseEntity<PageResponse<PaymentRecord>> listPaymentRecords(
      @Valid @ParameterObject ListPaymentRecordRequest req) {
    return paymentRecordsService.listPaymentRecords(CurrentUser.id(), req);
  }

  @GetMapping("/summary")
  @Operation(
      summary = "Get payment summary",
      description =
          "Returns the total credit (income), total debit (spend) and net for the logged-in user. "
              + "By default, returns the given month (onlyThisMonth=true) or January up to the given "
              + "month (onlyThisMonth=false). If both toYear and toMonth are provided, returns the "
              + "custom range from year/month to toYear/toMonth (inclusive) and onlyThisMonth is ignored.")
  public ResponseEntity<PaymentSummaryResponse> getSummary(
      @Valid @ParameterObject PaymentSummaryRequest req) {
    return paymentRecordsService.getSummary(CurrentUser.id(), req);
  }
}
