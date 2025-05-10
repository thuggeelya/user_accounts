package ru.thuggeelya.useraccounts.dao.repository.system;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.thuggeelya.useraccounts.dao.entity.Account;

import java.math.BigDecimal;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    @Modifying
    @Transactional
    @CacheEvict(value = "users", key = "#userId")
    @Query(value = "update account set balance = :balance where user_id = :userId", nativeQuery = true)
    void updateBalance(final BigDecimal balance, final Long userId);
}