package ru.thuggeelya.useraccounts.model.request.phone;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import static ru.thuggeelya.useraccounts.util.ValidationUtils.PHONE_REGEX;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class PhoneCreateRequest {

    @NotBlank(message = "Phone is null")
    @Pattern(regexp = PHONE_REGEX, message = "String is not a valid phone")
    private String newPhone;
}
