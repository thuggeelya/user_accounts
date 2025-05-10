package ru.thuggeelya.useraccounts.service.system;

import ru.thuggeelya.useraccounts.model.dto.UserDto;

public interface UserService {

    UserDto addEmail(final Long userId, final String email);

    UserDto addPhone(final Long userId, final String phone);

    UserDto changeEmail(final Long userId, final String oldEmail, final String newEmail);

    UserDto changePhone(final Long userId, final String oldPhone, final String newPhone);

    UserDto deleteEmail(final Long userId, final String email);

    UserDto deletePhone(final Long userId, final String phone);
}
