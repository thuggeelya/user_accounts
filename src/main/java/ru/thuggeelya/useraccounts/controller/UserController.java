package ru.thuggeelya.useraccounts.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.thuggeelya.useraccounts.model.dto.UserDto;
import ru.thuggeelya.useraccounts.model.request.email.EmailChangeRequest;
import ru.thuggeelya.useraccounts.model.request.email.EmailCreateRequest;
import ru.thuggeelya.useraccounts.model.request.phone.PhoneChangeRequest;
import ru.thuggeelya.useraccounts.model.request.phone.PhoneCreateRequest;
import ru.thuggeelya.useraccounts.service.search.UserSearchService;
import ru.thuggeelya.useraccounts.service.system.UserService;

import static org.springframework.http.ResponseEntity.ok;
import static ru.thuggeelya.useraccounts.model.request.search.SearchParamValues.DATE;
import static ru.thuggeelya.useraccounts.model.request.search.SearchParamValues.EMAIL;
import static ru.thuggeelya.useraccounts.model.request.search.SearchParamValues.NAME;
import static ru.thuggeelya.useraccounts.model.request.search.SearchParamValues.PHONE;

@RestController
@RequiredArgsConstructor
@RequestMapping("${spring.application.base-prefix}/users")
public class UserController {

    private final UserService userService;
    private final UserSearchService userSearchService;

    @PostMapping("/{id}/emails")
    public ResponseEntity<UserDto> addEmail(@PathVariable("id") final Long id,
                                            @Valid @RequestBody final EmailCreateRequest request) {

        return ok(userService.addEmail(id, request.getNewEmail()));
    }

    @PostMapping("/{id}/phones")
    public ResponseEntity<UserDto> addPhone(@PathVariable("id") final Long id,
                                            @Valid @RequestBody final PhoneCreateRequest request) {

        return ok(userService.addPhone(id, request.getNewPhone()));
    }

    @PutMapping("/{id}/emails")
    public ResponseEntity<UserDto> changeEmail(@PathVariable("id") final Long id,
                                               @Valid @RequestBody final EmailChangeRequest request) {

        return ok(userService.changeEmail(id, request.getOldEmail(), request.getNewEmail()));
    }

    @PutMapping("/{id}/phones")
    public ResponseEntity<UserDto> changePhone(@PathVariable("id") final Long id,
                                               @Valid @RequestBody final PhoneChangeRequest request) {

        return ok(userService.changePhone(id, request.getOldPhone(), request.getNewPhone()));
    }

    @DeleteMapping("/{id}/emails")
    public ResponseEntity<?> deleteEmail(@PathVariable("id") final Long id, @RequestParam("email") final String email) {
        return ok(userService.deleteEmail(id, email));
    }

    @DeleteMapping("/{id}/phones")
    public ResponseEntity<?> deletePhone(@PathVariable("id") final Long id, @RequestParam("phone") final String phone) {
        return ok(userService.deletePhone(id, phone));
    }

    @GetMapping
    public ResponseEntity<Page<UserDto>> findByParam(@RequestParam(name = "param") final String param,
                                                     @RequestParam(name = "value", required = false) final String value,
                                                     @RequestParam(name = "page", defaultValue = "0") final int page,
                                                     @RequestParam(name = "size", defaultValue = "5") final int size) {

        final PageRequest pageable = PageRequest.of(page, size);

        return ok(
                switch (param) {
                    case NAME -> userSearchService.findByNameStarts(value, pageable);
                    case DATE -> userSearchService.findByDateOfBirthAfter(value, pageable);
                    case EMAIL -> userSearchService.findByEmail(value, pageable);
                    case PHONE -> userSearchService.findByPhone(value, pageable);
                    default -> Page.empty().map(o -> new UserDto());
                }
        );
    }
}
