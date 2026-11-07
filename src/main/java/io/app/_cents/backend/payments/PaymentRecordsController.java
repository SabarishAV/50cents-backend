package io.app._cents.backend.payments;

import io.app._cents.backend.common.dtos.Response;
import io.app._cents.backend.payments.dtos.create.CreatePaymentRecordRequest;
import io.app._cents.backend.payments.dtos.update.UpdatePaymentRecordRequest;
import io.app._cents.backend.payments.models.PaymentRecord;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
            @Valid @RequestBody CreatePaymentRecordRequest payment
    ) {
        return paymentRecordsService.createPaymentRecord(payment);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete record", description = "Delete payment record")
    public ResponseEntity<Response> deletePaymentRecord(
            @Parameter(description = "Payment record ID", required = true)
            @PathVariable String id
            ) {
        return paymentRecordsService.deletePaymentRecord(id);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update record", description = "Update payment record")
    public ResponseEntity<Response> updatePaymentRecord(
            @Parameter(description = "Payment record ID", required = true)
            @PathVariable String id,
            @Valid @RequestBody UpdatePaymentRecordRequest body
    ) {
        return paymentRecordsService.updatePaymentRecord(id, body);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get record", description = "Get payment record")
    public ResponseEntity<PaymentRecord> getPaymentRecord(
            @Parameter(description = "Payment record ID", required = true)
            @PathVariable String id
    ) {
        return paymentRecordsService.getById(id);
    }

}
