package com.finguard.apifinguardpayments.web.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentRequestDTO {

    @NotNull(message = "O valor do pagamento é obrigatório")
    @Min(value = 1, message = "O valor deve ser maior que zero")
    private BigDecimal amount;

    @NotBlank(message = "A moeda é obrigatória")
    private String currency;

    @NotBlank(message = "O método de pagamento é obrigatório")
    private String paymentMethod;
}

