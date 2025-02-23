package com.finguard.apifinguardpayments.domain;

public enum PaymentMethod {
    PIX("Pagamento via PIX"),
    CREDIT_CARD("Pagamento com cartão de crédito"),
    DEBIT_CARD("Pagamento com cartão de débito"),
    BANK_TRANSFER("Transferência bancária");

    private final String description;

    PaymentMethod(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}