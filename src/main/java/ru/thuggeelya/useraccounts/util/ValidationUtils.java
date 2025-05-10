package ru.thuggeelya.useraccounts.util;

import ru.thuggeelya.useraccounts.exception.ClientException;

public final class ValidationUtils {

    public static String normalizeRuPhone(final String input) {

        final String phone = input.replaceAll("^(\\+7|8)", "7").replaceAll("[^0-9]", "");

        if (phone.length() == 11) {
            return phone;
        }

        throw new ClientException("Phone is incorrect: " + input);
    }
}
