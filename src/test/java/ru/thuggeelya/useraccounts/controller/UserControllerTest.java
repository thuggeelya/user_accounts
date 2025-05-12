package ru.thuggeelya.useraccounts.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.thuggeelya.useraccounts.config.TestContainersIT;
import ru.thuggeelya.useraccounts.dao.entity.Account;
import ru.thuggeelya.useraccounts.dao.entity.EmailData;
import ru.thuggeelya.useraccounts.dao.entity.PhoneData;
import ru.thuggeelya.useraccounts.dao.entity.User;
import ru.thuggeelya.useraccounts.dao.repository.system.UserRepository;
import ru.thuggeelya.useraccounts.model.dto.UserDto;
import ru.thuggeelya.useraccounts.model.request.phone.PhoneChangeRequest;
import ru.thuggeelya.useraccounts.model.response.ResponseDto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static java.time.LocalDate.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.thuggeelya.useraccounts.model.response.ResponseResult.FAILED;

public class UserControllerTest extends TestContainersIT {

    private static final String NAME = "test";
    private static final String PHONE = "79998887766";
    private static final String EMAIL = "test1@test.com";
    private static final String NEW_EMAIL = "test2@test.com";
    private static final String NEW_PHONE = "8 (900) 800 70-60";
    private static final String NEW_PHONE_NORMAL = "79008007060";
    private static final String NEW_PHONE_INVALID = "8 (900) 800 70-601";
    private static final String BASE_URL = "/api/1/users/{id}/";
    private static final LocalDate DATE_OF_BIRTH = now().minusYears(25);

    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    protected UserRepository userRepository;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void changePhone_shouldCompleteSuccessfully() throws Exception {

        final Long id = createUser(EMAIL, PHONE);

        final String url = BASE_URL + "phones";

        final PhoneChangeRequest request = PhoneChangeRequest.builder()
                                                             .oldPhone(PHONE)
                                                             .newPhone(NEW_PHONE)
                                                             .build();

        mockMvc
                .perform(
                        put(url, id)
                                .contentType(APPLICATION_JSON_VALUE)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(this::checkPhone);
    }

    private void checkPhone(final MvcResult result) throws Exception {

        final String contentAsString = result.getResponse().getContentAsString();

        final UserDto response = new Gson().fromJson(contentAsString, new TypeToken<UserDto>() { }.getType());

        assertThat(response).isNotNull();

        final List<String> phoneData = response.getPhoneData();

        assertThat(phoneData).containsOnly(NEW_PHONE_NORMAL);
    }

    @Test
    void changePhone_shouldFailWhenInvalid() throws Exception {

        final Long id = createUser(EMAIL, PHONE);

        final String url = BASE_URL + "phones";

        final PhoneChangeRequest request = PhoneChangeRequest.builder()
                                                             .oldPhone(PHONE)
                                                             .newPhone(NEW_PHONE_INVALID)
                                                             .build();

        mockMvc
                .perform(
                        put(url, id)
                                .contentType(APPLICATION_JSON_VALUE)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(this::checkPhoneInvalid);
    }

    private void checkPhoneInvalid(final MvcResult result) throws Exception {

        final String contentAsString = result.getResponse().getContentAsString();

        final ResponseDto response = new Gson().fromJson(contentAsString, new TypeToken<ResponseDto>() { }.getType());

        assertThat(response).isNotNull();
        assertThat(response.getResult()).isEqualTo(FAILED);
    }

    @Test
    void changePhone_shouldFailWhenExists() throws Exception {

        final Long id = createUser(EMAIL, PHONE);
        createUser(NEW_EMAIL, NEW_PHONE_NORMAL);

        final String url = BASE_URL + "phones";

        final PhoneChangeRequest request = PhoneChangeRequest.builder()
                                                             .oldPhone(PHONE)
                                                             .newPhone(NEW_PHONE)
                                                             .build();

        mockMvc
                .perform(
                        put(url, id)
                                .contentType(APPLICATION_JSON_VALUE)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(this::checkPhoneInvalid);
    }

    @Test
    void changePhone_shouldFailWhenTheSame() throws Exception {

        final Long id = createUser(EMAIL, PHONE);

        final String url = BASE_URL + "phones";

        final PhoneChangeRequest request = PhoneChangeRequest.builder()
                                                             .oldPhone(PHONE)
                                                             .newPhone(PHONE)
                                                             .build();

        mockMvc
                .perform(
                        put(url, id)
                                .contentType(APPLICATION_JSON_VALUE)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(this::checkPhoneInvalid);
    }

    private Long createUser(final String email, final String phone) {

        final User user = userRepository.save(buildUser());

        final Long userId = user.getId();

        user.setAccount(buildAccount(userId));
        user.setEmailData(List.of(buildEmailData(userId, email)));
        user.setPhoneData(List.of(buildPhoneData(userId, phone)));

        userRepository.save(user);

        return userId;
    }

    private static User buildUser() {

        final User user = new User();

        user.setName(NAME);
        user.setPassword(NAME);
        user.setDateOfBirth(DATE_OF_BIRTH);

        return user;
    }

    private static PhoneData buildPhoneData(final Long userId, final String phone) {

        final PhoneData phoneData = new PhoneData();

        phoneData.setUserId(userId);
        phoneData.setPhone(phone);

        return phoneData;
    }

    private static EmailData buildEmailData(final Long userId, final String email) {

        final EmailData emailData = new EmailData();

        emailData.setUserId(userId);
        emailData.setEmail(email);

        return emailData;
    }

    private static Account buildAccount(final Long userId) {

        final Account account = new Account();

        account.setUserId(userId);
        account.setBalance(BigDecimal.valueOf(100));

        return account;
    }
}
