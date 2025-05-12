package ru.thuggeelya.useraccounts.service.system;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.support.SimpleTransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import ru.thuggeelya.useraccounts.dao.entity.Account;
import ru.thuggeelya.useraccounts.dao.entity.UserBalanceHistory;
import ru.thuggeelya.useraccounts.dao.repository.system.AccountRepository;
import ru.thuggeelya.useraccounts.dao.repository.system.UserBalanceHistoryRepository;
import ru.thuggeelya.useraccounts.model.dto.PaymentDto;

import java.math.BigDecimal;
import java.sql.Timestamp;

import static java.time.Instant.now;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserBalanceServiceTest {

    private static final Long FROM_ID = 1L;
    private static final Long TO_ID = 2L;
    private static final BigDecimal TRANSFER_VALUE = new BigDecimal("100.00");
    private static final BigDecimal INITIAL_BALANCE = TRANSFER_VALUE;
    private static final Timestamp CREATED = Timestamp.from(now());

    @Mock
    private AccountRepository accountRepository;
    @Mock
    private ApplicationEventPublisher eventPublisher;
    @Mock
    private UserBalanceHistoryRepository historyRepository;
    @Mock
    private TransactionTemplate transactionTemplateSerializable;
    @InjectMocks
    private UserBalanceService userBalanceService;

    private Account fromAccount;
    private Account toAccount;
    private UserBalanceHistory fromHistory;
    private UserBalanceHistory toHistory;

    @BeforeEach
    void setup() {

        fromAccount = new Account();
        fromAccount.setUserId(FROM_ID);
        fromAccount.setBalance(new BigDecimal("500.00"));

        toAccount = new Account();
        toAccount.setUserId(TO_ID);
        toAccount.setBalance(new BigDecimal("200.00"));

        fromHistory = new UserBalanceHistory();
        fromHistory.setId(FROM_ID);
        fromHistory.setInitialBalance(INITIAL_BALANCE);
        fromHistory.setCurrentBalance(fromAccount.getBalance());
        fromHistory.setMaxBalance(new BigDecimal("500.00"));
        fromHistory.setIncrement(false);
        fromHistory.setCreated(CREATED);

        toHistory = new UserBalanceHistory();
        toHistory.setId(TO_ID);
        toHistory.setInitialBalance(INITIAL_BALANCE);
        toHistory.setCurrentBalance(toAccount.getBalance());
        toHistory.setMaxBalance(new BigDecimal("200.00"));
        toHistory.setIncrement(false);
        toHistory.setCreated(CREATED);
    }

    @Test
    void transfer_shouldCompleteSuccessfully() {

        final PaymentDto dto = new PaymentDto(FROM_ID, TO_ID, TRANSFER_VALUE);

        setUpMockMethods();

        when(transactionTemplateSerializable.execute(any())).thenAnswer(invocation -> {
            final TransactionCallback<?> callback = invocation.getArgument(0);
            return callback.doInTransaction(new SimpleTransactionStatus());
        });

        userBalanceService.transfer(dto);

        verify(accountRepository).updateBalance(new BigDecimal("400.00"), FROM_ID);
        verify(accountRepository).updateBalance(new BigDecimal("300.00"), TO_ID);
        verify(historyRepository).save(fromHistory);
        verify(historyRepository).save(toHistory);
    }

    @Test
    void transfer_shouldFailIfSameUser() {

        final PaymentDto dto = new PaymentDto(FROM_ID, FROM_ID, TRANSFER_VALUE);

        when(transactionTemplateSerializable.execute(any())).thenAnswer(invocation -> {
            final TransactionCallback<?> callback = invocation.getArgument(0);
            return callback.doInTransaction(new SimpleTransactionStatus());
        });

        userBalanceService.transfer(dto);

        verify(accountRepository, never()).updateBalance(any(), any());
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void transfer_shouldFailIfValueIsNonPositive() {

        final PaymentDto dto = new PaymentDto(FROM_ID, TO_ID, BigDecimal.ZERO);

        when(transactionTemplateSerializable.execute(any())).thenAnswer(invocation -> {
            final TransactionCallback<?> callback = invocation.getArgument(0);
            return callback.doInTransaction(new SimpleTransactionStatus());
        });

        userBalanceService.transfer(dto);

        verify(accountRepository, never()).updateBalance(any(), any());
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void transfer_shouldFailIfInsufficientBalance() {

        fromAccount.setBalance(new BigDecimal("50.00"));

        final PaymentDto dto = new PaymentDto(FROM_ID, TO_ID, TRANSFER_VALUE);

        setUpMockMethods();

        when(transactionTemplateSerializable.execute(any())).thenAnswer(invocation -> {
            final TransactionCallback<?> callback = invocation.getArgument(0);
            return callback.doInTransaction(new SimpleTransactionStatus());
        });

        userBalanceService.transfer(dto);

        verify(accountRepository, never()).updateBalance(any(), any());
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void transfer_shouldFailIfHistoryMissing() {

        final PaymentDto dto = new PaymentDto(FROM_ID, TO_ID, TRANSFER_VALUE);

        when(accountRepository.findByUserIdForReadWrite(FROM_ID)).thenReturn(fromAccount);
        when(accountRepository.findByUserIdForReadWrite(TO_ID)).thenReturn(toAccount);
        when(historyRepository.findHistoryForReadWrite(FROM_ID)).thenReturn(null);
        when(historyRepository.findHistoryForReadWrite(TO_ID)).thenReturn(null);

        when(transactionTemplateSerializable.execute(any()))
                .thenAnswer(invocation -> {
                    TransactionCallback<?> callback = invocation.getArgument(0);
                    return callback.doInTransaction(new SimpleTransactionStatus());
                });

        userBalanceService.transfer(dto);

        verify(accountRepository, never()).updateBalance(any(), any());
        verify(eventPublisher, never()).publishEvent(any());
    }

    private void setUpMockMethods() {

        when(accountRepository.findByUserIdForReadWrite(FROM_ID)).thenReturn(fromAccount);
        when(accountRepository.findByUserIdForReadWrite(TO_ID)).thenReturn(toAccount);
        when(historyRepository.findHistoryForReadWrite(FROM_ID)).thenReturn(fromHistory);
        when(historyRepository.findHistoryForReadWrite(TO_ID)).thenReturn(toHistory);
    }
}
