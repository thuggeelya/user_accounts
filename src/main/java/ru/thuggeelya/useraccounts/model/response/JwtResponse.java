package ru.thuggeelya.useraccounts.model.response;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class JwtResponse {

    private String type = "Bearer";
    private final String token;
    private final Long userId;
    private final String login;
}
