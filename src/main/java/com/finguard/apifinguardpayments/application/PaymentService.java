package com.finguard.apifinguardpayments.application;

import com.finguard.apifinguardpayments.domain.*;
import com.finguard.apifinguardpayments.infrastructure.FraudAnalysisRepository;
import com.finguard.apifinguardpayments.infrastructure.PaymentRepository;
import com.finguard.apifinguardpayments.infrastructure.RefundRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final FraudAnalysisRepository fraudAnalysisRepository;
    private final RefundRepository refundRepository;
    private final RedisService redisService;

    public PaymentService(PaymentRepository paymentRepository,
                          FraudAnalysisRepository fraudAnalysisRepository,
                          RefundRepository refundRepository,
                          RedisService redisService) {
        this.paymentRepository = paymentRepository;
        this.fraudAnalysisRepository = fraudAnalysisRepository;
        this.refundRepository = refundRepository;
        this.redisService = redisService;
    }

    // =========================== //
    //       CRUD OPERATIONS       //
    // =========================== //

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

    public Payment getPaymentById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found with ID: " + id));
    }

    public Payment getPaymentByTransactionId(String transactionId) {
        return paymentRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new RuntimeException("Payment not found with transaction ID: " + transactionId));
    }

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

    public List<Payment> getPaymentsByStatus(PaymentStatus status) {
        return paymentRepository.findByStatus(status);
    }

    public List<Payment> getPaymentsByPayerId(String payerId) {
        return paymentRepository.findByPayerId(payerId);
    }

    public List<Payment> getPaymentsByPayeeId(String payeeId) {
        return paymentRepository.findByPayeeId(payeeId);
    }

    public List<Payment> getFraudulentPayments() {
        return paymentRepository.findByIsFraudulentTrue();
    }

    public List<Payment> getRefundablePayments() {
        return paymentRepository.findByStatusAndRefundedAmountLessThan(PaymentStatus.COMPLETED, BigDecimal.ZERO);
    }

    // =========================== //
    //      BUSINESS LOGIC         //
    // =========================== //

    /**
     * Registers a refund instead of updating the payment entity directly.
     * @param transactionId The transaction ID of the payment.
     * @param amount The refund amount.
     * @return The refund record.
     */
    @Transactional
    public Refund processRefund(String transactionId, BigDecimal amount) {
        Payment payment = paymentRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found"));

        if (payment.getStatus() != PaymentStatus.COMPLETED) {
            throw new IllegalStateException("Only completed payments can be refunded.");
        }
        if (amount.compareTo(payment.getAmount()) > 0) {
            throw new IllegalStateException("Refund amount exceeds the original payment.");
        }

        Refund refund = new Refund(
                payment,
                amount,
                LocalDateTime.now(),
                "system",
                "Refund processed successfully"
        );

        refundRepository.save(refund);

        return refund;
    }

    /**
     * Flags a payment as fraudulent and records the details.
     * @param transactionId The transaction ID of the payment.
     * @param reason The reason for marking the payment as fraud.
     * @param riskScore A numeric risk score for fraud detection.
     * @param flaggedBy The identifier of the entity flagging the fraud.
     */
    @Transactional
    public void flagAsFraudulent(String transactionId, String reason, BigDecimal riskScore, String flaggedBy) {
        Payment payment = paymentRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found"));

        payment.setStatus(PaymentStatus.FRAUDULENT);
        payment.setUpdatedAt(LocalDateTime.now());
        paymentRepository.save(payment);

        FraudAnalysis fraudAnalysis = new FraudAnalysis(payment, reason, riskScore, flaggedBy);
        fraudAnalysisRepository.save(fraudAnalysis);

        redisService.setValue("payment-status-" + payment.getId(), PaymentStatus.FRAUDULENT.name());
    }

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

    private void cachePaymentStatus(Long paymentId, String status) {
        redisService.setValue("payment-status-" + paymentId, status);
    }

    public String getCachedPaymentStatus(Long paymentId) {
        return (String) redisService.getValue("payment-status-" + paymentId);
    }

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
