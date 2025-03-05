package com.finguard.apifinguardpayments.web;

import com.finguard.apifinguardpayments.application.PaymentService;
import com.finguard.apifinguardpayments.domain.Payment;
import com.finguard.apifinguardpayments.domain.PaymentStatus;
import com.finguard.apifinguardpayments.domain.Refund;
import com.finguard.apifinguardpayments.web.api.PaymentApi;
import com.finguard.apifinguardpayments.web.request.PaymentRequestDTO;
import com.finguard.apifinguardpayments.web.request.RefundRequestDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class PaymentController implements PaymentApi {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Override
    public ResponseEntity<Payment> createPayment(PaymentRequestDTO paymentRequest) {
        Payment createdPayment = paymentService.createPayment(paymentRequest);
        return ResponseEntity.ok(createdPayment);
    }

    @Override
    public ResponseEntity<Payment> getPaymentById(Long id) {
        Payment payment = paymentService.getPaymentById(id);
        return ResponseEntity.ok(payment);
    }

    @Override
    public ResponseEntity<Payment> getPaymentByTransactionId(String transactionId) {
        Payment payment = paymentService.getPaymentByTransactionId(transactionId);
        return ResponseEntity.ok(payment);
    }

    @Override
    public ResponseEntity<Payment> updatePaymentStatus(String transactionId, PaymentStatus status) {
        Payment updatedPayment = paymentService.updatePaymentStatus(transactionId, status);
        return ResponseEntity.ok(updatedPayment);
    }

    @Override
    public ResponseEntity<Void> deletePayment(Long id) {
        paymentService.deletePayment(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<List<Payment>> getPaymentsByStatus(PaymentStatus status) {
        List<Payment> payments = paymentService.getPaymentsByStatus(status);
        return ResponseEntity.ok(payments);
    }

    @Override
    public ResponseEntity<List<Payment>> getPaymentsByPayerId(String payerId) {
        List<Payment> payments = paymentService.getPaymentsByPayerId(payerId);
        return ResponseEntity.ok(payments);
    }

    @Override
    public ResponseEntity<List<Payment>> getPaymentsByPayeeId(String payeeId) {
        List<Payment> payments = paymentService.getPaymentsByPayeeId(payeeId);
        return ResponseEntity.ok(payments);
    }

    @Override
    public ResponseEntity<List<Payment>> getFraudulentPayments() {
        List<Payment> fraudulentPayments = paymentService.getFraudulentPayments();
        return ResponseEntity.ok(fraudulentPayments);
    }

    @Override
    public ResponseEntity<Refund> processRefund(RefundRequestDTO refundRequest) {
        Refund refund = paymentService.processRefund(refundRequest.getTransactionId(), refundRequest.getAmount());
        return ResponseEntity.ok(refund);
    }

    @Override
    public ResponseEntity<Void> retryPayment(Long id) {
        paymentService.retryPayment(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<String> getCachedPaymentStatus(Long id) {
        String status = paymentService.getCachedPaymentStatus(id);
        return ResponseEntity.ok(status);
    }
}