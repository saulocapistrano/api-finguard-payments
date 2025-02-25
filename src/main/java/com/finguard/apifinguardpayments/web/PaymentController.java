package com.finguard.apifinguardpayments.web;


import com.finguard.apifinguardpayments.application.PaymentService;
import com.finguard.apifinguardpayments.domain.*;
import com.finguard.apifinguardpayments.web.api.PaymentApi;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/payments")
public class PaymentController implements PaymentApi {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Override
    public ResponseEntity<Payment> createPayment(String transactionId, BigDecimal amount, Currency currency,
                                                 PaymentMethod paymentMethod, String payerId, String payeeId) {
        return ResponseEntity.ok(paymentService.createPayment(transactionId, amount, currency, paymentMethod, payerId, payeeId));
    }

    @Override
    public ResponseEntity<Payment> getPaymentById(Long id) {
        return ResponseEntity.ok(paymentService.getPaymentById(id));
    }

    @Override
    public ResponseEntity<Payment> getPaymentByTransactionId(String transactionId) {
        return ResponseEntity.ok(paymentService.getPaymentByTransactionId(transactionId));
    }

    @Override
    public ResponseEntity<Payment> updatePaymentStatus(String transactionId, PaymentStatus status) {
        return ResponseEntity.ok(paymentService.updatePaymentStatus(transactionId, status));
    }

    @Override
    public ResponseEntity<Void> deletePayment(Long id) {
        paymentService.deletePayment(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<List<Payment>> getPaymentsByStatus(PaymentStatus status) {
        return ResponseEntity.ok(paymentService.getPaymentsByStatus(status));
    }

    @Override
    public ResponseEntity<List<Payment>> getPaymentsByPayerId(String payerId) {
        return ResponseEntity.ok(paymentService.getPaymentsByPayerId(payerId));
    }

    @Override
    public ResponseEntity<List<Payment>> getPaymentsByPayeeId(String payeeId) {
        return ResponseEntity.ok(paymentService.getPaymentsByPayeeId(payeeId));
    }

    @Override
    public ResponseEntity<List<Payment>> getFraudulentPayments() {
        return ResponseEntity.ok(paymentService.getFraudulentPayments());
    }

    @Override
    public ResponseEntity<Refund> processRefund(String transactionId, BigDecimal amount) {
        return ResponseEntity.ok(paymentService.processRefund(transactionId, amount));
    }

    @Override
    public ResponseEntity<Void> retryPayment(Long id) {
        paymentService.retryPayment(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<String> getCachedPaymentStatus(Long id) {
        return ResponseEntity.ok(paymentService.getCachedPaymentStatus(id));
    }
}