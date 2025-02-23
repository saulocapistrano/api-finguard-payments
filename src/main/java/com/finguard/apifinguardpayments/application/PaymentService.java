package com.finguard.apifinguardpayments.application;

import com.finguard.apifinguardpayments.domain.*;
import com.finguard.apifinguardpayments.infrastructure.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final RedisService redisService;

    public PaymentService(PaymentRepository paymentRepository, RedisService redisService) {
        this.paymentRepository = paymentRepository;
        this.redisService = redisService;
    }

    // =========================== //
    //       CRUD OPERATIONS       //
    // =========================== //

    /**
     * Creates a new payment with default values.
     * @param transactionId The unique transaction ID.
     * @param amount The payment amount.
     * @param currency The currency of the payment.
     * @param paymentMethod The payment method used.
     * @param payerId The payer's unique identifier.
     * @param payeeId The payee's unique identifier.
     * @return The newly created payment.
     */
    @Transactional
    public Payment createPayment(String transactionId, BigDecimal amount, Currency currency,
                                 PaymentMethod paymentMethod, String payerId, String payeeId) {
        validatePaymentDetails(amount, currency, paymentMethod, payerId, payeeId);

        Payment payment = new Payment(
                null, transactionId, amount, currency, PaymentStatus.PENDING,
                paymentMethod, RecurrenceType.ONCE, false, null, payerId, payeeId,
                null, null, BigDecimal.ZERO, null, null, null, 0,
                LocalDateTime.now(), LocalDateTime.now()
        );

        Payment savedPayment = paymentRepository.save(payment);
        cachePaymentStatus(savedPayment.getId(), savedPayment.getStatus().name());
        return savedPayment;
    }

    /**
     * Retrieves a payment by its ID.
     * @param id The ID of the payment.
     * @return The found payment.
     */
    public Payment getPaymentById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found with ID: " + id));
    }

    /**
     * Retrieves a payment by its transaction ID.
     * @param transactionId The unique transaction ID.
     * @return The found payment.
     */
    public Payment getPaymentByTransactionId(String transactionId) {
        return paymentRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new RuntimeException("Payment not found with transaction ID: " + transactionId));
    }

    /**
     * Updates the status of a payment.
     * @param transactionId The unique transaction ID.
     * @param status The new payment status.
     * @return The updated payment.
     */
    @Transactional
    public Payment updatePaymentStatus(String transactionId, PaymentStatus status) {
        Payment payment = paymentRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found"));

        payment.setStatus(status);
        payment.setUpdatedAt(LocalDateTime.now());
        paymentRepository.save(payment);
        cachePaymentStatus(payment.getId(), status.name());
        return payment;
    }

    /**
     * Deletes a payment by its ID, ensuring it is not completed.
     * @param id The ID of the payment to delete.
     */
    @Transactional
    public void deletePayment(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found with ID: " + id));

        if (payment.getStatus() == PaymentStatus.COMPLETED) {
            throw new IllegalStateException("Completed payments cannot be deleted.");
        }

        paymentRepository.delete(payment);
        redisService.setValue("payment-status-" + id, null);
    }

    // =========================== //
    //       QUERY METHODS         //
    // =========================== //

    /**
     * Retrieves payments by status.
     * @param status The payment status.
     * @return List of payments with the specified status.
     */
    public List<Payment> getPaymentsByStatus(PaymentStatus status) {
        return paymentRepository.findByStatus(status);
    }

    /**
     * Retrieves payments made by a specific payer.
     * @param payerId The payer's unique identifier.
     * @return List of payments made by the specified payer.
     */
    public List<Payment> getPaymentsByPayerId(String payerId) {
        return paymentRepository.findByPayerId(payerId);
    }

    /**
     * Retrieves payments received by a specific payee.
     * @param payeeId The payee's unique identifier.
     * @return List of payments received by the specified payee.
     */
    public List<Payment> getPaymentsByPayeeId(String payeeId) {
        return paymentRepository.findByPayeeId(payeeId);
    }

    /**
     * Retrieves all fraudulent payments.
     * @return List of fraudulent payments.
     */
    public List<Payment> getFraudulentPayments() {
        return paymentRepository.findByIsFraudulentTrue();
    }

    /**
     * Retrieves refundable payments (COMPLETED and not fully refunded).
     * @return List of refundable payments.
     */
    public List<Payment> getRefundablePayments() {
        return paymentRepository.findByStatusAndRefundedAmountLessThan(PaymentStatus.COMPLETED, BigDecimal.ZERO);
    }

    // =========================== //
    //      BUSINESS LOGIC         //
    // =========================== //

    /**
     * Processes a refund for a payment.
     * @param transactionId The transaction ID of the payment.
     * @param amount The refund amount.
     * @return The updated payment.
     */
    @Transactional
    public Payment refundPayment(String transactionId, BigDecimal amount) {
        Payment payment = paymentRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found"));

        if (payment.getStatus() != PaymentStatus.COMPLETED) {
            throw new IllegalStateException("Only completed payments can be refunded.");
        }
        if (payment.getRefundedAmount().add(amount).compareTo(payment.getAmount()) > 0) {
            throw new IllegalStateException("Refund amount exceeds original payment amount.");
        }

        payment.setRefundedAmount(payment.getRefundedAmount().add(amount));
        if (payment.getRefundedAmount().compareTo(payment.getAmount()) == 0) {
            payment.setStatus(PaymentStatus.REFUNDED);
        }
        return paymentRepository.save(payment);
    }

    /**
     * Retries a failed payment.
     * @param paymentId The ID of the payment to retry.
     */
    @Transactional
    public void retryPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found with ID: " + paymentId));

        if (payment.getStatus() != PaymentStatus.FAILED) {
            throw new IllegalStateException("Only failed payments can be retried.");
        }
        if (payment.getRetryCount() >= 3) {
            throw new IllegalStateException("Maximum retry attempts exceeded.");
        }

        payment.setRetryCount(payment.getRetryCount() + 1);
        payment.setStatus(PaymentStatus.PENDING);
        payment.setUpdatedAt(LocalDateTime.now());

        paymentRepository.save(payment);
        cachePaymentStatus(paymentId, PaymentStatus.PENDING.toString());
    }

    // =========================== //
    //      CACHE OPERATIONS       //
    // =========================== //

    /**
     * Caches the payment status in Redis.
     * @param paymentId The payment ID.
     * @param status The payment status.
     */
    private void cachePaymentStatus(Long paymentId, String status) {
        redisService.setValue("payment-status-" + paymentId, status);
    }

    /**
     * Retrieves the cached payment status from Redis.
     * @param paymentId The payment ID.
     * @return The cached payment status.
     */
    public String getCachedPaymentStatus(Long paymentId) {
        return (String) redisService.getValue("payment-status-" + paymentId);
    }

    /**
     * Validates the required details for a payment.
     * @param amount The payment amount (must be greater than zero).
     * @param currency The currency of the payment (cannot be null).
     * @param paymentMethod The payment method used (cannot be null).
     * @param payerId The payer's unique identifier (cannot be null or empty).
     * @param payeeId The payee's unique identifier (cannot be null or empty).
     */
    private void validatePaymentDetails(BigDecimal amount, Currency currency, PaymentMethod paymentMethod,
                                        String payerId, String payeeId) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Payment amount must be greater than zero.");
        }
        if (currency == null) {
            throw new IllegalArgumentException("Currency cannot be null.");
        }
        if (paymentMethod == null) {
            throw new IllegalArgumentException("Payment method cannot be null.");
        }
        if (payerId == null || payerId.trim().isEmpty()) {
            throw new IllegalArgumentException("Payer ID is required.");
        }
        if (payeeId == null || payeeId.trim().isEmpty()) {
            throw new IllegalArgumentException("Payee ID is required.");
        }
    }

}