package com.finguard.apifinguardpayments.domain;

public enum RecurrenceType {
    ONCE("Pagamento único"),
    MONTHLY("Pagamento mensal"),
    YEARLY("Pagamento anual");

    private final String description;

    RecurrenceType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}