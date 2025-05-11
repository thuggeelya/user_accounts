package ru.thuggeelya.useraccounts.util;

import ru.thuggeelya.useraccounts.exception.ClientException;

import static org.apache.commons.lang3.StringUtils.isBlank;

public final class ValidationUtils {

    public static final String EMAIL_REGEX = "^.+@.+\\..+$";
    public static final String PHONE_REGEX
            = "^((8|(\\+)?7)[\\- ]?)(\\(?\\d{3}\\)?[\\- ]?\\d{3}[\\- ]?)\\d{2}[\\- ]?\\d{2}$";

    public static String normalizeRuPhone(final String input) {

        if (isBlank(input) || input.matches(EMAIL_REGEX)) {
            return input;
        }

        final String phone = input.replaceAll("^(\\+7|8)", "7").replaceAll("[^0-9]", "");

        if (phone.length() == 11) {
            return phone;
        }

        throw new ClientException("Phone is incorrect: " + input);
    }
}
