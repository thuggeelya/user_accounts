package ru.thuggeelya.useraccounts.security.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.thuggeelya.useraccounts.dao.repository.system.UserRepository;
import ru.thuggeelya.useraccounts.exception.AuthenticationException;
import ru.thuggeelya.useraccounts.model.request.auth.LoginRequest;
import ru.thuggeelya.useraccounts.model.response.JwtResponse;
import ru.thuggeelya.useraccounts.security.utils.JwtUtils;

import static ru.thuggeelya.useraccounts.util.ValidationUtils.normalizeRuPhone;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;

    public JwtResponse login(final LoginRequest request) {

        final String login = request.getLogin();

        log.info("Login request: {}", login);

        final Long userId = userRepository.findIdOfAuthUser(normalizeRuPhone(login), request.getPassword())
                                          .orElseThrow(AuthenticationException::new);

        return new JwtResponse(jwtUtils.generateTokenFromUserId(userId), userId, login);
    }
}
