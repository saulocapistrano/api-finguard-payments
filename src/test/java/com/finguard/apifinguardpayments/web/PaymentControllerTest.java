package com.finguard.apifinguardpayments.web;

import com.finguard.apifinguardpayments.application.PaymentService;
import com.finguard.apifinguardpayments.application.RedisService;
import com.finguard.apifinguardpayments.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(PaymentController.class)
@ImportAutoConfiguration(exclude = {KafkaAutoConfiguration.class})
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentService paymentService;

    @MockBean
    private RedisService redisService;

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
    void shouldCreatePaymentSuccessfully() throws Exception {
        when(paymentService.createPayment(any(), any(), any(), any(), any(), any()))
                .thenReturn(samplePayment);

        mockMvc.perform(post("/payments")
                        .with(user("testuser").roles("USER")) // Simula um usuário autenticado
                        .with(csrf()) // Adiciona o CSRF token para evitar erro 403
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "transactionId": "txn123",
                            "amount": 100.00,
                            "currency": "USD",
                            "paymentMethod": "CREDIT_CARD",
                            "payerId": "payer123",
                            "payeeId": "payee123"
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionId").value("txn123"));
    }


    @Test
    void shouldGetPaymentById() throws Exception {
        when(paymentService.getPaymentById(1L)).thenReturn(samplePayment);

        mockMvc.perform(get("/payments/{id}", 1L)
                        .with(user("testuser").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.transactionId").value("txn123"));
    }

    @Test
    void shouldGetPaymentByTransactionId() throws Exception {
        when(paymentService.getPaymentByTransactionId("txn123")).thenReturn(samplePayment);

        mockMvc.perform(get("/payments/transaction/{transactionId}", "txn123")
                        .with(user("testuser").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionId").value("txn123"));
    }

    @Test
    void shouldUpdatePaymentStatus() throws Exception {
        samplePayment.setStatus(PaymentStatus.COMPLETED);
        when(paymentService.updatePaymentStatus("txn123", PaymentStatus.COMPLETED))
                .thenReturn(samplePayment);

        mockMvc.perform(put("/payments/transaction/{transactionId}/status", "txn123")
                        .with(user("testuser").roles("USER"))
                        .with(csrf()) // Adicionando CSRF para evitar erro 403
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "status": "COMPLETED"
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

}