package ru.thuggeelya.useraccounts.event.listener;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import ru.thuggeelya.useraccounts.event.model.UserBalanceUpdatedEvent;
import ru.thuggeelya.useraccounts.service.search.UserSearchService;

@Component
@RequiredArgsConstructor
public class UserBalanceUpdatedEventListener {

    private final UserSearchService userSearchService;

    @EventListener
    public void handle(final UserBalanceUpdatedEvent event) {
        userSearchService.setBalance(event.getUserId(), event.getBalance());
    }
}
