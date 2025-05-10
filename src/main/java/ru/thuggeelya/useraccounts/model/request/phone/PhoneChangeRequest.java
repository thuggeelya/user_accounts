package ru.thuggeelya.useraccounts.model.request.phone;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PhoneChangeRequest extends PhoneCreateRequest {

    private String oldPhone;
}
