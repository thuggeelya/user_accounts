package ru.thuggeelya.useraccounts.dao.repository.system;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.thuggeelya.useraccounts.dao.entity.PhoneData;

@Repository
public interface PhoneDataRepository extends JpaRepository<PhoneData, Long> {

    @Modifying(flushAutomatically = true)
    @Transactional(propagation = Propagation.MANDATORY)
    @Query("update PhoneData p set p.phone = ?2 where p.phone = ?1")
    void changePhone(final String oldPhone, final String newPhone);

    @Modifying(flushAutomatically = true)
    @Transactional(propagation = Propagation.MANDATORY)
    @Query("delete from PhoneData p where p.phone = ?1")
    void deleteByPhone(final String phone);
}