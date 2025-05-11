package ru.thuggeelya.useraccounts.model.request.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "login")
public class LoginRequest {

    @NotBlank
    private String login;
    @NotBlank
    private String password;
}
