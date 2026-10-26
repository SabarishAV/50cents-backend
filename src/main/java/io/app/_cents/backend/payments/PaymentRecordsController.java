package io.app._cents.backend.payments;

import io.app._cents.backend.payments.dtos.create.CreatePaymentRecordRequest;
import io.app._cents.backend.payments.dtos.create.CreatePaymentRecordResponse;
import io.app._cents.backend.payments.dtos.delete.DeletePaymentRecordResponse;
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
    public ResponseEntity<CreatePaymentRecordResponse> createPaymentRecord(
            @Valid @RequestBody CreatePaymentRecordRequest payment) {
        return paymentRecordsService.createPaymentRecord(payment);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete record", description = "Delete payment record")
    public ResponseEntity<DeletePaymentRecordResponse> deletePaymentRecord(
            @Parameter(description = "Payment record ID", required = true)
            @PathVariable String id
            ) {
        return paymentRecordsService.deletePaymentRecord(id);
    }

}
