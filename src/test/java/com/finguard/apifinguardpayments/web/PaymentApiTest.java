package com.finguard.apifinguardpayments.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.finguard.apifinguardpayments.application.PaymentService;
import com.finguard.apifinguardpayments.domain.Currency;
import com.finguard.apifinguardpayments.domain.Payment;
import com.finguard.apifinguardpayments.domain.PaymentMethod;
import com.finguard.apifinguardpayments.web.api.PaymentApi;
import com.finguard.apifinguardpayments.web.request.PaymentRequestDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PaymentApi.class)
@AutoConfigureMockMvc(addFilters = false)
public class PaymentApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentService paymentService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void shouldCreatePaymentSuccessfully() throws Exception {
        PaymentRequestDTO request = new PaymentRequestDTO(
        );

        Payment payment = new Payment(
                "txn123",
                request.getAmount(),
                request.getCurrency(),
                request.getPaymentMethod(),
                request.getPayerId(),
                request.getPayeeId(),
                Collections.emptyMap()
        );

        when(paymentService.createPayment(any(PaymentRequestDTO.class))).thenReturn(payment);

        mockMvc.perform(post("/api/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldGetPaymentById() throws Exception {
        Payment payment = new Payment(
                "txn123",
                BigDecimal.valueOf(100.00),
                Currency.USD,
                PaymentMethod.CREDIT_CARD,
                "payer123",
                "payee123",
                Collections.emptyMap()
        );

        when(paymentService.getPaymentById(1L)).thenReturn(payment);

        mockMvc.perform(get("/api/payments/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldGetPaymentByTransactionId() throws Exception {
        Payment payment = new Payment(
                "txn123",
                BigDecimal.valueOf(100.00),
                Currency.USD,
                PaymentMethod.CREDIT_CARD,
                "payer123",
                "payee123",
                Collections.emptyMap()
        );

        when(paymentService.getPaymentByTransactionId("txn123")).thenReturn(payment);

        mockMvc.perform(get("/api/payments/transaction/txn123")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}