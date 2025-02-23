package com.finguard.apifinguardpayments.web.api;


import com.finguard.apifinguardpayments.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RequestMapping("/api/payments")
public interface PaymentApi {

    @PostMapping
    ResponseEntity<Payment> createPayment(@RequestParam String transactionId,
                                          @RequestParam BigDecimal amount,
                                          @RequestParam Currency currency,
                                          @RequestParam PaymentMethod paymentMethod,
                                          @RequestParam String payerId,
                                          @RequestParam String payeeId);

    @GetMapping("/{id}")
    ResponseEntity<Payment> getPaymentById(@PathVariable Long id);

    @GetMapping("/transaction/{transactionId}")
    ResponseEntity<Payment> getPaymentByTransactionId(@PathVariable String transactionId);

    @PatchMapping("/update-status/{transactionId}")
    ResponseEntity<Payment> updatePaymentStatus(@PathVariable String transactionId,
                                                @RequestParam PaymentStatus status);

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
    ResponseEntity<Payment> refundPayment(@RequestParam String transactionId,
                                          @RequestParam BigDecimal amount);

    @PostMapping("/retry/{id}")
    ResponseEntity<Void> retryPayment(@PathVariable Long id);

    @GetMapping("/cache/status/{id}")
    ResponseEntity<String> getCachedPaymentStatus(@PathVariable Long id);
}