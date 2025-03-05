package com.finguard.apifinguardpayments.web.api;

import com.finguard.apifinguardpayments.domain.Payment;
import com.finguard.apifinguardpayments.domain.PaymentStatus;
import com.finguard.apifinguardpayments.domain.Refund;
import com.finguard.apifinguardpayments.web.request.PaymentRequestDTO;
import com.finguard.apifinguardpayments.web.request.RefundRequestDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/payments")
public interface PaymentApi {

    @PostMapping
    ResponseEntity<Payment> createPayment(@RequestBody PaymentRequestDTO paymentRequest);

    @GetMapping("/{id}")
    ResponseEntity<Payment> getPaymentById(@PathVariable Long id);

    @GetMapping("/transaction/{transactionId}")
    ResponseEntity<Payment> getPaymentByTransactionId(@PathVariable String transactionId);

    @PatchMapping("/update-status/{transactionId}")
    ResponseEntity<Payment> updatePaymentStatus(
            @PathVariable String transactionId,
            @RequestBody PaymentStatus status
    );

    @DeleteMapping("/{id}")
    ResponseEntity<Void> deletePayment(@PathVariable Long id);

    @GetMapping("/status/{status}")
    ResponseEntity<List<Payment>> getPaymentsByStatus(@PathVariable PaymentStatus status);

    @GetMapping("/payer/{payerId}")
    ResponseEntity<List<Payment>> getPaymentsByPayerId(@PathVariable String payerId);

    @GetMapping("/payee/{payeeId}")
    ResponseEntity<List<Payment>> getPaymentsByPayeeId(@PathVariable String payeeId);

    @GetMapping("/fraudulent")
    ResponseEntity<List<Payment>> getFraudulentPayments();

    @PostMapping("/refund")
    ResponseEntity<Refund> processRefund(@RequestBody RefundRequestDTO refundRequest);

    @PostMapping("/retry/{id}")
    ResponseEntity<Void> retryPayment(@PathVariable Long id);

    @GetMapping("/cache/status/{id}")
    ResponseEntity<String> getCachedPaymentStatus(@PathVariable Long id);
}