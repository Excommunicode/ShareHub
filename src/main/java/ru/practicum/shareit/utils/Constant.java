package ru.practicum.shareit.utils;

import lombok.experimental.UtilityClass;

import java.time.format.DateTimeFormatter;

@UtilityClass
public final class Constant {

    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    public static final String ALL = "ALL";

    public static final String X_SHARER_USER_ID = "X-Sharer-User-Id";

    public static final String REGEX_LOGIN = "^\\S*$";

    public static final String REGEX_EMAIL = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
}