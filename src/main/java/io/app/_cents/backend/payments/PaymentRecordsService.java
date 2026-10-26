package io.app._cents.backend.payments;

import io.app._cents.backend.categories.CategoriesService;
import io.app._cents.backend.categories.models.Category;
import io.app._cents.backend.common.exception.NotFoundException;
import io.app._cents.backend.common.utils.CurrentUser;
import io.app._cents.backend.payments.dtos.create.CreatePaymentRecordRequest;
import io.app._cents.backend.payments.dtos.create.CreatePaymentRecordResponse;
import io.app._cents.backend.payments.dtos.delete.DeletePaymentRecordResponse;
import io.app._cents.backend.payments.models.PaymentRecord;
import io.app._cents.backend.users.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class PaymentRecordsService {

    @Autowired PaymentRecordsRepository paymentRecordsRepository;

    @Autowired CategoriesService categoriesService;
    @Autowired UsersService usersService;

    public ResponseEntity<CreatePaymentRecordResponse> createPaymentRecord(
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

        return new ResponseEntity<>(new CreatePaymentRecordResponse(
                true,
                "Payment created successfully"
        ), HttpStatus.CREATED);

    }

    @Transactional
    public ResponseEntity<DeletePaymentRecordResponse> deletePaymentRecord(String id){
        PaymentRecord record = paymentRecordsRepository
                .findByIdAndUserId(id, CurrentUser.id())
                .orElseThrow(() -> new NotFoundException("Payment Record"));
        paymentRecordsRepository.deleteById(id);
        return new ResponseEntity<>(
                new DeletePaymentRecordResponse(
                        true,
                        "Payment record deleted successfully"
                ),
                HttpStatus.OK
        );
    }
}
