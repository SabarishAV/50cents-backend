package io.app._cents.backend.payments;

import io.app._cents.backend.payments.models.PaymentRecord;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRecordsRepository extends JpaRepository<PaymentRecord, String> {
  Optional<PaymentRecord> findByIdAndUserId(String id, String userId);
}
