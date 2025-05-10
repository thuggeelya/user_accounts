package ru.thuggeelya.useraccounts.dao.repository.projection;

import java.math.BigDecimal;
import java.sql.Timestamp;

public interface UserBalanceHistoryProjection {

    Long getUserId();

    BigDecimal getCurrentBalance();

    BigDecimal getMaxBalance();

    Timestamp getLastUpdated();
}
