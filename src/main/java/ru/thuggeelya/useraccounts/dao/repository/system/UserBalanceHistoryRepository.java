package ru.thuggeelya.useraccounts.dao.repository.system;

import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.thuggeelya.useraccounts.dao.entity.UserBalanceHistory;

import java.math.BigDecimal;
import java.util.List;

import static jakarta.persistence.LockModeType.PESSIMISTIC_WRITE;

@Repository
public interface UserBalanceHistoryRepository extends JpaRepository<UserBalanceHistory, Long> {

    @Query(
            value = """
                    select * from user_balance_history
                    where (last_updated is null or last_updated < now() - interval '30 seconds') and increment is true
                    for update skip locked
                    """,
            nativeQuery = true
    )
    @Transactional
    List<UserBalanceHistory> findRelevantBalanceHistories();

    @Transactional
    @Lock(PESSIMISTIC_WRITE)
    @Query("SELECT h FROM UserBalanceHistory h WHERE h.id = :userId")
    @QueryHints({@QueryHint(name = "jakarta.persistence.lock.timeout", value = "5000")})
    UserBalanceHistory findHistoryForReadWrite(final Long userId);

    @Query(
            """
                    update UserBalanceHistory h
                    set h.increment = :increment,
                        h.currentBalance = :balance,
                        h.lastUpdated = current_timestamp
                    where h.id = :userId
                    """
    )
    @Modifying
    @Transactional
    void updateHistory(final Long userId, final boolean increment, final BigDecimal balance);
}