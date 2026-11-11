package io.app._cents.backend.payments.mappers;

import io.app._cents.backend.categories.models.Category;
import io.app._cents.backend.payments.dtos.repository.PaymentRecordRow;
import io.app._cents.backend.payments.models.PaymentRecord;
import io.app._cents.backend.payments.models.enums.TransactionType;

public class PaymentRecordMapper {

  public static PaymentRecord toPaymentRecord(PaymentRecordRow row) {
    if (row == null) return null;

    return PaymentRecord.builder()
        .id(String.valueOf(row.getId()))
        .amount(row.getAmount())
        .recipientName(row.getRecipientName())
        .recipientIdentifier(row.getRecipientIdentifier())
        .note(row.getNote())
        .paymentMedium(row.getPaymentMedium())
        .transactionType(
            row.getTransactionType() == null
                ? null
                : TransactionType.valueOf(row.getTransactionType()))
        .paidAt(row.getPaidAt())
        .category(
            Category.builder()
                .id(String.valueOf(row.getCategoryId()))
                .name(row.getCategoryName())
                .code(row.getCategoryCode())
                .image(row.getCategoryImage())
                .build())
        .build();
  }
}
