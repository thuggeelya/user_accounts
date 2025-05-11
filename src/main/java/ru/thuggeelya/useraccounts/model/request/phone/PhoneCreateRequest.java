package ru.thuggeelya.useraccounts.model.request.phone;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;

import static ru.thuggeelya.useraccounts.util.ValidationUtils.PHONE_REGEX;

@Data
@NoArgsConstructor
public class PhoneCreateRequest {

    @NotBlank(message = "Phone is null")
    @Pattern(regexp = PHONE_REGEX, message = "String is not a valid phone")
    private String newPhone;
}
