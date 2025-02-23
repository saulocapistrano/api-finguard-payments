package com.finguard.apifinguardpayments.infrastructure;
import com.finguard.apifinguardpayments.domain.Currency;
import com.finguard.apifinguardpayments.domain.Payment;
import com.finguard.apifinguardpayments.domain.PaymentMethod;
import com.finguard.apifinguardpayments.domain.PaymentStatus;
import com.finguard.apifinguardpayments.domain.RecurrenceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing Payment entity persistence.
 * Provides advanced queries for payment processing, fraud detection, and audit logging.
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    /**
     * Finds a payment by its unique transaction ID.
     * @param transactionId the unique transaction identifier
     * @return an Optional containing the payment if found
     */
    Optional<Payment> findByTransactionId(String transactionId);

    /**
     * Finds all payments with a specific status.
     * @param status the payment status (e.g., PENDING, COMPLETED, FAILED)
     * @return a list of payments with the given status
     */
    List<Payment> findByStatus(PaymentStatus status);

    /**
     * Finds all payments made by a specific payer.
     * @param payerId the ID of the payer
     * @return a list of payments associated with the given payer
     */
    List<Payment> findByPayerId(String payerId);

    /**
     * Finds all payments received by a specific payee.
     * @param payeeId the ID of the payee
     * @return a list of payments associated with the given payee
     */
    List<Payment> findByPayeeId(String payeeId);

    /**
     * Finds all payments within a specific date range.
     * @param startDate start of the date range
     * @param endDate end of the date range
     * @return a list of payments within the given date range
     */
    List<Payment> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Finds all fraudulent payments.
     * @return a list of fraudulent payments
     */
    List<Payment> findByIsFraudulentTrue();

    /**
     * Finds all payments that can be refunded (status COMPLETED).
     * @return a list of refundable payments
     */
    List<Payment> findByStatusAndRefundedAmountLessThan(PaymentStatus status, BigDecimal refundedAmount);

    /**
     * Finds payments by currency.
     * @param currency the currency type
     * @return a list of payments in the specified currency
     */
    List<Payment> findByCurrency(Currency currency);

    /**
     * Finds payments by payment method.
     * @param paymentMethod the payment method
     * @return a list of payments made with the specified method
     */
    List<Payment> findByPaymentMethod(PaymentMethod paymentMethod);

    /**
     * Finds all recurring payments.
     * @return a list of recurring payments
     */
    List<Payment> findByRecurrenceNot(RecurrenceType recurrenceType);

    /**
     * Counts the total number of payments processed.
     * @return the total count of payments
     */
    long count();

    /**
     * Counts the total number of successful payments.
     * @return the count of successful payments
     */
    long countByStatus(PaymentStatus status);

    /**
     * Finds all payments that failed and have retries available.
     * @param retryCount the maximum number of retries allowed
     * @return a list of failed payments that can be retried
     */
    List<Payment> findByStatusAndRetryCountLessThan(PaymentStatus status, int retryCount);

    /**
     * Finds all payments flagged for fraud within a given date range.
     * @param startDate the start of the date range
     * @param endDate the end of the date range
     * @return a list of fraudulent payments within the date range
     */
    List<Payment> findByIsFraudulentTrueAndCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Retrieves a list of all unique payment methods used.
     * @return a list of distinct payment methods
     */
    @Query("SELECT DISTINCT p.paymentMethod FROM Payment p")
    List<PaymentMethod> findDistinctPaymentMethods();

    /**
     * Retrieves a list of all unique currencies used in payments.
     * @return a list of distinct currencies
     */
    @Query("SELECT DISTINCT p.currency FROM Payment p")
    List<Currency> findDistinctCurrencies();
}