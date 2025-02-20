package com.finguard.apifinguardpayments.web.mapper;


import com.finguard.apifinguardpayments.domain.Payment;

import com.finguard.apifinguardpayments.web.request.PaymentRequestDTO;
import com.finguard.apifinguardpayments.web.response.PaymentResponseDTO;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class PaymentMapper {

    public Payment toEntity(PaymentRequestDTO dto) {
        return Payment.builder()
                .transactionId(UUID.randomUUID().toString())
                .amount(dto.getAmount())
                .currency(dto.getCurrency())
                .status("PENDING")
                .paymentMethod(dto.getPaymentMethod())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public PaymentResponseDTO toResponseDTO(Payment payment) {
        return PaymentResponseDTO.builder()
                .transactionId(payment.getTransactionId())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .status(payment.getStatus())
                .paymentMethod(payment.getPaymentMethod())
                .createdAt(payment.getCreatedAt())
                .updatedAt(payment.getUpdatedAt())
                .build();
    }
}