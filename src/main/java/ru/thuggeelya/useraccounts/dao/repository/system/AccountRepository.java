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

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    @Lock(PESSIMISTIC_READ)
    @Transactional(readOnly = true)
    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value ="3000")})
    Account findByUserId(final Long userId);

    @Modifying
    @Transactional
    @CacheEvict(value = "users", key = "#userId")
    @Query("update Account a set a.balance = :balance where a.userId = :userId")
    void updateBalance(final BigDecimal balance, final Long userId);
}