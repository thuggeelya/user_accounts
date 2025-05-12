package ru.thuggeelya.useraccounts.service.system;

import jakarta.persistence.PersistenceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import ru.thuggeelya.useraccounts.dao.entity.Account;
import ru.thuggeelya.useraccounts.dao.entity.UserBalanceHistory;
import ru.thuggeelya.useraccounts.dao.repository.system.AccountRepository;
import ru.thuggeelya.useraccounts.dao.repository.system.UserBalanceHistoryRepository;
import ru.thuggeelya.useraccounts.event.model.UserBalanceUpdatedEvent;
import ru.thuggeelya.useraccounts.exception.ClientException;
import ru.thuggeelya.useraccounts.model.dto.PaymentDto;
import ru.thuggeelya.useraccounts.model.response.ResponseDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static java.math.BigDecimal.ZERO;
import static ru.thuggeelya.useraccounts.model.response.ResponseResult.FAILED;
import static ru.thuggeelya.useraccounts.model.response.ResponseResult.OK;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserBalanceService {

    private final AccountRepository accountRepository;
    private final TransactionTemplate transactionTemplate;
    private final ApplicationEventPublisher eventPublisher;
    private final UserBalanceHistoryRepository historyRepository;
    private final TransactionTemplate transactionTemplateSerializable;

    @Transactional
    public List<UserBalanceHistory> findForUpdate() {
        return historyRepository.findRelevantBalanceHistories();
    }

    public ResponseDto transfer(final PaymentDto payment) {

        final AtomicReference<String> message = new AtomicReference<>("Transfer succeeded");

        final BalanceTransactionResult result = transactionTemplateSerializable.execute(s -> {

            try {
                final Long fromId = payment.getTransferFromId();
                final Long toId = payment.getTransferToId();

                final UserBalanceHistory fromHistory = historyRepository.findHistoryForReadWrite(fromId);
                final UserBalanceHistory toHistory = historyRepository.findHistoryForReadWrite(toId);

                final Account accountFrom = accountRepository.findByUserIdForReadWrite(fromId);
                final Account accountTo = accountRepository.findByUserIdForReadWrite(toId);

                final BigDecimal value = payment.getValue();

                validateInputData(fromId, toId, value);

                if (fromHistory == null || toHistory == null) {
                    throw new IllegalStateException("Cannot transfer to the same user");
                }

                BigDecimal fromBalance = accountFrom.getBalance();

                if (fromBalance.compareTo(value) <= 0) {
                    throw new IllegalStateException("Insufficient balance of user with id = " + fromId);
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

                return new BalanceTransactionResult(s, Map.of(fromId, fromBalance, toId, toBalance));
            } catch (final PersistenceException | ClientException | IllegalStateException ex) {

                message.set(ex.getMessage());

                log.error("Method transfer failed for {}. {}", payment, message, ex);
                s.setRollbackOnly();

                return new BalanceTransactionResult(s, Map.of());
            }
        });

        afterCommit(result);

        return ResponseDto.builder()
                             .id(payment.getTransferFromId())
                             .result(result.status.isRollbackOnly() ? FAILED : OK)
                             .message(message.get())
                             .build();
    }

    @Async
    public void incrementBalance(final UserBalanceHistory history) {

        final BalanceTransactionResult result = transactionTemplate.execute(s -> {

            final Long userId = history.getId();

            try {

                historyRepository.findHistoryForReadWrite(userId);
                final Account account = accountRepository.findByUserIdForRead(userId);

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

                return new BalanceTransactionResult(s, Map.of(userId, finalBalance));
            } catch (final PersistenceException ex) {

                log.error("Method incrementBalance failed for userId = {}. {}", userId, ex.getMessage(), ex);
                s.setRollbackOnly();

                return new BalanceTransactionResult(s, Map.of());
            }
        });

        afterCommit(result);
    }

    private static void validateInputData(final Long fromId, final Long toId, final BigDecimal value) {

        if (fromId.equals(toId)) {
            throw new ClientException("Cannot transfer to the same user");
        }

        if (value.compareTo(ZERO) <= 0) {
            throw new ClientException("Value must be positive");
        }
    }

    private static BigDecimal getNewBalance(final BigDecimal current) {
        return BigDecimal.valueOf(current.multiply(BigDecimal.valueOf(1.1d)).doubleValue());
    }

    private void afterCommit(final BalanceTransactionResult result) {

        if (result.status != null && result.status.isCompleted() && !result.status.isRollbackOnly()) {

            result.userBalances.forEach((id, balance) -> eventPublisher.publishEvent(
                    new UserBalanceUpdatedEvent(this, id, balance)
            ));
        }
    }

    private record BalanceTransactionResult(TransactionStatus status, Map<Long, BigDecimal> userBalances) { }
}
