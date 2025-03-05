package com.finguard.apifinguardpayments.web.request;

import com.finguard.apifinguardpayments.domain.Currency;
import com.finguard.apifinguardpayments.domain.Payment;
import com.finguard.apifinguardpayments.domain.PaymentMethod;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.Map;

public class PaymentRequestDTO {

    @NotNull(message = "Amount is required")
    @Min(value = 1, message = "Amount must be greater than zero")
    private BigDecimal amount;

    @NotNull(message = "Currency is required")
    private Currency currency;

    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;

    @NotBlank(message = "Payer ID is required")
    private String payerId;

    @NotBlank(message = "Payee ID is required")
    private String payeeId;

    private String description;
    private Map<String, String> metadata;

    public PaymentRequestDTO() {}

    public PaymentRequestDTO(BigDecimal amount, Currency currency, PaymentMethod paymentMethod,
                             String payerId, String payeeId, String description, Map<String, String> metadata) {
        this.amount = amount;
        this.currency = currency;
        this.paymentMethod = paymentMethod;
        this.payerId = payerId;
        this.payeeId = payeeId;
        this.description = description;
        this.metadata = metadata;
    }

    public Payment toEntity(String transactionId) {
        return new Payment(transactionId, this.amount, this.currency,
                this.paymentMethod, this.payerId, this.payeeId, this.metadata);
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPayerId() {
        return payerId;
    }

    public void setPayerId(String payerId) {
        this.payerId = payerId;
    }

    public String getPayeeId() {
        return payeeId;
    }

    public void setPayeeId(String payeeId) {
        this.payeeId = payeeId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }
}