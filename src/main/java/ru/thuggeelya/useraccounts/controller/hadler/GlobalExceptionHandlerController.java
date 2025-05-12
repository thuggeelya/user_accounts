package ru.thuggeelya.useraccounts.controller.hadler;

import jakarta.servlet.ServletException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.thuggeelya.useraccounts.exception.AccessDeniedException;
import ru.thuggeelya.useraccounts.exception.AuthenticationException;
import ru.thuggeelya.useraccounts.exception.ClientException;
import ru.thuggeelya.useraccounts.model.response.ResponseDto;

import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.ResponseEntity.status;
import static ru.thuggeelya.useraccounts.model.response.ResponseResult.FAILED;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandlerController {

    private static final String ERROR_MESSAGE = "Ошибка: статус = [{}] : детали = [{}].";

    @ResponseBody
    @ExceptionHandler({ClientException.class, DataAccessException.class, ServletException.class})
    public ResponseEntity<ResponseDto> handleClientException(final Exception ex) {

        final String message = getMessage(ex);

        log.warn(ERROR_MESSAGE, BAD_REQUEST, message, ex);

        return status(BAD_REQUEST).body(ResponseDto.builder().result(FAILED).message(message).build());
    }

    @ResponseBody
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ResponseDto> handleAccessDeniedException(final AccessDeniedException ex) {

        final String message = getMessage(ex);

        log.warn(ERROR_MESSAGE, FORBIDDEN, message, ex);

        return status(FORBIDDEN).body(ResponseDto.builder().result(FAILED).message(message).build());
    }

    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseDto> handleValidationException(final MethodArgumentNotValidException ex) {

        final String message = ex.getBindingResult()
                                 .getFieldErrors()
                                 .stream()
                                 .map(FieldError::getDefaultMessage)
                                 .collect(Collectors.joining("; "));

        log.warn(ERROR_MESSAGE, BAD_REQUEST, message, ex);

        return status(BAD_REQUEST).body(ResponseDto.builder().result(FAILED).message(message).build());
    }

    @ResponseBody
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ResponseDto> handleAuthenticationException(final AuthenticationException ex) {

        final String message = getMessage(ex);

        log.warn(ERROR_MESSAGE, UNAUTHORIZED, message, ex);

        return status(UNAUTHORIZED).body(ResponseDto.builder().result(FAILED).message(message).build());
    }

    @ResponseBody
    @ExceptionHandler
    public ResponseEntity<ResponseDto> handleException(final Throwable ex) {

        final String message = getMessage(ex);

        log.warn(ERROR_MESSAGE, INTERNAL_SERVER_ERROR, message, ex);

        return status(INTERNAL_SERVER_ERROR).body(ResponseDto.builder().result(FAILED).message(message).build());
    }

    private static String getMessage(final Throwable ex) {
        return isBlank(ex.getMessage()) ? ex.getClass().getSimpleName() : ex.getMessage();
    }
}
