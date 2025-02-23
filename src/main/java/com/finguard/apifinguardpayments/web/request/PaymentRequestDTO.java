package com.finguard.apifinguardpayments.web.request;

import com.finguard.apifinguardpayments.domain.Currency;
import com.finguard.apifinguardpayments.domain.PaymentMethod;
import com.finguard.apifinguardpayments.domain.PaymentStatus;
import com.finguard.apifinguardpayments.domain.RecurrenceType;
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

    private PaymentStatus status;
    private RecurrenceType recurrence;
    private Boolean isFraudulent;
    private String fraudReason;

    @NotBlank(message = "Payer ID is required")
    private String payerId;

    @NotBlank(message = "Payee ID is required")
    private String payeeId;

    private String description;
    private Map<String, String> metadata;
    private BigDecimal refundedAmount;
    private String paymentGateway;
    private String cancellationReason;

    public PaymentRequestDTO() {
    }

    public PaymentRequestDTO(BigDecimal amount, Currency currency, PaymentMethod paymentMethod, PaymentStatus status, RecurrenceType recurrence, Boolean isFraudulent, String fraudReason, String payerId, String payeeId, String description, Map<String, String> metadata, BigDecimal refundedAmount, String paymentGateway, String cancellationReason) {
        this.amount = amount;
        this.currency = currency;
        this.paymentMethod = paymentMethod;
        this.status = status;
        this.recurrence = recurrence;
        this.isFraudulent = isFraudulent;
        this.fraudReason = fraudReason;
        this.payerId = payerId;
        this.payeeId = payeeId;
        this.description = description;
        this.metadata = metadata;
        this.refundedAmount = refundedAmount;
        this.paymentGateway = paymentGateway;
        this.cancellationReason = cancellationReason;
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

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public RecurrenceType getRecurrence() {
        return recurrence;
    }

    public void setRecurrence(RecurrenceType recurrence) {
        this.recurrence = recurrence;
    }

    public Boolean getFraudulent() {
        return isFraudulent;
    }

    public void setFraudulent(Boolean fraudulent) {
        isFraudulent = fraudulent;
    }

    public String getFraudReason() {
        return fraudReason;
    }

    public void setFraudReason(String fraudReason) {
        this.fraudReason = fraudReason;
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

    public BigDecimal getRefundedAmount() {
        return refundedAmount;
    }

    public void setRefundedAmount(BigDecimal refundedAmount) {
        this.refundedAmount = refundedAmount;
    }

    public String getPaymentGateway() {
        return paymentGateway;
    }

    public void setPaymentGateway(String paymentGateway) {
        this.paymentGateway = paymentGateway;
    }

    public String getCancellationReason() {
        return cancellationReason;
    }

    public void setCancellationReason(String cancellationReason) {
        this.cancellationReason = cancellationReason;
    }
}