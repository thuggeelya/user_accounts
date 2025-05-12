package ru.thuggeelya.useraccounts.event.model;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.math.BigDecimal;

@Getter
public class UserBalanceUpdatedEvent extends ApplicationEvent {

    private final Long userId;
    private final BigDecimal balance;

    public UserBalanceUpdatedEvent(final Object source, final Long userId, final BigDecimal balance) {

        super(source);
        this.userId = userId;
        this.balance = balance;
    }
}
