package ru.thuggeelya.useraccounts.model.request.email;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EmailCreateRequest {

    @NotBlank(message = "Email is null")
    @Email(message = "String is not a valid email")
    private String newEmail;
}
