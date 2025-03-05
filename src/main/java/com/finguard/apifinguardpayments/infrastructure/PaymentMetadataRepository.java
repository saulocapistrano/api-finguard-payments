package com.finguard.apifinguardpayments.infrastructure;

import com.finguard.apifinguardpayments.domain.PaymentMetadata;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentMetadataRepository extends JpaRepository<PaymentMetadata, Long> {
    List<PaymentMetadata> findByPaymentId(Long paymentId);
}
