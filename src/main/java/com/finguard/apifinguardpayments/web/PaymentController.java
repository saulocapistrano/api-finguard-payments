package com.finguard.apifinguardpayments.web;


import com.finguard.apifinguardpayments.application.PaymentService;
import com.finguard.apifinguardpayments.web.request.PaymentRequestDTO;
import com.finguard.apifinguardpayments.web.response.PaymentResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<PaymentResponseDTO> createPayment(@Valid @RequestBody PaymentRequestDTO request) {
        PaymentResponseDTO response = paymentService.createPayment(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<PaymentResponseDTO> getPayment(@PathVariable String transactionId) {
        Optional<PaymentResponseDTO> payment = paymentService.getPaymentByTransactionId(transactionId);
        return payment.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}