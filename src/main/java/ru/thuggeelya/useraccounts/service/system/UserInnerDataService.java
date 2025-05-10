package ru.thuggeelya.useraccounts.service.system;

public interface UserInnerDataService {

    void save(final String value, final Long userId);

    void change(final String oldValue, final String newValue, final Long userId);

    void delete(final String value, final Long userId);
}
