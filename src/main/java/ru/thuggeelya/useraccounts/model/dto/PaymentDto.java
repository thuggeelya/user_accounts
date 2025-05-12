package ru.thuggeelya.useraccounts.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.thuggeelya.useraccounts.model.request.payment.PaymentRequest;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class PaymentDto {

    private Long transferFromId;
    private Long transferToId;
    private BigDecimal value;

    public PaymentDto(final Long transferFromId, final PaymentRequest request) {

        this.transferFromId = transferFromId;
        this.transferToId = request.getTransferToId();
        this.value = request.getValue();
    }

    public PaymentDto(final Long transferFromId, final Long transferToId, final BigDecimal value) {

        this.transferFromId = transferFromId;
        this.transferToId = transferToId;
        this.value = value;
    }
}
