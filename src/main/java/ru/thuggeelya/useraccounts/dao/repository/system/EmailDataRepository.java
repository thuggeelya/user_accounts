package ru.thuggeelya.useraccounts.dao.repository.system;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.thuggeelya.useraccounts.dao.entity.EmailData;

@Repository
public interface EmailDataRepository extends JpaRepository<EmailData, Long> {

    @Modifying(flushAutomatically = true)
    @Transactional(propagation = Propagation.MANDATORY)
    @Query("update EmailData e set e.email = ?2 where e.email = ?1")
    void changeEmail(final String oldEmail, final String newEmail);

    @Modifying(flushAutomatically = true)
    @Transactional(propagation = Propagation.MANDATORY)
    @Query("delete from EmailData e where e.email = ?1")
    void deleteByEmail(final String email);
}