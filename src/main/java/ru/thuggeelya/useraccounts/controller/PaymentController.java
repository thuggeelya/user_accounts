package ru.thuggeelya.useraccounts.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.thuggeelya.useraccounts.exception.AuthenticationException;
import ru.thuggeelya.useraccounts.model.dto.PaymentDto;
import ru.thuggeelya.useraccounts.model.request.payment.PaymentRequest;
import ru.thuggeelya.useraccounts.model.response.ResponseDto;
import ru.thuggeelya.useraccounts.model.response.ResponseResult;
import ru.thuggeelya.useraccounts.security.utils.JwtUtils;
import ru.thuggeelya.useraccounts.service.system.UserBalanceService;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RestController
@RequiredArgsConstructor
@RequestMapping("${spring.application.base-prefix}/actions")
public class PaymentController {

    private final JwtUtils jwtUtils;
    private final UserBalanceService userBalanceService;

    @PostMapping("/transfer")
    public ResponseEntity<ResponseDto> transferMoney(@Valid @RequestBody final PaymentRequest request,
                                                     @RequestHeader(value = AUTHORIZATION, required = false)
                                                             final String headerAuth) {

        final String jwt = jwtUtils.parseJwt(headerAuth);

        if (jwt == null || !jwtUtils.validateJwtToken(jwt)) {
            throw new AuthenticationException("Invalid JWT token");
        }

        final ResponseDto response = userBalanceService.transfer(
                new PaymentDto(jwtUtils.getUserIdFromJwtToken(jwt), request)
        );

        return ResponseEntity
                .status(ResponseResult.OK.equals(response.getResult()) ? HttpStatus.OK : HttpStatus.BAD_REQUEST)
                .body(response);
    }
}
