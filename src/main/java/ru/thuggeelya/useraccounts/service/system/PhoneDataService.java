package ru.thuggeelya.useraccounts.service.system;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.thuggeelya.useraccounts.dao.entity.PhoneData;
import ru.thuggeelya.useraccounts.dao.repository.system.PhoneDataRepository;

@Service
@RequiredArgsConstructor
public class PhoneDataService implements UserInnerDataService {

    private final PhoneDataRepository phoneDataRepository;

    @Override
    @Transactional
    @CacheEvict(value = "users", key = "#userId")
    public void save(final String phone, final Long userId) {
        phoneDataRepository.saveAndFlush(PhoneData.builder().phone(phone).userId(userId).build());
    }

    @Override
    @Transactional
    @CacheEvict(value = "users", key = "#userId")
    public void change(final String oldPhone, final String newPhone, final Long userId) {
        phoneDataRepository.changePhone(oldPhone, newPhone);
    }

    @Override
    @Transactional
    @CacheEvict(value = "users", key = "#userId")
    public void delete(final String phone, final Long userId) {
        phoneDataRepository.deleteByPhone(phone);
    }
}
