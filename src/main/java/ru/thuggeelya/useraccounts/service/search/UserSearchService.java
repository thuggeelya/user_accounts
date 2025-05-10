package ru.thuggeelya.useraccounts.service.search;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.thuggeelya.useraccounts.model.dto.UserDto;
import ru.thuggeelya.useraccounts.model.search.SearchUser;

import java.math.BigDecimal;

public interface UserSearchService {

    void indexUser(final SearchUser user);

    void setBalance(final Long id, final BigDecimal balance);

    Page<UserDto> findByDateOfBirthAfter(final String dateOfBirth, final Pageable pageable);

    Page<UserDto> findByPhone(final String phone, final Pageable pageable);

    Page<UserDto> findByEmail(final String email, final Pageable pageable);

    Page<UserDto> findByNameStarts(final String name, final Pageable pageable);
}
