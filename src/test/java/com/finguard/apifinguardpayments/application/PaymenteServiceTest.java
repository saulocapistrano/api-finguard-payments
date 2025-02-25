package com.finguard.apifinguardpayments.application;

import com.finguard.apifinguardpayments.domain.*;
import com.finguard.apifinguardpayments.infrastructure.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private RedisService redisService;

    @InjectMocks
    private PaymentService paymentService;

    private Payment samplePayment;

    @BeforeEach
    void setUp() {
        samplePayment = new Payment(
                1L, "txn123", BigDecimal.valueOf(100.00), Currency.USD,
                PaymentStatus.PENDING, PaymentMethod.CREDIT_CARD, RecurrenceType.ONCE,
                false, null, "payer123", "payee123", null, null, BigDecimal.ZERO,
                null, null, null, 0, null, null
        );
    }

    @Test
    void shouldCreatePaymentSuccessfully() {
        when(paymentRepository.save(any(Payment.class))).thenReturn(samplePayment);

        Payment createdPayment = paymentService.createPayment(
                "txn123", BigDecimal.valueOf(100.00), Currency.USD,
                PaymentMethod.CREDIT_CARD, "payer123", "payee123"
        );

        assertNotNull(createdPayment);
        assertEquals(PaymentStatus.PENDING, createdPayment.getStatus());
        verify(paymentRepository, times(1)).save(any(Payment.class));
        verify(redisService, times(1)).setValue(anyString(), anyString());
    }

    @Test
    void shouldRetrievePaymentById() {
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(samplePayment));

        Payment foundPayment = paymentService.getPaymentById(1L);

        assertNotNull(foundPayment);
        assertEquals("txn123", foundPayment.getTransactionId());
        verify(paymentRepository, times(1)).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenPaymentNotFoundById() {
        when(paymentRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> paymentService.getPaymentById(1L));

        assertEquals("Payment not found with ID: 1", exception.getMessage());
        verify(paymentRepository, times(1)).findById(1L);
    }

    @Test
    void shouldUpdatePaymentStatusSuccessfully() {
        when(paymentRepository.findByTransactionId("txn123")).thenReturn(Optional.of(samplePayment));
        when(paymentRepository.save(any(Payment.class))).thenReturn(samplePayment);

        Payment updatedPayment = paymentService.updatePaymentStatus("txn123", PaymentStatus.COMPLETED);

        assertNotNull(updatedPayment);
        assertEquals(PaymentStatus.COMPLETED, updatedPayment.getStatus());
        verify(paymentRepository, times(1)).save(samplePayment);
        verify(redisService, times(1)).setValue("payment-status-1", "COMPLETED");
    }

//    @Test
//    void shouldProcessRefundSuccessfully() {
//        samplePayment.setStatus(PaymentStatus.COMPLETED);
//        samplePayment.setAmount(BigDecimal.valueOf(100.00));
//        samplePayment.setRefundedAmount(BigDecimal.ZERO);
//
//        when(paymentRepository.findByTransactionId("txn123")).thenReturn(Optional.of(samplePayment));
//        when(paymentRepository.save(any(Payment.class))).thenReturn(samplePayment);
//
//        Payment refundedPayment = paymentService.refundPayment("txn123", BigDecimal.valueOf(50.00));
//
//        assertNotNull(refundedPayment);
//        assertEquals(BigDecimal.valueOf(50.00), refundedPayment.getRefundedAmount());
//        assertEquals(PaymentStatus.COMPLETED, refundedPayment.getStatus());
//    }
//
//    @Test
//    void shouldThrowExceptionWhenRefundExceedsAmount() {
//        samplePayment.setStatus(PaymentStatus.COMPLETED);
//        samplePayment.setAmount(BigDecimal.valueOf(100.00));
//        samplePayment.setRefundedAmount(BigDecimal.valueOf(90.00));
//
//        when(paymentRepository.findByTransactionId("txn123")).thenReturn(Optional.of(samplePayment));
//
//        Exception exception = assertThrows(IllegalStateException.class, () -> paymentService.refundPayment("txn123", BigDecimal.valueOf(20.00)));
//
//        assertEquals("Refund amount exceeds original payment amount.", exception.getMessage());
//    }

    @Test
    void shouldRetryFailedPaymentSuccessfully() {
        samplePayment.setStatus(PaymentStatus.FAILED);
        samplePayment.setRetryCount(2);

        when(paymentRepository.findById(1L)).thenReturn(Optional.of(samplePayment));
        when(paymentRepository.save(any(Payment.class))).thenReturn(samplePayment);

        paymentService.retryPayment(1L);

        assertEquals(PaymentStatus.PENDING, samplePayment.getStatus());
        assertEquals(3, samplePayment.getRetryCount());
        verify(paymentRepository, times(1)).save(samplePayment);
        verify(redisService, times(1)).setValue("payment-status-1", "PENDING");
    }

    @Test
    void shouldThrowExceptionWhenRetryingNonFailedPayment() {
        samplePayment.setStatus(PaymentStatus.COMPLETED);

        when(paymentRepository.findById(1L)).thenReturn(Optional.of(samplePayment));

        Exception exception = assertThrows(IllegalStateException.class, () -> paymentService.retryPayment(1L));

        assertEquals("Only failed payments can be retried.", exception.getMessage());
        verify(paymentRepository, never()).save(samplePayment);
    }

    @Test
    void shouldThrowExceptionWhenMaxRetryExceeded() {
        samplePayment.setStatus(PaymentStatus.FAILED);
        samplePayment.setRetryCount(3);

        when(paymentRepository.findById(1L)).thenReturn(Optional.of(samplePayment));

        Exception exception = assertThrows(IllegalStateException.class, () -> paymentService.retryPayment(1L));

        assertEquals("Maximum retry attempts exceeded.", exception.getMessage());
        verify(paymentRepository, never()).save(samplePayment);
    }
}
