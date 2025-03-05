package com.finguard.apifinguardpayments.application;

import com.finguard.apifinguardpayments.domain.*;
import com.finguard.apifinguardpayments.infrastructure.FraudAnalysisRepository;
import com.finguard.apifinguardpayments.infrastructure.PaymentMetadataRepository;
import com.finguard.apifinguardpayments.infrastructure.PaymentRepository;
import com.finguard.apifinguardpayments.infrastructure.RefundRepository;
import com.finguard.apifinguardpayments.web.mapper.PaymentMapper;
import com.finguard.apifinguardpayments.web.request.PaymentRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentMetadataRepository paymentMetadataRepository;

    @Mock
    private FraudAnalysisRepository fraudAnalysisRepository;

    @Mock
    private RefundRepository refundRepository;

    @Mock
    private RedisService redisService;

    @Mock
    private PaymentMapper paymentMapper;

    @InjectMocks
    private PaymentService paymentService;


    private Payment samplePayment;

    @BeforeEach
    void setUp() {
        // Usar construtor vazio e setters
        samplePayment = new Payment();
        samplePayment.setId(1L);
        samplePayment.setTransactionId("txn123");
        samplePayment.setAmount(BigDecimal.valueOf(100.00));
        samplePayment.setCurrency(Currency.USD);
        samplePayment.setStatus(PaymentStatus.PENDING);
        samplePayment.setPaymentMethod(PaymentMethod.CREDIT_CARD);
        samplePayment.setRecurrence(RecurrenceType.ONCE);
        samplePayment.setFraudulent(false);
        samplePayment.setFraudReason(null);
        samplePayment.setPayerId("payer123");
        samplePayment.setPayeeId("payee123");
        samplePayment.setDescription(null);
        samplePayment.setMetadata((Map<String, String>) null);
        samplePayment.setRefundedAmount(BigDecimal.ZERO);
        samplePayment.setPaymentGateway(null);
        samplePayment.setPaymentDate(null);
        samplePayment.setCancellationReason(null);
        samplePayment.setRetryCount(0);
        samplePayment.setCreatedAt(null);
        samplePayment.setUpdatedAt(null);
    }

    // No teste do createPayment
    @Test
    void shouldCreatePaymentSuccessfully() {
        PaymentRequestDTO dto = new PaymentRequestDTO();
        dto.setAmount(BigDecimal.valueOf(100.00));
        dto.setCurrency(Currency.USD);
        dto.setPaymentMethod(PaymentMethod.CREDIT_CARD);
        dto.setPayerId("payer123");
        dto.setPayeeId("payee123");

        // Este é o objeto Payment que o mapper retornará
        Payment paymentFromMapper = new Payment();
        paymentFromMapper.setTransactionId("txn123");
        paymentFromMapper.setAmount(BigDecimal.valueOf(100.00));
        paymentFromMapper.setCurrency(Currency.USD);
        paymentFromMapper.setPaymentMethod(PaymentMethod.CREDIT_CARD);
        paymentFromMapper.setPayerId("payer123");
        paymentFromMapper.setPayeeId("payee123");
        paymentFromMapper.setStatus(PaymentStatus.PENDING);

        // Configura o comportamento do mock
        when(paymentMapper.toEntity(dto)).thenReturn(paymentFromMapper);

        when(paymentRepository.save(any(Payment.class))).thenReturn(samplePayment);

        Payment createdPayment = paymentService.createPayment(dto);

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
