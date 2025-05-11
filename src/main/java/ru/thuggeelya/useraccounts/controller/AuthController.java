package ru.thuggeelya.useraccounts.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.thuggeelya.useraccounts.model.request.auth.LoginRequest;
import ru.thuggeelya.useraccounts.model.response.JwtResponse;
import ru.thuggeelya.useraccounts.security.service.AuthService;

@RestController
@RequiredArgsConstructor
@RequestMapping("${spring.application.base-prefix}")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody final LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
