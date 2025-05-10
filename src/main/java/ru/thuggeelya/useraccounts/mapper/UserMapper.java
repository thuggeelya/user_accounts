package ru.thuggeelya.useraccounts.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import ru.thuggeelya.useraccounts.dao.entity.EmailData;
import ru.thuggeelya.useraccounts.dao.entity.PhoneData;
import ru.thuggeelya.useraccounts.dao.entity.User;
import ru.thuggeelya.useraccounts.model.dto.UserDto;
import ru.thuggeelya.useraccounts.model.search.ElasticSearchUser;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;

import static java.lang.Long.parseLong;
import static ru.thuggeelya.useraccounts.util.DateTimeUtils.DATE_FORMATTER;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        builder = @Builder(disableBuilder = true)
)
public interface UserMapper {

    @Mapping(target = "emailData", ignore = true)
    @Mapping(target = "phoneData", ignore = true)
    @Mapping(target = "dateOfBirth", ignore = true)
    @Mapping(target = "balance", source = "user.account.balance")
    UserDto toUserDto(final User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "balance", ignore = true)
    @Mapping(target = "emailData", ignore = true)
    @Mapping(target = "phoneData", ignore = true)
    @Mapping(target = "dateOfBirth", ignore = true)
    UserDto toUserDto(final ElasticSearchUser searchUser);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "balance", ignore = true)
    @Mapping(target = "emailData", ignore = true)
    @Mapping(target = "phoneData", ignore = true)
    @Mapping(target = "dateOfBirth", ignore = true)
    ElasticSearchUser toElasticSearchUser(final UserDto dto);

    @AfterMapping
    default void finishMapping(@MappingTarget final UserDto dto, final User user) {

        dto.setDateOfBirth(user.getDateOfBirth().format(DATE_FORMATTER));
        dto.setEmailData(user.getEmailData().stream().map(EmailData::getEmail).toList());
        dto.setPhoneData(user.getPhoneData().stream().map(PhoneData::getPhone).toList());
    }

    @AfterMapping
    default void finishMapping(@MappingTarget final UserDto dto, final ElasticSearchUser searchUser) {

        dto.setId(parseLong(searchUser.getId().trim()));
        dto.setBalance(new BigDecimal(searchUser.getBalance().trim()));
        dto.setDateOfBirth(searchUser.getDateOfBirth().format(DATE_FORMATTER));
        dto.setEmailData(Arrays.stream(searchUser.getEmailData()).toList());
        dto.setPhoneData(Arrays.stream(searchUser.getPhoneData()).toList());
    }

    @AfterMapping
    default void finishMapping(@MappingTarget final ElasticSearchUser searchUser, final UserDto dto) {

        searchUser.setId(dto.getId().toString());
        searchUser.setBalance(dto.getBalance().toString());
        searchUser.setDateOfBirth(LocalDate.parse(dto.getDateOfBirth(), DATE_FORMATTER));
        searchUser.setEmailData(dto.getEmailData().toArray(new String[0]));
        searchUser.setPhoneData(dto.getPhoneData().toArray(new String[0]));
    }
}