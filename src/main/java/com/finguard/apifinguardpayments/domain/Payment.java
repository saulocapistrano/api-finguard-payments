package com.finguard.apifinguardpayments.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Table(name = "payments")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String transactionId;

    private BigDecimal amount;

    private String currency;

    private String status; // PENDING, COMPLETED, FAILED

    private String paymentMethod; // PIX, CREDIT_CARD, DEBIT_CARD

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
