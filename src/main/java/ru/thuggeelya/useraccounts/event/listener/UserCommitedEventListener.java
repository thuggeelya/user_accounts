package ru.thuggeelya.useraccounts.event.listener;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;
import ru.thuggeelya.useraccounts.event.model.UserCommitedEvent;
import ru.thuggeelya.useraccounts.mapper.UserMapper;
import ru.thuggeelya.useraccounts.service.search.UserSearchService;

import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;

@Component
@RequiredArgsConstructor
public class UserCommitedEventListener {

    private final UserMapper userMapper;
    private final UserSearchService userSearchService;

    @Async
    @TransactionalEventListener
    @Transactional(propagation = REQUIRES_NEW)
    public void handle(final UserCommitedEvent event) {
        userSearchService.indexUser(userMapper.toElasticSearchUser(event.getUser()));
    }
}
