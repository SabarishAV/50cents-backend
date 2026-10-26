package io.app._cents.backend.payments.dtos.delete;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeletePaymentRecordResponse {

    private boolean success;

    private String message;

}
