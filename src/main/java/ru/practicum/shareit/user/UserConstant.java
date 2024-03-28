package ru.practicum.shareit.user;

import lombok.experimental.UtilityClass;

@UtilityClass
public class UserConstant {
    public static final String REGEX_LOGIN = "^\\S*$";
    public static final String USER_REPOSITORY_IMPL = "я люблю нюхать бебру";
    public static final String REGEX_EMAIL = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    public static final String IN_MEMORY_USER_REPOSITORY = "inMemoryUserRepository";
}