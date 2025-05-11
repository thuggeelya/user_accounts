package ru.thuggeelya.useraccounts.service.system;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.thuggeelya.useraccounts.aspect.RuPhoneValidated;
import ru.thuggeelya.useraccounts.dao.repository.system.UserRepository;
import ru.thuggeelya.useraccounts.event.model.UserCommitedEvent;
import ru.thuggeelya.useraccounts.exception.ClientException;
import ru.thuggeelya.useraccounts.mapper.UserMapper;
import ru.thuggeelya.useraccounts.model.dto.UserDto;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final UserInnerDataService emailDataService;
    private final UserInnerDataService phoneDataService;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public UserDto addEmail(final Long userId, final String email) {

        log.info("Adding email for user {}: {}", userId, email);

        emailDataService.save(email, userId);

        log.info("saving");

        final UserDto userDto = getUserDto(userId);
        eventPublisher.publishEvent(new UserCommitedEvent(this, userDto));

        return userDto;
    }

    @Override
    @Transactional
    @RuPhoneValidated
    public UserDto addPhone(final Long userId, final String phone) {

        log.info("Adding phone for user {}: {}", userId, phone);

        phoneDataService.save(phone, userId);

        final UserDto userDto = getUserDto(userId);
        eventPublisher.publishEvent(new UserCommitedEvent(this, userDto));

        return userDto;
    }

    @Override
    @Transactional
    public UserDto changeEmail(final Long userId, final String oldEmail, final String newEmail) {

        log.info("Changing email for user {} from {} to {}", userId, oldEmail, newEmail);

        emailDataService.change(oldEmail, newEmail, userId);

        final UserDto userDto = getUserDto(userId);
        eventPublisher.publishEvent(new UserCommitedEvent(this, userDto));

        return userDto;
    }

    @Override
    @Transactional
    @RuPhoneValidated
    public UserDto changePhone(final Long userId, final String oldPhone, final String newPhone) {

        log.info("Changing phone for user {} from {} to {}", userId, oldPhone, newPhone);

        phoneDataService.change(oldPhone, newPhone, userId);

        final UserDto userDto = getUserDto(userId);
        eventPublisher.publishEvent(new UserCommitedEvent(this, userDto));

        return userDto;
    }

    @Override
    @Transactional
    public UserDto deleteEmail(final Long userId, final String email) {

        log.info("Deleting email for user {}: {}", userId, email);

        emailDataService.delete(email, userId);

        final UserDto userDto = getUserDto(userId);
        eventPublisher.publishEvent(new UserCommitedEvent(this, userDto));

        return userDto;
    }

    @Override
    @Transactional
    @RuPhoneValidated
    public UserDto deletePhone(final Long userId, final String phone) {

        log.info("Deleting phone for user {}: {}", userId, phone);

        phoneDataService.delete(phone, userId);

        final UserDto userDto = getUserDto(userId);
        eventPublisher.publishEvent(new UserCommitedEvent(this, userDto));

        return userDto;
    }

    private UserDto getUserDto(final Long userId) {

        return userMapper.toUserDto(userRepository.findById(userId).orElseThrow(
                () -> new ClientException("ElasticSearchUser not found with id " + userId)
        ));
    }
}
