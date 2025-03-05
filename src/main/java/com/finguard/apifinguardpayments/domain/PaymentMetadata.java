package com.finguard.apifinguardpayments.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "payment_metadata", schema = "homologacao")
public class PaymentMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    @Column(name = "metadata_key", nullable = false)
    private String key;

    @Column(name = "metadata_value", nullable = false)
    private String value;

    public PaymentMetadata() {}

    public PaymentMetadata(Payment payment, String key, String value) {
        this.payment = payment;
        this.key = key;
        this.value = value;
    }

    public Long getId() { return id; }

    public Payment getPayment() { return payment; }

    public void setPayment(Payment payment) { this.payment = payment; }

    public String getKey() { return key; }

    public void setKey(String key) { this.key = key; }

    public String getValue() { return value; }

    public void setValue(String value) { this.value = value; }
}
