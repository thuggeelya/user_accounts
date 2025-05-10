package ru.thuggeelya.useraccounts.event.model;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import ru.thuggeelya.useraccounts.model.dto.UserDto;

@Getter
public class UserCommitedEvent extends ApplicationEvent {

    private final UserDto user;

    public UserCommitedEvent(final Object source, final UserDto user) {

        super(source);
        this.user = user;
    }
}
