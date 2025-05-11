package ru.thuggeelya.useraccounts.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AcceptedResponseDto {

    private String location;
    private ResponseResult result;
}
