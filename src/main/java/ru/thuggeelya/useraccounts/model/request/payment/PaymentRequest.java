package ru.thuggeelya.useraccounts.model.request.payment;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class PaymentRequest {

    @NotNull(message = "TransferToId is null")
    private Long transferToId;

    @Positive(message = "Value must be positive")
    @NotNull(message = "Value is null")
    private BigDecimal value;
}
