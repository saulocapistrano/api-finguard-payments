package com.finguard.apifinguardpayments.domain;

public enum Currency {
    BRL("Real Brasileiro"),
    USD("Dólar Americano"),
    EUR("Euro"),
    GBP("Libra Esterlina"),
    JPY("Iene Japonês");

    private final String description;

    Currency(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
