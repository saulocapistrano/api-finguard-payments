package com.finguard.apifinguardpayments.domain;

public enum PaymentStatus {
    PENDING("Pagamento pendente"),
    COMPLETED("Pagamento concluído"),
    FAILED("Pagamento falhou"),
    REFUNDED("Pagamento reembolsado"),
    CANCELLED("Pagamento cancelado");

    private final String description;

    PaymentStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}