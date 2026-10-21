package io.app._cents.backend.payments;

import io.app._cents.backend.payments.dtos.create.CreatePaymentRecordRequest;
import io.app._cents.backend.payments.dtos.create.CreatePaymentRecordResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
