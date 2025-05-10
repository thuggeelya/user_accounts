package ru.thuggeelya.useraccounts.model.request.email;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class EmailChangeRequest extends EmailCreateRequest {

    private String oldEmail;
}
