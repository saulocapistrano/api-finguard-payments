package com.finguard.apifinguardpayments.web.mapper;

import com.finguard.apifinguardpayments.domain.Payment;
import com.finguard.apifinguardpayments.domain.PaymentStatus;
import com.finguard.apifinguardpayments.web.request.PaymentRequestDTO;
import com.finguard.apifinguardpayments.web.response.PaymentResponseDTO;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Component
public class PaymentMapper {

    public Payment toEntity(PaymentRequestDTO dto) {
        Payment payment = new Payment();

        payment.setTransactionId(UUID.randomUUID().toString());
        payment.setAmount(dto.getAmount());
        payment.setCurrency(dto.getCurrency());
        payment.setStatus(PaymentStatus.PENDING);
        payment.setPaymentMethod(dto.getPaymentMethod());
        payment.setPayerId(dto.getPayerId());
        payment.setPayeeId(dto.getPayeeId());
        payment.setDescription(dto.getDescription());
        payment.setMetadata(dto.getMetadata());
        payment.setCreatedAt(LocalDateTime.now());
        payment.setUpdatedAt(LocalDateTime.now());

        payment.setFraudulent(false);

        return payment;
    }

    public PaymentResponseDTO toResponseDTO(Payment payment) {
        PaymentResponseDTO responseDTO = new PaymentResponseDTO();

        responseDTO.setTransactionId(payment.getTransactionId());
        responseDTO.setAmount(payment.getAmount());
        responseDTO.setCurrency(payment.getCurrency());
        responseDTO.setStatus(payment.getStatus());
        responseDTO.setPaymentMethod(payment.getPaymentMethod());
        responseDTO.setRecurrence(payment.getRecurrence());
        responseDTO.setFraudulent(payment.getFraudulent());
        responseDTO.setFraudReason(payment.getFraudReason());
        responseDTO.setPayerId(payment.getPayerId());
        responseDTO.setPayeeId(payment.getPayeeId());
        responseDTO.setDescription(payment.getDescription());
        responseDTO.setMetadata(payment.getMetadata());
        responseDTO.setRefundedAmount(payment.getRefundedAmount());
        responseDTO.setPaymentGateway(payment.getPaymentGateway());
        responseDTO.setPaymentDate(payment.getPaymentDate());
        responseDTO.setCancellationReason(payment.getCancellationReason());
        responseDTO.setRetryCount(payment.getRetryCount());
        responseDTO.setCreatedAt(payment.getCreatedAt());
        responseDTO.setUpdatedAt(payment.getUpdatedAt());

        return responseDTO;
    }

    public void updateEntity(PaymentRequestDTO dto, Payment payment) {
        Optional.ofNullable(dto.getAmount())
                .ifPresent(payment::setAmount);

        Optional.ofNullable(dto.getCurrency())
                .ifPresent(payment::setCurrency);

        Optional.ofNullable(dto.getPaymentMethod())
                .ifPresent(payment::setPaymentMethod);

        Optional.ofNullable(dto.getPayerId())
                .ifPresent(payment::setPayerId);

        Optional.ofNullable(dto.getPayeeId())
                .ifPresent(payment::setPayeeId);

        Optional.ofNullable(dto.getDescription())
                .ifPresent(payment::setDescription);

        Optional.ofNullable(dto.getMetadata())
                .ifPresent(payment::setMetadata);

        payment.setUpdatedAt(LocalDateTime.now());
    }
}