package ru.thuggeelya.useraccounts.model.request.phone;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PhoneCreateRequest {

    @NotBlank(message = "Phone is null")
    @Pattern(regexp = "^((8|(\\+)?7)[\\- ]?)(\\(?\\d{3}\\)?[\\- ]?)[\\d\\- ]{7}$", message = "String is not a valid phone")
    private String newPhone;
}
