package com.finguard.apifinguardpayments.web.response;

import com.finguard.apifinguardpayments.domain.Currency;
import com.finguard.apifinguardpayments.domain.PaymentMethod;
import com.finguard.apifinguardpayments.domain.PaymentStatus;
import com.finguard.apifinguardpayments.domain.RecurrenceType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;


public class PaymentResponseDTO {

    private String transactionId;
    private BigDecimal amount;
    private Currency currency;
    private PaymentStatus status;
    private PaymentMethod paymentMethod;
    private RecurrenceType recurrence;
    private Boolean isFraudulent;
    private String fraudReason;
    private String payerId;
    private String payeeId;
    private String description;
    private Map<String, String> metadata;
    private BigDecimal refundedAmount;
    private String paymentGateway;
    private LocalDateTime paymentDate;
    private String cancellationReason;
    private Integer retryCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public PaymentResponseDTO() {
    }

    public PaymentResponseDTO(String transactionId, BigDecimal amount, Currency currency, PaymentStatus status, PaymentMethod paymentMethod, RecurrenceType recurrence, Boolean isFraudulent, String fraudReason, String payerId, String payeeId, String description, Map<String, String> metadata, BigDecimal refundedAmount, String paymentGateway, LocalDateTime paymentDate, String cancellationReason, Integer retryCount, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.transactionId = transactionId;
        this.amount = amount;
        this.currency = currency;
        this.status = status;
        this.paymentMethod = paymentMethod;
        this.recurrence = recurrence;
        this.isFraudulent = isFraudulent;
        this.fraudReason = fraudReason;
        this.payerId = payerId;
        this.payeeId = payeeId;
        this.description = description;
        this.metadata = metadata;
        this.refundedAmount = refundedAmount;
        this.paymentGateway = paymentGateway;
        this.paymentDate = paymentDate;
        this.cancellationReason = cancellationReason;
        this.retryCount = retryCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
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

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
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

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getCancellationReason() {
        return cancellationReason;
    }

    public void setCancellationReason(String cancellationReason) {
        this.cancellationReason = cancellationReason;
    }

    public Integer getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}