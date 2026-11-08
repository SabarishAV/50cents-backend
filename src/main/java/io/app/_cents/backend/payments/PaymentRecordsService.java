package io.app._cents.backend.payments;

import static io.app._cents.backend.payments.mappers.PaymentRecordMapper.toPaymentRecord;

import io.app._cents.backend.categories.models.Category;
import io.app._cents.backend.categories.services.CategoriesService;
import io.app._cents.backend.common.dtos.Response;
import io.app._cents.backend.common.exceptions.NotFoundException;
import io.app._cents.backend.common.pagination.PageResponse;
import io.app._cents.backend.common.utils.CurrentUser;
import io.app._cents.backend.payments.dtos.create.CreatePaymentRecordRequest;
import io.app._cents.backend.payments.dtos.list.ListPaymentRecordRequest;
import io.app._cents.backend.payments.dtos.repository.PaymentTotalsRow;
import io.app._cents.backend.payments.dtos.summary.PaymentSummaryRequest;
import io.app._cents.backend.payments.dtos.summary.PaymentSummaryResponse;
import io.app._cents.backend.payments.dtos.update.UpdatePaymentRecordRequest;
import io.app._cents.backend.payments.models.PaymentRecord;
import io.app._cents.backend.payments.models.enums.TransactionType;
import io.app._cents.backend.users.UsersService;
import io.app._cents.backend.users.models.User;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentRecordsService {

  @Autowired PaymentRecordsRepository paymentRecordsRepository;

  @Autowired CategoriesService categoriesService;
  @Autowired UsersService usersService;

  public ResponseEntity<Response> createPaymentRecord(CreatePaymentRecordRequest payment) {

    Category category =
        categoriesService
            .getByCode(payment.getCategory() != null ? payment.getCategory() : "OTHER")
            .orElseThrow(
                () -> {
                  throw new NotFoundException("Category");
                });

    User user =
        usersService
            .findById(CurrentUser.id())
            .orElseThrow(
                () -> {
                  throw new NotFoundException("User");
                });

    if (payment.isUpdateBankBalance()) {
      BigDecimal amount = payment.getAmount();
      if (payment.getTransactionType() == TransactionType.DEBIT) {
        amount = amount.negate();
      }
      user.setBankBalance(user.getBankBalance().add(amount));
    }

    PaymentRecord record =
        PaymentRecord.builder()
            .amount(payment.getAmount())
            .recipientName(payment.getRecipientName())
            .note(payment.getNote())
            .paymentMedium(payment.getPaymentMedium())
            .transactionType(payment.getTransactionType())
            .bankUpdated(payment.isUpdateBankBalance())
            .paidAt(payment.getPaidAt() != null ? payment.getPaidAt() : Instant.now())
            .user(user)
            .category(category)
            .build();

    paymentRecordsRepository.save(record);

    return new ResponseEntity<>(
        new Response(true, "Payment created successfully"), HttpStatus.CREATED);
  }

  @Transactional
  public ResponseEntity<Response> deletePaymentRecord(String id) {
    PaymentRecord record =
        paymentRecordsRepository
            .findByIdAndUserId(id, CurrentUser.id())
            .orElseThrow(() -> new NotFoundException("Payment Record"));
    paymentRecordsRepository.deleteById(record.getId());
    return new ResponseEntity<>(
        new Response(true, "Payment record deleted successfully"), HttpStatus.OK);
  }

  @Transactional
  public ResponseEntity<Response> updatePaymentRecord(
      String paymentId, UpdatePaymentRecordRequest request) {
    PaymentRecord payment =
        paymentRecordsRepository
            .findByIdAndUserId(paymentId, CurrentUser.id())
            .orElseThrow(() -> new NotFoundException("Payment record not found"));

    if (request.getAmount() != null) {
      payment.setAmount(request.getAmount());
    }
    if (request.getRecipientName() != null) {
      payment.setRecipientName(request.getRecipientName().trim());
    }
    if (request.getNote() != null) {
      payment.setNote(request.getNote().trim());
    }
    if (request.getPaymentMedium() != null) {
      payment.setPaymentMedium(request.getPaymentMedium().trim());
    }
    if (request.getTransactionType() != null) {
      payment.setTransactionType(request.getTransactionType());
    }
    if (request.getCategoryId() != null) {
      Category category = categoriesService.findById(request.getCategoryId().trim());
      if (category == null) {
        throw new NotFoundException("Category not found");
      }
      payment.setCategory(category);
    }
    paymentRecordsRepository.save(payment);

    return new ResponseEntity<>(
        new Response(true, "Payment record updated successfully"), HttpStatus.OK);
  }

  public ResponseEntity<PaymentRecord> getById(String paymentId) {
    PaymentRecord paymentRecord =
        paymentRecordsRepository
            .findByIdAndUserId(paymentId, CurrentUser.id())
            .orElseThrow(
                () -> {
                  throw new NotFoundException("Payment Record");
                });
    User oldUser = paymentRecord.getUser();
    paymentRecord.setUser(
        User.builder()
            .id(oldUser.getId())
            .name(oldUser.getName())
            .username(oldUser.getUsername())
            .email(oldUser.getEmail())
            .avatar(oldUser.getAvatar())
            .build());
    return new ResponseEntity<>(paymentRecord, HttpStatus.OK);
  }

  public ResponseEntity<PageResponse<PaymentRecord>> listPaymentRecords(
      String userId, ListPaymentRecordRequest req) {

    List<PaymentRecord> rows =
        paymentRecordsRepository
            .getPaymentsOfUser(
                userId,
                req.getYear(),
                req.getMonth(),
                req.getToYear(),
                req.getToMonth(),
                req.getOnlyThisMonth(),
                req.getTransactionType() == null ? null : req.getTransactionType().name(),
                req.getPageNumber() - 1,
                req.getPageSize())
            .stream()
            .map(row -> toPaymentRecord(row))
            .toList();
    long total =
        paymentRecordsRepository.countPaymentsOfUser(
            CurrentUser.id(),
            req.getYear(),
            req.getMonth(),
            req.getToYear(),
            req.getToMonth(),
            req.getOnlyThisMonth(),
            req.getTransactionType() == null ? null : req.getTransactionType().name());

    PageResponse<PaymentRecord> res =
        PageResponse.toPaginated(rows, req.getPageNumber(), req.getPageSize(), total);

    return new ResponseEntity<>(res, HttpStatus.OK);
  }

  public ResponseEntity<PaymentSummaryResponse> getSummary(
      String userId, PaymentSummaryRequest request) {
    PaymentTotalsRow totals =
        paymentRecordsRepository.getPaymentTotalsOfUser(
            userId,
            request.year(),
            request.month(),
            request.onlyThisMonth(),
            request.toYear(),
            request.toMonth());

    return new ResponseEntity<>(
        PaymentSummaryResponse.of(totals.getTotalCredit(), totals.getTotalDebit()), HttpStatus.OK);
  }
}
