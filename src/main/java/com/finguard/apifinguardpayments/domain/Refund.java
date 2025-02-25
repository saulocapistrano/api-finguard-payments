package com.finguard.apifinguardpayments.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "refunds")
public class Refund {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "refunded_at", nullable = false)
    private LocalDateTime refundedAt;

    @Column(name = "refunded_by", nullable = false)
    private String refundedBy;

    @Column(name = "reason", length = 255)
    private String reason;

    /**
     * Construtor para criar um novo reembolso.
     *
     * @param payment O pagamento associado ao reembolso.
     * @param amount O valor reembolsado.
     * @param refundedAt A data e hora do reembolso.
     * @param refundedBy Quem processou o reembolso.
     * @param reason Motivo do reembolso.
     */
    public Refund(Payment payment, BigDecimal amount, LocalDateTime refundedAt, String refundedBy, String reason) {
        this.payment = payment;
        this.amount = amount;
        this.refundedAt = refundedAt;
        this.refundedBy = refundedBy;
        this.reason = reason;
    }
}
