package com.finguard.apifinguardpayments.application;

import com.finguard.apifinguardpayments.domain.*;
import com.finguard.apifinguardpayments.infrastructure.FraudAnalysisRepository;
import com.finguard.apifinguardpayments.infrastructure.PaymentMetadataRepository;
import com.finguard.apifinguardpayments.infrastructure.PaymentRepository;
import com.finguard.apifinguardpayments.infrastructure.RefundRepository;
import com.finguard.apifinguardpayments.web.mapper.PaymentMapper;
import com.finguard.apifinguardpayments.web.request.PaymentRequestDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMetadataRepository paymentMetadataRepository;
    private final FraudAnalysisRepository fraudAnalysisRepository;
    private final RefundRepository refundRepository;
    private final RedisService redisService;
    private final PaymentMapper paymentMapper;

    public PaymentService(
            PaymentRepository paymentRepository,
            PaymentMetadataRepository paymentMetadataRepository,
            FraudAnalysisRepository fraudAnalysisRepository,
            RefundRepository refundRepository,
            RedisService redisService,
            PaymentMapper paymentMapper
    ) {
        this.paymentRepository = paymentRepository;
        this.paymentMetadataRepository = paymentMetadataRepository;
        this.fraudAnalysisRepository = fraudAnalysisRepository;
        this.refundRepository = refundRepository;
        this.redisService = redisService;
        this.paymentMapper = paymentMapper;
    }

    @Transactional
    public Payment createPayment(PaymentRequestDTO paymentRequest) {
        if (paymentRequest.getPayerId() == null || paymentRequest.getPayerId().isBlank()) {
            throw new IllegalArgumentException("Payer ID cannot be null or empty");
        }

        validatePaymentDetails(
                paymentRequest.getAmount(),
                paymentRequest.getCurrency(),
                paymentRequest.getPaymentMethod(),
                paymentRequest.getPayerId(),
                paymentRequest.getPayeeId()
        );

        Payment payment = paymentMapper.toEntity(paymentRequest);

        Payment savedPayment = paymentRepository.save(payment);

        cachePaymentStatus(savedPayment.getId(), savedPayment.getStatus().name());

        return savedPayment;
    }

    public Payment getPaymentById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found with ID: " + id));

        List<PaymentMetadata> metadataList = paymentMetadataRepository.findByPaymentId(id);
        Map<String, String> metadataMap = metadataList.stream()
                .collect(Collectors.toMap(PaymentMetadata::getKey, PaymentMetadata::getValue));

        payment.setMetadata(metadataMap);
        return payment;
    }


    public Payment getPaymentByTransactionId(String transactionId) {
        return paymentRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new RuntimeException("Payment not found with transaction ID: " + transactionId));
    }

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

        BigDecimal totalRefunded = payment.getRefundedAmount() == null ? BigDecimal.ZERO : payment.getRefundedAmount();
        payment.setRefundedAmount(totalRefunded.add(amount));

        paymentRepository.save(payment);

        return refund;
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

    private void cachePaymentStatus(Long paymentId, String status) {
        redisService.setValue("payment-status-" + paymentId, status);
    }

    public String getCachedPaymentStatus(Long paymentId) {
        return (String) redisService.getValue("payment-status-" + paymentId);
    }

    private void validatePaymentDetails(
            BigDecimal amount,
            Currency currency,
            PaymentMethod paymentMethod,
            String payerId,
            String payeeId
    ) {
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