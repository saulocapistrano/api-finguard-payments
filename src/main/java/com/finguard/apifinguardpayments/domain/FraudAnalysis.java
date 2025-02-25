package com.finguard.apifinguardpayments.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Entity
@Table(name = "fraud_analysis")
public class FraudAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    @Column(name = "reason", nullable = false)
    private String reason;

    @Column(name = "risk_score", nullable = false)
    private BigDecimal riskScore;

    @Column(name = "flagged_by", nullable = false)
    private String flaggedBy;

    @Column(name = "flagged_at", nullable = false)
    private LocalDateTime flaggedAt;

    public FraudAnalysis() {}

    public FraudAnalysis(Payment payment, String reason, BigDecimal riskScore, String flaggedBy) {
        this.payment = payment;
        this.reason = reason;
        this.riskScore = riskScore;
        this.flaggedBy = flaggedBy;
        this.flaggedAt = LocalDateTime.now();
    }

}
