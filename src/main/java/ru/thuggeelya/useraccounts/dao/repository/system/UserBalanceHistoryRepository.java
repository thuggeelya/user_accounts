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

import static jakarta.persistence.LockModeType.PESSIMISTIC_READ;

@Repository
public interface UserBalanceHistoryRepository extends JpaRepository<UserBalanceHistory, Long> {

    @Query(
            """
                    select h
                    from UserBalanceHistory h
                    where (h.lastUpdated is null or h.lastUpdated < current_timestamp - 30000) and h.increment = true
                    """
    )
    @Transactional(readOnly = true)
    List<UserBalanceHistory> findRelevantBalanceHistories();

    @Transactional
    @Lock(PESSIMISTIC_READ)
    @Query("SELECT h FROM UserBalanceHistory h WHERE h.id = :userId")
    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = "3000")})
    UserBalanceHistory lockHistory(final Long userId);

    @Query(
            """
                    update UserBalanceHistory h
                    set h.increment = :increment, h.currentBalance = :balance, h.lastUpdated = current_timestamp
                    where h.id = :userId
                    """
    )
    @Modifying
    @Transactional
    void updateHistory(final Long userId, final boolean increment, final BigDecimal balance);
}