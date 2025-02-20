package com.finguard.apifinguardpayments.application;

import com.finguard.apifinguardpayments.domain.Payment;
import com.finguard.apifinguardpayments.infrastructure.PaymentRepository;
import com.finguard.apifinguardpayments.web.mapper.PaymentMapper;
import com.finguard.apifinguardpayments.web.request.PaymentRequestDTO;
import com.finguard.apifinguardpayments.web.response.PaymentResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;

    @Transactional
    public PaymentResponseDTO createPayment(PaymentRequestDTO dto) {
        Payment payment = paymentMapper.toEntity(dto);
        Payment savedPayment = paymentRepository.save(payment);
        return paymentMapper.toResponseDTO(savedPayment);
    }

    public Optional<PaymentResponseDTO> getPaymentByTransactionId(String transactionId) {
        return paymentRepository.findByTransactionId(transactionId)
                .map(paymentMapper::toResponseDTO);
    }
}