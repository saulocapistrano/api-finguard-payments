package com.finguard.apifinguardpayments.infrastructure;

import com.finguard.apifinguardpayments.domain.Refund;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RefundRepository extends JpaRepository<Refund, Long> {
    List<Refund> findByPaymentId(Long paymentId);
}
