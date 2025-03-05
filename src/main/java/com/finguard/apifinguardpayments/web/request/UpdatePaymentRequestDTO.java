package com.finguard.apifinguardpayments.web.request;

import com.finguard.apifinguardpayments.domain.PaymentStatus;
import jakarta.validation.constraints.NotNull;

public class UpdatePaymentRequestDTO {

    @NotNull(message = "Status is required")
    private PaymentStatus status;

    public UpdatePaymentRequestDTO() {}

    public UpdatePaymentRequestDTO(PaymentStatus status) {
        this.status = status;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }
}