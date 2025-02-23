package com.finguard.apifinguardpayments.web.mapper;

import com.finguard.apifinguardpayments.domain.Payment;
import com.finguard.apifinguardpayments.domain.PaymentStatus;
import com.finguard.apifinguardpayments.web.request.PaymentRequestDTO;
import com.finguard.apifinguardpayments.web.response.PaymentResponseDTO;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class PaymentMapper {

    public Payment toEntity(PaymentRequestDTO dto) {
        return new Payment(
                null,
                UUID.randomUUID().toString(),
                dto.getAmount(),
                dto.getCurrency(),
                dto.getStatus() != null ? dto.getStatus() : PaymentStatus.PENDING,
                dto.getPaymentMethod(),
                dto.getRecurrence(),
                dto.getFraudulent(),
                dto.getFraudReason(),
                dto.getPayerId(),
                dto.getPayeeId(),
                dto.getDescription(),
                dto.getMetadata(),
                dto.getRefundedAmount(),
                dto.getPaymentGateway(),
                null, // Payment date is set when the payment is completed
                dto.getCancellationReason(),
                0, // Retry count starts at zero
                LocalDateTime.now(),
                LocalDateTime.now()
        );
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
        if (dto.getAmount() != null) {
            payment.setAmount(dto.getAmount());
        }
        if (dto.getCurrency() != null) {
            payment.setCurrency(dto.getCurrency());
        }
        if (dto.getPaymentMethod() != null) {
            payment.setPaymentMethod(dto.getPaymentMethod());
        }
        if (dto.getStatus() != null) {
            payment.setStatus(dto.getStatus());
        }
        if (dto.getRecurrence() != null) {
            payment.setRecurrence(dto.getRecurrence());
        }
        if (dto.getFraudulent() != null) {
            payment.setFraudulent(dto.getFraudulent());
        }
        if (dto.getFraudReason() != null) {
            payment.setFraudReason(dto.getFraudReason());
        }
        if (dto.getDescription() != null) {
            payment.setDescription(dto.getDescription());
        }
        if (dto.getMetadata() != null) {
            payment.setMetadata(dto.getMetadata());
        }
        if (dto.getRefundedAmount() != null) {
            payment.setRefundedAmount(dto.getRefundedAmount());
        }
        if (dto.getPaymentGateway() != null) {
            payment.setPaymentGateway(dto.getPaymentGateway());
        }
        if (dto.getCancellationReason() != null) {
            payment.setCancellationReason(dto.getCancellationReason());
        }
        payment.setUpdatedAt(LocalDateTime.now());
    }
}