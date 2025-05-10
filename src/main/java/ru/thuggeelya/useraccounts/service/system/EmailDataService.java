package ru.thuggeelya.useraccounts.service.system;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.thuggeelya.useraccounts.dao.entity.EmailData;
import ru.thuggeelya.useraccounts.dao.repository.system.EmailDataRepository;

@Service
@RequiredArgsConstructor
public class EmailDataService implements UserInnerDataService {

    private final EmailDataRepository emailDataRepository;

    @Override
    @Transactional
    @CacheEvict(value = "users", key = "#userId")
    public void save(final String email, final Long userId) {
        emailDataRepository.saveAndFlush(EmailData.builder().email(email).userId(userId).build());
    }

    @Override
    @Transactional
    @CacheEvict(value = "users", key = "#userId")
    public void change(final String oldEmail, final String newEmail, final Long userId) {
        emailDataRepository.changeEmail(oldEmail, newEmail);
    }

    @Override
    @Transactional
    @CacheEvict(value = "users", key = "#userId")
    public void delete(final String email, final Long userId) {
        emailDataRepository.deleteByEmail(email);
    }
}
