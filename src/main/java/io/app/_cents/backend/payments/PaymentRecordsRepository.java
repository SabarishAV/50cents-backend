package io.app._cents.backend.payments;

import io.app._cents.backend.payments.dtos.repository.PaymentRecordRow;
import io.app._cents.backend.payments.dtos.repository.PaymentTotalsRow;
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
                      pr.recipient_identifier AS "recipientIdentifier",
                      pr.note AS "note",
                      pr.payment_medium AS "paymentMedium",
                      pr.transaction_type AS "transactionType",
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
                      AND (
                        -- custom range: (year, month) to (toYear, toMonth), inclusive
                        (
                          CAST(:toYear AS INTEGER) IS NOT NULL
                          AND CAST(:toMonth AS INTEGER) IS NOT NULL
                          AND (EXTRACT(YEAR FROM pr.paid_at) * 12 + EXTRACT(MONTH FROM pr.paid_at))
                              BETWEEN (:year * 12 + :month)
                              AND (CAST(:toYear AS INTEGER) * 12 + CAST(:toMonth AS INTEGER))
                        )
                        OR
                        -- default behaviour
                        (
                          (CAST(:toYear AS INTEGER) IS NULL OR CAST(:toMonth AS INTEGER) IS NULL)
                          AND EXTRACT(YEAR FROM pr.paid_at) = :year
                          AND (
                            (:onlyThisMonth = true AND EXTRACT(MONTH FROM pr.paid_at) = :month)
                            OR
                            (:onlyThisMonth = false AND EXTRACT(MONTH FROM pr.paid_at) <= :month)
                          )
                        )
                      )
                      AND (
                        CAST(:transactionType AS VARCHAR) IS NULL
                        OR pr.transaction_type = CAST(:transactionType AS VARCHAR)
                      )
                    ORDER BY pr.paid_at DESC, pr.id DESC
                    LIMIT :pageSize OFFSET (:pageNumber * :pageSize)
                    """,
      nativeQuery = true)
  List<PaymentRecordRow> getPaymentsOfUser(
      @Param("userId") String userId,
      @Param("year") int year,
      @Param("month") int month,
      @Param("toYear") Integer toYear,
      @Param("toMonth") Integer toMonth,
      @Param("onlyThisMonth") boolean onlyThisMonth,
      @Param("transactionType") String transactionType,
      @Param("pageNumber") int pageNumber,
      @Param("pageSize") int pageSize);

  @Query(
      value =
          """
                    SELECT COUNT(*)
                    FROM payment_records pr
                    WHERE
                      pr.user_id = :userId
                      AND (
                        -- custom range: (year, month) to (toYear, toMonth), inclusive
                        (
                          CAST(:toYear AS INTEGER) IS NOT NULL
                          AND CAST(:toMonth AS INTEGER) IS NOT NULL
                          AND (EXTRACT(YEAR FROM pr.paid_at) * 12 + EXTRACT(MONTH FROM pr.paid_at))
                              BETWEEN (:year * 12 + :month)
                              AND (CAST(:toYear AS INTEGER) * 12 + CAST(:toMonth AS INTEGER))
                        )
                        OR
                        -- default behaviour
                        (
                          (CAST(:toYear AS INTEGER) IS NULL OR CAST(:toMonth AS INTEGER) IS NULL)
                          AND EXTRACT(YEAR FROM pr.paid_at) = :year
                          AND (
                            (:onlyThisMonth = true AND EXTRACT(MONTH FROM pr.paid_at) = :month)
                            OR
                            (:onlyThisMonth = false AND EXTRACT(MONTH FROM pr.paid_at) <= :month)
                          )
                        )
                      )
                      AND (
                        CAST(:transactionType AS VARCHAR) IS NULL
                        OR pr.transaction_type = CAST(:transactionType AS VARCHAR)
                      );
                    """,
      nativeQuery = true)
  long countPaymentsOfUser(
      @Param("userId") String userId,
      @Param("year") int year,
      @Param("month") int month,
      @Param("toYear") Integer toYear,
      @Param("toMonth") Integer toMonth,
      @Param("onlyThisMonth") boolean onlyThisMonth,
      @Param("transactionType") String transactionType);

  @Query(
      value =
          """
                    SELECT
                      COALESCE(SUM(pr.amount) FILTER (WHERE pr.transaction_type = 'CREDIT'), 0) AS "totalCredit",
                      COALESCE(SUM(pr.amount) FILTER (WHERE pr.transaction_type = 'DEBIT'), 0) AS "totalDebit"
                    FROM payment_records pr
                    WHERE
                      pr.user_id = :userId
                      AND (
                        -- custom range: (year, month) to (toYear, toMonth), inclusive
                        (
                          CAST(:toYear AS INTEGER) IS NOT NULL
                          AND CAST(:toMonth AS INTEGER) IS NOT NULL
                          AND (EXTRACT(YEAR FROM pr.paid_at) * 12 + EXTRACT(MONTH FROM pr.paid_at))
                              BETWEEN (:year * 12 + :month)
                              AND (CAST(:toYear AS INTEGER) * 12 + CAST(:toMonth AS INTEGER))
                        )
                        OR
                        -- default behaviour, same as the list query
                        (
                          (CAST(:toYear AS INTEGER) IS NULL OR CAST(:toMonth AS INTEGER) IS NULL)
                          AND EXTRACT(YEAR FROM pr.paid_at) = :year
                          AND (
                            (:onlyThisMonth = true AND EXTRACT(MONTH FROM pr.paid_at) = :month)
                            OR
                            (:onlyThisMonth = false AND EXTRACT(MONTH FROM pr.paid_at) <= :month)
                          )
                        )
                      )
                    """,
      nativeQuery = true)
  PaymentTotalsRow getPaymentTotalsOfUser(
      @Param("userId") String userId,
      @Param("year") int year,
      @Param("month") int month,
      @Param("onlyThisMonth") boolean onlyThisMonth,
      @Param("toYear") Integer toYear,
      @Param("toMonth") Integer toMonth);
}
