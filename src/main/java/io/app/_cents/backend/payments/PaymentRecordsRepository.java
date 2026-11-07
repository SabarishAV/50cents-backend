package io.app._cents.backend.payments;

import io.app._cents.backend.payments.dtos.repository.PaymentRecordRow;
import io.app._cents.backend.payments.models.PaymentRecord;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRecordsRepository extends JpaRepository<PaymentRecord, String> {
  Optional<PaymentRecord> findByIdAndUserId(String id, String userId);

  @Query(
      value =
          """
                            SELECT
                              pr.id AS "id",
                              pr.amount AS "amount",
                              pr.recipient_name AS "recipientName",
                              pr.note AS "note",
                              pr.payment_medium AS "paymentMedium",
                              pr.paid_at AS "paidAt",
                              c.id AS "categoryId",
                              c.name AS "categoryName",
                              c.code AS "categoryCode",
                              c.image AS "categoryImage"
                            FROM payment_records pr
                            INNER JOIN users u ON u.id = pr.user_id
                            INNER JOIN categories c ON c.id = pr.category_id
                            WHERE
                              pr.user_id = :userId
                              AND EXTRACT(YEAR FROM pr.paid_at) = :year
                              AND (
                                (:onlyThisMonth = true AND EXTRACT(MONTH FROM pr.paid_at) = :month)
                                OR
                                (:onlyThisMonth = false AND EXTRACT(MONTH FROM pr.paid_at) <= :month)
                              )
                            ORDER BY pr.paid_at DESC, pr.id DESC
                            LIMIT :pageSize OFFSET (:pageNumber * :pageSize);
                                     \s""",
      nativeQuery = true)
  List<PaymentRecordRow> getPaymentsOfUser(
      @Param("userId") String userId,
      @Param("year") int year,
      @Param("month") int month,
      @Param("onlyThisMonth") boolean onlyThisMonth,
      @Param("pageNumber") int pageNumber,
      @Param("pageSize") int pageSize);

  @Query(
      value =
          """
                    SELECT COUNT(*)
                    FROM payment_records pr
                    WHERE
                        pr.user_id = :userId
                        AND EXTRACT(YEAR FROM pr.paid_at) = :year
                        AND (
                            (:onlyThisMonth = true AND EXTRACT(MONTH FROM pr.paid_at) = :month)
                            OR
                            (:onlyThisMonth = false AND EXTRACT(MONTH FROM pr.paid_at) <= :month)
                        );
                    """,
      nativeQuery = true)
  long countPaymentsOfUser(
      @Param("userId") String userId,
      @Param("year") int year,
      @Param("month") int month,
      @Param("onlyThisMonth") boolean onlyThisMonth);
}
