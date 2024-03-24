package ru.practicum.shareit.constant;

import lombok.experimental.UtilityClass;

@UtilityClass
public class UserConstant {
    public static final String REGEX_LOGIN = "^\\S*$";
    public static final String USER_REPOSITORY_IMPL = "UserRepositoryImpl";
    public static final String REGEX_EMAIL = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
}
