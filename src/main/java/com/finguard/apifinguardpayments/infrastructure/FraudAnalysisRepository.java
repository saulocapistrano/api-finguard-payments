package com.finguard.apifinguardpayments.infrastructure;

import com.finguard.apifinguardpayments.domain.FraudAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FraudAnalysisRepository extends JpaRepository<FraudAnalysis, Long> {
    List<FraudAnalysis> findByPaymentId(Long paymentId);
}
