package ru.thuggeelya.useraccounts.service.system;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import ru.thuggeelya.useraccounts.dao.entity.Account;
import ru.thuggeelya.useraccounts.dao.entity.UserBalanceHistory;
import ru.thuggeelya.useraccounts.dao.repository.system.AccountRepository;
import ru.thuggeelya.useraccounts.dao.repository.system.UserBalanceHistoryRepository;
import ru.thuggeelya.useraccounts.exception.ClientException;
import ru.thuggeelya.useraccounts.model.dto.PaymentDto;
import ru.thuggeelya.useraccounts.service.search.UserSearchService;

import java.math.BigDecimal;

import static org.springframework.transaction.annotation.Isolation.SERIALIZABLE;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserBalanceService {

    private final AccountRepository accountRepository;
    private final UserSearchService userSearchService;
    private final TransactionTemplate transactionTemplate;
    private final UserBalanceHistoryRepository historyRepository;

    @Async
    @Transactional(isolation = SERIALIZABLE)
    public void transfer(final PaymentDto payment) {

        final Long fromId = payment.getTransferFromId();
        final Long toId = payment.getTransferToId();
        final BigDecimal value = payment.getValue();

        if (fromId.equals(toId)) {
            throw new ClientException("Cannot transfer to the same user");
        }

        if (value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ClientException("Value must be positive");
        }

        final Account accountFrom = accountRepository.findByUserId(fromId);
        final Account accountTo = accountRepository.findByUserId(toId);

        final UserBalanceHistory fromHistory = historyRepository.lockHistory(fromId);
        final UserBalanceHistory toHistory = historyRepository.lockHistory(toId);

        if (fromHistory == null || toHistory == null) {
            throw new IllegalStateException("Cannot transfer to the same user");
        }

        BigDecimal fromBalance = accountFrom.getBalance();

        if (fromBalance.compareTo(value) <= 0) {
            throw new IllegalArgumentException("Insufficient balance of user with id = " + fromId);
        }

        BigDecimal toBalance = accountTo.getBalance();

        fromBalance = fromBalance.subtract(value);
        toBalance = toBalance.add(value);

        accountRepository.updateBalance(fromBalance, fromId);
        accountRepository.updateBalance(toBalance, toId);

        fromHistory.setCurrentBalance(fromBalance);
        fromHistory.setIncrement(fromBalance.compareTo(fromHistory.getMaxBalance()) < 0);
        historyRepository.save(fromHistory);

        toHistory.setCurrentBalance(toBalance);
        toHistory.setIncrement(toBalance.compareTo(toHistory.getMaxBalance()) < 0);
        historyRepository.save(toHistory);

        log.info("Transfer completed: {} -> {} : {}", fromId, toId, value);

        // todo event elastic setBalance
    }

    @Async
    public void incrementBalance(final UserBalanceHistory history) {

        final Pair<Long, BigDecimal> userBalance = transactionTemplate.execute(s -> {

            final Long userId = history.getId();

            final Account account = accountRepository.findByUserId(userId);
            historyRepository.lockHistory(userId);

            final BigDecimal maxBalance = history.getMaxBalance();
            BigDecimal currentBalance = history.getCurrentBalance();

            log.info(
                    "Processing user balance history: id = {}, balance = {}, max = {}, lastUpdated = {}",
                    userId, currentBalance, maxBalance, history.getLastUpdated()
            );

            if (account.getBalance().compareTo(currentBalance) != 0) {
                currentBalance = account.getBalance();
            }

            final BigDecimal newBalance = getNewBalance(currentBalance);
            boolean increment = newBalance.compareTo(maxBalance) < 0;

            final BigDecimal finalBalance = increment ? newBalance : maxBalance;

            accountRepository.updateBalance(finalBalance, userId);
            historyRepository.updateHistory(userId, increment, finalBalance);

            return Pair.of(userId, finalBalance);
        });

        userSearchService.setBalance(userBalance.getFirst(), userBalance.getSecond());
    }

    private static BigDecimal getNewBalance(final BigDecimal current) {
        return BigDecimal.valueOf(current.multiply(BigDecimal.valueOf(1.1d)).doubleValue());
    }
}
