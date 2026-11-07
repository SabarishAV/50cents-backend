package io.app._cents.backend.seeders;

import io.app._cents.backend.categories.CategoriesRepository;
import io.app._cents.backend.categories.models.Category;
import io.app._cents.backend.payments.PaymentRecordsRepository;
import io.app._cents.backend.payments.models.PaymentRecord;
import io.app._cents.backend.users.UsersRepository;
import io.app._cents.backend.users.models.User;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Profile("seed")
@Order(3)
public class PaymentRecordSeeder implements CommandLineRunner {

    private final CategoriesRepository categoriesRepository;
    private final UsersRepository usersRepository;
    private final PaymentRecordsRepository paymentRecordsRepository;


    public PaymentRecordSeeder(
            CategoriesRepository categoriesRepository,
            UsersRepository usersRepository,
            PaymentRecordsRepository paymentRecordsRepository
    ) {
        this.categoriesRepository = categoriesRepository;
        this.usersRepository = usersRepository;
        this.paymentRecordsRepository = paymentRecordsRepository;
    }


    @Override
    public void run(String... args) throws Exception {
        long existingRecordsLength = paymentRecordsRepository.count();
        if(existingRecordsLength > 0){
            System.out.println("Payment records already seeded");
            return;
        }

        List<Category> categories = categoriesRepository.findAll();
        if(categories.isEmpty()){
            throw new Exception("Categories not seeded");
        }
        Map<String, Category> categoryByCode = categories.stream()
                .collect(Collectors.toMap(
                        Category::getCode,
                        category -> category
                ));

        User user = usersRepository.findByUsername("user");
        if(user == null){
            throw new Exception("User not seeded");
        }

        List<PaymentRecord> payments = List.of(
                PaymentRecord.builder()
                        .amount(BigDecimal.valueOf(1000))
                        .recipientName("ABC Petrol Pump")
                        .note("Petrol")
                        .paymentMedium("Google Pay")
                        .user(user)
                        .category(categoryByCode.get("FUEL"))
                        .build(),
                PaymentRecord.builder()
                        .amount(BigDecimal.valueOf(250))
                        .recipientName("XYZ Stores")
                        .note("Grocery Items")
                        .paymentMedium("Amazon Pay")
                        .user(user)
                        .category(categoryByCode.get("GROCERIES"))
                        .build()
        );

        paymentRecordsRepository.saveAll(payments);

    }

}
