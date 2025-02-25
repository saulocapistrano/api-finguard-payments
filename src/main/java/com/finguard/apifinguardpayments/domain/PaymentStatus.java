package com.finguard.apifinguardpayments.domain;

public enum PaymentStatus {
    PENDING("Payment pending"),
    COMPLETED("Payment completed"),
    FAILED("Payment failed"),
    REFUNDED("Payment refunded"),
    CANCELLED("Payment cancelled"),
    FRAUDULENT("Payment marked as fraudulent"); // ✅ Updated message to English

    private final String description;

    PaymentStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
