package ru.thuggeelya.useraccounts.service.system;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import ru.thuggeelya.useraccounts.dao.repository.projection.UserBalanceHistoryProjection;
import ru.thuggeelya.useraccounts.dao.repository.system.AccountRepository;
import ru.thuggeelya.useraccounts.dao.repository.system.UserRepository;
import ru.thuggeelya.useraccounts.service.search.UserSearchService;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserBalanceService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final UserSearchService userSearchService;
    private final TransactionTemplate transactionTemplate;

    @Async
    public void processBalance(final UserBalanceHistoryProjection history) {

        final Long userId = history.getUserId();

        var maxBalance = history.getMaxBalance();
        log.info(
                "processing user balance history: id = {}, balance = {}, max = {}, lastUpdated = {}",
                userId, history.getCurrentBalance(), maxBalance, history.getLastUpdated()
        );

        userRepository.lockHistory(userId);

        final BigDecimal newBalance = getNewBalance(history.getCurrentBalance());
        boolean increment = newBalance.compareTo(maxBalance) < 0;

        final BigDecimal finalBalance = increment ? newBalance : maxBalance;

        transactionTemplate.executeWithoutResult(s -> {

            userRepository.saveAndReleaseHistory(userId, increment, finalBalance);
            accountRepository.updateBalance(finalBalance, userId);
        });

        userSearchService.setBalance(userId, finalBalance);
    }

    private static BigDecimal getNewBalance(final BigDecimal current) {
        return BigDecimal.valueOf(current.multiply(BigDecimal.valueOf(1.1d)).doubleValue());
    }
}
