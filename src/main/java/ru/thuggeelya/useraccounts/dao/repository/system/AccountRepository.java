package ru.thuggeelya.useraccounts.dao.repository.system;

import jakarta.persistence.QueryHint;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.thuggeelya.useraccounts.dao.entity.Account;

import java.math.BigDecimal;

import static jakarta.persistence.LockModeType.PESSIMISTIC_READ;
import static jakarta.persistence.LockModeType.PESSIMISTIC_WRITE;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    @Transactional
    @Lock(PESSIMISTIC_READ)
    @Query("select a from Account a where a.userId = ?1")
    @QueryHints({@QueryHint(name = "jakarta.persistence.lock.timeout", value = "1000")})
    Account findByUserIdForRead(final Long userId);

    @Transactional
    @Lock(PESSIMISTIC_WRITE)
    @Query("select a from Account a where a.userId = ?1")
    @QueryHints({@QueryHint(name = "jakarta.persistence.lock.timeout", value = "3000")})
    Account findByUserIdForReadWrite(final Long userId);

    @Modifying
    @Transactional
    @CacheEvict(value = "users", key = "#userId")
    @Query("update Account a set a.balance = :balance where a.userId = :userId")
    void updateBalance(final BigDecimal balance, final Long userId);
}