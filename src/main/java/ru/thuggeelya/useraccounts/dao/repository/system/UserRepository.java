package ru.thuggeelya.useraccounts.dao.repository.system;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.thuggeelya.useraccounts.dao.entity.User;
import ru.thuggeelya.useraccounts.dao.repository.projection.UserBalanceHistoryProjection;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "users", key = "#id")
    Optional<User> findById(final Long id);

    @Query(
            value = """
                    SELECT user_id AS userId,
                           current_balance AS currentBalance,
                           max_balance AS maxBalance,
                           last_updated AS lastUpdated
                    FROM user_balance_history
                    WHERE (last_updated IS NULL OR (last_updated < NOW() - INTERVAL '30 seconds'))
                      AND increment AND NOT lock
                    """,
            nativeQuery = true
    )
    @Transactional(readOnly = true)
    List<UserBalanceHistoryProjection> findRelevantBalanceHistories();

    @Transactional
    @Modifying(flushAutomatically = true)
    @Query(value = "update user_balance_history set lock = true where user_id = :userId", nativeQuery = true)
    void lockHistory(final Long userId);

    @Modifying
    @Query(
            value = """
                    update user_balance_history
                    set lock = false, increment = :increment, current_balance = :balance, last_updated = NOW()
                    where user_id = :userId
                    """,
            nativeQuery = true
    )
    @Transactional
    void saveAndReleaseHistory(final Long userId, final boolean increment, final BigDecimal balance);
}
