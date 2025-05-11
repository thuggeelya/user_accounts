package ru.thuggeelya.useraccounts.dao.repository.system;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.thuggeelya.useraccounts.dao.entity.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @NonNull
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "users", key = "#id")
    Optional<User> findById(@NonNull final Long id);

    @Query(
            value = """
                    select u.id
                    from "user" u
                    where u.password = :password and (
                        exists(select 1 from email_data e where e.user_id = u.id and e.email = :login)
                        or
                        exists(select 1 from phone_data p where p.user_id = u.id and p.phone = :login)
                    )
                    limit 1
                    """,
            nativeQuery = true
    )
    @Transactional(readOnly = true)
    Optional<Long> findIdOfAuthUser(final String login, final String password);
}
