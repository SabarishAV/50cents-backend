package io.app._cents.backend.payments;

import io.app._cents.backend.categories.services.CategoriesService;
import io.app._cents.backend.categories.models.Category;
import io.app._cents.backend.common.dtos.Response;
import io.app._cents.backend.common.exceptions.NotFoundException;
import io.app._cents.backend.common.utils.CurrentUser;
import io.app._cents.backend.payments.dtos.create.CreatePaymentRecordRequest;
import io.app._cents.backend.payments.dtos.update.UpdatePaymentRecordRequest;
import io.app._cents.backend.payments.models.PaymentRecord;
import io.app._cents.backend.users.UsersService;
import io.app._cents.backend.users.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class PaymentRecordsService {

    @Autowired PaymentRecordsRepository paymentRecordsRepository;

    @Autowired CategoriesService categoriesService;
    @Autowired UsersService usersService;

    public ResponseEntity<Response> createPaymentRecord(
            CreatePaymentRecordRequest payment) {

        List<Category> categories = categoriesService.listAll();

        PaymentRecord record = PaymentRecord.builder()
                .amount(payment.getAmount())
                .recipientName(payment.getRecipientName())
                .note(payment.getNote())
                .paymentMedium(payment.getPaymentMedium())
                .paidAt(Instant.now())
                .user(usersService.getReferenceById(CurrentUser.id()))
                .build();

        // TODO: fix auto category selection
        if(!payment.getCategory().isEmpty()){
            for(Category category: categories){
                if(category.getName().toLowerCase().contains(payment.getCategory().toLowerCase())){
                    record.setCategory(category);
                    break;
                }
            }
        }

        // Setting category to "OTHER" if null
        if(record.getCategory() == null){
            for(Category category: categories){
                if(category.getCode().contains("OTHER")){
                    record.setCategory(category);
                    break;
                }
            }
        }
        paymentRecordsRepository.save(record);

        return new ResponseEntity<>(new Response(
                true,
                "Payment created successfully"
        ), HttpStatus.CREATED);

    }

    @Transactional
    public ResponseEntity<Response> deletePaymentRecord(String id){
        PaymentRecord record = paymentRecordsRepository
                .findByIdAndUserId(id, CurrentUser.id())
                .orElseThrow(() -> new NotFoundException("Payment Record"));
        paymentRecordsRepository.deleteById(record.getId());
        return new ResponseEntity<>(
                new Response(
                        true,
                        "Payment record deleted successfully"
                ),
                HttpStatus.OK
        );
    }

    @Transactional
    public ResponseEntity<Response> updatePaymentRecord( String paymentId, UpdatePaymentRecordRequest request) {
        PaymentRecord payment = paymentRecordsRepository.findByIdAndUserId(paymentId, CurrentUser.id())
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
        if (request.getCategoryId() != null) {
            Category category = categoriesService.findById(request.getCategoryId().trim());
            if (category == null) {
                throw new NotFoundException("Category not found");
            }
            payment.setCategory(category);
        }
        paymentRecordsRepository.save(payment);

        return new ResponseEntity<>(
                new Response(
                        true,
                        "Payment record updated successfully"
                ),
                HttpStatus.OK
        );
    }

    public ResponseEntity<PaymentRecord> getById(String paymentId){
        PaymentRecord paymentRecord = paymentRecordsRepository
                .findByIdAndUserId(paymentId, CurrentUser.id())
                .orElseThrow(() -> { throw new NotFoundException("Payment Record"); });
        User oldUser = paymentRecord.getUser();
        paymentRecord.setUser(
                User.builder()
                .id(oldUser.getId())
                .name(oldUser.getName())
                        .username(oldUser.getUsername())
                        .email(oldUser.getEmail())
                        .avatar(oldUser.getAvatar())
                        .build()
        );
        return new ResponseEntity<>(paymentRecord, HttpStatus.OK);
    }
}
