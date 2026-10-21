package io.app._cents.backend.payments.dtos.create;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreatePaymentRecordResponse {

    private boolean success;

    private String message;

}
